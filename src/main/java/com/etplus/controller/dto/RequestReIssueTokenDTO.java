package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestReIssueTokenDTO(
    @NotBlank
    String refreshToken
) {


}
