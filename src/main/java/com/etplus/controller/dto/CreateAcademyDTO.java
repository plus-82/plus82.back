package com.etplus.controller.dto;

import com.etplus.repository.domain.code.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateAcademyDTO(
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
//    @NotEmpty
//    String address,
    String detailedAddress,             // 상세 주소
    @NotNull
    double lat,
    @NotNull
    double lng,
    @NotNull
    boolean forKindergarten,
    @NotNull
    boolean forElementary,
    @NotNull
    boolean forMiddleSchool,
    @NotNull
    boolean forHighSchool,
    @NotNull
    boolean forAdult,

    // Images
    @NotNull
    List<MultipartFile> images
) {

}
