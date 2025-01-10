package com.etplus.service;

import com.etplus.controller.dto.CreateJobPostDTO;
import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.controller.dto.SubmitResumeDTO;
import com.etplus.exception.JobPostException;
import com.etplus.exception.JobPostException.JobPostExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.FileRepository;
import com.etplus.repository.JobPostRepository;
import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.ResumeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.JobPostEntity;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.vo.JobPostDetailVO;
import com.etplus.vo.JobPostVO;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    jobPostRepository.save(new JobPostEntity(null, dto.title(), dto.description(), dto.salary(),
        dto.salaryNegotiable(), dto.jobStartDate(), dto.dueDate(), academy));
  }

  @Transactional
  public void submitResume(long userId, long jobPostId, long resumeId, SubmitResumeDTO dto) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    if (jobPost.getDueDate().isBefore(LocalDate.now())) {
      throw new JobPostException(JobPostExceptionCode.JOB_POST_CLOSED);
    }

    ResumeEntity resume = resumeRepository.findByIdAndUserId(resumeId, userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    if (jobPostResumeRelationRepository.existsByJobPostIdAndResumeId(jobPostId, resumeId)) {
      throw new JobPostException(JobPostExceptionCode.RESUME_ALREADY_SUBMITTED);
    }

    jobPostResumeRelationRepository.save(
        new JobPostResumeRelationEntity(null, dto.coverLetter(),
            JobPostResumeRelationStatus.SUBMITTED, LocalDate.now(), resume, jobPost));
  }

}
