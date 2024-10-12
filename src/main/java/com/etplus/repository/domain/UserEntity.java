package com.etplus.repository.domain;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String country;   // TODO enum 클래스나 테이블로 변경
  private GenderType genderType;
  private LocalDate birthDate;

  @Column(nullable = false, updatable = false)
  private String email;
  @Column(nullable = false)
  private String password;

//  @Enumerated(EnumType.STRING)
//  private OauthProviderType providerType;
//  private String providerId;

  @Column(nullable = false)
  private boolean deleted;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private RoleType roleType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academy_id", referencedColumnName = "id", nullable = true, updatable = false)
  private AcademyEntity academy;

  public UserEntity(Long id, String name, String country, GenderType genderType,
      LocalDate birthDate, String email, String password, RoleType roleType,
      AcademyEntity academy) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.genderType = genderType;
    this.birthDate = birthDate;
    this.email = email;
    this.password = password;
    this.roleType = roleType;
    this.academy = academy;
  }
}
