package com.etplus.repository;

import com.etplus.vo.NotificationVO;
import java.util.List;

interface NotificationRepositoryCustom {

  List<NotificationVO> findAllNotificationsByUserId(long userId);

}
