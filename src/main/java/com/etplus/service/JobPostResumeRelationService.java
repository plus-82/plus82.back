package com.etplus.service;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.EmailProvider;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.NotificationRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.NotificationEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.repository.domain.code.MessageTemplateType;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.JobPostResumeRelationDetailVO;
import com.etplus.vo.JobPostResumeRelationSummaryVO;
import com.etplus.vo.JobPostResumeRelationVO;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class JobPostResumeRelationService {

  private final JobPostResumeRelationRepository jobPostResumeRelationRepository;
  private final AcademyRepository academyRepository;
  private final NotificationRepository notificationRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final EmailProvider emailProvider;

  public Page<JobPostResumeRelationVO> getAllJobPostResumeRelations(RoleType roleType, long userId, SearchJobPostResumeRelationDTO dto) {
    if (RoleType.TEACHER.equals(roleType)) {
      return jobPostResumeRelationRepository.findAllJobPostResumeRelationsByTeacher(dto, userId);
    } else if (RoleType.ACADEMY.equals(roleType)) {
      AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));
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
      AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

      if (jobPostResumeRelationEntity.getJobPost().getAcademy().getId() != academy.getId()) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }
    } else {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
    return JobPostResumeRelationDetailVO.valueOf(jobPostResumeRelationEntity);
  }

  public JobPostResumeRelationDetailVO getJobPostResumeRelationByCode(String code) {
    JobPostResumeRelationEntity jobPostResumeRelationEntity = jobPostResumeRelationRepository
        .findByCode(code).orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_RESUME_RELATION_NOT_FOUND));

    // 마감일 +2주 까지 조회 가능
    LocalDate dueDate = jobPostResumeRelationEntity.getJobPost().getDueDate();
    if (dueDate == null) {
      if (LocalDate.now().isAfter(dueDate.plusWeeks(2))) {
        throw new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_RESUME_RELATION_NOT_FOUND);
      }
    }
    return JobPostResumeRelationDetailVO.valueOf(jobPostResumeRelationEntity);
  }

  @Transactional
  public void updateJobPostResumeRelationStatus(long jobPostResumeRelationId, JobPostResumeRelationStatus status, long userId) {
    JobPostResumeRelationEntity jobPostResumeRelation = jobPostResumeRelationRepository.findById(
        jobPostResumeRelationId).orElseThrow(() -> new ResourceNotFoundException(
        ResourceNotFoundExceptionCode.JOB_POST_RESUME_RELATION_NOT_FOUND));

    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

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

    UserEntity teacher = jobPostResumeRelation.getUser();

    // 이메일 템플릿 조회 & 파싱 & 발송
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "JOB_POST_STATUS_" + status, MessageTemplateType.EMAIL).orElse(null);

    Map params = new HashMap();
    params.put("name", teacher.getFirstName() + " " + teacher.getLastName());
    params.put("jobTitle", jobPostResumeRelation.getJobPost().getTitle());
    params.put("academyName", academy.getName());
    params.put("link", "https://plus82.co/my-page");

    StringSubstitutor sub = new StringSubstitutor(params);
    String emailTitle = sub.replace(emailTemplate.getTitle());
    String emailContent = sub.replace(emailTemplate.getContent());

    emailProvider.send(teacher.getEmail(), emailTitle, emailContent);

    // 선생님 알림 목록 추가
    String title = "", titleEn = "", content = "", contentEn = "";
    switch (status) {
      case REVIEWED -> {
        title = "서류합격";
        titleEn = "Reviewed";
        content = String.format("%s에 제출한 이력서에 업데이트가 있습니다", academy.getName());
        contentEn = String.format("Update on your resume at %s", academy.getNameEn());
      }
      case ACCEPTED -> {
        title = "최종합격";
        titleEn = "Accepted";
        content = String.format("%s의 %s 포지션에 최종 합격했습니다",
            academy.getName(), jobPostResumeRelation.getJobPost().getTitle());
        contentEn = String.format("Final acceptance for %s at %s",
            jobPostResumeRelation.getJobPost().getTitle(), academy.getNameEn());
      }
      case REJECTED -> {
        title = "불합격";
        titleEn = "Rejected";
        content = String.format("%s의 %s 포지션에 안타깝게도 불합격했습니다",
            academy.getName(), jobPostResumeRelation.getJobPost().getTitle());
        contentEn = String.format("Unfortunately, rejected for %s at %s",
            jobPostResumeRelation.getJobPost().getTitle(), academy.getNameEn());
      }
    }
    notificationRepository.save(new NotificationEntity(null, title, titleEn, content, contentEn, teacher));

    jobPostResumeRelation.setStatus(status);
    jobPostResumeRelationRepository.save(jobPostResumeRelation);
  }

  public JobPostResumeRelationSummaryVO getJobPostResumeRelationSummary(RoleType roleType, long userId) {
    if (RoleType.TEACHER.equals(roleType)) {
      return jobPostResumeRelationRepository.getJobPostResumeRelationSummaryByTeacher(userId);
    } else if (RoleType.ACADEMY.equals(roleType)) {
      AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

      return jobPostResumeRelationRepository.getJobPostResumeRelationSummaryByAcademy(academy.getId());
    } else {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
  }

}
