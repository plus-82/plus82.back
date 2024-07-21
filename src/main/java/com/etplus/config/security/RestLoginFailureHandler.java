package com.etplus.config.security;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.exception.AuthExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    CommonResponse<?> responseData;
    if (exception instanceof BadCredentialsException) {
      responseData = new CommonResponse<>(AuthExceptionCode.ID_OR_PW_NOT_CORRECT);
    } else {
      log.error("Authentication Unknown Exception", exception);
      responseData = new CommonResponse<>(CommonResponseCode.FAIL);
    }

    response.setStatus(HttpStatus.BAD_REQUEST.value());
    jsonConverter.write(responseData, MediaType.APPLICATION_JSON,
        new ServletServerHttpResponse(response));
  }

}