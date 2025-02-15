package com.etplus.service;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.JobPostResumeRelationDetailVO;
import com.etplus.vo.JobPostResumeRelationSummaryVO;
import com.etplus.vo.JobPostResumeRelationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public JobPostResumeRelationDetailVO getJobPostResumeRelation(RoleType roleType, long userId, long jobPostResumeRelationId) {
    JobPostResumeRelationEntity jobPostResumeRelationEntity = jobPostResumeRelationRepository
        .findById(jobPostResumeRelationId).orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_RESUME_RELATION_NOT_FOUND));

    if (RoleType.TEACHER.equals(roleType)) {
      if(jobPostResumeRelationEntity.getUser().getId() != userId) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }

    } else if (RoleType.ACADEMY.equals(roleType)) {
      UserEntity user = userRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.USER_NOT_FOUND));
      AcademyEntity academy = user.getAcademy();

      if (jobPostResumeRelationEntity.getJobPost().getAcademy().getId() != academy.getId()) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }
    } else {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
    return JobPostResumeRelationDetailVO.valueOf(jobPostResumeRelationEntity);
  }

  @Transactional
  public void updateJobPostResumeRelationStatus(long jobPostResumeRelationId, JobPostResumeRelationStatus status, long userId) {
    JobPostResumeRelationEntity jobPostResumeRelation = jobPostResumeRelationRepository.findById(
        jobPostResumeRelationId).orElseThrow(() -> new ResourceNotFoundException(
        ResourceNotFoundExceptionCode.JOB_POST_RESUME_RELATION_NOT_FOUND));

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    AcademyEntity academy = user.getAcademy();
    if (academy == null) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // 요청한 사용자 학원의 공고인지 확인
    if (jobPostResumeRelation.getJobPost().getAcademy().getId() != academy.getId()) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // 종료된 상태는 변경할 수 없음
    JobPostResumeRelationStatus currentStatus = jobPostResumeRelation.getStatus();
    if (currentStatus.equals(JobPostResumeRelationStatus.ACCEPTED)
        || currentStatus.equals(JobPostResumeRelationStatus.REJECTED)) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    jobPostResumeRelation.setStatus(status);
    jobPostResumeRelationRepository.save(jobPostResumeRelation);
  }

  public JobPostResumeRelationSummaryVO getJobPostResumeRelationSummary(RoleType roleType, long userId) {
    if (RoleType.TEACHER.equals(roleType)) {
      return jobPostResumeRelationRepository.getJobPostResumeRelationSummaryByTeacher(userId);
    } else if (RoleType.ACADEMY.equals(roleType)) {
      UserEntity user = userRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.USER_NOT_FOUND));
      AcademyEntity academy = user.getAcademy();

      if (academy == null) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }
      return jobPostResumeRelationRepository.getJobPostResumeRelationSummaryByAcademy(academy.getId());
    } else {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
  }

}
