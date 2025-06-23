package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class FeedException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum FeedExceptionCode implements ResponseCode {
    ALREADY_LIKED_FEED("FED-001", "already liked feed"),
    ;

    private String code;
    private String message;
  }

  public FeedException(ResponseCode responseCode) {
    super(responseCode);
  }
}
