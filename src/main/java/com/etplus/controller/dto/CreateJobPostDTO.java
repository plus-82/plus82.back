package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record CreateJobPostDTO(
    @NotBlank
    String title,
    @NotBlank
    String jobDescription,
    String requiredQualification,
    String preferredQualification,
    String benefits,
    Integer salary,
    boolean salaryNegotiable,
    LocalDate jobStartDate,
    LocalDate dueDate,

    // 대상
    boolean forKindergarten,
    boolean forElementary,
    boolean forMiddleSchool,
    boolean forHighSchool,
    boolean forAdult
) {

}
