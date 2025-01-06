package com.etplus.repository.domain;

import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
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
@Getter
@Setter
@Entity
@Table(name = "job_post_resume_relation")
public class JobPostResumeRelationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JobPostResumeRelationStatus status;
  @Column(nullable = false)
  private LocalDate submittedDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resume_id", referencedColumnName = "id", nullable = false, updatable = false)
  private ResumeEntity resume;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_post_id", referencedColumnName = "id", nullable = false, updatable = false)
  private JobPostEntity jobPost;

  public JobPostResumeRelationEntity(Long id, JobPostResumeRelationStatus status,
      LocalDate submittedDate, ResumeEntity resume, JobPostEntity jobPost) {
    this.id = id;
    this.status = status;
    this.submittedDate = submittedDate;
    this.resume = resume;
    this.jobPost = jobPost;
  }
}
