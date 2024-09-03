package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum UserExceptionCode implements ResponseCode {
    USED_EMAIL("already used email"),
    ;

    private String message;
  }

  public UserException(ResponseCode responseCode) {
    super(responseCode);
  }
}
