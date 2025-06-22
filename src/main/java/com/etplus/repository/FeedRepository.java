package com.etplus.repository;

import com.etplus.repository.domain.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedRepository extends JpaRepository<FeedEntity, Long> {

  Optional<FeedEntity> findByIdAndDeletedIsFalse(Long id);
  
}
