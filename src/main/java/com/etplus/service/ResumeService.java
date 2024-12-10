package com.etplus.service;

import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.ResumeRepository;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.vo.ResumeDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;

  public ResumeDetailVO getResumeDetail(long userId, long resumeId) {
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    if (resume.getUser().getId() != userId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
    // todo 지원한 학원도 조회 가능하도록?

    return ResumeDetailVO.valueOf(resume);
  }

}
