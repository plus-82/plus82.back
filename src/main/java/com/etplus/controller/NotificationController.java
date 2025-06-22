package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.PagingDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.NotificationService;
import com.etplus.vo.NotificationSettingVO;
import com.etplus.vo.NotificationVO;
import com.etplus.vo.UnreadNotificationCount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public CommonResponse<Slice<NotificationVO>> getNotifications(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @Valid PagingDTO dto) {
    Slice<NotificationVO> vo = notificationService.getNotifications(loginUser.userId(), dto);
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/unread-count")
  public CommonResponse<UnreadNotificationCount> getUnreadNotificationCount(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    UnreadNotificationCount count = notificationService.getUnreadNotificationCount(loginUser.userId());
    return new CommonResponse<>(count, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/setting/me")
  public CommonResponse<NotificationSettingVO> getMyNotificationSetting(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser) {
    NotificationSettingVO vo = notificationService.getMyNotificationSetting(loginUser.userId());
    return new CommonResponse<>(vo, CommonResponseCode.SUCCESS);
  }

  @PutMapping("/setting/me")
  public CommonResponse<Void> updateMyNotificationSetting(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @RequestParam boolean allowEmail) {
    notificationService.updateMyNotificationSetting(loginUser.userId(), allowEmail);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

}
