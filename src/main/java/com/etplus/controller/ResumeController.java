package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateResumeDTO;
import com.etplus.controller.dto.PagingDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.ResumeService;
import com.etplus.vo.ResumeDetailVO;
import com.etplus.vo.ResumeVO;
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
@RequestMapping("/api/v1/resumes")
public class ResumeController {

  private final ResumeService resumeService;

  @GetMapping("/me")
  public CommonResponse<Slice<ResumeVO>> getMyResumes(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser, @Valid PagingDTO dto) {
    Slice<ResumeVO> result = resumeService.getMyResumes(loginUser.userId(), dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/{resume-id}")
  public CommonResponse<ResumeDetailVO> getResumeDetail(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("resume-id") Long resumeId) {
    ResumeDetailVO vo = resumeService.getResumeDetail(loginUser.userId(), resumeId);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @PostMapping
  public CommonResponse<Void> createResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @RequestBody @Valid CreateResumeDTO dto) {
    resumeService.createResume(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

}
