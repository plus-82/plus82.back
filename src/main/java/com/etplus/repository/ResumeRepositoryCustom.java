package com.etplus.repository;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.vo.ResumeVO;
import org.springframework.data.domain.Slice;

interface ResumeRepositoryCustom {

  Slice<ResumeVO> findAllByUserId(long userId, PagingDTO dto);

}
