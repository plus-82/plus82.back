package com.etplus.vo;

import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.repository.domain.code.VisaType;
import java.time.LocalDate;

public record JobPostResumeRelationDetailVO(
    Long id,
    String coverLetter,
    JobPostResumeRelationStatus status,
    LocalDate submittedDate,

    // 지원 당시 resume 데이터
    String resumeTitle,
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
    Long userId,

    // profile image
    String profileImagePath,

    // JobPost
    Long jobPostId,
    String jobPostTitle,

    // file
    String filePath,
    String fileName
) {

  public static JobPostResumeRelationDetailVO valueOf(JobPostResumeRelationEntity entity) {
    return new JobPostResumeRelationDetailVO(
        entity.getId(),
        entity.getCoverLetter(),
        entity.getStatus(),
        entity.getSubmittedDate(),
        entity.getResumeTitle(),
        entity.getPersonalIntroduction(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getEmail(),
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
        entity.getUser().getId(),
        entity.getProfileImage() == null ? null : entity.getProfileImage().getPath(),
        entity.getJobPost().getId(),
        entity.getJobPost().getTitle(),
        entity.getFile() == null ? null : entity.getFile().getPath(),
        entity.getFile() == null ? null : entity.getFile().getFileName()
    );
  }

}
