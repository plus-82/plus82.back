package com.etplus.vo;

import com.etplus.repository.domain.JobPostEntity;
import com.etplus.repository.domain.code.LocationType;
import java.time.LocalDate;
import java.util.List;

public record JobPostDetailVO(
    // job post
    Long id,
    String title,
    String jobDescription,
    String requiredQualification,
    String preferredQualification,
    String benefits,
    Integer salary,
    boolean salaryNegotiable,
    LocalDate jobStartDate,
    LocalDate dueDate,
    boolean forKindergarten,
    boolean forElementary,
    boolean forMiddleSchool,
    boolean forHighSchool,
    boolean forAdult,
    boolean isDraft,

    // academy
    Long academyId,
    String academyName,
    String academyNameEn,
    String academyRepresentativeName,
    String academyDescription,
    LocationType academyLocationType,
    double lat,
    double lng,
    String academyAddress,
    String academyDetailedAddress,
    List<String> academyImageUrls
) {

  public static JobPostDetailVO valueOf(JobPostEntity jobPostEntity,
      List<String> academyImageUrls) {
    return new JobPostDetailVO(
        jobPostEntity.getId(),
        jobPostEntity.getTitle(),
        jobPostEntity.getJobDescription(),
        jobPostEntity.getRequiredQualification(),
        jobPostEntity.getPreferredQualification(),
        jobPostEntity.getBenefits(),
        jobPostEntity.getSalary(),
        jobPostEntity.isSalaryNegotiable(),
        jobPostEntity.getJobStartDate(),
        jobPostEntity.getDueDate(),
        jobPostEntity.isForKindergarten(),
        jobPostEntity.isForElementary(),
        jobPostEntity.isForMiddleSchool(),
        jobPostEntity.isForHighSchool(),
        jobPostEntity.isForAdult(),
        jobPostEntity.isDraft(),
        jobPostEntity.getAcademy().getId(),
        jobPostEntity.getAcademy().getName(),
        jobPostEntity.getAcademy().getNameEn(),
        jobPostEntity.getAcademy().getRepresentativeName(),
        jobPostEntity.getAcademy().getDescription(),
        jobPostEntity.getAcademy().getLocationType(),
        jobPostEntity.getAcademy().getLat(),
        jobPostEntity.getAcademy().getLng(),
        jobPostEntity.getAcademy().getAddress(),
        jobPostEntity.getAcademy().getDetailedAddress(),
        academyImageUrls
    );
  }

}
