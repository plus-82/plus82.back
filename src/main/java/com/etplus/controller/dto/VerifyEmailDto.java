package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailDto(
    @NotBlank
    String code
) {

}
