package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateJobPostDTO;
import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.controller.dto.SubmitResumeDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.JobPostService;
import com.etplus.vo.JobPostDetailByTeacherVO;
import com.etplus.vo.JobPostDetailVO;
import com.etplus.vo.JobPostVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping("/{job-post-id}")
  public CommonResponse<JobPostDetailVO> getJobPostDetail(
      @PathVariable("job-post-id") Long jobPostId) {
    JobPostDetailVO vo = jobPostService.getJobPostDetail(jobPostId);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/{job-post-id}/by-teacher")
  public CommonResponse<JobPostDetailByTeacherVO> getJobPostDetailByTeacher(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("job-post-id") Long jobPostId) {
    JobPostDetailByTeacherVO vo = jobPostService.getJobPostDetailByTeacher(loginUser.userId(), jobPostId);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @PostMapping
  public CommonResponse<Void> createJobPost(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @RequestBody @Valid CreateJobPostDTO dto) {
    jobPostService.createJobPost(loginUser.userId(), dto);
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
