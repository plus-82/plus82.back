package com.etplus.exception;

import com.etplus.common.CustomForbiddenException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResourceDeniedException extends CustomForbiddenException {

  @AllArgsConstructor
  @Getter
  public enum ResourceDeniedExceptionCode implements ResponseCode {
    ACCESS_DENIED("RDE-001", "access denied"),
    INVALID_ROLE("RDE-002", "invalid role"),
    ;

    private String code;
    private String message;
  }

  public ResourceDeniedException(ResponseCode responseCode) {
    super(responseCode);
  }
}
