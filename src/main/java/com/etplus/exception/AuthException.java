package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum AuthExceptionCode implements ResponseCode {
    NOT_AUTHENTICATED("not authenticated"),
    ACCESS_DENIED("access denied"),
    TOKEN_NOT_FOUND("token not found"),
    ID_OR_PW_NOT_CORRECT("id or password is wrong"),
    EXPIRED_TOKEN("expired token"),
    INVALID_TOKEN_TYPE("invalid token type"),
    INVALID_TOKEN("invalid token"),
    ;

    private String message;
  }

  public AuthException(ResponseCode responseCode) {
    super(responseCode);
  }
}
