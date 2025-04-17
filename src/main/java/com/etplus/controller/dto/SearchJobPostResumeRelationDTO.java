package com.etplus.controller.dto;

import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchJobPostResumeRelationDTO extends PagingDTO {

  private JobPostResumeRelationStatus status;
  private Long jobPostId;

}
