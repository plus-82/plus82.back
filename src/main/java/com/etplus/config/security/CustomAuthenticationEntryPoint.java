package com.etplus.config.security;

import com.etplus.common.CommonResponse;
import com.etplus.common.ResponseCode;
import com.etplus.exception.AuthException.AuthExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    AuthExceptionCode exceptionCode = (AuthExceptionCode) request.getAttribute("exception");

    if (exceptionCode == null) {
      setResponse(response, AuthExceptionCode.INVALID_TOKEN_TYPE);
    } else {
      setResponse(response, exceptionCode);
    }
  }

  private void setResponse(HttpServletResponse response, ResponseCode responseCode)
      throws IOException {
    CommonResponse<?> responseData = new CommonResponse<>(responseCode);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    jsonConverter.write(responseData, MediaType.APPLICATION_JSON,
        new ServletServerHttpResponse(response));
  }
}