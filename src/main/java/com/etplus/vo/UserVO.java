package com.etplus.vo;

import com.etplus.repository.domain.code.GenderType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;

public record UserVO(
    Long id,
    String firstName,
    String lastName,
    String fullName,
    GenderType genderType,
    LocalDate birthDate,
    String email,

    // country
    Long countryId,
    String countryNameEn,
    String countryCode,
    String countryCallingCode,
    String flag,

    // file
    String profileImagePath
) {

  @QueryProjection
  public UserVO {
  }
}
