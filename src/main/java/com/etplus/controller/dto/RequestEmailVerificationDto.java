package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestEmailVerificationDto(
    @NotBlank
    String email
) {

}
