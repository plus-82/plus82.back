package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class EmailVerificationException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum EmailVerificationExceptionCode implements ResponseCode {
    TOO_MANY_REQUEST("too many request"),
    ;

    private String message;
  }

  public EmailVerificationException(ResponseCode responseCode) {
    super(responseCode);
  }

}
