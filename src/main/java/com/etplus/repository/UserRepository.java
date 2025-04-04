package com.etplus.repository;

import java.util.Optional;

import com.etplus.repository.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

  Optional<UserEntity> findByIdAndDeletedIsFalse(Long id);
  Optional<UserEntity> findByEmailAndDeletedIsFalse(String email);
  Optional<UserEntity> findByEmail(String email);
  boolean existsByEmail(String email);

}
