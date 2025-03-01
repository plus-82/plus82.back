package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;

public record NotificationVO(
    Long id,
    String title,
    String titleEn,
    String content,
    String contentEn
) {

  @QueryProjection
  public NotificationVO {
  }
}
