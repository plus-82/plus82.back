package com.etplus.vo;

import com.querydsl.core.annotations.QueryProjection;

public record CountryVO(
    Long id,
    String countryNameEn,
    String countryNameLocal,
    String countryCode,
    String countryCallingCode,
    String flag
) {

  @QueryProjection
  public CountryVO {
  }
}
