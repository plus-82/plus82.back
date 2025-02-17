package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public record UpdateResumeDTO(

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

    MultipartFile profileImage, // null 인 경우 user 의 profileImage 사용
    @NotNull
    Long countryId,
    @NotNull
    Long residenceCountryId
) {

}
