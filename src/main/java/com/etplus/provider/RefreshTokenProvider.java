package com.etplus.provider;

import com.etplus.repository.RefreshTokenRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.RefreshTokenEntity;
import com.etplus.repository.domain.UserEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class RefreshTokenProvider {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public String get(Long userId) {
    RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
        .findByUserIdAndExpireDateTimeAfter(userId, LocalDateTime.now())
        .orElse(null);

    if (refreshTokenEntity == null) {
      return null;
    }

    // 만료 시간 체크
    if (refreshTokenEntity.getExpireDateTime().isBefore(LocalDateTime.now())) {
      return null;
    }

    return refreshTokenEntity.getRefreshToken();
  }

  @Transactional
  public void save(Long userId, String refreshToken, long expireTimeMillis) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // 만료 시간 계산 (밀리초를 LocalDateTime으로 변환)
    LocalDateTime expireDateTime = LocalDateTime.now().plusNanos(expireTimeMillis * 1_000_000);

    // 기존 토큰이 있으면 삭제
    refreshTokenRepository.deleteByUserId(userId);

    // 새 토큰 저장
    RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(
        null,
        user,
        refreshToken,
        expireDateTime
    );
    refreshTokenRepository.save(refreshTokenEntity);
  }

  @Transactional
  public void delete(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }
}

