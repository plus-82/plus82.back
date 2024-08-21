package com.etplus.repository;

import com.etplus.repository.domain.EmailVerificationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

  Optional<EmailVerificationCode> findByEmailAndCode(String email, String code);
}
