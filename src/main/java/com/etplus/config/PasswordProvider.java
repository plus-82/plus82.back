package com.etplus.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordProvider {

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public PasswordProvider() {
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
  }

  public String encode(String rawPassword) {
    return bCryptPasswordEncoder.encode(rawPassword);
  }

  public boolean matches(String rawPassword, String encodedPassword) {
    return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
  }
}
