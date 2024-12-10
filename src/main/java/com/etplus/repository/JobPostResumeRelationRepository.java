package com.etplus.repository;

import com.etplus.repository.domain.JobPostResumeRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostResumeRelationRepository extends JpaRepository<JobPostResumeRelationEntity, Long> {

  boolean existsByJobPostIdAndResumeId(Long jobPostId, Long resumeId);

}
