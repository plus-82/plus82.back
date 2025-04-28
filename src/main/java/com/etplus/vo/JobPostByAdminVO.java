package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobPostByAdminVO(
    Long id,
    String title,
    LocalDate dueDate,
    LocalDate openDate,
    LocalDateTime createdAt,
    Integer salary,

    // academy
    Long academyId,
    String academyName,

    long resumeCount
) {

  @QueryProjection
  public JobPostByAdminVO {
  }
}
