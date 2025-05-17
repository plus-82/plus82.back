package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateAcademyUserDto(
    @NotBlank
    String fullName,
    @NotBlank
    GenderType genderType,
    @NotNull
    LocalDate birthDate
) {

}