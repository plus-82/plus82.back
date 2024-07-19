package com.etplus.provider;

import com.etplus.repository.domain.code.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
        .claim("id", loginUser.getUserId())
        .claim("name", loginUser.getEmail())
        .claim("role", loginUser.getRoleType())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME))
        .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY)
        .compact();
  }

//  public AuthUser decrypt(String jwtToken) {
//    try {
//      Claims body = Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build()
//          .parseClaimsJws(jwtToken).getBody();
//      return new AuthUser(body.get("id", Long.class), body.get("name", String.class));
//    } catch (ExpiredJwtException e) {
//      throw new CustomUnauthorizedException(UnauthorizedExceptionCode.EXPIRED_TOKEN);
//    } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
//      throw new CustomUnauthorizedException(UnauthorizedExceptionCode.INVALID_TOKEN_TYPE);
//    } catch (Exception e) {
//      throw new CustomUnauthorizedException(UnauthorizedExceptionCode.UNAUTHORIZED);
//    }
//  }

}
