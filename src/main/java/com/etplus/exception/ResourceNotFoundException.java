package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResourceNotFoundException extends CustomBadRequestException {

  @AllArgsConstructor
  @Getter
  public enum ResourceNotFoundExceptionCode implements ResponseCode {
    EMAIL_VERIFICATION_CODE_NOT_FOUND("RNF-001", "email verification code not found"),
    USER_NOT_FOUND("RNF-002", "user not found"),
    COUNTRY_NOT_FOUND("RNF-003", "country not found"),
    ACADEMY_NOT_FOUND("RNF-004", "academy not found"),
    JOB_POST_NOT_FOUND("RNF-005", "job post not found"),
    RESUME_NOT_FOUND("RNF-006", "resume not found"),
    JOB_POST_RESUME_RELATION_NOT_FOUND("RNF-007", "job post resume relation not found"),
    FEED_NOT_FOUND("RNF-008", "feed not found"),
    FEED_LIKE_NOT_FOUND("RNF-009", "feed like not found"),
    FEED_COMMENT_NOT_FOUND("RNF-010", "feed comment not found"),
    FEED_COMMENT_LIKE_NOT_FOUND("RNF-011", "feed comment like not found"),
    ;

    private String code;
    private String message;
  }

  public ResourceNotFoundException(ResponseCode responseCode) {
    super(responseCode);
  }

}
