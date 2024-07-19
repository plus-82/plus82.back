package com.etplus.config;

import com.etplus.repository.domain.code.RoleType;
import java.io.Serial;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

// UserDetails 상속받은 클래스
@Getter
public class LoginUser extends org.springframework.security.core.userdetails.User {

  @Serial
  private static final long serialVersionUID = 1L;
  private final Long userId;
  private final String email;
  private final RoleType roleType;

  public LoginUser(Long userId, String email, String password, RoleType roleType) {
    super(email, password, List.of(new SimpleGrantedAuthority("ROLE_" + roleType.toString().toUpperCase())));
    this.userId = userId;
    this.email = email;
    this.roleType = roleType;
  }

}