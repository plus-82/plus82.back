package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record FeedVO(
    Long id,
    String content,
    LocalDateTime createdAt,
    String imagePath,
    Long commentCount,
    Long likeCount,
    boolean isLiked,
    boolean isCommented
) {

  @QueryProjection
  public FeedVO {
  }
} 