package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FeedDetailVO(
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
    boolean isCommented,

    // comments
    List<CommentVO> comments
) {

  @Builder
  public record CommentVO(
      Long id,
      String comment,
      LocalDateTime createdAt,
      int likeCount,

      Long userId,
      String userName,
      boolean isLiked
  ) {

    @QueryProjection
    public CommentVO {
    }
  }

}
