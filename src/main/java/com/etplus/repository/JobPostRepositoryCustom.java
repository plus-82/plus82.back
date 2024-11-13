package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.vo.JobPostVO;
import org.springframework.data.domain.Slice;

interface JobPostRepositoryCustom {

  Slice<JobPostVO> findAllJobPost(SearchJobPostDTO dto);

}
