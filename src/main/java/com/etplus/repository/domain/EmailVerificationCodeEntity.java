package com.etplus.repository.domain;

import com.etplus.repository.domain.code.EmailVerificationCodeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "email_verification_code")
public class EmailVerificationCodeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private String email;
  @Column(nullable = false, updatable = false)
  private String code;
  @Column(nullable = false)
  private LocalDateTime expireDateTime;
  private boolean verified;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private EmailVerificationCodeType emailVerificationCodeType;

  public EmailVerificationCodeEntity(Long id, String email, String code, LocalDateTime expireDateTime,
      boolean verified, EmailVerificationCodeType emailVerificationCodeType) {
    this.id = id;
    this.email = email;
    this.code = code;
    this.expireDateTime = expireDateTime;
    this.verified = verified;
    this.emailVerificationCodeType = emailVerificationCodeType;
  }
}
