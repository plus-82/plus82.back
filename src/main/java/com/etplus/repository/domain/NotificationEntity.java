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
@Table(name = "notification")
public class NotificationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 30)
  private String title;
  @Column(length = 30)
  private String titleEn;
  @Column(length = 100)
  private String content;
  @Column(length = 100)
  private String contentEn;
  @Column(length = 100)
  private String targetUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
  private UserEntity user;

  public NotificationEntity(Long id, String title, String titleEn, String content, String contentEn,
      String targetUrl, UserEntity user) {
    this.id = id;
    this.title = title;
    this.titleEn = titleEn;
    this.content = content;
    this.contentEn = contentEn;
    this.targetUrl = targetUrl;
    this.user = user;
  }
}
