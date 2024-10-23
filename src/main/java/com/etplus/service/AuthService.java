package com.etplus.service;

import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.RequestResetPasswordDto;
import com.etplus.controller.dto.ResetPasswordDto;
import com.etplus.controller.dto.SignUpAcademyDto;
import com.etplus.controller.dto.VerifyEmailDto;
import com.etplus.exception.AcademyException;
import com.etplus.exception.AcademyException.AcademyExceptionCode;
import com.etplus.exception.EmailVerificationCodeException;
import com.etplus.exception.EmailVerificationCodeException.EmailVerificationCodeExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.UserException;
import com.etplus.exception.UserException.UserExceptionCode;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.EmailVerificationCodeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.CountryEntity;
import com.etplus.repository.domain.EmailVerificationCode;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.EmailVerificationCodeType;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.util.UuidProvider;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final AcademyRepository academyRepository;
  private final CountryRepository countryRepository;
  private final EmailVerificationCodeRepository emailVerificationCodeRepository;
  private final PasswordProvider passwordProvider;
  private final EmailProvider emailProvider;

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
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        RoleType.TEACHER,
        null,
        country
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
            null,
            dto.businessRegistrationNumber(),
            null, null, false, false, false, false, false
        ));

    // 사용자 저장
    UserEntity userEntity = new UserEntity(
        null,
        dto.firstName(),
        dto.lastName(),
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        RoleType.TEACHER,
        academy,
        null
    );
    userRepository.save(userEntity);
  }

  @Transactional
  public void requestVerification(RequestEmailVerificationDto dto) {
    // 이미 가입한 이메일인 경우 예외 처리
    if (userRepository.existsByEmail(dto.email())) {
      throw new UserException(UserExceptionCode.ALREADY_USED_EMAIL);
    }

    // 3회 이상 요청한 경우 예외 처리
    int numberOfEmailVerification = emailVerificationCodeRepository
        .countByEmailAndExpireDateTimeAfter(dto.email(), LocalDateTime.now());
    if (numberOfEmailVerification > 3) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.TOO_MANY_REQUEST);
    }

    EmailVerificationCode emailVerificationCode = new EmailVerificationCode(
        null,
        dto.email(),
        UuidProvider.generateCode(),
        LocalDateTime.now().plusMinutes(10),
        false,
        EmailVerificationCodeType.SIGN_UP
    );
    emailVerificationCodeRepository.save(emailVerificationCode);

    emailProvider.send(dto.email(), "[Plus82] Verify your email",
        "input this code: " + emailVerificationCode.getCode());
  }

  @Transactional
  public void verifyCode(VerifyEmailDto dto) {
    EmailVerificationCode emailVerificationCode = emailVerificationCodeRepository
        .findByEmailAndCodeAndEmailVerificationCodeType(dto.email(), dto.code(), EmailVerificationCodeType.SIGN_UP)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.EMAIL_VERIFICATION_CODE_NOT_FOUND)
        );

    if (emailVerificationCode.isVerified()) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.ALREADY_VERIFIED_CODE);
    }

    if (emailVerificationCode.getExpireDateTime().isBefore(LocalDateTime.now())) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.EXPIRED_CODE);
    }

    emailVerificationCode.setVerified(true);
    emailVerificationCodeRepository.save(emailVerificationCode);
  }

  @Transactional
  public void requestResetPassword(RequestResetPasswordDto dto) {
    userRepository.findByEmailAndDeletedIsFalse(dto.email()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    // 3회 이상 요청한 경우 예외 처리
    int numberOfEmailVerification = emailVerificationCodeRepository
        .countByEmailAndExpireDateTimeAfter(dto.email(), LocalDateTime.now());
    if (numberOfEmailVerification > 3) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.TOO_MANY_REQUEST);
    }

    EmailVerificationCode emailVerificationCode = new EmailVerificationCode(
        null,
        dto.email(),
        UuidProvider.generateCode(),
        LocalDateTime.now().plusMinutes(10),
        false,
        EmailVerificationCodeType.RESET_PASSWORD
    );
    emailVerificationCodeRepository.save(emailVerificationCode);

    emailProvider.send(dto.email(), "[Plus82] Reset your password",
        "visit here: https://plus82.co/reset-password?code=" + emailVerificationCode.getCode());
  }

  @Transactional
  public void resetPassword(ResetPasswordDto dto) {
    EmailVerificationCode emailVerificationCode = emailVerificationCodeRepository
        .findByEmailAndCodeAndEmailVerificationCodeType(dto.email(), dto.code(), EmailVerificationCodeType.RESET_PASSWORD)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.EMAIL_VERIFICATION_CODE_NOT_FOUND)
        );

    if (emailVerificationCode.isVerified()) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.ALREADY_VERIFIED_CODE);
    }

    if (emailVerificationCode.getExpireDateTime().isBefore(LocalDateTime.now())) {
      throw new EmailVerificationCodeException(EmailVerificationCodeExceptionCode.EXPIRED_CODE);
    }

    emailVerificationCode.setVerified(true);
    emailVerificationCodeRepository.save(emailVerificationCode);

    // 비밀번호 변경
    UserEntity userEntity = userRepository.findByEmailAndDeletedIsFalse(dto.email()).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    userEntity.setPassword(passwordProvider.encode(dto.password()));
    userRepository.save(userEntity);
  }
}
