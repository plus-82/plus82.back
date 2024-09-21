package com.etplus.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonResponseCode implements ResponseCode {
  SUCCESS("CM-001", "success"),
  FAIL("CM-002", "fail"),
  ;

  private String code;
  private String message;

}
