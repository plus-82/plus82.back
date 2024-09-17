package com.etplus.repository;

import com.etplus.repository.domain.EmailVerificationCode;
import com.etplus.repository.domain.code.EmailVerificationCodeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends
    JpaRepository<EmailVerificationCode, Long> {

  Optional<EmailVerificationCode> findByEmailAndCodeAndEmailVerificationCodeType(
      String email, String code, EmailVerificationCodeType emailVerificationCodeType);

  boolean existsByEmailAndEmailVerificationCodeTypeAndVerifiedIsTrue(String email, EmailVerificationCodeType emailVerificationCodeType);

  int countByEmailAndExpireDateTimeAfter(String email, LocalDateTime nowDateTime);
}
