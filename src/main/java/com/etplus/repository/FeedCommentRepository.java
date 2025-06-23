package com.etplus.repository;

import com.etplus.repository.domain.FeedCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedCommentEntity, Long> {

} 