package com.etplus.controller;

import com.etplus.controller.dto.SignUpDto;
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
  public void signUp(@RequestBody @Valid SignUpDto dto) {
    authService.signUp(dto);
  }

}