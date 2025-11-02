package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactResumeDTO(
    @Size(max = 1000)
    String interestReason,    // ?님께 관심이 생긴 이유
    @Size(max = 1000)
    String appealMessage,     // ?님이 우리에게 관심을 가질만한 이유
    @Size(max = 1000)
    String additionalMessage, // 추가로 하고 싶은 말
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "Invalid email format")
    String contactEmail       // 이메일
) {

}

