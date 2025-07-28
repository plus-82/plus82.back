package com.etplus.repository;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.controller.dto.SearchRepresentativeResumeDto;
import com.etplus.vo.RepresentativeResumeVO;
import com.etplus.vo.ResumeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

interface ResumeRepositoryCustom {

  Slice<ResumeVO> findAllByUserId(long userId, PagingDTO dto);
  Page<RepresentativeResumeVO> findAllRepresentativeResumes(SearchRepresentativeResumeDto dto);

}
