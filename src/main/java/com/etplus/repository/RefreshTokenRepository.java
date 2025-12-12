package com.etplus.repository;

import com.etplus.repository.domain.RefreshTokenEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

  Optional<RefreshTokenEntity> findByUserIdAndExpireDateTimeAfter(Long userId, LocalDateTime now);

  void deleteByUserId(Long userId);
}

