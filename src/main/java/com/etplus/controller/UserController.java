package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.UpdateAcademyUserDto;
import com.etplus.controller.dto.UpdatePasswordDto;
import com.etplus.controller.dto.UpdateProfileImageDTO;
import com.etplus.controller.dto.UpdateRepresentativeResumePublicDto;
import com.etplus.controller.dto.UpdateUserDto;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.UserService;
import com.etplus.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public CommonResponse<UserVO> getMe(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    UserVO vo = userService.getMe(loginUser.userId());
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

//  @GetMapping
//  public CommonResponse<Slice<UserVO>> getAllUsers(
//      @AuthUser(RoleType.ADMIN) LoginUser loginUser,
//      @Valid SearchUserDTO dto) {
//    Slice<UserVO> users = userService.getAllUsers(dto);
//    return new CommonResponse<>(users, CommonResponseCode.SUCCESS);
//  }

  @PostMapping("/admin")
  public CommonResponse<Void> createAdminUser(@RequestParam String email, @RequestParam String password) {
    userService.createAdminUser(email, password);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me")
  public CommonResponse<Void> updateMe(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @RequestBody @Valid UpdateUserDto dto) {
    userService.updateMe(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me/by-academy")
  public CommonResponse<Void> updateMeByAcademy(
      @AuthUser({RoleType.ACADEMY}) LoginUser loginUser,
      @RequestBody @Valid UpdateAcademyUserDto dto) {
    userService.updateMeByAcademy(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping(value="/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> updateProfileImage(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @ModelAttribute @Valid UpdateProfileImageDTO dto) {
    userService.updateProfileImage(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping(value = "/me/profile-image")
  public CommonResponse<Void> deleteProfileImage(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    userService.deleteProfileImage(loginUser.userId());
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/me")
  public CommonResponse<Void> deleteMe(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    userService.deleteUser(loginUser.userId());
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me/password")
  public CommonResponse<Void> updatePassword(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @RequestBody @Valid UpdatePasswordDto dto) {
    userService.updatePassword(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/me/representative-resume-public")
  public CommonResponse<Void> updateRepresentativeResumePublic(
      @AuthUser({RoleType.TEACHER}) LoginUser loginUser,
      @RequestBody @Valid UpdateRepresentativeResumePublicDto dto) {
    userService.updateRepresentativeResumePublic(loginUser.userId(), dto);
    return new CommonResponse(CommonResponseCode.SUCCESS);
  }
}
