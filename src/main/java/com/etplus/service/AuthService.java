package com.etplus.service;

import com.etplus.controller.dto.VerifyEmailDto;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.EmailRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.EmailEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.util.UuidProvider;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final EmailRepository emailRepository;
  private final PasswordProvider passwordProvider;
  private final EmailProvider emailProvider;

  @Transactional
  public void signUp(SignUpDto dto) {
    // check email exists
    boolean existsByEmail = userRepository.existsByEmail(dto.email());

    if (existsByEmail) {
      throw new IllegalArgumentException("Email already exists");
    }

    // create user
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

    // create email
    EmailEntity emailEntity = new EmailEntity(null, UuidProvider.generateUuid(), false,
        LocalDate.now().plusDays(7), null, userEntity);
    emailRepository.save(emailEntity);

    // send email
    emailProvider.send(dto.email(), "[Plus82] Verify your email",
        "visit here: https://plus82.co/verify-email?code=" + emailEntity.getCode());
  }

  @Transactional
  public void verifyEmail(VerifyEmailDto dto) {
    EmailEntity emailEntity = emailRepository.findByCode(dto.code())
        .orElseThrow(() -> new IllegalArgumentException("Email not found"));

    if (emailEntity.isCompleted()) {
      throw new IllegalArgumentException("Already verified");
    }

    if (emailEntity.getExpiredDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Expired code");
    }

    emailEntity.setCompleted(true);
    emailRepository.save(emailEntity);

    UserEntity userEntity = emailEntity.getToUser();
    userEntity.setVerified(true);
    userRepository.save(userEntity);
  }

}
