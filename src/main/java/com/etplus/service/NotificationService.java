package com.etplus.service;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.NotificationRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.UserEntity;
import com.etplus.vo.NotificationSettingVO;
import com.etplus.vo.NotificationVO;
import com.etplus.vo.UnreadNotificationCount;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  public Slice<NotificationVO> getNotifications(long userId, PagingDTO dto) {
    Slice<NotificationVO> notifications = notificationRepository
        .findAllNotificationsByUserId(userId, dto);

    // TODO async event publish
    UserEntity user = userRepository.findById(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    user.setLastNotificationReadAt(LocalDateTime.now());
    userRepository.save(user);

    return notifications;
  }

  public UnreadNotificationCount getUnreadNotificationCount(long userId) {
    UserEntity user = userRepository.findById(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    long count = notificationRepository.countByCreatedAtAfter(user.getLastNotificationReadAt());
    return new UnreadNotificationCount(count > 0, count);
  }

  public NotificationSettingVO getMyNotificationSetting(long userId) {
    UserEntity user = userRepository.findById(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    return new NotificationSettingVO(user.isAllowEmail());
  }

  @Transactional
  public void updateMyNotificationSetting(long userId, boolean allowEmail) {
    UserEntity user = userRepository.findById(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    user.setAllowEmail(allowEmail);
    userRepository.save(user);
  }
}
