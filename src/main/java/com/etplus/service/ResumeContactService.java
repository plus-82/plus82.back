package com.etplus.service;

import com.etplus.controller.dto.SearchResumeContactDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.ResumeContactRepository;
import com.etplus.repository.domain.ResumeContactEntity;
import com.etplus.vo.ResumeContactDetailVO;
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

  public ResumeContactDetailVO getResumeContactDetail(long academyUserId, long contactId) {
    log.info("Getting resume contact detail - contactId: {}, academyUserId: {}", contactId, academyUserId);
    ResumeContactEntity contact = resumeContactRepository.findById(contactId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_CONTACT_NOT_FOUND));

    // 본인이 컨택한 것만 조회 가능
    if (contact.getAcademyUser().getId() != academyUserId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    return ResumeContactDetailVO.valueOfWithMasking(contact);
  }
}

