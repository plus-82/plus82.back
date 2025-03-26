package com.etplus.repository.domain;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "job_post_resume_relation",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_post_id", "user_id"}),
    }
)
public class JobPostResumeRelationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String coverLetter;         // 설명
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JobPostResumeRelationStatus status;
  @Column(nullable = false)
  private LocalDate submittedDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_post_id", referencedColumnName = "id", nullable = false, updatable = false)
  private JobPostEntity jobPost;

  // 지원 당시 resume 데이터
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

  // 접근 코드
  private String code;

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

  public JobPostResumeRelationEntity(Long id, String coverLetter,
      JobPostResumeRelationStatus status,
      LocalDate submittedDate, JobPostEntity jobPost, String resumeTitle,
      String personalIntroduction,
      String firstName, String lastName, String email, String degree, String major,
      GenderType genderType, LocalDate birthDate, Boolean hasVisa, VisaType visaType,
      Boolean forKindergarten, Boolean forElementary, Boolean forMiddleSchool,
      Boolean forHighSchool,
      Boolean forAdult, String code, CountryEntity country, CountryEntity residenceCountry,
      UserEntity user, FileEntity profileImage, FileEntity file) {
    this.id = id;
    this.coverLetter = coverLetter;
    this.status = status;
    this.submittedDate = submittedDate;
    this.jobPost = jobPost;
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
    this.code = code;
    this.country = country;
    this.residenceCountry = residenceCountry;
    this.user = user;
    this.profileImage = profileImage;
    this.file = file;
  }
}
