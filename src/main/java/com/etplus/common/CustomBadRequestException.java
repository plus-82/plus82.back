package com.etplus.common;

public abstract class CustomBadRequestException extends RuntimeException {

  private final ResponseCode responseCode;

  protected CustomBadRequestException(ResponseCode responseCode) {
    super(responseCode.getMessage());
    this.responseCode = responseCode;
  }

  public ResponseCode getResponseCode() {
    return responseCode;
  }

}