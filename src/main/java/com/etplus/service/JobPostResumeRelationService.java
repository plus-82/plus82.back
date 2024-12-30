package com.etplus.service;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.JobPostResumeRelationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobPostResumeRelationService {

  private final JobPostResumeRelationRepository jobPostResumeRelationRepository;
  private final UserRepository userRepository;

  public Page<JobPostResumeRelationVO> getAllJobPostResumeRelations(RoleType roleType, long userId, SearchJobPostResumeRelationDTO dto) {
    if (RoleType.TEACHER.equals(roleType)) {
      return jobPostResumeRelationRepository.findAllJobPostResumeRelationsByTeacher(dto, userId);
    } else if (RoleType.ACADEMY.equals(roleType)) {
      UserEntity user = userRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.USER_NOT_FOUND));
      AcademyEntity academy = user.getAcademy();

      if (academy == null) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }
      return jobPostResumeRelationRepository.findAllJobPostResumeRelationsByAcademy(dto, academy.getId());
    } else {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
  }

}
