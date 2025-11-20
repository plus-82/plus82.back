package com.etplus.service;

import com.etplus.cache.RedisStorage;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.RequestReIssueTokenDTO;
import com.etplus.controller.dto.RequestResetPasswordDto;
import com.etplus.controller.dto.ResetPasswordDto;
import com.etplus.controller.dto.SignInDto;
import com.etplus.controller.dto.VerifyEmailDto;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.EmailVerificationCodeException;
import com.etplus.exception.EmailVerificationCodeException.EmailVerificationCodeExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.UserException;
import com.etplus.exception.UserException.UserExceptionCode;
import com.etplus.provider.DiscordNotificationProvider;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.JwtProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.EmailVerificationCodeRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.CountryEntity;
import com.etplus.repository.domain.EmailVerificationCodeEntity;
import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.EmailVerificationCodeType;
import com.etplus.repository.domain.code.MessageTemplateType;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.util.UuidProvider;
import com.etplus.vo.TokenVO;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

  @Value("${email.expiration-minute}")
  private Integer EMAIL_EXPIRATION_MINUTE;
  @Value("${email.max-try-count}")
  private Integer EMAIL_MAX_TRY_COUNT;
  @Value("${url.front}")
  private String FRONT_URL;

  private final UserRepository userRepository;
  private final CountryRepository countryRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final EmailVerificationCodeRepository emailVerificationCodeRepository;
  private final PasswordProvider passwordProvider;
  private final EmailProvider emailProvider;
  private final JwtProvider jwtProvider;
  private final RedisStorage redisStorage;
  private final DiscordNotificationProvider discordNotificationProvider;

  @Transactional
  public void signUp(SignUpDto dto) {
    log.info("signUp 시작 - email: {}, firstName: {}, lastName: {}, genderType: {}, birthDate: {}, countryId: {}",
        dto.email(), dto.firstName(), dto.lastName(), dto.genderType(), dto.birthDate(), dto.countryId());
    // 이미 가입한 이메일인 경우 예외 처리
    if (userRepository.existsByEmail(dto.email())) {
      log.warn("이미 가입한 이메일 - email: {}", dto.email());
      throw new UserException(UserExceptionCode.ALREADY_USED_EMAIL);
    }

    // 인증된 이메일이 있는지 확인 후 예외 처리
    boolean isEmailVerified = emailVerificationCodeRepository
        .existsByEmailAndEmailVerificationCodeTypeAndVerifiedIsTrue(dto.email(), EmailVerificationCodeType.SIGN_UP);
    if (!isEmailVerified) {
      log.warn("이메일 인증되지 않음 - email: {}", dto.email());
      throw new UserException(UserExceptionCode.NOT_VERIFIED_EMAIL);
    }

    CountryEntity country = countryRepository.findById(dto.countryId()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND));

    // 사용자 저장
    UserEntity userEntity = new UserEntity(
        null,
        dto.firstName(),
        dto.lastName(),
        null,
        dto.firstName() + " " + dto.lastName(),
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        true,
        RoleType.TEACHER,
        country,
        null,
        true
    );
    userRepository.save(userEntity);

    // Discord 알림 전송
    String userName = userEntity.getName() != null ? userEntity.getName() : 
        (userEntity.getFirstName() + " " + userEntity.getLastName());
    String countryName = country != null && country.getCountryNameEn() != null ? 
        country.getCountryNameEn() : "";
    
    String message = String.format("🎉 새로운 선생님 회원가입 🎉\n" +
        "이름: %s\n" +
        "이메일: %s\n" +
        "%s" +
        "%s" +
        "%s",
        userName,
        userEntity.getEmail(),
        userEntity.getGenderType() != null ? 
            String.format("성별: %s\n", userEntity.getGenderType()) : "",
        userEntity.getBirthDate() != null ? 
            String.format("생년월일: %s\n", userEntity.getBirthDate()) : "",
        !countryName.isEmpty() ? String.format("국가: %s", countryName) : ""
    );
    discordNotificationProvider.sendDiscordNotification(message);
    
    log.info("signUp 완료 - userId: {}, email: {}", userEntity.getId(), dto.email());
  }

  public TokenVO signIn(SignInDto dto) {
    log.info("signIn 시작 - email: {}", dto.email());
    
    UserEntity user = userRepository.findByEmail(dto.email()).orElseThrow(
        () -> new AuthException(AuthExceptionCode.EMAIL_NOT_CORRECT));

    if (user.isDeleted()) {
      log.warn("삭제된 사용자 로그인 시도 - userId: {}, email: {}", user.getId(), dto.email());
      throw new AuthException(AuthExceptionCode.DELETED_USER);
    }

    if (!passwordProvider.matches(dto.password(), user.getPassword())) {
      log.warn("비밀번호 불일치 - userId: {}, email: {}", user.getId(), dto.email());
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    // 선생님이 아닌 경우 예외 처리
    if (!RoleType.TEACHER.equals(user.getRoleType())) {
      log.warn("접근 권한 없음 - userId: {}, roleType: {}", user.getId(), user.getRoleType());
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    TokenVO tokenVO = jwtProvider.generateToken(new LoginUser(user.getId(), user.getEmail(), user.getRoleType()));

    // TODO key 에 deviceId 추가?
    redisStorage.save("RefreshToken::userId=" + user.getId(),
        tokenVO.refreshToken(), tokenVO.refreshTokenExpireTime());
    
    log.info("signIn 완료 - userId: {}, email: {}", user.getId(), dto.email());
    return tokenVO;
  }

  @Transactional
  public TokenVO reIssue(RequestReIssueTokenDTO dto) {
    log.info("Reissue token request: {}", dto);
    // token 검증
    Long userId = jwtProvider.getId(dto.refreshToken());

    // redis 에 저장된 refreshToken 확인
    String refreshToken = redisStorage.get("RefreshToken::userId=" + userId);
    if (refreshToken == null) {
      throw new AuthException(AuthExceptionCode.EXPIRED_TOKEN);
    }

    // user 조회
    UserEntity user = userRepository.findById(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    // token 재발급
    TokenVO tokenVO = jwtProvider.generateToken(new LoginUser(user.getId(), user.getEmail(), user.getRoleType()));

    // TODO key 에 deviceId 추가?
    redisStorage.save("RefreshToken::userId=" + user.getId(),
        tokenVO.refreshToken(), tokenVO.refreshTokenExpireTime());
    return tokenVO;
  }

  @Transactional
  public void requestEmailVerification(RequestEmailVerificationDto dto) {
    log.info("Email verification request: {}", dto);
    // 이미 가입한 이메일인 경우 예외 처리
    if (userRepository.existsByEmail(dto.email())) {
      throw new UserException(UserExceptionCode.ALREADY_USED_EMAIL);
    }

    // 3회 이상 요청한 경우 예외 처리
    int numberOfEmailVerification = emailVerificationCodeRepository
        .countByEmailAndExpireDateTimeAfter(dto.email(), LocalDateTime.now());
    if (numberOfEmailVerification >= EMAIL_MAX_TRY_COUNT) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.TOO_MANY_REQUEST);
    }

    // verification code 생성 & 저장
    EmailVerificationCodeEntity emailVerificationCodeEntity = new EmailVerificationCodeEntity(
        null,
        dto.email(),
        UuidProvider.generateCode(),
        LocalDateTime.now().plusMinutes(EMAIL_EXPIRATION_MINUTE),
        false,
        EmailVerificationCodeType.SIGN_UP
    );
    emailVerificationCodeRepository.save(emailVerificationCodeEntity);

    // 이메일 템플릿 조회 & 파싱 & 발송
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "EMAIL_VERIFICATION_SIGN_UP", MessageTemplateType.EMAIL).orElse(null);

    StringSubstitutor sub = new StringSubstitutor(Map.of("code", emailVerificationCodeEntity.getCode()));
    String title = sub.replace(emailTemplate.getTitle());
    String content = sub.replace(emailTemplate.getContent());

    emailProvider.send(dto.email(), title, content);
  }

  @Transactional
  public void verifyCode(VerifyEmailDto dto) {
    log.info("Email verification code request: {}", dto);
    EmailVerificationCodeEntity emailVerificationCodeEntity = emailVerificationCodeRepository
        .findByCodeAndEmailVerificationCodeTypeAndExpireDateTimeAfter(dto.code(),
            EmailVerificationCodeType.SIGN_UP, LocalDateTime.now())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.EMAIL_VERIFICATION_CODE_NOT_FOUND)
        );

    if (emailVerificationCodeEntity.isVerified()) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.ALREADY_VERIFIED_CODE);
    }

    if (emailVerificationCodeEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.EXPIRED_CODE);
    }

    emailVerificationCodeEntity.setVerified(true);
    emailVerificationCodeRepository.save(emailVerificationCodeEntity);
  }

  @Transactional
  public void requestResetPassword(RequestResetPasswordDto dto) {
    log.info("Reset password request: {}", dto);
    UserEntity user = userRepository.findByEmailAndDeletedIsFalse(dto.email()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    // 학원 회원이 아닌 경우 예외 처리
    if (!RoleType.TEACHER.equals(user.getRoleType())) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND);
    }

    // 3회 이상 요청한 경우 예외 처리
    int numberOfEmailVerification = emailVerificationCodeRepository
        .countByEmailAndExpireDateTimeAfter(dto.email(), LocalDateTime.now());
    if (numberOfEmailVerification >= EMAIL_MAX_TRY_COUNT) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.TOO_MANY_REQUEST);
    }

    // verification code 생성 & 저장
    EmailVerificationCodeEntity emailVerificationCodeEntity = new EmailVerificationCodeEntity(
        null,
        dto.email(),
        UuidProvider.generateCode(),
        LocalDateTime.now().plusMinutes(EMAIL_EXPIRATION_MINUTE),
        false,
        EmailVerificationCodeType.RESET_PASSWORD
    );
    emailVerificationCodeRepository.save(emailVerificationCodeEntity);

    // 이메일 템플릿 조회 & 파싱 & 발송
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "EMAIL_VERIFICATION_RESET_PASSWORD", MessageTemplateType.EMAIL).orElse(null);
    StringSubstitutor sub = new StringSubstitutor(Map.of("link", FRONT_URL + "/password/reset?code=" + emailVerificationCodeEntity.getCode()));
    String title = sub.replace(emailTemplate.getTitle());
    String content = sub.replace(emailTemplate.getContent());

    emailProvider.send(dto.email(), title, content);
  }

  public void validateResetPasswordCode(String code) {
    EmailVerificationCodeEntity emailVerificationCodeEntity = emailVerificationCodeRepository
        .findByCodeAndEmailVerificationCodeTypeAndExpireDateTimeAfter(code,
            EmailVerificationCodeType.RESET_PASSWORD, LocalDateTime.now())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.EMAIL_VERIFICATION_CODE_NOT_FOUND)
        );

    if (emailVerificationCodeEntity.isVerified()) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.ALREADY_VERIFIED_CODE);
    }

    if (emailVerificationCodeEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.EXPIRED_CODE);
    }
  }

  @Transactional
  public void resetPassword(ResetPasswordDto dto) {
    log.info("resetPassword 시작 - code: {}", dto.code());
    
    EmailVerificationCodeEntity emailVerificationCodeEntity = emailVerificationCodeRepository
        .findByCodeAndEmailVerificationCodeTypeAndExpireDateTimeAfter(dto.code(),
            EmailVerificationCodeType.RESET_PASSWORD, LocalDateTime.now())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.EMAIL_VERIFICATION_CODE_NOT_FOUND));

    if (emailVerificationCodeEntity.isVerified()) {
      log.warn("이미 검증된 코드 - code: {}", dto.code());
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.ALREADY_VERIFIED_CODE);
    }

    if (emailVerificationCodeEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
      log.warn("만료된 인증 코드 - code: {}", dto.code());
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.EXPIRED_CODE);
    }

    emailVerificationCodeEntity.setVerified(true);
    emailVerificationCodeRepository.save(emailVerificationCodeEntity);

    // 비밀번호 변경
    UserEntity userEntity = userRepository.findByEmailAndDeletedIsFalse(emailVerificationCodeEntity.getEmail()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    userEntity.setPassword(passwordProvider.encode(dto.password()));
    userRepository.save(userEntity);
    
    log.info("resetPassword 완료 - userId: {}, email: {}", userEntity.getId(), emailVerificationCodeEntity.getEmail());
  }
}
