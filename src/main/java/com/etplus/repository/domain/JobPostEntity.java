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
public class JobPostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;               // 제목
  @Column(columnDefinition = "TEXT")
  private String description;         // 설명

  private Integer salary;             // 급여
  private boolean salaryNegotiable;   // 급여 협의 가능 여부

  private LocalDate jobStartDate;     // 근무 시작 가능 날짜
  private LocalDate dueDate;          // 마감일

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academy_id", referencedColumnName = "id")
  private AcademyEntity academy;

  public JobPostEntity(Long id, String title, String description, Integer salary,
      boolean salaryNegotiable, LocalDate jobStartDate, LocalDate dueDate, AcademyEntity academy) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.salary = salary;
    this.salaryNegotiable = salaryNegotiable;
    this.jobStartDate = jobStartDate;
    this.dueDate = dueDate;
    this.academy = academy;
  }
}
