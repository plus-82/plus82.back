package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class InvalidInputValueException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum InvalidInputValueExceptionCode implements ResponseCode {
    INVALID_INPUT_VALUE("IIV-001", "invalid input value"),
    ;

    private String code;
    private String message;
  }

  public InvalidInputValueException(ResponseCode responseCode) {
    super(responseCode);
  }
}
