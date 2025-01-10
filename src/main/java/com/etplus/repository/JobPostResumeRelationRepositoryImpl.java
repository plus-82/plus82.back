package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.QAcademyEntity;
import com.etplus.repository.domain.QJobPostEntity;
import com.etplus.repository.domain.QJobPostResumeRelationEntity;
import com.etplus.repository.domain.QResumeEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.util.QuerydslRepositorySupportCustom;
import com.etplus.vo.JobPostResumeRelationSummaryVO;
import com.etplus.vo.JobPostResumeRelationVO;
import com.etplus.vo.QJobPostResumeRelationVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;

public class JobPostResumeRelationRepositoryImpl extends QuerydslRepositorySupportCustom
    implements JobPostResumeRelationRepositoryCustom {

  private final JPAQueryFactory query;
  private QJobPostResumeRelationEntity jobPostResumeRelation;
  private QResumeEntity resume;
  private QJobPostEntity jobPost;
  private QAcademyEntity academy;

  public JobPostResumeRelationRepositoryImpl(JPAQueryFactory query) {
    super(JobPostResumeRelationEntity.class);
    this.query = query;
    jobPostResumeRelation = new QJobPostResumeRelationEntity("jobPostResumeRelation");
    resume = new QResumeEntity("resume");
    jobPost = new QJobPostEntity("jobPost");
    academy = new QAcademyEntity("academy");
  }

  @Override
  public Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByAcademy(
      SearchJobPostResumeRelationDTO dto, long academyId) {
    JPAQuery<JobPostResumeRelationVO> jpaQuery = query.select(
            new QJobPostResumeRelationVO(
                jobPostResumeRelation.id,
                jobPostResumeRelation.status,
                jobPostResumeRelation.submittedDate,
                resume.id,
                resume.title,
                resume.firstName,
                resume.lastName,
                jobPost.id,
                jobPost.title,
                academy.id,
                academy.name
            ))
        .from(jobPostResumeRelation)
        .innerJoin(jobPostResumeRelation.resume, resume)
        .innerJoin(jobPostResumeRelation.jobPost, jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(academy.id.eq(academyId)
            .and(getWhereCondition(dto))
        )
        .orderBy(jobPostResumeRelation.id.desc());

    return applyPagination(jpaQuery, dto.toPageable());
  }

  @Override
  public Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByTeacher(
      SearchJobPostResumeRelationDTO dto, long teacherId) {
    JPAQuery<JobPostResumeRelationVO> jpaQuery = query.select(
            new QJobPostResumeRelationVO(
                jobPostResumeRelation.id,
                jobPostResumeRelation.status,
                jobPostResumeRelation.submittedDate,
                resume.id,
                resume.title,
                resume.firstName,
                resume.lastName,
                jobPost.id,
                jobPost.title,
                academy.id,
                academy.name
            ))
        .from(jobPostResumeRelation)
        .innerJoin(jobPostResumeRelation.resume, resume)
        .innerJoin(jobPostResumeRelation.jobPost, jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(resume.user.id.eq(teacherId)
            .and(getWhereCondition(dto))
        )
        .orderBy(jobPostResumeRelation.id.desc());

    return applyPagination(jpaQuery, dto.toPageable());
  }

  @Override
  public JobPostResumeRelationSummaryVO getJobPostResumeRelationSummaryByTeacher(long teacherId) {
    List<Tuple> results = query
        .select(jobPostResumeRelation.status, jobPostResumeRelation.count())
        .from(jobPostResumeRelation)
        .where(resume.user.id.eq(teacherId))
        .groupBy(jobPostResumeRelation.status)
        .fetch();

    int submitted = 0;
    int reviewed = 0;
    int accepted = 0;
    int rejected = 0;
    for (Tuple result : results) {
      JobPostResumeRelationStatus status = result.get(jobPostResumeRelation.status);
      Long count = result.get(jobPostResumeRelation.count());

      if (JobPostResumeRelationStatus.ACCEPTED.equals(status)) {
        accepted = count.intValue();
      } else if (JobPostResumeRelationStatus.REJECTED.equals(status)) {
        rejected = count.intValue();
      } else if (JobPostResumeRelationStatus.REVIEWED.equals(status)) {
        reviewed = count.intValue();
      } else if (JobPostResumeRelationStatus.SUBMITTED.equals(status)) {
        submitted = count.intValue();
      }
    }
    int total = submitted + reviewed + accepted + rejected;

    // Return the VO
    return new JobPostResumeRelationSummaryVO(submitted, reviewed, accepted, rejected, total);
  }

  @Override
  public JobPostResumeRelationSummaryVO getJobPostResumeRelationSummaryByAcademy(long academyId) {
    List<Tuple> results = query
        .select(jobPostResumeRelation.status, jobPostResumeRelation.count())
        .from(jobPostResumeRelation)
        .where(academy.id.eq(academyId))
        .groupBy(jobPostResumeRelation.status)
        .fetch();

    int submitted = 0;
    int reviewed = 0;
    int accepted = 0;
    int rejected = 0;
    for (Tuple result : results) {
      JobPostResumeRelationStatus status = result.get(jobPostResumeRelation.status);
      Long count = result.get(jobPostResumeRelation.count());

      if (JobPostResumeRelationStatus.ACCEPTED.equals(status)) {
        accepted = count.intValue();
      } else if (JobPostResumeRelationStatus.REJECTED.equals(status)) {
        rejected = count.intValue();
      } else if (JobPostResumeRelationStatus.REVIEWED.equals(status)) {
        reviewed = count.intValue();
      } else if (JobPostResumeRelationStatus.SUBMITTED.equals(status)) {
        submitted = count.intValue();
      }
    }
    int total = submitted + reviewed + accepted + rejected;

    // Return the VO
    return new JobPostResumeRelationSummaryVO(submitted, reviewed, accepted, rejected, total);
  }

  private BooleanBuilder getWhereCondition(SearchJobPostResumeRelationDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();

    if (Objects.nonNull(dto.getStatus())) {
      whereCondition.and(jobPostResumeRelation.status.eq(dto.getStatus()));
    }
    return whereCondition;
  }
}
