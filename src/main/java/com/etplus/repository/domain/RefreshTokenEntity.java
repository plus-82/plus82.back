package com.etplus.repository.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "refresh_token")
public class RefreshTokenEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private String refreshToken;

  @Column(nullable = false)
  private LocalDateTime expireDateTime;

  public RefreshTokenEntity(Long id, UserEntity user, String refreshToken, LocalDateTime expireDateTime) {
    this.id = id;
    this.user = user;
    this.refreshToken = refreshToken;
    this.expireDateTime = expireDateTime;
  }
}

