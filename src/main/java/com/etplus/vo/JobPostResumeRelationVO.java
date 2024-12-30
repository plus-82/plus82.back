package com.etplus.vo;

import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;

public record JobPostResumeRelationVO(
    Long id,
    JobPostResumeRelationStatus status,
    LocalDate submittedDate,

    // Resume
    Long resumeId,
    String resumeTitle,
    String resumeFirstName,
    String resumeLastName,

    // JobPost
    Long jobPostId,
    String jobPostTitle,

    // Academy
    Long academyId,
    String academyName
) {

  @QueryProjection
  public JobPostResumeRelationVO {
  }
}
