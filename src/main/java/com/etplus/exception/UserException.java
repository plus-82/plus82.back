package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum UserExceptionCode implements ResponseCode {
    ALREADY_USED_EMAIL("already used email"),
    NOT_VERIFIED_EMAIL("not verified email"),
    ;

    private String message;
  }

  public UserException(ResponseCode responseCode) {
    super(responseCode);
  }
}
