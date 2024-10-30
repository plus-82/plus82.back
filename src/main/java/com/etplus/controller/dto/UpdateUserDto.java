package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateUserDto(
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotNull
    GenderType genderType,  // 성별
    @NotNull
    LocalDate birthDate,    // 생년월일
    Long countryId          // 국가
) {

}