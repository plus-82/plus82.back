package com.etplus.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateReportDTO(
    @NotEmpty
    String reason,
    String otherReason
) {

}