package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordDto (
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{9,28}$",
        message = "Invalid password format"
    )
    String currentPassword,
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{9,28}$",
        message = "Invalid password format"
    )
    String newPassword
) {


}
