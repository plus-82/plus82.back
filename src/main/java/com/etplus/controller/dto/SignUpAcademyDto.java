package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record SignUpAcademyDto(
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
    @NotBlank
    String fullName,
    @NotNull
    GenderType genderType,  // 성별
    @NotNull
    LocalDate birthDate,    // 생년월일

    // 학원 정보
    @NotBlank
    String academyName,     // 학원 이름
    @NotBlank
    String academyNameEn,   // 학원 이름 (영어)
    @NotBlank
    String representativeName,          // 대표자명
    @NotNull
    LocationType locationType,          // 위치 (시,도)
    @NotEmpty
    String address,                     // 주소
    String detailedAddress,             // 상세 주소
    @NotNull
    Double lat,
    @NotNull
    Double lng,
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid business registration number format")
    String businessRegistrationNumber   // 사업자 등록번호
) {


}
