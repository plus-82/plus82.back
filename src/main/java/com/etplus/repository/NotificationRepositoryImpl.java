package com.etplus.repository;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.repository.domain.QNotificationEntity;
import com.etplus.vo.NotificationVO;
import com.etplus.vo.QNotificationVO;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

  private final JPAQueryFactory query;
  private QNotificationEntity notification;

  public NotificationRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    notification = new QNotificationEntity("notification");
  }

  @Override
  public Slice<NotificationVO> findAllNotificationsByUserId(long userId, PagingDTO dto) {
    JPAQuery<NotificationVO> jpaQuery = query.select(
            new QNotificationVO(
                notification.id,
                notification.title,
                notification.titleEn,
                notification.content,
                notification.contentEn,
                notification.targetUrl,
                notification.createdAt
            ))
        .from(notification)
        .where(notification.user.id.eq(userId)
            .and(notification.createdAt.after(LocalDate.now().atStartOfDay().minusDays(90))))
        .orderBy(notification.id.desc());

    List<NotificationVO> content = jpaQuery
        .offset(dto.getPageNumber() * dto.getRowCount())
        .limit(dto.getRowCount() + 1)
        .fetch();

    boolean hasNext = false;
    if (content.size() > dto.getRowCount()) {
      content.remove(dto.getRowCount());
      hasNext = true;
    }

    return new SliceImpl<>(content, dto.toPageable(), hasNext);
  }
}
