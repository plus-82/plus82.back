package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateJobPostDTO(
    @NotBlank
    String title,
    String description,
    Integer salary,
    @NotNull
    Boolean salaryNegotiable,
    LocalDate jobStartDate,
    LocalDate dueDate
) {

}
