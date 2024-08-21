package com.etplus.repository;

import com.etplus.repository.domain.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

}
