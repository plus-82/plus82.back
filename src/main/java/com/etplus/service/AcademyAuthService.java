package com.etplus.service;

import com.etplus.cache.RedisStorage;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.RequestResetPasswordDto;
import com.etplus.controller.dto.SignInDto;
import com.etplus.controller.dto.SignUpAcademyDto;
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
import com.etplus.provider.DiscordNotificationProvider;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.JwtProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.EmailVerificationCodeRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
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
public class AcademyAuthService {

  @Value("${email.expiration-minute}")
  private Integer EMAIL_EXPIRATION_MINUTE;
  @Value("${email.max-try-count}")
  private Integer EMAIL_MAX_TRY_COUNT;
  @Value("${url.front}")
  private String FRONT_URL;

  private final UserRepository userRepository;
  private final AcademyRepository academyRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final EmailVerificationCodeRepository emailVerificationCodeRepository;
  private final PasswordProvider passwordProvider;
  private final EmailProvider emailProvider;
  private final JwtProvider jwtProvider;
  private final RedisStorage redisStorage;
  private final DiscordNotificationProvider discordNotificationProvider;

  @Transactional
  public void signUpAcademy(SignUpAcademyDto dto) {
    log.info("Academy sign up request: {}", dto);
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

    // 사용자 저장
    UserEntity userEntity = userRepository.save(new UserEntity(
        null,
        null,
        null,
        dto.fullName(),
        dto.fullName(),
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        true,
        RoleType.ACADEMY,
        null,
        null
    ));

    // 학원 저장
    academyRepository.save(
        new AcademyEntity(
            null,
            dto.academyName(),
            dto.academyNameEn(),
            dto.representativeName(),
            dto.email(),
            null,
            dto.businessRegistrationNumber(),
            dto.locationType(),
            dto.address(),
            dto.detailedAddress(),
            dto.lat(),
            dto.lng(),
            false, false, false, false, false,
            null,
            false,
            userEntity,
            null
        ));

    // Discord 알림 전송
    String message = String.format("🎉 새로운 학원 회원가입 🎉\n" +
        "학원명: %s\n" +
        "이름: %s\n" +
        "대표자명: %s\n" +
        "이메일: %s\n" +
        "사업자등록번호: %s\n" +
        "%s",
        dto.academyName(),
        dto.fullName(),
        dto.representativeName(),
        dto.email(),
        dto.businessRegistrationNumber(),
        dto.address() != null ? String.format("주소: %s\n", dto.address()) : ""
    );
    discordNotificationProvider.sendDiscordNotification(message);
  }

  public TokenVO signInAcademy(SignInDto dto) {
    UserEntity user = userRepository.findByEmail(dto.email()).orElseThrow(
        () -> new AuthException(AuthExceptionCode.EMAIL_NOT_CORRECT));

    if (user.isDeleted()) {
      throw new AuthException(AuthExceptionCode.DELETED_USER);
    }

    if (!passwordProvider.matches(dto.password(), user.getPassword())) {
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    // 학원 회원이 아닌 경우 예외 처리
    if (!RoleType.ACADEMY.equals(user.getRoleType())) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    TokenVO tokenVO = jwtProvider.generateToken(new LoginUser(user.getId(), user.getEmail(), user.getRoleType()));

    // TODO key 에 deviceId 추가?
    redisStorage.save("RefreshToken::userId=" + user.getId(),
        tokenVO.refreshToken(), tokenVO.refreshTokenExpireTime());
    return tokenVO;
  }

  @Transactional
  public void requestEmailVerification(RequestEmailVerificationDto dto) {
    log.info("Academy email verification request: {}", dto);
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
        "ACADEMY_EMAIL_VERIFICATION_SIGN_UP", MessageTemplateType.EMAIL).orElse(null);

    StringSubstitutor sub = new StringSubstitutor(Map.of("code", emailVerificationCodeEntity.getCode()));
    String title = sub.replace(emailTemplate.getTitle());
    String content = sub.replace(emailTemplate.getContent());

    emailProvider.send(dto.email(), title, content);
  }

  @Transactional
  public void requestResetPassword(RequestResetPasswordDto dto) {
    log.info("Academy reset password request: {}", dto);
    UserEntity user = userRepository.findByEmailAndDeletedIsFalse(dto.email()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    // 학원 회원이 아닌 경우 예외 처리
    if (!RoleType.ACADEMY.equals(user.getRoleType())) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
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
        "ACADEMY_EMAIL_VERIFICATION_RESET_PASSWORD", MessageTemplateType.EMAIL).orElse(null);
    StringSubstitutor sub = new StringSubstitutor(Map.of("link", FRONT_URL + "/business/password/reset?code=" + emailVerificationCodeEntity.getCode()));
    String title = sub.replace(emailTemplate.getTitle());
    String content = sub.replace(emailTemplate.getContent());

    emailProvider.send(dto.email(), title, content);
  }

}
