package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateJobPostDTO;
import com.etplus.controller.dto.SearchJobPostByAcademyDTO;
import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.controller.dto.SubmitResumeDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.JobPostService;
import com.etplus.vo.JobPostByAcademyVO;
import com.etplus.vo.JobPostByAdminVO;
import com.etplus.vo.JobPostDetailVO;
import com.etplus.vo.JobPostResumeRelationVO;
import com.etplus.vo.JobPostVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job-posts")
public class JobPostController {

  private final JobPostService jobPostService;

  @GetMapping
  public CommonResponse<Slice<JobPostVO>> getJobPosts(@Valid SearchJobPostDTO dto) {
    Slice<JobPostVO> result = jobPostService.getJobPosts(dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/by-academy")
  public CommonResponse<Page<JobPostByAcademyVO>> getJobPostsByAcademy(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @Valid SearchJobPostByAcademyDTO dto) {
    Page<JobPostByAcademyVO> result = jobPostService.getJobPostsByAcademy(loginUser.userId(), dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/by-admin")
  public CommonResponse<Page<JobPostByAdminVO>> getJobPostsByAdmin(
      @AuthUser({RoleType.ADMIN}) LoginUser loginUser,
      @Valid SearchJobPostByAcademyDTO dto) {
    Page<JobPostByAdminVO> result = jobPostService.getJobPostsByAdmin(loginUser.userId(), dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/{job-post-id}")
  public CommonResponse<JobPostDetailVO> getJobPostDetail(
      @PathVariable("job-post-id") Long jobPostId) {
    JobPostDetailVO vo = jobPostService.getJobPostDetail(jobPostId);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/{job-post-id}/teacher/submitted-resume")
  public CommonResponse<JobPostResumeRelationVO> getSubmittedJobPostResumeRelation(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId) {
    JobPostResumeRelationVO vo = jobPostService
        .getSubmittedJobPostResumeRelation(loginUser.userId(), jobPostId);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @PostMapping
  public CommonResponse<Void> createJobPost(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @RequestBody @Valid CreateJobPostDTO dto) {
    jobPostService.createJobPost(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/{job-post-id}/copy")
  public CommonResponse<Void> copyJobPost(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId) {
    jobPostService.copyJobPost(loginUser.userId(), jobPostId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/by-admin/academy/{academy-id}")
  public CommonResponse<Void> createJobPostByAdmin(
      @AuthUser({RoleType.ADMIN}) LoginUser loginUser,
      @PathVariable("academy-id") Long academyId,
      @RequestBody @Valid CreateJobPostDTO dto) {
    jobPostService.createJobPostByAdmin(academyId, dto, loginUser.userId());
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/draft")
  public CommonResponse<Void> createDraftJobPost(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @RequestBody CreateJobPostDTO dto) {
    jobPostService.createDraftJobPost(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/{job-post-id}/by-admin/academy/{academy-id}")
  public CommonResponse<Void> updateJobPostByAdmin(
      @AuthUser({RoleType.ADMIN}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId,
      @PathVariable("academy-id") Long academyId,
      @RequestBody @Valid CreateJobPostDTO dto) {
    jobPostService.updateJobPostByAdmin(academyId, jobPostId, dto, loginUser.userId());
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/{job-post-id}")
  public CommonResponse<Void> updateJobPost(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId,
      @RequestBody @Valid CreateJobPostDTO dto) {
    jobPostService.updateJobPost(loginUser.userId(), jobPostId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/{job-post-id}/draft")
  public CommonResponse<Void> updateDraftJobPost(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId,
      @RequestBody CreateJobPostDTO dto) {
    jobPostService.updateDraftJobPost(loginUser.userId(), jobPostId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/{job-post-id}/submit-resume/{resume-id}")
  public CommonResponse<Void> submitResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId,
      @PathVariable("resume-id") Long resumeId,
      @RequestBody SubmitResumeDTO dto) {
    jobPostService.submitResume(loginUser.userId(), jobPostId, resumeId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

}
