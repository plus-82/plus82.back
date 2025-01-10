package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;

public record JobPostResumeRelationSummaryVO(
    int submitted,
    int reviewed,
    int accepted,
    int rejected,
    int total
) {

  @QueryProjection
  public JobPostResumeRelationSummaryVO {
  }
}
