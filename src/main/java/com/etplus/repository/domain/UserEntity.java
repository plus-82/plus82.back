package com.etplus.repository.domain;

import com.etplus.repository.domain.code.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nickName;

  @Column(nullable = false, updatable = false)
  private String email;
  @Column(nullable = false)
  private String password;

//  @Enumerated(EnumType.STRING)
//  private OauthProviderType providerType;
//  private String providerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private RoleType roleType;

  public UserEntity(Long id, String nickName, String email, String password, RoleType roleType) {
    this.id = id;
    this.nickName = nickName;
    this.email = email;
    this.password = password;
    this.roleType = roleType;
  }
}
