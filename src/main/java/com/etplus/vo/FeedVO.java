package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record FeedVO(
    Long id,
    String content,
    LocalDateTime createdAt,

    // creator
    String creatorName,
    String creatorProfileImagePath,

    // image
    String imagePath,

    int commentCount,
    int likeCount,
    boolean isLiked,
    boolean isCommented
) {

  @QueryProjection
  public FeedVO {
  }
} 