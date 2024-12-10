package com.etplus.service;

import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.JobPostResumeRelationVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobPostResumeRelationService {

  private final JobPostResumeRelationRepository jobPostResumeRelationRepository;

  public List<JobPostResumeRelationVO> getAllJobPostResumeRelations(RoleType roleType, long userId) {

    if (RoleType.TEACHER.equals(roleType)) {
//      return jobPostResumeRelationRepository.findAllJobPostResumeRelationsByTeacherId(userId);
    } else if (RoleType.ACADEMY.equals(roleType)) {
//      return jobPostResumeRelationRepository.findAllJobPostResumeRelationsByAcademyId(userId);
    }
    return null;
  }


}
