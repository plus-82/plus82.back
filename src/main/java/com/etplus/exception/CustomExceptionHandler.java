package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.CustomForbiddenException;
import com.etplus.common.CustomUnauthorizedException;
import com.etplus.exception.FileException.FileExceptionCode;
import com.etplus.exception.InvalidInputValueException.InvalidInputValueExceptionCode;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

  @ExceptionHandler(value = CustomForbiddenException.class)
  public ResponseEntity<Object> handleCustomForbiddenException(CustomForbiddenException e) {
    log.warn("handleCustomForbiddenException : {}", e);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CommonResponse(e.getResponseCode()));
  }

  /**
   * Spring Exception Handlers
   */
  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
    // @Valid 유효성 검사 실패 시
    log.info("handleValidationException {}", e);
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new CommonResponse<>(errors, InvalidInputValueExceptionCode.INVALID_INPUT_VALUE));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
    // 없는 url로 요청이 들어왔을 때
    log.info("handleNoResourceFoundException : {}", e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    // json 형식이 잘못된 경우
    log.info("handleHttpMessageNotReadableException : {}", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new CommonResponse<>(InvalidInputValueExceptionCode.INVALID_INPUT_VALUE));
  }

  @ExceptionHandler({MaxUploadSizeExceededException.class})
  public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    // 파일 업로드 사이즈 초과
    log.info("handleMaxUploadSizeExceededException : {}", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new CommonResponse<>(FileExceptionCode.FILE_SIZE_EXCEEDED));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
    // 지원하지 않는 미디어 타입
    log.info("handleHttpMediaTypeNotSupportedException : {}", e);
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body(new CommonResponse("Content-Type is not supported"));
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
