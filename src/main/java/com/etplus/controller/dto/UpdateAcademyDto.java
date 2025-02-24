package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UpdateAcademyDto(
    // Academy
    @NotBlank
    String name,
    @NotBlank
    String nameEn,   // 학원 이름 (영어)
    String description,
    @NotNull
    Boolean forKindergarten,
    @NotNull
    Boolean forElementary,
    @NotNull
    Boolean forMiddleSchool,
    @NotNull
    Boolean forHighSchool,
    @NotNull
    Boolean forAdult,

    // Images
    @NotNull
    List<MultipartFile> images
) {

}
