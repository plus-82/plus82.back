package com.etplus.service;

import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.VerifyEmailDto;
import com.etplus.exception.EmailVerificationCodeException;
import com.etplus.exception.EmailVerificationCodeException.EmailVerificationCodeExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.UserException;
import com.etplus.exception.UserException.UserExceptionCode;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.EmailVerificationCodeRepository;
import com.etplus.repository.UserRepository;
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
    boolean isEmailVerified = emailVerificationCodeRepository.existsByEmailAndVerifiedIsTrue(dto.email());
    if (!isEmailVerified) {
      throw new UserException(UserExceptionCode.NOT_VERIFIED_EMAIL);
    }

    UserEntity userEntity = new UserEntity(
        null,
        dto.name(),
        dto.country(),
        dto.genderType(),
        dto.birthDate(),
        dto.backupEmail(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        RoleType.TEACHER
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
        .findByEmailAndCode(dto.email(), dto.code())
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

}
