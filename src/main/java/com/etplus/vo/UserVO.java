package com.etplus.vo;

import com.etplus.repository.domain.code.GenderType;
import java.time.LocalDate;

public record UserVO(
    Long id,
    String firstName,
    String lastName,
    GenderType genderType,
    LocalDate birthDate,
    String email,
    String countryNameEn,

    // file
    String profileImagePath
) {

}
