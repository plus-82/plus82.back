package com.etplus.service;

import com.etplus.controller.dto.SearchResumeContactDTO;
import com.etplus.repository.ResumeContactRepository;
import com.etplus.vo.ResumeContactVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResumeContactService {

  private final ResumeContactRepository resumeContactRepository;

  public Page<ResumeContactVO> getMyResumeContacts(long academyUserId,
      SearchResumeContactDTO dto) {
    log.info("Getting resume contacts for academyUserId: {}, dto: {}", academyUserId, dto);
    return resumeContactRepository.findAllByAcademyUserId(academyUserId, dto);
  }
}

