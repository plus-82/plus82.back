package com.etplus.vo;

import com.etplus.repository.domain.ResumeContactEntity;
import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import com.etplus.util.MaskingUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResumeContactDetailVO (
    Long id,

    String interestReason,
    String appealMessage,
    String additionalMessage,
    String contactEmail,

    // 연락 당시 resume 데이터
    Long resumeId,
    String resumeTitle,
    String firstName,
    String lastName,
    String email,
    String degree,
    String major,
    GenderType genderType,
    LocalDate birthDate,
    Boolean hasVisa,
    VisaType visaType,
    Boolean forKindergarten,
    Boolean forElementary,
    Boolean forMiddleSchool,
    Boolean forHighSchool,
    Boolean forAdult,

    // country
    Long countryId,
    String countryNameEn,
    String countryCode,
    String countryCallingCode,
    String flag,

    // residenceCountry
    Long residenceCountryId,
    String residenceCountryNameEn,
    String residenceCountryCode,
    String residenceCountryCallingCode,
    String residenceFlag,

    // user
    Long teacherId,

    // profile image
    String profileImagePath,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

  public static ResumeContactDetailVO valueOfWithMasking(ResumeContactEntity entity) {
    return new ResumeContactDetailVO(
        entity.getId(),
        entity.getInterestReason(),
        entity.getAppealMessage(),
        entity.getAdditionalMessage(),
        entity.getContactEmail(),
        entity.getResumeId(),
        entity.getResumeTitle(),
        entity.getFirstName(),
        entity.getLastName(),
        MaskingUtil.maskEmail(entity.getEmail()),
        entity.getDegree(),
        entity.getMajor(),
        entity.getGenderType(),
        entity.getBirthDate(),
        entity.getHasVisa(),
        entity.getVisaType(),
        entity.getForKindergarten(),
        entity.getForElementary(),
        entity.getForMiddleSchool(),
        entity.getForHighSchool(),
        entity.getForAdult(),
        entity.getCountry() == null ? null : entity.getCountry().getId(),
        entity.getCountry() == null ? null : entity.getCountry().getCountryNameEn(),
        entity.getCountry() == null ? null : entity.getCountry().getCountryCode(),
        entity.getCountry() == null ? null : entity.getCountry().getCountryCallingCode(),
        entity.getCountry() == null ? null : entity.getCountry().getFlag(),
        entity.getResidenceCountry() == null ? null : entity.getResidenceCountry().getId(),
        entity.getResidenceCountry() == null ? null : entity.getResidenceCountry().getCountryNameEn(),
        entity.getResidenceCountry() == null ? null : entity.getResidenceCountry().getCountryCode(),
        entity.getResidenceCountry() == null ? null : entity.getResidenceCountry().getCountryCallingCode(),
        entity.getResidenceCountry() == null ? null : entity.getResidenceCountry().getFlag(),
        entity.getTeacher() == null ? null : entity.getTeacher().getId(),
        entity.getProfileImage() == null ? null : entity.getProfileImage().getPath(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

}
