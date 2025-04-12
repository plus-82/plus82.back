package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.RequestResetPasswordDto;
import com.etplus.controller.dto.SignInDto;
import com.etplus.controller.dto.SignUpAcademyDto;
import com.etplus.service.AcademyAuthService;
import com.etplus.vo.TokenVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/academy")
public class AcademyAuthController {

  private final AcademyAuthService academyAuthService;

  @PostMapping("/sign-up")
  public CommonResponse<Void> signUpAcademy(@RequestBody @Valid SignUpAcademyDto dto) {
    academyAuthService.signUpAcademy(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/sign-in")
  public CommonResponse<TokenVO> signIn(@RequestBody @Valid SignInDto dto) {
    TokenVO token = academyAuthService.signInAcademy(dto);
    return new CommonResponse<>(token, CommonResponseCode.SUCCESS);
  }

  @PostMapping("/request-verification")
  public CommonResponse<Void> requestEmailVerification(@RequestBody @Valid RequestEmailVerificationDto dto) {
    academyAuthService.requestEmailVerification(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/reset-password/request")
  public CommonResponse<Void> requestResetPassword(@RequestBody @Valid RequestResetPasswordDto dto) {
    academyAuthService.requestResetPassword(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

}
