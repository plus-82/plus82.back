package com.etplus.vo;

import com.etplus.repository.domain.ResumeEntity;
import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResumeDetailVO(
    Long id,
    String title,
    String personalIntroduction,
    String firstName,
    String lastName,
    String email,
    String degree,
    String major,
    GenderType genderType,
    LocalDate birthDate,
    Boolean hasVisa,
    VisaType visaType,
    Boolean isRepresentative,
    Boolean forKindergarten,
    Boolean forElementary,
    Boolean forMiddleSchool,
    Boolean forHighSchool,
    Boolean forAdult,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    // country
    Long countryId,
    String countryNameEn,

    // residence country
    Long residenceCountryId,
    String residenceCountryNameEn,


    // profile image
    String profileImagePath,

    // file
    String filePath,
    String fileName
) {

  public static ResumeDetailVO valueOf(ResumeEntity resumeEntity) {
    return new ResumeDetailVO(
        resumeEntity.getId(),
        resumeEntity.getTitle(),
        resumeEntity.getPersonalIntroduction(),
        resumeEntity.getFirstName(),
        resumeEntity.getLastName(),
        resumeEntity.getEmail(),
        resumeEntity.getDegree(),
        resumeEntity.getMajor(),
        resumeEntity.getGenderType(),
        resumeEntity.getBirthDate(),
        resumeEntity.getHasVisa(),
        resumeEntity.getVisaType(),
        resumeEntity.getIsRepresentative(),
        resumeEntity.getForKindergarten(),
        resumeEntity.getForElementary(),
        resumeEntity.getForMiddleSchool(),
        resumeEntity.getForHighSchool(),
        resumeEntity.getForAdult(),
        resumeEntity.getCreatedAt(),
        resumeEntity.getUpdatedAt(),

        resumeEntity.getCountry() == null ? null : resumeEntity.getCountry().getId(),
        resumeEntity.getCountry() == null ? null : resumeEntity.getCountry().getCountryNameEn(),
        resumeEntity.getResidenceCountry() == null ? null : resumeEntity.getResidenceCountry().getId(),
        resumeEntity.getResidenceCountry() == null ? null : resumeEntity.getResidenceCountry().getCountryNameEn(),
        resumeEntity.getProfileImage() == null ? null : resumeEntity.getProfileImage().getPath(),
        resumeEntity.getFile() == null ? null : resumeEntity.getFile().getPath(),
        resumeEntity.getFile() == null ? null : resumeEntity.getFile().getFileName()
    );
  }

}
