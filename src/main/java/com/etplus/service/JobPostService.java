package com.etplus.service;

import com.etplus.controller.dto.CreateJobPostDTO;
import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.controller.dto.SubmitResumeDTO;
import com.etplus.exception.JobPostException;
import com.etplus.exception.JobPostException.JobPostExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.EmailProvider;
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
import com.etplus.vo.JobPostDetailVO;
import com.etplus.vo.JobPostVO;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobPostService {

  private final JobPostRepository jobPostRepository;
  private final UserRepository userRepository;
  private final FileRepository fileRepository;
  private final ResumeRepository resumeRepository;
  private final JobPostResumeRelationRepository jobPostResumeRelationRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final NotificationRepository notificationRepository;
  private final EmailProvider emailProvider;

  public Slice<JobPostVO> getJobPosts(SearchJobPostDTO dto) {
    Slice<JobPostVO> allJobPost = jobPostRepository.findAllJobPost(dto);

    for (JobPostVO jobPost : allJobPost) {
      List<String> imageUrls = fileRepository.findAllByIdIn(jobPost.getImageFileIdList())
          .stream().map(FileEntity::getPath).toList();
      jobPost.setImageUrls(imageUrls);
    }

    return allJobPost;
  }

  public JobPostDetailVO getJobPostDetail(Long jobPostId) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    List<Long> imageFileIdList = jobPost.getAcademy().getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(FileEntity::getPath).toList();

    return JobPostDetailVO.valueOf(jobPost, imagePathList);
  }

  @Transactional
  public void createJobPost(long userId, CreateJobPostDTO dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    AcademyEntity academy = user.getAcademy();

    if (academy == null) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND);
    }

    jobPostRepository.save(new JobPostEntity(null, dto.title(), dto.jobDescription(),
        dto.requiredQualification(), dto.preferredQualification(), dto.benefits(), dto.salary(),
        dto.salaryNegotiable(), dto.jobStartDate(), dto.dueDate(),
        dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(),
        dto.forHighSchool(), dto.forAdult(), academy));
  }

  @Transactional
  public void submitResume(long userId, long jobPostId, long resumeId, SubmitResumeDTO dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    if (jobPost.getDueDate().isBefore(LocalDate.now())) {
      throw new JobPostException(JobPostExceptionCode.JOB_POST_CLOSED);
    }

    ResumeEntity resume = resumeRepository.findByIdAndUserId(resumeId, userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    if (jobPostResumeRelationRepository.existsByJobPostIdAndUserId(jobPostId, userId)) {
      throw new JobPostException(JobPostExceptionCode.RESUME_ALREADY_SUBMITTED);
    }

    // 이메일 템플릿 조회 & 파싱 & 발송
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "JOB_POST_STATUS_" + JobPostResumeRelationStatus.SUBMITTED, MessageTemplateType.EMAIL).orElse(null);

    Map params = new HashMap();
    params.put("name", user.getFirstName() + " " + user.getLastName());
    params.put("jobTitle", jobPost.getTitle());
    params.put("academyName", jobPost.getAcademy().getName());
    params.put("link", "https://plus82.co/my-page");

    StringSubstitutor sub = new StringSubstitutor(params);
    String emailTitle = sub.replace(emailTemplate.getTitle());
    String emailContent = sub.replace(emailTemplate.getContent());

    emailProvider.send(user.getEmail(), emailTitle, emailContent);

    // 선생님 알림 목록 추가
    notificationRepository.save(new NotificationEntity(null, "지원완료", "Submitted",
        String.format("%s에 이력서를 제출 완료했습니다", jobPost.getAcademy().getName()),
        String.format("Resume submitted to %s", jobPost.getAcademy().getNameEn()),
        user));

    jobPostResumeRelationRepository.save(
        new JobPostResumeRelationEntity(null, dto.coverLetter(),
            JobPostResumeRelationStatus.SUBMITTED, LocalDate.now(), jobPost,
            resume.getTitle(), resume.getPersonalIntroduction(), resume.getFirstName(),
            resume.getLastName(), resume.getEmail(), resume.getDegree(), resume.getMajor(),
            resume.getGenderType(), resume.getBirthDate(), resume.getHasVisa(),
            resume.getVisaType(), resume.getForKindergarten(), resume.getForElementary(),
            resume.getForMiddleSchool(), resume.getForHighSchool(), resume.getForAdult(),
            resume.getCountry(), resume.getResidenceCountry(), resume.getUser(),
            resume.getProfileImage(), resume.getFile()
        ));
  }

}
