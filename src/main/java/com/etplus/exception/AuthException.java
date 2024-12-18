package com.etplus.exception;

import com.etplus.common.CustomUnauthorizedException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthException extends CustomUnauthorizedException {

  @AllArgsConstructor
  @Getter
  public enum AuthExceptionCode implements ResponseCode {
    TOKEN_NOT_FOUND("AE-001", "token not found"),
    EMAIL_NOT_CORRECT("AE-002", "check your email"),
    PW_NOT_CORRECT("AE-003", "check your password"),
    EXPIRED_TOKEN("AE-004", "expired token"),
    INVALID_TOKEN_TYPE("AE-005", "invalid token type"),
    INVALID_TOKEN("AE-006", "invalid token"),
    DELETED_USER("AE-007", "deleted user"),
    ACCESS_DENIED("AE-008", "access denied"),
    ;

    private String code;
    private String message;
  }

  public AuthException(ResponseCode responseCode) {
    super(responseCode);
  }
}
