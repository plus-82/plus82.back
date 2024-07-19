package com.etplus.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Value("${api-path.sign-in}")
  private String SECURITY_SIGN_IN_URI;

  private final CustomAuthenticationProvider customAuthenticationProvider;
  private final ObjectPostProcessor<Object> objectPostProcessor;
  private final RestLoginSuccessHandler restLoginSuccessHandler;
  private final RestLoginFailureHandler restLoginFailureHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        // JWT로 인증하여 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))
        // 로그인 Filter 추가
        .addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        // TODO 검증 필터 추가
        .authorizeHttpRequests(authorize -> authorize
            // TODO 범위 설정, Swagger 설정
//            .requestMatchers("/api/v1/login").permitAll()
//            .requestMatchers("/api/v1/**").authenticated()
            .anyRequest().permitAll()
        )
    ;
    return http.build();
  }

  // 로그인 API Filter 등록
  private UsernamePasswordAuthenticationFilter customAuthenticationFilter() throws Exception {
    UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
    filter.setFilterProcessesUrl(SECURITY_SIGN_IN_URI);
    filter.setUsernameParameter("email");
    filter.setPasswordParameter("password");

    AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
    builder.authenticationProvider(customAuthenticationProvider);
    filter.setAuthenticationManager(builder.build());

    filter.setAuthenticationSuccessHandler(restLoginSuccessHandler);
    filter.setAuthenticationFailureHandler(restLoginFailureHandler);
    return filter;
  }

}