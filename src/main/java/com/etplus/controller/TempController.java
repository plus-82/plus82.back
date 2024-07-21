package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.config.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/temp")
public class TempController {

  @GetMapping("/user")
  public CommonResponse<String> getUser(@AuthenticationPrincipal LoginUser loginUser) {
    return new CommonResponse<>("hello " + loginUser.getEmail(), CommonResponseCode.SUCCESS);
  }

  @GetMapping("/guest")
  public CommonResponse<String> getAdmin() {
    return new CommonResponse<>("hello guest", CommonResponseCode.SUCCESS);
  }

}
