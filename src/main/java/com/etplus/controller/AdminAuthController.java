package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.controller.dto.SignInDto;
import com.etplus.service.AdminAuthService;
import com.etplus.vo.TokenVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthController {

  private final AdminAuthService adminAuthService;

  @PostMapping("/sign-in")
  public CommonResponse<TokenVO> signIn(@RequestBody @Valid SignInDto dto) {
    TokenVO token = adminAuthService.signIn(dto);
    return new CommonResponse<>(token, CommonResponseCode.SUCCESS);
  }

}
