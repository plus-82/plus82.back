package com.etplus.repository.domain;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
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
@Getter
@Setter
@Entity
@Table(name = "resume")
public class ResumeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  @Column(columnDefinition = "TEXT")
  private String personalIntroduction;

  private String firstName;
  private String lastName;
  private String email;
  private String degree;
  private String major;
  @Enumerated(EnumType.STRING)
  private GenderType genderType;
  private LocalDate birthDate;

  private Boolean hasVisa;
  private VisaType visaType;

  private Boolean isRepresentative; // 대표 이력서인지? (유저 당 1개)

  // 대상
  private Boolean forKindergarten;
  private Boolean forElementary;
  private Boolean forMiddleSchool;
  private Boolean forHighSchool;
  private Boolean forAdult;

  // 이력서 작성 상태
  @Column(nullable = false)
  private boolean isDraft;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id", referencedColumnName = "id")
  private CountryEntity country;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "residence_country_id", referencedColumnName = "id")
  private CountryEntity residenceCountry;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_image_id", referencedColumnName = "id")
  private FileEntity profileImage;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", referencedColumnName = "id")
  private FileEntity file;

  public ResumeEntity(Long id, String title, String personalIntroduction, String firstName,
      String lastName, String email, String degree, String major, GenderType genderType,
      LocalDate birthDate, Boolean hasVisa, VisaType visaType, Boolean isRepresentative,
      Boolean forKindergarten, Boolean forElementary, Boolean forMiddleSchool,
      Boolean forHighSchool,
      Boolean forAdult, boolean isDraft, CountryEntity country, CountryEntity residenceCountry, UserEntity user,
      FileEntity profileImage, FileEntity file) {
    this.id = id;
    this.title = title;
    this.personalIntroduction = personalIntroduction;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.degree = degree;
    this.major = major;
    this.genderType = genderType;
    this.birthDate = birthDate;
    this.hasVisa = hasVisa;
    this.visaType = visaType;
    this.isRepresentative = isRepresentative;
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
    this.isDraft = isDraft;
    this.country = country;
    this.residenceCountry = residenceCountry;
    this.user = user;
    this.profileImage = profileImage;
    this.file = file;
  }

  public ResumeEntity(String title, UserEntity user, FileEntity file) {
    this.user = user;
    this.file = file;
  }
}
