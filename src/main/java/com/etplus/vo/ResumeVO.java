package com.etplus.vo;

import com.etplus.repository.domain.code.VisaType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record ResumeVO(
    Long id,
    String title,
    String firstName,
    String lastName,
    String email,
    boolean hasVisa,
    VisaType visaType,
    boolean isRepresentative,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    String filePath,
    String fileName
) {

  @QueryProjection
  public ResumeVO {
  }
}
