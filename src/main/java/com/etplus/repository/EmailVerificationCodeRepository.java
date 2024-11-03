package com.etplus.repository;

import com.etplus.repository.domain.EmailVerificationCodeEntity;
import com.etplus.repository.domain.code.EmailVerificationCodeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends
    JpaRepository<EmailVerificationCodeEntity, Long> {

  Optional<EmailVerificationCodeEntity> findByCodeAndEmailVerificationCodeTypeAndExpireDateTimeAfter(
      String code, EmailVerificationCodeType emailVerificationCodeType, LocalDateTime expireDateTime);

  boolean existsByEmailAndEmailVerificationCodeTypeAndVerifiedIsTrue(String email, EmailVerificationCodeType emailVerificationCodeType);

  int countByEmailAndExpireDateTimeAfter(String email, LocalDateTime nowDateTime);
}
