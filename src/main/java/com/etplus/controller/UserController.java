package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.config.security.LoginUser;
import com.etplus.controller.dto.UpdatePasswordDto;
import com.etplus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @DeleteMapping("/me")
  public CommonResponse<Void> deleteMe(@AuthenticationPrincipal LoginUser loginUser) {
    userService.deleteUser(loginUser.getUserId());
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me/password")
  public CommonResponse<Void> updatePassword(@AuthenticationPrincipal LoginUser loginUser,
  @RequestBody @Valid UpdatePasswordDto dto) {
    userService.updatePassword(loginUser.getUserId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }
}
