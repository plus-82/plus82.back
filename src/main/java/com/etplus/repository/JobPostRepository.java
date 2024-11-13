package com.etplus.repository;

import com.etplus.repository.domain.JobPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends JpaRepository<JobPostEntity, Long>,
    JobPostRepositoryCustom {

}
