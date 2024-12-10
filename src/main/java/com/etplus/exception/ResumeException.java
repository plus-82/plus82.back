package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResumeException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum ResumeExceptionCode implements ResponseCode {
    REPRESENTATIVE_RESUME_EXISTS("RSM-001", "representative resume already exists"),
    ;

    private String code;
    private String message;
  }

  public ResumeException(ResponseCode responseCode) {
    super(responseCode);
  }

}
