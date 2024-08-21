package com.etplus.repository.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class EmailVerificationCode {

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

  public EmailVerificationCode(Long id, String email, String code,
      LocalDateTime expireDateTime, boolean verified) {
    this.id = id;
    this.email = email;
    this.code = code;
    this.expireDateTime = expireDateTime;
    this.verified = verified;
  }
}
