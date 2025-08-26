package com.etplus.vo;

import com.etplus.repository.domain.code.FeedVisibility;
import com.etplus.vo.common.ImageVO;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FeedDetailVO(
    Long id,
    FeedVisibility feedVisibility,
    String content,
    LocalDateTime createdAt,

    // creator
    Long creatorId,
    String creatorName,
    String creatorProfileImagePath,

    int commentCount,
    int likeCount,
    boolean isLiked,
    boolean isCommented,

    ImageVO image,

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
      boolean isLiked,

      // file
      String profileImagePath
  ) {

    @QueryProjection
    public CommentVO {
    }
  }

}
