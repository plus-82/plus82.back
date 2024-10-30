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

  private String firstName;
  private String lastName;
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
  @JoinColumn(name = "academy_id", referencedColumnName = "id", updatable = false)
  private AcademyEntity academy;

  // academy 는 country null
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id", referencedColumnName = "id")
  private CountryEntity country;

  public UserEntity(Long id, String firstName, String lastName, GenderType genderType,
      LocalDate birthDate, String email, String password, RoleType roleType,
      AcademyEntity academy, CountryEntity country) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.genderType = genderType;
    this.birthDate = birthDate;
    this.email = email;
    this.password = password;
    this.roleType = roleType;
    this.academy = academy;
    this.country = country;
  }
}
