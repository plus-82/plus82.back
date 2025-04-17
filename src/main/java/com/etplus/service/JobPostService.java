package com.etplus.service;

import com.etplus.controller.dto.CreateJobPostDTO;
import com.etplus.controller.dto.SearchJobPostByAcademyDTO;
import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.controller.dto.SubmitResumeDTO;
import com.etplus.exception.JobPostException;
import com.etplus.exception.JobPostException.JobPostExceptionCode;
import com.etplus.exception.ResourceDeniedException;
import com.etplus.exception.ResourceDeniedException.ResourceDeniedExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.ResumeException;
import com.etplus.exception.ResumeException.ResumeExceptionCode;
import com.etplus.provider.EmailProvider;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.FileRepository;
import com.etplus.repository.JobPostRepository;
import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.NotificationRepository;
import com.etplus.repository.ResumeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.JobPostEntity;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.NotificationEntity;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.repository.domain.code.MessageTemplateType;
import com.etplus.util.UuidProvider;
import com.etplus.vo.JobPostByAcademyVO;
import com.etplus.vo.JobPostByAdminVO;
import com.etplus.vo.JobPostDetailVO;
import com.etplus.vo.JobPostResumeRelationVO;
import com.etplus.vo.JobPostVO;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class JobPostService {

  private final JobPostRepository jobPostRepository;
  private final UserRepository userRepository;
  private final FileRepository fileRepository;
  private final AcademyRepository academyRepository;
  private final ResumeRepository resumeRepository;
  private final JobPostResumeRelationRepository jobPostResumeRelationRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final NotificationRepository notificationRepository;
  private final EmailProvider emailProvider;

  @Value("${url.front}")
  private String FRONT_URL;

  public Slice<JobPostVO> getJobPosts(SearchJobPostDTO dto) {
    Slice<JobPostVO> allJobPost = jobPostRepository.findAllJobPost(dto);

    for (JobPostVO jobPost : allJobPost) {
      List<String> imageUrls = fileRepository.findAllByIdIn(jobPost.getImageFileIdList())
          .stream().map(FileEntity::getPath).toList();
      jobPost.setImageUrls(imageUrls);
    }

    return allJobPost;
  }

  public Page<JobPostByAcademyVO> getJobPostsByAcademy(long userId, SearchJobPostByAcademyDTO dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    AcademyEntity academy = academyRepository.findByRepresentativeUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    return jobPostRepository.findAllJobPostByAcademy(academy.getId(), dto);
  }

  public Page<JobPostByAdminVO> getJobPostsByAdmin(long userId, SearchJobPostByAcademyDTO dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    List<AcademyEntity> academyList = academyRepository.findByAdminUserId(user.getId());
    if (academyList.isEmpty()) {
      return Page.empty();
    }

    List<Long> academyIdList = academyList.stream().map(AcademyEntity::getId).toList();
    return jobPostRepository.findAllJobPostByAdmin(academyIdList, dto);
  }

  public JobPostDetailVO getJobPostDetail(Long jobPostId) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    List<Long> imageFileIdList = jobPost.getAcademy().getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(FileEntity::getPath).toList();

    return JobPostDetailVO.valueOf(jobPost, imagePathList);
  }

  public JobPostResumeRelationVO getSubmittedJobPostResumeRelation(
      long userId, long jobPostId) {
    JobPostResumeRelationEntity jobPostResumeRelation = jobPostResumeRelationRepository
        .findByUserIdAndJobPostId(userId, jobPostId).orElseThrow(() ->
            new ResourceNotFoundException(ResourceNotFoundExceptionCode.JOB_POST_RESUME_RELATION_NOT_FOUND));

    return JobPostResumeRelationVO.valueOf(jobPostResumeRelation);
  }

  @Transactional
  public void createJobPost(long userId, CreateJobPostDTO dto) {
    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    jobPostRepository.save(new JobPostEntity(null, dto.title(), dto.jobDescription(),
        dto.requiredQualification(), dto.preferredQualification(), dto.benefits(), dto.salary(),
        dto.salaryNegotiable(), dto.jobStartDate(), dto.dueDate(),
        dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(),
        dto.forHighSchool(), dto.forAdult(), false, false, academy));

    // 이메일 템플릿 조회 & 파싱 & 발송
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "NEW_JOB_POST", MessageTemplateType.EMAIL).orElse(null);

    Map params = new HashMap();
    params.put("name", user.getFullName());
    params.put("jobTitle", dto.title());
    params.put("link", FRONT_URL + "/job-postings");

    StringSubstitutor sub = new StringSubstitutor(params);
    String emailTitle = sub.replace(emailTemplate.getTitle());
    String emailContent = sub.replace(emailTemplate.getContent());

    emailProvider.send(user.getEmail(), emailTitle, emailContent);

    // 학원 알림 목록 추가
    notificationRepository.save(new NotificationEntity(null, "등록", "Registered",
        "새로운 공고를 성공적으로 등록했습니다", "New job posting registered", user));
  }

  @Transactional
  public void copyJobPost(long userId, long jobPostId) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    // 내 학원의 공고가 아닐 경우
    if (jobPost.getAcademy().getId() != academy.getId()) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.ACCESS_DENIED);
    }

    jobPostRepository.save(new JobPostEntity(null, jobPost.getTitle(), jobPost.getJobDescription(),
        jobPost.getRequiredQualification(), jobPost.getPreferredQualification(),
        jobPost.getBenefits(), jobPost.getSalary(), jobPost.isSalaryNegotiable(),
        jobPost.getJobStartDate(), jobPost.getDueDate(),
        jobPost.isForKindergarten(), jobPost.isForElementary(),
        jobPost.isForMiddleSchool(), jobPost.isForHighSchool(),
        jobPost.isForAdult(), false, true, jobPost.getAcademy()));
  }

  @Transactional
  public void createJobPostByAdmin(long academyId, CreateJobPostDTO dto, long adminUserId) {
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    if (academy.getAdminUser().getId() != adminUserId) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.ACCESS_DENIED);
    }

    jobPostRepository.save(new JobPostEntity(null, dto.title(), dto.jobDescription(),
        dto.requiredQualification(), dto.preferredQualification(), dto.benefits(), dto.salary(),
        dto.salaryNegotiable(), dto.jobStartDate(), dto.dueDate(),
        dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(),
        dto.forHighSchool(), dto.forAdult(), false, false, academy));
  }

  @Transactional
  public void createDraftJobPost(long userId, CreateJobPostDTO dto) {
    if (!StringUtils.hasText(dto.title())) {
      throw new JobPostException(JobPostExceptionCode.CHECK_TITLE);
    }
    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    jobPostRepository.save(new JobPostEntity(null, dto.title(), dto.jobDescription(),
        dto.requiredQualification(), dto.preferredQualification(), dto.benefits(), dto.salary(),
        dto.salaryNegotiable(), dto.jobStartDate(), dto.dueDate(),
        dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(),
        dto.forHighSchool(), dto.forAdult(), false, true, academy));
  }

  @Transactional
  public void updateJobPostByAdmin(long academyId, long jobPostId, CreateJobPostDTO dto, long adminUserId) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    if (jobPost.getAcademy().getAdminUser().getId() != adminUserId) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.ACCESS_DENIED);
    }

    if (jobPost.getAcademy().getId() != academyId) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND);
    }

    jobPost.setTitle(dto.title());
    jobPost.setJobDescription(dto.jobDescription());
    jobPost.setRequiredQualification(dto.requiredQualification());
    jobPost.setPreferredQualification(dto.preferredQualification());
    jobPost.setBenefits(dto.benefits());
    jobPost.setSalary(dto.salary());
    jobPost.setSalaryNegotiable(dto.salaryNegotiable());
    jobPost.setJobStartDate(dto.jobStartDate());
    jobPost.setDueDate(dto.dueDate());
    jobPost.setForKindergarten(dto.forKindergarten());
    jobPost.setForElementary(dto.forElementary());
    jobPost.setForMiddleSchool(dto.forMiddleSchool());
    jobPost.setForHighSchool(dto.forHighSchool());
    jobPost.setForAdult(dto.forAdult());
    // 수정 시
    jobPost.setDraft(false);

    jobPostRepository.save(jobPost);
  }

  @Transactional
  public void updateJobPost(long userId, long jobPostId,CreateJobPostDTO dto) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    // 내 학원의 공고가 아닐 경우
    if (jobPost.getAcademy().getId() != academy.getId()) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.ACCESS_DENIED);
    }

    jobPost.setTitle(dto.title());
    jobPost.setJobDescription(dto.jobDescription());
    jobPost.setRequiredQualification(dto.requiredQualification());
    jobPost.setPreferredQualification(dto.preferredQualification());
    jobPost.setBenefits(dto.benefits());
    jobPost.setSalary(dto.salary());
    jobPost.setSalaryNegotiable(dto.salaryNegotiable());
    jobPost.setJobStartDate(dto.jobStartDate());
    jobPost.setDueDate(dto.dueDate());
    jobPost.setForKindergarten(dto.forKindergarten());
    jobPost.setForElementary(dto.forElementary());
    jobPost.setForMiddleSchool(dto.forMiddleSchool());
    jobPost.setForHighSchool(dto.forHighSchool());
    jobPost.setForAdult(dto.forAdult());

    // 수정 시
    jobPost.setDraft(false);

    jobPostRepository.save(jobPost);
  }

  @Transactional
  public void updateDraftJobPost(long userId, long jobPostId, CreateJobPostDTO dto) {
    if (!StringUtils.hasText(dto.title())) {
      throw new JobPostException(JobPostExceptionCode.CHECK_TITLE);
    }

    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    // 내 학원 공고, 임시저장 아닌 경우
    if (jobPost.getAcademy().getId() != academy.getId() || (!jobPost.isDraft())) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.ACCESS_DENIED);
    }

    jobPost.setTitle(dto.title());
    jobPost.setJobDescription(dto.jobDescription());
    jobPost.setRequiredQualification(dto.requiredQualification());
    jobPost.setPreferredQualification(dto.preferredQualification());
    jobPost.setBenefits(dto.benefits());
    jobPost.setSalary(dto.salary());
    jobPost.setSalaryNegotiable(dto.salaryNegotiable());
    jobPost.setJobStartDate(dto.jobStartDate());
    jobPost.setDueDate(dto.dueDate());
    jobPost.setForKindergarten(dto.forKindergarten());
    jobPost.setForElementary(dto.forElementary());
    jobPost.setForMiddleSchool(dto.forMiddleSchool());
    jobPost.setForHighSchool(dto.forHighSchool());
    jobPost.setForAdult(dto.forAdult());

    jobPostRepository.save(jobPost);
  }

  @Transactional
  public void submitResume(long userId, long jobPostId, long resumeId, SubmitResumeDTO dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    if (jobPost.isDraft()) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND);
    }

    if (jobPost.getDueDate() != null && jobPost.getDueDate().isBefore(LocalDate.now())) {
      throw new JobPostException(JobPostExceptionCode.JOB_POST_CLOSED);
    }

    if (jobPost.isClosed()) {
      throw new JobPostException(JobPostExceptionCode.JOB_POST_CLOSED);
    }

    ResumeEntity resume = resumeRepository.findByIdAndUserId(resumeId, userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 임시 이력서인 경우
    if (resume.isDraft()) {
      throw new ResumeException(ResumeExceptionCode.DRAFT_RESUME);
    }

    if (jobPostResumeRelationRepository.existsByJobPostIdAndUserId(jobPostId, userId)) {
      throw new JobPostException(JobPostExceptionCode.RESUME_ALREADY_SUBMITTED);
    }

    // 선생님 이메일 템플릿 조회 & 파싱 & 발송
    try {
      MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
              "JOB_POST_STATUS_" + JobPostResumeRelationStatus.SUBMITTED, MessageTemplateType.EMAIL)
          .orElse(null);

      Map params = new HashMap();
      params.put("name", user.getFirstName() + " " + user.getLastName());
      params.put("jobTitle", jobPost.getTitle());
      params.put("academyName", jobPost.getAcademy().getNameEn());
      params.put("link", FRONT_URL + "/setting/my-job-posting");

      StringSubstitutor sub = new StringSubstitutor(params);
      String emailTitle = sub.replace(emailTemplate.getTitle());
      String emailContent = sub.replace(emailTemplate.getContent());

      emailProvider.send(user.getEmail(), emailTitle, emailContent);
    } catch (Exception e) {
      log.error("Failed to send email teacher for job post resume submission", e);
    }

    // 접근 코드
    String code = UuidProvider.generateUuid();

    // 어드민이 올린 공고인 경우 학원에 이메일 전달
    if (jobPost.getAcademy().isByAdmin()) {
      try {
        String adminUserEmail = jobPost.getAcademy().getAdminUser().getEmail();

        MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
            "ADMIN_JOB_POST_SUBMITTED", MessageTemplateType.EMAIL).orElse(null);

        Map params = new HashMap();
        params.put("name", jobPost.getAcademy().getRepresentativeName());
        params.put("jobTitle", jobPost.getTitle());
        params.put("adminEmail", adminUserEmail);
        params.put("link", FRONT_URL + "/guest/resume?code=" + code);

        StringSubstitutor academySub = new StringSubstitutor(params);
        String emailTitle = academySub.replace(emailTemplate.getTitle());
        String emailContent = academySub.replace(emailTemplate.getContent());

        // 학원 대표 이메일로 전송
        String academyUserEmail = jobPost.getAcademy().getRepresentativeEmail();
        emailProvider.send(academyUserEmail, emailTitle, emailContent);

        // 어드민 이메일로 전송
        emailProvider.send(adminUserEmail, emailTitle, emailContent);
      } catch (Exception e) {
        log.error("Failed to send email to academy for job post resume submission", e);
      }
    }

    // 선생님 알림 추가
    notificationRepository.save(new NotificationEntity(null, "지원완료", "Submitted",
        String.format("%s에 이력서를 제출 완료했습니다", jobPost.getAcademy().getName()),
        String.format("Resume submitted to %s", jobPost.getAcademy().getNameEn()),
        user));

    // 학원 알림 추가
    UserEntity representativeUser = jobPost.getAcademy().getRepresentativeUser();
    if (representativeUser != null) {
      notificationRepository.save(new NotificationEntity(null, "신규 지원자", "Applicated",
          String.format("{%s} 공고에 새로운 지원자가 있어요.", jobPost.getTitle()),
          String.format("New application for {%s}", jobPost.getTitle()),
          representativeUser));
    }

    jobPostResumeRelationRepository.save(
        new JobPostResumeRelationEntity(null, dto.coverLetter(),
            JobPostResumeRelationStatus.SUBMITTED, LocalDate.now(), null, jobPost,
            resume.getTitle(), resume.getPersonalIntroduction(), resume.getFirstName(),
            resume.getLastName(), resume.getEmail(), resume.getDegree(), resume.getMajor(),
            resume.getGenderType(), resume.getBirthDate(), resume.getHasVisa(),
            resume.getVisaType(), resume.getForKindergarten(), resume.getForElementary(),
            resume.getForMiddleSchool(), resume.getForHighSchool(), resume.getForAdult(),
            code, resume.getCountry(), resume.getResidenceCountry(), resume.getUser(),
            resume.getProfileImage(), resume.getFile()
        ));
  }

}
