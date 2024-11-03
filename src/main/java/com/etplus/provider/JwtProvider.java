package com.etplus.provider;

import com.etplus.common.LoginUser;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.repository.domain.code.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  @Value("${jwt.key}")
  private String JWT_SECRET_KEY;
  @Value("${jwt.expiration}")
  private long TOKEN_VALID_TIME;

  // TODO refreshToken 까지 포함한 DTO 반환
  public String generateToken(LoginUser loginUser) {
    Date now = new Date();

    return Jwts.builder()
        .claim("id", loginUser.userId())
        .claim("name", loginUser.email())
        .claim("role", loginUser.roleType())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME))
        .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY)
        .compact();
  }

  public LoginUser decrypt(String jwtToken) throws AuthException {
    try {
      Claims body = Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build()
          .parseClaimsJws(jwtToken).getBody();

      return new LoginUser(
          body.get("id", Long.class),
          body.get("name", String.class),
          RoleType.valueOf(body.get("role", String.class))
      );
    } catch (ExpiredJwtException e) {
      throw new AuthException(AuthExceptionCode.EXPIRED_TOKEN);
    } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      throw new AuthException(AuthExceptionCode.INVALID_TOKEN_TYPE);
    } catch (Exception e) {
      throw new AuthException(AuthExceptionCode.INVALID_TOKEN);
    }
  }

}
