package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum UserExceptionCode implements ResponseCode {
    ALREADY_USED_EMAIL("UE-001","already used email"),
    NOT_VERIFIED_EMAIL("UE-002","not verified email"),
    ;

    private String code;
    private String message;
  }

  public UserException(ResponseCode responseCode) {
    super(responseCode);
  }
}
