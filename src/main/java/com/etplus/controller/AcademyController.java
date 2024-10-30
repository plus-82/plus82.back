package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.config.security.LoginUser;
import com.etplus.controller.dto.UpdateAcademyDto;
import com.etplus.service.AcademyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/academies")
public class AcademyController {

  private final AcademyService academyService;

  @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> updateMyAcademy(@ModelAttribute @Valid UpdateAcademyDto dto,
      @AuthenticationPrincipal LoginUser loginUser) {
    academyService.updateMyAcademy(dto, loginUser);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

}
