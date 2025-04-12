package com.etplus.service;

import com.etplus.cache.RedisStorage;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.SignInDto;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.provider.JwtProvider;
import com.etplus.provider.PasswordProvider;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.TokenVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminAuthService {

  private final UserRepository userRepository;
  private final PasswordProvider passwordProvider;
  private final JwtProvider jwtProvider;
  private final RedisStorage redisStorage;

  public TokenVO signIn(SignInDto dto) {
    UserEntity user = userRepository.findByEmail(dto.email()).orElseThrow(
        () -> new AuthException(AuthExceptionCode.EMAIL_NOT_CORRECT));

    if (user.isDeleted()) {
      throw new AuthException(AuthExceptionCode.DELETED_USER);
    }

    if (!passwordProvider.matches(dto.password(), user.getPassword())) {
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    // 선생님이 아닌 경우 예외 처리
    if (!RoleType.ADMIN.equals(user.getRoleType())) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    TokenVO tokenVO = jwtProvider.generateToken(new LoginUser(user.getId(), user.getEmail(), user.getRoleType()));

    // TODO key 에 deviceId 추가?
    redisStorage.save("RefreshToken::userId=" + user.getId(),
        tokenVO.refreshToken(), tokenVO.refreshTokenExpireTime());
    return tokenVO;
  }
}
