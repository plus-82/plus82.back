package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobPostByAcademyVO(
    Long id,
    String title,
    LocalDate dueDate,
    LocalDateTime createdAt,
    Integer salary,

    long resumeCount
) {

  @QueryProjection
  public JobPostByAcademyVO {
  }
}
