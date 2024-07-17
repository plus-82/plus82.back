package com.etplus.user.domain;

import com.etplus.user.domain.code.RoleType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

  private Long id;
  private String nickName;
  private String email;
  private String password;
  private RoleType role;

  @Builder(access = AccessLevel.PRIVATE)
  public User(Long id, String nickName, String email, String password, RoleType role) {
    this.id = id;
    this.nickName = nickName;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public static User create(String nickName, String email, String password, RoleType role) {
    return User.builder()
        .nickName(nickName)
        .email(email)
        .password(password)
        .role(role)
        .build();
  }

}
