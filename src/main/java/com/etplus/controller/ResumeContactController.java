package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.SearchResumeContactDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.ResumeContactService;
import com.etplus.vo.ResumeContactVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/resume-contacts")
public class ResumeContactController {

  private final ResumeContactService resumeContactService;

  @GetMapping
  public CommonResponse<Page<ResumeContactVO>> getMyResumeContacts(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @Valid SearchResumeContactDTO dto) {
    Page<ResumeContactVO> result = resumeContactService.getMyResumeContacts(
        loginUser.userId(), dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }
}

