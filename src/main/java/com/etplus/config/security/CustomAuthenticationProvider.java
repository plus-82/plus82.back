package com.etplus.config.security;

import com.etplus.provider.PasswordProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final PasswordProvider passwordProvider;
  private final UserDetailsService userDetailsService;

  // 조회 후 User가 로그인에 성공해도 되는지 검증
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);

    if (loginUser == null) {
      log.error("EMAIL_NOT_CORRECT");
      throw new BadCredentialsException("check your email");
    }

    if (!passwordProvider.matches(password, loginUser.getPassword())) {
      log.error("PW_NOT_CORRECT");
      throw new BadCredentialsException("check your password");
    }

    return new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPassword(),
        loginUser.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
