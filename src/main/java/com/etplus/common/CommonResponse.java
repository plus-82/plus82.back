package com.etplus.common;

public record CommonResponse<T>(
    T data,
    String code,
    String message
) {

  public CommonResponse(T data, ResponseCode responseCode) {
    this(data, responseCode.getCode(), responseCode.getMessage());
  }

  public CommonResponse(ResponseCode responseCode) {
    this(null, responseCode.getCode(), responseCode.getMessage());
  }
}
