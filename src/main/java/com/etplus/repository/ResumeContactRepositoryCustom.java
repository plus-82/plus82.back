package com.etplus.repository;

import com.etplus.controller.dto.SearchResumeContactDTO;
import com.etplus.vo.ResumeContactVO;
import org.springframework.data.domain.Page;

public interface ResumeContactRepositoryCustom {

  Page<ResumeContactVO> findAllByAcademyUserId(long academyUserId, SearchResumeContactDTO dto);

}

