package com.etplus.repository.domain;

import com.etplus.repository.domain.code.FeedVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "feed")
public class FeedEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FeedVisibility feedVisibility;

  @Column(nullable = false)
  private boolean deleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_user_id", referencedColumnName = "id", nullable = false)
  private UserEntity createdUser;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id", referencedColumnName = "id")
  private FileEntity image;

  public FeedEntity(String content, FeedVisibility feedVisibility, UserEntity createdUser,
      FileEntity image) {
    this.content = content;
    this.feedVisibility = feedVisibility;
    this.deleted = false;
    this.createdUser = createdUser;
    this.image = image;
  }
}
