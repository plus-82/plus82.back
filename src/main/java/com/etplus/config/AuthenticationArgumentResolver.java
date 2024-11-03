package com.etplus.config;

import com.etplus.common.AuthUser;
import com.etplus.common.LoginUser;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

  private final JwtProvider jwtProvider;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean hasAnnotation = parameter.hasParameterAnnotation(AuthUser.class);
    boolean isLoginUserType = LoginUser.class.isAssignableFrom(parameter.getParameterType());

    return hasAnnotation && isLoginUserType;
  }

  @Override
  public LoginUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    String token = parseJwt(webRequest);
    return jwtProvider.decrypt(token);
  }

  private String parseJwt(NativeWebRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")) {
      return headerAuth.substring(7);
    } else {
      throw new AuthException(AuthExceptionCode.TOKEN_NOT_FOUND);
    }
  }

}