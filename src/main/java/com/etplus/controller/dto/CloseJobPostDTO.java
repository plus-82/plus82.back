package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CloseJobPostDTO(
    @NotBlank
    String closeReason
) {

}
