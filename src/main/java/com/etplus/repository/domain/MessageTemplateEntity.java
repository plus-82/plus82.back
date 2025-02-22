package com.etplus.repository.domain;

import com.etplus.repository.domain.code.MessageTemplateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "message_template",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "type"}),
    }
)
public class MessageTemplateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String code;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MessageTemplateType type;
  @Column(nullable = false)
  private String title;
  private String description;
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  public MessageTemplateEntity(Long id, String code, MessageTemplateType type, String title,
      String description, String content) {
    this.id = id;
    this.code = code;
    this.type = type;
    this.title = title;
    this.description = description;
    this.content = content;
  }
}