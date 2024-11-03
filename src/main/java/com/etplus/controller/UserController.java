package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.UpdatePasswordDto;
import com.etplus.controller.dto.UpdateUserDto;
import com.etplus.service.UserService;
import com.etplus.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public CommonResponse<UserVO> getMe(@AuthUser LoginUser loginUser) {
    UserVO vo = userService.getMe(loginUser.userId());
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me")
  public CommonResponse<Void> updateMe(@AuthUser LoginUser loginUser,
      @RequestBody @Valid UpdateUserDto dto) {
    userService.updateMe(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/me")
  public CommonResponse<Void> deleteMe(@AuthUser LoginUser loginUser) {
    userService.deleteUser(loginUser.userId());
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me/password")
  public CommonResponse<Void> updatePassword(@AuthUser LoginUser loginUser,
  @RequestBody @Valid UpdatePasswordDto dto) {
    userService.updatePassword(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }
}
