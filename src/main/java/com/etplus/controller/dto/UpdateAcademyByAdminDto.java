package com.etplus.controller.dto;

import com.etplus.repository.domain.code.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UpdateAcademyByAdminDto(
    // Academy
    @NotBlank
    String name,
    @NotBlank
    String nameEn,   // 학원 이름 (영어)
    @NotBlank
    String representativeName,          // 대표자명
    String representativeEmail,         // 대표자 이메일
    String description,
    @NotNull
    LocationType locationType,          // 위치 (시,도)
    @NotEmpty
    String address,
    String detailedAddress,             // 상세 주소
    @NotNull
    Double lat,
    @NotNull
    Double lng,
    @NotNull
    Boolean forKindergarten,
    @NotNull
    Boolean forElementary,
    @NotNull
    Boolean forMiddleSchool,
    @NotNull
    Boolean forHighSchool,
    @NotNull
    Boolean forAdult,

    // Images
    @NotNull
    List<MultipartFile> newImages,
    @NotNull
    List<Long> oldImageIds
) {

}
