package com.etplus.repository.domain;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "resume_contact")
public class ResumeContactEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "VARCHAR(1000)")
  private String interestReason;    // ?님께 관심이 생긴 이유
  @Column(columnDefinition = "VARCHAR(1000)")
  private String appealMessage;     // ?님이 우리에게 관심을 가질만한 이유
  @Column(columnDefinition = "VARCHAR(1000)")
  private String additionalMessage; // 추가로 하고 싶은 말
  @Column(nullable = false)
  private String contactEmail;      // 연락 이메일

  // 지원 당시 resume 데이터
  private long resumeId;
  private String resumeTitle;
  private String personalIntroduction;
  private String firstName;
  private String lastName;
  private String email;
  private String degree;
  private String major;
  private GenderType genderType;
  private LocalDate birthDate;
  private Boolean hasVisa;
  private VisaType visaType;
  private Boolean forKindergarten;
  private Boolean forElementary;
  private Boolean forMiddleSchool;
  private Boolean forHighSchool;
  private Boolean forAdult;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id", referencedColumnName = "id")
  private CountryEntity country;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "residence_country_id", referencedColumnName = "id")
  private CountryEntity residenceCountry;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacher_user_id", referencedColumnName = "id", nullable = false)
  private UserEntity teacher;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academy_user_id", referencedColumnName = "id", nullable = false)
  private UserEntity academyUser;

  @Builder
  public ResumeContactEntity(Long id, String interestReason, String appealMessage,
      String additionalMessage, String contactEmail, long resumeId, String resumeTitle,
      String personalIntroduction, String firstName, String lastName, String email, String degree,
      String major, GenderType genderType, LocalDate birthDate, Boolean hasVisa, VisaType visaType,
      Boolean forKindergarten, Boolean forElementary, Boolean forMiddleSchool, Boolean forHighSchool,
      Boolean forAdult,
      CountryEntity country, CountryEntity residenceCountry, UserEntity teacher, UserEntity academyUser) {
    this.id = id;
    this.interestReason = interestReason;
    this.appealMessage = appealMessage;
    this.additionalMessage = additionalMessage;
    this.contactEmail = contactEmail;
    this.resumeId = resumeId;
    this.resumeTitle = resumeTitle;
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
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
    this.country = country;
    this.residenceCountry = residenceCountry;
    this.teacher = teacher;
    this.academyUser = academyUser;
  }

  public static ResumeContactEntity create(ResumeEntity resume, UserEntity academyUser,
      String interestReason, String appealMessage, String additionalMessage, String contactEmail) {
    return ResumeContactEntity.builder()
        .interestReason(interestReason)
        .appealMessage(appealMessage)
        .additionalMessage(additionalMessage)
        .contactEmail(contactEmail)

        .resumeId(resume.getId())
        .resumeTitle(resume.getTitle())
        .personalIntroduction(resume.getPersonalIntroduction())
        .firstName(resume.getFirstName())
        .lastName(resume.getLastName())
        .email(resume.getEmail())
        .degree(resume.getDegree())
        .major(resume.getMajor())
        .genderType(resume.getGenderType())
        .birthDate(resume.getBirthDate())
        .hasVisa(resume.getHasVisa())
        .visaType(resume.getVisaType())
        .forKindergarten(resume.getForKindergarten())
        .forElementary(resume.getForElementary())
        .forMiddleSchool(resume.getForMiddleSchool())
        .forHighSchool(resume.getForHighSchool())
        .forAdult(resume.getForAdult())
        
        .country(resume.getCountry())
        .residenceCountry(resume.getResidenceCountry())
        .teacher(resume.getUser())
        .academyUser(academyUser)
        .build();
  }
}
