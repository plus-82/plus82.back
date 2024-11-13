package com.etplus.service;

import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.ImageFileRepository;
import com.etplus.repository.JobPostRepository;
import com.etplus.repository.domain.ImageFileEntity;
import com.etplus.repository.domain.JobPostEntity;
import com.etplus.vo.JobPostDetailVO;
import com.etplus.vo.JobPostVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobPostService {

  private final JobPostRepository jobPostRepository;
  private final ImageFileRepository imageFileRepository;

  public Slice<JobPostVO> getJobPosts(SearchJobPostDTO dto) {
    return jobPostRepository.findAllJobPost(dto);
  }

  public JobPostDetailVO getJobPostDetail(Long jobPostId) {
    JobPostEntity jobPost = jobPostRepository.findById(jobPostId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.JOB_POST_NOT_FOUND));

    List<Long> imageFileIdList = jobPost.getAcademy().getImageFileIdList();
    List<ImageFileEntity> imageFileList = imageFileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(ImageFileEntity::getPath).toList();

    return JobPostDetailVO.valueOf(jobPost, imagePathList);
  }

}
