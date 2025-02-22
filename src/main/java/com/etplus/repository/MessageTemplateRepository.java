package com.etplus.repository;

import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.code.MessageTemplateType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplateEntity, Long> {

  Optional<MessageTemplateEntity> findByCodeAndType(String code, MessageTemplateType type);

}
