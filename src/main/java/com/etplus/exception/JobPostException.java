package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class JobPostException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum JobPostExceptionCode implements ResponseCode {
    JOB_POST_CLOSED("JP-001", "job post closed"),
    RESUME_ALREADY_SUBMITTED("JP-002", "resume already submitted"),
    ;

    private String code;
    private String message;
  }

  public JobPostException(ResponseCode responseCode) {
    super(responseCode);
  }
}
