package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.controller.dto.UpdateJobPostResumeRelationStatusDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.JobPostResumeRelationService;
import com.etplus.vo.JobPostResumeRelationDetailVO;
import com.etplus.vo.JobPostResumeRelationSummaryVO;
import com.etplus.vo.JobPostResumeRelationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job-post-resume-relations")
public class JobPostResumeRelationController {

  private final JobPostResumeRelationService jobPostResumeRelationService;

  @GetMapping
  public CommonResponse<Page<JobPostResumeRelationVO>> getJobPostResumeRelations(
      @AuthUser({RoleType.TEACHER, RoleType.ACADEMY}) LoginUser loginUser,
      @Valid SearchJobPostResumeRelationDTO dto) {
    Page<JobPostResumeRelationVO> result = jobPostResumeRelationService
        .getAllJobPostResumeRelations(loginUser.roleType(), loginUser.userId(), dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/{job-post-resume-relation-id}")
  public CommonResponse<JobPostResumeRelationDetailVO> getJobPostResumeRelation(
      @AuthUser({RoleType.TEACHER, RoleType.ACADEMY}) LoginUser loginUser,
      @PathVariable("job-post-resume-relation-id") Long jobPostResumeRelationId) {
    JobPostResumeRelationDetailVO result = jobPostResumeRelationService
        .getJobPostResumeRelation(loginUser.roleType(), loginUser.userId(), jobPostResumeRelationId);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/by-code")
  public CommonResponse<JobPostResumeRelationDetailVO> getJobPostResumeRelationByCode(
      @RequestParam("code") String code) {
    JobPostResumeRelationDetailVO result = jobPostResumeRelationService
        .getJobPostResumeRelationByCode(code);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @PutMapping("/{job-post-resume-relation-id}/status")
  public CommonResponse<Void> updateJobPostResumeRelationStatus(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @PathVariable("job-post-resume-relation-id") Long jobPostResumeRelationId,
      @Valid @RequestBody UpdateJobPostResumeRelationStatusDTO dto) {
    jobPostResumeRelationService.updateJobPostResumeRelationStatus(
        jobPostResumeRelationId, dto.status(), loginUser.userId());
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @GetMapping("/summary")
  public CommonResponse<JobPostResumeRelationSummaryVO> getJobPostResumeRelationSummary(
      @AuthUser({RoleType.TEACHER, RoleType.ACADEMY}) LoginUser loginUser) {
    JobPostResumeRelationSummaryVO result = jobPostResumeRelationService
        .getJobPostResumeRelationSummary(loginUser.roleType(), loginUser.userId());
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }
}
