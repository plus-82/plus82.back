package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record FeedVO(
    Long id,
    String content,
    LocalDateTime createdAt,

    // creator
    String creatorFirstName,
    String creatorLastName,
    String creatorFullName,
    String creatorProfileImagePath,

    // image
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