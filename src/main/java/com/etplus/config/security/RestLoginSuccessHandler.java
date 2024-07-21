package com.etplus.config.security;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.provider.JwtProvider;
import com.etplus.vo.TokenVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    TokenVO tokenVO = new TokenVO(jwtProvider.generateToken(loginUser));

    jsonConverter.write(new CommonResponse<>(tokenVO, CommonResponseCode.SUCCESS),
        MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
  }
}
