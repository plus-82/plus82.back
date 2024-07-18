package com.etplus.controller.dto;

import com.etplus.repository.domain.code.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpDto(
    @NotBlank
    String nickName,
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "Invalid email format")
    String email,
    @NotBlank
    String password,
    @NotNull
    RoleType roleType
) {

}
