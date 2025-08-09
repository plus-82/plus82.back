package com.etplus.repository.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "report")
public class ReportEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "VARCHAR(100)", nullable = false)
  private String reason;

  @Column(columnDefinition = "VARCHAR(1000)")
  private String otherReason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false)
  private UserEntity reporter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feed_id", referencedColumnName = "id")
  private FeedEntity feed;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id", referencedColumnName = "id")
  private FeedCommentEntity comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private UserEntity user;

  public ReportEntity(String reason, String otherReason, UserEntity reporter,
      FeedEntity feed) {
    this.reason = reason;
    this.otherReason = otherReason;
    this.reporter = reporter;
    this.feed = feed;
  }

  public ReportEntity(String reason, String otherReason, UserEntity reporter,
      FeedCommentEntity comment) {
    this.reason = reason;
    this.otherReason = otherReason;
    this.reporter = reporter;
    this.comment = comment;
  }

  public ReportEntity(String reason, String otherReason, UserEntity reporter,
      UserEntity user) {
    this.reason = reason;
    this.otherReason = otherReason;
    this.reporter = reporter;
    this.user = user;
  }
} 