package com.etplus.config.security;

import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  // https://velog.io/@dltkdgns3435/%EC%8A%A4%ED%94%84%EB%A7%81%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0-JWT-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = parseJwt(request);

    if (token == null) {
      request.setAttribute("exception", AuthExceptionCode.TOKEN_NOT_FOUND);
    } else {
      try {
        LoginUser loginUser = jwtProvider.decrypt(token);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginUser, null,
                loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      } catch (AuthException e) {
        request.setAttribute("exception", e.getResponseCode());
      } catch (Exception e) {
        request.setAttribute("exception", AuthExceptionCode.INVALID_TOKEN);
      }
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")) {
      return headerAuth.substring(7);
    }
    return null;
  }
}
