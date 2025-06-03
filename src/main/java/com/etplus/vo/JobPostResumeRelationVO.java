package com.etplus.vo;

import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;

public record JobPostResumeRelationVO(
    Long id,
    String coverLetter,
    JobPostResumeRelationStatus status,
    LocalDate submittedDate,
    String academyMemo,

    // 지원 당시 resume 데이터
    String resumeTitle,
    String resumeFirstName,
    String resumeLastName,

    // Country
    Long countryId,
    String countryNameEn,
    String countryCode,

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

  public static JobPostResumeRelationVO valueOf(JobPostResumeRelationEntity entity) {
    return new JobPostResumeRelationVO(
        entity.getId(),
        entity.getCoverLetter(),
        entity.getStatus(),
        entity.getSubmittedDate(),
        null,
        entity.getResumeTitle(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getCountry() == null ? null : entity.getCountry().getId(),
        entity.getCountry() == null ? null : entity.getCountry().getCountryNameEn(),
        entity.getCountry() == null ? null : entity.getCountry().getCountryCode(),
        entity.getJobPost().getId(),
        entity.getJobPost().getTitle(),
        entity.getJobPost().getAcademy().getId(),
        entity.getJobPost().getAcademy().getName()
    );
  }
}
