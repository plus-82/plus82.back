package com.etplus.controller;

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
  public String getUser(@AuthenticationPrincipal LoginUser loginUser) {
    return "hello " + loginUser.getEmail();
  }

  @GetMapping("/guest")
  public String getAdmin() {
    return "guest";
  }

}
