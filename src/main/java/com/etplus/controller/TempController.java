package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.repository.domain.code.RoleType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/temp")
public class TempController {

  @GetMapping("/user/teacher")
  public CommonResponse<String> getTeacher(@AuthUser(RoleType.TEACHER) LoginUser loginUser) {
    return new CommonResponse<>("hello " + loginUser.email(), CommonResponseCode.SUCCESS);
  }

  @GetMapping("/user/academy")
  public CommonResponse<String> getAcademy(@AuthUser(RoleType.ACADEMY) LoginUser loginUser) {
    return new CommonResponse<>("hello " + loginUser.email(), CommonResponseCode.SUCCESS);
  }

  @GetMapping("/user/all-user")
  public CommonResponse<String> getUser(@AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    return new CommonResponse<>("hello " + loginUser.email(), CommonResponseCode.SUCCESS);
  }

  @GetMapping("/guest")
  public CommonResponse<String> getAdmin() {
    return new CommonResponse<>("hello guest", CommonResponseCode.SUCCESS);
  }

  @GetMapping("/time")
  public CommonResponse<Map<String, Object>> getTime() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    return new CommonResponse<>(Map.of(
        "now", now,
        "formatted", now.format(formatter)
        ),
        CommonResponseCode.SUCCESS);
  }

}
