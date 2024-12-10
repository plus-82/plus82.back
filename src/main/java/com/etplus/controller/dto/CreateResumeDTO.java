package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record CreateResumeDTO(

    @NotBlank
    String title,
    @NotBlank
    String personalIntroduction,
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "Invalid email format")
    String email,
    @NotBlank
    String degree,
    String major,
    @NotNull
    GenderType genderType,
    @NotNull
    LocalDate birthDate,
    @NotNull
    Boolean hasVisa,
    VisaType visaType,
    Boolean isRepresentative,
    Boolean forKindergarten,
    Boolean forElementary,
    Boolean forMiddleSchool,
    Boolean forHighSchool,
    Boolean forAdult,

    @NotNull
    Long countryId,
    @NotNull
    Long residenceCountryId
) {

}
