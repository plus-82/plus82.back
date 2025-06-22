package com.etplus.repository.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(
    name = "feed_comment_like",
    uniqueConstraints = @UniqueConstraint(columnNames = {"feed_comment_id", "user_id"})
)
public class FeedCommentLike extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private UserEntity user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feed_comment_id", referencedColumnName = "id", nullable = false)
  private FeedCommentEntity feedComment;

  public FeedCommentLike(Long id, UserEntity user, FeedCommentEntity feedComment) {
    this.id = id;
    this.user = user;
    this.feedComment = feedComment;
  }
}

