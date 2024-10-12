package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AcademyException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum AcademyExceptionCode implements ResponseCode {
    ALREADY_USED_BUSINESS_REGISTRATION_NUMBER("ACE-001","already used business registration number"),
    ;

    private String code;
    private String message;
  }

  public AcademyException(ResponseCode responseCode) {
    super(responseCode);
  }
}
