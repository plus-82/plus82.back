package com.etplus.config.security;

import com.etplus.provider.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
  private final JwtProvider jwtProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    LoginUser loginUser = (LoginUser) authentication.getPrincipal();

    String accessToken = jwtProvider.generateToken(loginUser.getUserId(), loginUser.getEmail(),
        loginUser.getRoleType());

    // TODO CommonResponse
    jsonConverter.write(Map.of("accessToken", accessToken),
        MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
  }
}
