package com.etplus.repository;

import com.etplus.repository.domain.ResumeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Long>,
    ResumeRepositoryCustom {

  boolean existsByUserIdAndIsRepresentativeIsTrue(long userId);
  Optional<ResumeEntity> findByIdAndUserId(long resumeId, long userId);

}
