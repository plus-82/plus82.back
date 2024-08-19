package com.etplus.config.security;

import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 조회만 담당하는 클래스
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userJpaRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = userJpaRepository.findByEmail(email).orElse(null);

    if (user != null) {
      if (!user.isVerified()) {
        throw new UsernameNotFoundException("need to verify your email");
      }

      // UserDetails에 담아서 return하면 AutneticationManager가 검증
      return new LoginUser(user.getId(), user.getEmail(), user.getPassword(), user.getRoleType(),
          user.isVerified());
    }

    return null;
  }
}