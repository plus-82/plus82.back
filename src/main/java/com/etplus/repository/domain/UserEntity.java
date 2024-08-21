package com.etplus.repository.domain;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
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

  private String name;
  private String country;
  private GenderType genderType;
  private LocalDate birthDate;
  private String backupEmail;

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

  public UserEntity(Long id, String name, String country, GenderType genderType,
      LocalDate birthDate, String backupEmail, String email, String password, RoleType roleType) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.genderType = genderType;
    this.birthDate = birthDate;
    this.backupEmail = backupEmail;
    this.email = email;
    this.password = password;
    this.roleType = roleType;
  }
}
