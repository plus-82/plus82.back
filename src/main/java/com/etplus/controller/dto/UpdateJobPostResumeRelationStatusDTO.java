package com.etplus.controller.dto;

import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateJobPostResumeRelationStatusDTO(
    @NotNull
    JobPostResumeRelationStatus status
) {

}
