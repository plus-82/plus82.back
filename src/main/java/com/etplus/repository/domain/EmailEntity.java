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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Entity
@Table(name = "email")
public class EmailEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // type
  @Column(nullable = false, unique = true)
  private String code;
  @Column(nullable = false)
  private boolean completed;
  @Column(nullable = false)
  private LocalDate expiredDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user_id", referencedColumnName = "id", updatable = false)
  private UserEntity fromUser;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_user_id", referencedColumnName = "id", nullable = false, updatable = false)
  private UserEntity toUser;

  public EmailEntity(Long id, String code, boolean completed, LocalDate expiredDate,
      UserEntity fromUser, UserEntity toUser) {
    this.id = id;
    this.code = code;
    this.completed = completed;
    this.expiredDate = expiredDate;
    this.fromUser = fromUser;
    this.toUser = toUser;
  }
}
