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

    // academy
    Long academyId,
    String academyName,
    String academyDescription,
    LocationType academyLocationType,
    String academyDetailedAddress,
    boolean forKindergarten,
    boolean forElementary,
    boolean forMiddleSchool,
    boolean forHighSchool,
    boolean forAdult,
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
        jobPostEntity.getAcademy().getId(),
        jobPostEntity.getAcademy().getName(),
        jobPostEntity.getAcademy().getDescription(),
        jobPostEntity.getAcademy().getLocationType(),
        jobPostEntity.getAcademy().getDetailedAddress(),
        jobPostEntity.getAcademy().isForKindergarten(),
        jobPostEntity.getAcademy().isForElementary(),
        jobPostEntity.getAcademy().isForMiddleSchool(),
        jobPostEntity.getAcademy().isForHighSchool(),
        jobPostEntity.getAcademy().isForAdult(),
        academyImageUrls
    );
  }

}
