package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateResumeDTO;
import com.etplus.controller.dto.CreateResumeWithFileDTO;
import com.etplus.controller.dto.PagingDTO;
import com.etplus.controller.dto.UpdateResumeDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.ResumeService;
import com.etplus.vo.ResumeDetailVO;
import com.etplus.vo.ResumeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> createResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @ModelAttribute @Valid CreateResumeDTO dto) {
    resumeService.createResume(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> createDraftResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @ModelAttribute CreateResumeDTO dto) {
    resumeService.createDraftResume(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/{resume-id}/copy")
  public CommonResponse<Void> copyResume(@AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("resume-id") Long resumeId) {
    resumeService.copyResume(loginUser.userId(), resumeId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> createResumeWithFile(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @ModelAttribute @Valid CreateResumeWithFileDTO dto) {
    resumeService.createResumeWithFile(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping(value = "/{resume-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> updateResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("resume-id") Long resumeId,
      @ModelAttribute @Valid UpdateResumeDTO dto) {
    resumeService.updateResume(loginUser.userId(), resumeId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping(value = "/{resume-id}/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> updateDraftResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("resume-id") Long resumeId,
      @ModelAttribute UpdateResumeDTO dto) {
    resumeService.updateDraftResume(loginUser.userId(), resumeId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/{resume-id}")
  public CommonResponse<Void> deleteResume(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("resume-id") Long resumeId) {
    resumeService.deleteResume(loginUser.userId(), resumeId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

}
