package com.etplus.repository;

import com.etplus.repository.domain.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {

}
