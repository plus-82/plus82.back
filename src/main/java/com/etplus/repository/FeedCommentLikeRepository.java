package com.etplus.repository;

import com.etplus.repository.domain.FeedCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedCommentLikeRepository extends JpaRepository<FeedCommentLike, Long> {

  Optional<FeedCommentLike> findByFeedCommentIdAndUserId(Long feedCommentId, Long userId);
  
  boolean existsByFeedCommentIdAndUserId(Long feedCommentId, Long userId);
  
} 