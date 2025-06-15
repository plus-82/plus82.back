package com.etplus.repository;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.vo.NotificationVO;
import org.springframework.data.domain.Slice;

interface NotificationRepositoryCustom {

  Slice<NotificationVO> findAllNotificationsByUserId(long userId, PagingDTO dto);

}
