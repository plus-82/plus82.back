package com.etplus.scheduler.vo;

import com.querydsl.core.annotations.QueryProjection;

public record JobPostNewApplicantNotiVO(
    long id,
    String title,

    // academy
    long academyId,
    String academyName,
    String representativeName, // 대표자 이름
    String representativeEmail, // 대표자 메일

    // adminUser
    long adminUserId,
    String adminUserEmail,

    // jobPostResume
    long yesterdayJobPostResumeTotalCount
) {

  @QueryProjection
  public JobPostNewApplicantNotiVO {
  }
}
