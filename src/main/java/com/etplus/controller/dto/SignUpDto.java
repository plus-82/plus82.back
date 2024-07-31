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
    String password,    // 비밀번호

    // 사용자 정보
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
