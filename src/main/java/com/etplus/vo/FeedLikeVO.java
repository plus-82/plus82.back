package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record FeedLikeVO(
    long id,
    LocalDateTime createdAt,

    long userId,
    String name,
    String profileImagePath
) {

  @QueryProjection
  public FeedLikeVO {
  }
}
