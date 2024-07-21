package com.etplus.common;

public record CommonResponse<T>(
    T data,
    String message
) {

  public CommonResponse(T data, ResponseCode responseCode) {
    this(data, responseCode.getMessage());
  }

  public CommonResponse(ResponseCode responseCode) {
    this(null, responseCode.getMessage());
  }
}
