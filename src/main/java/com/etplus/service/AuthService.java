package com.etplus.service;

import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.EmailVerificationCodeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.EmailVerificationCode;
import com.etplus.repository.domain.UserEntity;
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
    boolean existsByEmail = userRepository.existsByEmail(dto.email());

    if (existsByEmail) {
      throw new IllegalArgumentException("Email already exists");
    }

    UserEntity userEntity = new UserEntity(
        null,
        dto.name(),
        dto.country(),
        dto.genderType(),
        dto.birthDate(),
        dto.email(),
        passwordProvider.encode(dto.password()),
        false,
        RoleType.TEACHER
    );

    userRepository.save(userEntity);
  }

  @Transactional
  public void requestVerification(RequestEmailVerificationDto dto) {
    EmailVerificationCode emailVerificationCode = new EmailVerificationCode(
        null,
        dto.email(),
        UuidProvider.generateCode(),
        LocalDateTime.now().plusMinutes(10),
        false
    );
    emailVerificationCodeRepository.save(emailVerificationCode);

    emailProvider.send(dto.email(), "[Plus82] Verify your email",
        "input the code: " + emailVerificationCode.getCode());
  }

}
