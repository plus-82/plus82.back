package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record SignUpDto(
    // 계정 정보
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "Invalid email format")
    String email,       // 아이디
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{9,28}$",
        message = "Invalid password format"
    )
    String password,    // 비밀번호

    // 사용자 정보
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "Invalid email format")
    String backupEmail,     // 보조 이메일
    @NotBlank
    String name,            // 이름
    @NotBlank
    String country,         // 국가
    @NotNull
    GenderType genderType,  // 성별
    @NotNull
    LocalDate birthDate     // 생년월일
) {

}
