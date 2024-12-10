package com.etplus.vo;

import com.etplus.repository.domain.code.VisaType;
import com.querydsl.core.annotations.QueryProjection;

public record ResumeVO(
    Long id,
    String title,
    String firstName,
    String lastName,
    String email,
    boolean hasVisa,
    VisaType visaType,
    boolean isRepresentative,
    boolean hasFile
) {

  @QueryProjection
  public ResumeVO {
  }
}
