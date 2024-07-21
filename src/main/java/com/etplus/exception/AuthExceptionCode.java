package com.etplus.exception;

import com.etplus.common.ResponseCode;
import lombok.Getter;

@Getter
public enum AuthExceptionCode implements ResponseCode {
  NOT_AUTHENTICATED("not authenticated"),
  ID_OR_PW_NOT_CORRECT("id or password is wrong"),
  ;

  private String message;

  AuthExceptionCode(String message) {
    this.message = message;
  }

}