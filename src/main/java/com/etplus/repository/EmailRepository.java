package com.etplus.repository;

import com.etplus.repository.domain.EmailEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {

  Optional<EmailEntity> findByCode(String code);

}
