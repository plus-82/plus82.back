package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostByAcademyDTO;
import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.scheduler.vo.JobPostDueDateNotiVO;
import com.etplus.scheduler.vo.JobPostNewApplicantNotiVO;
import com.etplus.vo.JobPostByAcademyVO;
import com.etplus.vo.JobPostByAdminVO;
import com.etplus.vo.JobPostVO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

interface JobPostRepositoryCustom {

  Slice<JobPostVO> findAllJobPost(SearchJobPostDTO dto);
  Page<JobPostByAcademyVO> findAllJobPostByAcademy(long academyId, SearchJobPostByAcademyDTO dto);
  Page<JobPostByAdminVO> findAllJobPostByAdmin(List<Long> academyIdList, SearchJobPostByAcademyDTO dto);

  List<JobPostDueDateNotiVO> findDueDateNotificationTarget(LocalDate today);
  Optional<JobPostDueDateNotiVO> findDueDateNotificationTargetByJobPostId(long jobPostId);
  List<JobPostNewApplicantNotiVO> findNewApplicantNotificationTarget(LocalDate today);

}
