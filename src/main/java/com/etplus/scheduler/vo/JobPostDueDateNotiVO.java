package com.etplus.scheduler.vo;

import com.querydsl.core.annotations.QueryProjection;

public record JobPostDueDateNotiVO (
    long id,
    String title,

    // academy
    long academyId,
    String academyName,
    String representativeName, // 대표자 이름
    String representativeEmail, // 대표자 메일
    boolean byAdmin,

    // adminUser
    long adminUserId,
    String adminUserEmail,

    // jobPostResume
    long jobPostResumeCount
) {

  @QueryProjection
  public JobPostDueDateNotiVO {
  }



}
