package com.etplus.repository;

import com.etplus.repository.domain.FeedLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLikeEntity, Long>,
    FeedLikeRepositoryCustom {

  Optional<FeedLikeEntity> findByFeedIdAndUserId(Long feedId, Long userId);

  boolean existsByFeedIdAndUserId(Long feedId, Long userId);
}