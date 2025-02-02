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
@Getter
@Setter
@Entity
@Table(name = "job_post")
public class JobPostEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;    // 제목

  @Column(nullable = false, columnDefinition = "VARCHAR(500)")
  private String jobDescription;  // 직무 설명
  @Column(columnDefinition = "VARCHAR(500)")
  private String requiredQualification;   // 자격 요건
  @Column(columnDefinition = "VARCHAR(500)")
  private String preferredQualification;  // 우대 사항
  @Column(columnDefinition = "VARCHAR(500)")
  private String benefits;  // 복지

  private Integer salary;             // 급여
  private boolean salaryNegotiable;   // 급여 협의 가능 여부

  private LocalDate jobStartDate;     // 근무 시작 가능 날짜
  private LocalDate dueDate;          // 마감일 (null = 상시 채용)

  // 대상
  private boolean forKindergarten;
  private boolean forElementary;
  private boolean forMiddleSchool;
  private boolean forHighSchool;
  private boolean forAdult;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academy_id", referencedColumnName = "id", nullable = false)
  private AcademyEntity academy;

  public JobPostEntity(Long id, String title, String jobDescription, String requiredQualification,
      String preferredQualification, String benefits, Integer salary, boolean salaryNegotiable,
      LocalDate jobStartDate, LocalDate dueDate, boolean forKindergarten, boolean forElementary,
      boolean forMiddleSchool, boolean forHighSchool, boolean forAdult, AcademyEntity academy) {
    this.id = id;
    this.title = title;
    this.jobDescription = jobDescription;
    this.requiredQualification = requiredQualification;
    this.preferredQualification = preferredQualification;
    this.benefits = benefits;
    this.salary = salary;
    this.salaryNegotiable = salaryNegotiable;
    this.jobStartDate = jobStartDate;
    this.dueDate = dueDate;
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
    this.academy = academy;
  }
}
