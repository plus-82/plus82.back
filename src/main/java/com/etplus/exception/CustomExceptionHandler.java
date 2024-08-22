package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.CustomUnauthorizedException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

  /**
   * Custom Exception Handlers
   */
  @ExceptionHandler(value = CustomBadRequestException.class)
  public ResponseEntity<Object> handleClientException(CustomBadRequestException e) {
    log.warn("handleClientException : {}", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse<>(e.getResponseCode()));
  }

  @ExceptionHandler(value = CustomUnauthorizedException.class)
  public ResponseEntity<Object> handleCustomUnauthorizedException(CustomUnauthorizedException e) {
    log.warn("handleCustomUnauthorizedException : {}", e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonResponse(e.getResponseCode()));
  }

  /**
   * Spring Exception Handlers
   */
  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
    log.info("handleValidationException {}", e);
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse<>(errors, e.getBody().getDetail()));
  }

  /**
   * Unknown Exception Handlers
   */
  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Object> handleUnknownException(Exception e) {
    log.error("handleUnknownException : {}", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CommonResponse<>(CommonResponseCode.FAIL));
  }
}
