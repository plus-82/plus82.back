package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class EmailVerificationCodeException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum EmailVerificationCodeExceptionCode implements ResponseCode {
    TOO_MANY_REQUEST("too many request"),
    ALREADY_VERIFIED_CODE("already verified code"),
    EXPIRED_CODE("expired code"),
    ;

    private String message;
  }

  public EmailVerificationCodeException(ResponseCode responseCode) {
    super(responseCode);
  }

}
