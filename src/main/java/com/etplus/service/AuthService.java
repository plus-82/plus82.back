package com.etplus.service;

import com.etplus.provider.PasswordProvider;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordProvider passwordProvider;

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

    // TODO send email
  }

}
