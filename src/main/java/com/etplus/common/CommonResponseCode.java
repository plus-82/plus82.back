package com.etplus.common;

import lombok.Getter;

@Getter
public enum CommonResponseCode implements ResponseCode {
  SUCCESS("success"),
  FAIL("fail"),
  ;

  private String message;

  CommonResponseCode(String message) {
    this.message = message;
  }

}
