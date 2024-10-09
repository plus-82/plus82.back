package com.etplus.service;

import com.etplus.controller.dto.UpdatePasswordDto;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.PasswordProvider;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordProvider passwordProvider;

  @Transactional
  public void deleteUser(long userId) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setDeleted(true);
    userRepository.save(user);
  }

  @Transactional
  public void updatePassword(long userId, UpdatePasswordDto dto) {
    UserEntity userEntity = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    if (!passwordProvider.matches(dto.currentPassword(), userEntity.getPassword())) {
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    userEntity.setPassword(passwordProvider.encode(dto.newPassword()));
    userRepository.save(userEntity);
  }

}
