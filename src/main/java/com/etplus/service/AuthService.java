package com.etplus.service;

import com.etplus.cache.RedisStorage;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.RequestReIssueTokenDTO;
import com.etplus.controller.dto.RequestResetPasswordDto;
import com.etplus.controller.dto.ResetPasswordDto;
import com.etplus.controller.dto.SignInDto;
import com.etplus.controller.dto.SignUpAcademyDto;
import com.etplus.controller.dto.VerifyEmailDto;
import com.etplus.exception.AcademyException;
import com.etplus.exception.AcademyException.AcademyExceptionCode;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.EmailVerificationCodeException;
import com.etplus.exception.EmailVerificationCodeException.EmailVerificationCodeExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.UserException;
import com.etplus.exception.UserException.UserExceptionCode;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.JwtProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.EmailVerificationCodeRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
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
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

  @Value("${email.expiration-minute}")
  private Integer EMAIL_EXPIRATION_MINUTE;
  @Value("${email.max-try-count}")
  private Integer EMAIL_MAX_TRY_COUNT;

  private final UserRepository userRepository;
  private final AcademyRepository academyRepository;
  private final CountryRepository countryRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final EmailVerificationCodeRepository emailVerificationCodeRepository;
  private final PasswordProvider passwordProvider;
  private final EmailProvider emailProvider;
  private final JwtProvider jwtProvider;
  private final RedisStorage redisStorage;

  @Transactional
  public void signUp(SignUpDto dto) {
    // 이미 가입한 이메일인 경우 예외 처리
    if (userRepository.existsByEmail(dto.email())) {
      throw new UserException(UserExceptionCode.ALREADY_USED_EMAIL);
    }

    // 인증된 이메일이 있는지 확인 후 예외 처리
    boolean isEmailVerified = emailVerificationCodeRepository
        .existsByEmailAndEmailVerificationCodeTypeAndVerifiedIsTrue(dto.email(), EmailVerificationCodeType.SIGN_UP);
    if (!isEmailVerified) {
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
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        RoleType.TEACHER,
        null,
        country,
        null
    );
    userRepository.save(userEntity);
  }

  @Transactional
  public void signUpAcademy(SignUpAcademyDto dto) {
    // 이미 가입한 이메일인 경우 예외 처리
    if (userRepository.existsByEmail(dto.email())) {
      throw new UserException(UserExceptionCode.ALREADY_USED_EMAIL);
    }

    // 이미 등록된 사업자 등록번호인 경우 예외 처리
    if (academyRepository.existsByBusinessRegistrationNumber(dto.businessRegistrationNumber())) {
      throw new AcademyException(AcademyExceptionCode.ALREADY_USED_BUSINESS_REGISTRATION_NUMBER);
    }

    // 인증된 이메일이 있는지 확인 후 예외 처리
    boolean isEmailVerified = emailVerificationCodeRepository
        .existsByEmailAndEmailVerificationCodeTypeAndVerifiedIsTrue(dto.email(), EmailVerificationCodeType.SIGN_UP);
    if (!isEmailVerified) {
      throw new UserException(UserExceptionCode.NOT_VERIFIED_EMAIL);
    }

    // 학원 저장
    AcademyEntity academy = academyRepository.save(
        new AcademyEntity(
            null,
            dto.academyName(),
            dto.representativeName(),
            null,
            dto.businessRegistrationNumber(),
            dto.locationType(),
             dto.detailedAddress(),
             dto.lat(),
             dto.lng(),
             false, false, false, false, false, null
        ));

    // 사용자 저장
    UserEntity userEntity = new UserEntity(
        null,
        null,
        null,
        dto.fullName(),
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        RoleType.ACADEMY,
        academy,
        null,
        null
    );
    userRepository.save(userEntity);
  }

  public TokenVO signIn(SignInDto dto) {
    UserEntity user = userRepository.findByEmail(dto.email()).orElseThrow(
        () -> new AuthException(AuthExceptionCode.EMAIL_NOT_CORRECT));

    if (user.isDeleted()) {
      throw new AuthException(AuthExceptionCode.DELETED_USER);
    }

    if (!passwordProvider.matches(dto.password(), user.getPassword())) {
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    TokenVO tokenVO = jwtProvider.generateToken(new LoginUser(user.getId(), user.getEmail(), user.getRoleType()));

    // TODO key 에 deviceId 추가?
    redisStorage.save("RefreshToken::userId=" + user.getId(),
        tokenVO.refreshToken(), tokenVO.refreshTokenExpireTime());
    return tokenVO;
  }

  @Transactional
  public TokenVO reIssue(RequestReIssueTokenDTO dto) {
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
    userRepository.findByEmailAndDeletedIsFalse(dto.email()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

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
    StringSubstitutor sub = new StringSubstitutor(Map.of("link", "https://plus82.co/password/reset?code=" + emailVerificationCodeEntity.getCode()));
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
    EmailVerificationCodeEntity emailVerificationCodeEntity = emailVerificationCodeRepository
        .findByCodeAndEmailVerificationCodeTypeAndExpireDateTimeAfter(dto.code(),
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

    emailVerificationCodeEntity.setVerified(true);
    emailVerificationCodeRepository.save(emailVerificationCodeEntity);

    // 비밀번호 변경
    UserEntity userEntity = userRepository.findByEmailAndDeletedIsFalse(emailVerificationCodeEntity.getEmail()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    userEntity.setPassword(passwordProvider.encode(dto.password()));
    userRepository.save(userEntity);
  }
}
