package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.vo.JobPostResumeRelationSummaryVO;
import com.etplus.vo.JobPostResumeRelationVO;
import java.util.List;
import org.springframework.data.domain.Page;

interface JobPostResumeRelationRepositoryCustom {

  Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByTeacher(
      SearchJobPostResumeRelationDTO dto, long teacherId);
  Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByAcademy(
      SearchJobPostResumeRelationDTO dto, long academyId);
  Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByAcademy(
      SearchJobPostResumeRelationDTO dto, List<Long> academyIds);

  JobPostResumeRelationSummaryVO getJobPostResumeRelationSummaryByTeacher(long teacherId);
  JobPostResumeRelationSummaryVO getJobPostResumeRelationSummaryByAcademy(long academyId);

}
