package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.controller.dto.RequestEmailVerificationDto;
import com.etplus.controller.dto.RequestResetPasswordDto;
import com.etplus.controller.dto.ResetPasswordDto;
import com.etplus.controller.dto.SignUpDto;
import com.etplus.controller.dto.VerifyEmailDto;
import com.etplus.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/sign-up")
  public CommonResponse<Void> signUp(@RequestBody @Valid SignUpDto dto) {
    authService.signUp(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/request-verification")
  public CommonResponse<Void> requestVerification(@RequestBody @Valid RequestEmailVerificationDto dto) {
    authService.requestVerification(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/verify-code")
  public CommonResponse<Void> verifyCode(@RequestBody @Valid VerifyEmailDto dto) {
    authService.verifyCode(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/reset-password/request")
  public CommonResponse<Void> requestResetPassword(@RequestBody @Valid RequestResetPasswordDto dto) {
    authService.requestResetPassword(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/reset-password")
  public CommonResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordDto dto) {
    authService.resetPassword(dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

}
