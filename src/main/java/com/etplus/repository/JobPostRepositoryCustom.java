package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.scheduler.vo.JobPostDueDateNotiVO;
import com.etplus.scheduler.vo.JobPostNewApplicantNotiVO;
import com.etplus.vo.JobPostVO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Slice;

interface JobPostRepositoryCustom {

  Slice<JobPostVO> findAllJobPost(SearchJobPostDTO dto);

  List<JobPostDueDateNotiVO> findDueDateNotificationTarget(LocalDate today);
  List<JobPostNewApplicantNotiVO> findNewApplicantNotificationTarget(LocalDate today);

}
