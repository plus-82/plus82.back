package com.etplus.repository;

import com.etplus.repository.domain.QNotificationEntity;
import com.etplus.vo.NotificationVO;
import com.etplus.vo.QNotificationVO;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

  private final JPAQueryFactory query;
  private QNotificationEntity notification;

  public NotificationRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    notification = new QNotificationEntity("notification");
  }

  @Override
  public List<NotificationVO> findAllNotificationsByUserId(long userId) {
    JPAQuery<NotificationVO> jpaQuery = query.select(
            new QNotificationVO(
                notification.id,
                notification.title,
                notification.titleEn,
                notification.content,
                notification.contentEn
            ))
        .from(notification)
        .where(notification.user.id.eq(userId)
            .and(notification.createdAt.after(LocalDate.now().atStartOfDay().minusDays(90))))
        .orderBy(notification.id.desc());

    return jpaQuery.fetch();
  }
}
