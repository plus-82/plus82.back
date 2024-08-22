package com.etplus.common;

public abstract class CustomUnauthorizedException extends RuntimeException {

  private final ResponseCode responseCode;

  protected CustomUnauthorizedException(ResponseCode responseCode) {
    super(responseCode.getMessage());
    this.responseCode = responseCode;
  }

  public ResponseCode getResponseCode() {
    return responseCode;
  }

}