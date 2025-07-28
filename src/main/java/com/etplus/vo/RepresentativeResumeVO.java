package com.etplus.vo;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RepresentativeResumeVO(
    Long id,
    String title,
    String firstName,
    String lastName,
    String email,
    boolean hasVisa,
    VisaType visaType,
    GenderType genderType,
    LocalDate birthDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    // Country
    Long countryId,
    String countryNameEn,
    String countryCode,

    // User
    long userId
) {

  @QueryProjection
  public RepresentativeResumeVO {
  }
}