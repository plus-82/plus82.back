package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/temp")
public class TempController {

  @GetMapping("/user")
  public CommonResponse<String> getUser(@AuthUser LoginUser loginUser) {
    return new CommonResponse<>("hello " + loginUser.email(), CommonResponseCode.SUCCESS);
  }

  @GetMapping("/guest")
  public CommonResponse<String> getAdmin() {
    return new CommonResponse<>("hello guest", CommonResponseCode.SUCCESS);
  }

}
