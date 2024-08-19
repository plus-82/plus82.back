package com.etplus.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record VerifyEmailDto(
    @NotEmpty
    String code
) {

}
