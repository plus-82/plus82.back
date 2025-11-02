package com.etplus.vo;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResumeContactVO(
    Long id,

    String interestReason,
    String appealMessage,
    String additionalMessage,
    String contactEmail,

    Long resumeId,
    String resumeTitle,
    String firstName,
    String lastName,
    String email,
    String degree,
    String major,
    GenderType genderType,
    LocalDate birthDate,
    Boolean hasVisa,
    VisaType visaType,
    Boolean forKindergarten,
    Boolean forElementary,
    Boolean forMiddleSchool,
    Boolean forHighSchool,
    Boolean forAdult,

    Long countryId,
    String countryNameEn,

    Long teacherId,
    Long academyUserId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

  @QueryProjection
  public ResumeContactVO {
  }
}

