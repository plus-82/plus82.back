package com.etplus.repository;

import com.etplus.repository.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>,
    NotificationRepositoryCustom {

}
