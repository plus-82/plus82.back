package com.etplus.repository;

import com.etplus.repository.domain.ResumeContactEntity;
import com.etplus.repository.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeContactRepository extends JpaRepository<ResumeContactEntity, Long>,
    ResumeContactRepositoryCustom {

  boolean existsByResumeIdAndAcademyUser(long resumeId, UserEntity academyUser);
}
