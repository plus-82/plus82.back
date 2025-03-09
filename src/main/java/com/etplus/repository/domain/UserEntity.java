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
import jakarta.persistence.OneToOne;
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
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;
  private String lastName;
  private String fullName;
  @Enumerated(EnumType.STRING)
  private GenderType genderType;
  private LocalDate birthDate;

  @Column(nullable = false, updatable = false)
  private String email;
  @Column(nullable = false)
  private String password;

  private boolean allowEmail;

//  @Enumerated(EnumType.STRING)
//  private OauthProviderType providerType;
//  private String providerId;

  @Column(nullable = false)
  private boolean deleted;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private RoleType roleType;

  // academy 는 country null
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id", referencedColumnName = "id")
  private CountryEntity country;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_image_id", referencedColumnName = "id")
  private FileEntity profileImage;

  public UserEntity(Long id, String firstName, String lastName, String fullName,
      GenderType genderType, LocalDate birthDate, String email, String password, boolean allowEmail,
      RoleType roleType, CountryEntity country, FileEntity profileImage) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.fullName = fullName;
    this.genderType = genderType;
    this.birthDate = birthDate;
    this.email = email;
    this.password = password;
    this.allowEmail = allowEmail;
    this.roleType = roleType;
    this.country = country;
    this.profileImage = profileImage;
  }
}
