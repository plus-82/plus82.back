package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record NotificationVO(
    Long id,
    String title,
    String titleEn,
    String content,
    String contentEn,
    String targetUrl,
    LocalDateTime createdAt
) {

  @QueryProjection
  public NotificationVO {
  }
}
