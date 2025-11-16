package com.etplus.vo;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResumeContactVO(
    Long id,

    Long resumeId,
    String resumeTitle,
    String firstName,
    String lastName,
    String email,
    boolean hasVisa,
    VisaType visaType,
    GenderType genderType,
    LocalDate birthDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    Boolean forKindergarten,
    Boolean forElementary,
    Boolean forMiddleSchool,
    Boolean forHighSchool,
    Boolean forAdult,
    // Country
    Long countryId,
    String countryNameEn,
    String countryCode,

    Long teacherId,
    Long academyUserId
) {

  @QueryProjection
  public ResumeContactVO {
  }
}

