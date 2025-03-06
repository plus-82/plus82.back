package com.etplus.vo;

import com.etplus.repository.domain.JobPostEntity;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.repository.domain.code.LocationType;
import java.time.LocalDate;
import java.util.List;

public record JobPostDetailByTeacherVO(
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

    // academy
    Long academyId,
    String academyName,
    String academyNameEn,
    String academyRepresentativeName,
    String academyDescription,
    LocationType academyLocationType,
    double lat,
    double lng,
    String academyDetailedAddress,

    List<String> academyImageUrls,

    // jobPostResumeRelation
    Long jobPostResumeRelationId,
    JobPostResumeRelationStatus jobPostResumeRelationStatus,
    LocalDate jobPostResumeRelationSubmittedDate,
    String jobPostResumeRelationResumeTitle
) {

  public static JobPostDetailByTeacherVO valueOf(JobPostEntity jobPostEntity,
      List<String> academyImageUrls, JobPostResumeRelationEntity jobPostResumeRelationEntity) {
    return new JobPostDetailByTeacherVO(
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
        jobPostEntity.getAcademy().getId(),
        jobPostEntity.getAcademy().getName(),
        jobPostEntity.getAcademy().getNameEn(),
        jobPostEntity.getAcademy().getRepresentativeName(),
        jobPostEntity.getAcademy().getDescription(),
        jobPostEntity.getAcademy().getLocationType(),
        jobPostEntity.getAcademy().getLat(),
        jobPostEntity.getAcademy().getLng(),
        jobPostEntity.getAcademy().getDetailedAddress(),
        academyImageUrls,
        jobPostResumeRelationEntity == null ? null : jobPostResumeRelationEntity.getId(),
        jobPostResumeRelationEntity == null ? null : jobPostResumeRelationEntity.getStatus(),
        jobPostResumeRelationEntity == null ? null : jobPostResumeRelationEntity.getSubmittedDate(),
        jobPostResumeRelationEntity == null ? null : jobPostResumeRelationEntity.getResumeTitle()
    );
  }

}
