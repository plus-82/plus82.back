package com.etplus.service;

import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.NotificationRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.UserEntity;
import com.etplus.vo.NotificationSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  public NotificationSettingVO getMyNotificationSetting(long userId) {
    UserEntity user = userRepository.findById(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    return new NotificationSettingVO(user.isAllowEmail());
  }
}
