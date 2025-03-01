package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.NotificationService;
import com.etplus.vo.NotificationSettingVO;
import com.etplus.vo.NotificationVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public CommonResponse<List<NotificationVO>> getNotifications(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    List<NotificationVO> vo = notificationService.getNotifications(loginUser.userId());
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/setting/me")
  public CommonResponse<NotificationSettingVO> getMyNotificationSetting(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    NotificationSettingVO vo = notificationService.getMyNotificationSetting(loginUser.userId());
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

}
