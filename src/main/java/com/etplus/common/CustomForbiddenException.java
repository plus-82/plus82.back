package com.etplus.common;

public abstract class CustomForbiddenException extends RuntimeException {

  private final ResponseCode responseCode;

  protected CustomForbiddenException(ResponseCode responseCode) {
    super(responseCode.getMessage());
    this.responseCode = responseCode;
  }

  public ResponseCode getResponseCode() {
    return responseCode;
  }

}
