package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResourceNotFoundException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum ResourceNotFoundExceptionCode implements ResponseCode {
    EMAIL_VERIFICATION_CODE_NOT_FOUND("email verification code not found"),
    USER_NOT_FOUND("user not found"),
    ;

    private String message;
  }

  public ResourceNotFoundException(ResponseCode responseCode) {
    super(responseCode);
  }

}
