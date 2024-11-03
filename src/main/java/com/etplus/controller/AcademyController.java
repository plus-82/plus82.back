package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.UpdateAcademyDto;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.AcademyService;
import com.etplus.vo.AcademyDetailVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/academies")
public class AcademyController {

  private final AcademyService academyService;

  @GetMapping(value = "/me")
  public CommonResponse<AcademyDetailVO> getMyAcademy(
      @AuthUser(RoleType.ACADEMY) LoginUser loginUser) {
    AcademyDetailVO vo = academyService.getMyAcademy(loginUser);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> updateMyAcademy(@ModelAttribute @Valid UpdateAcademyDto dto,
      @AuthUser(RoleType.ACADEMY) LoginUser loginUser) {
    academyService.updateMyAcademy(dto, loginUser);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

}
