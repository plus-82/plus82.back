package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.QAcademyEntity;
import com.etplus.repository.domain.QJobPostEntity;
import com.etplus.repository.domain.QJobPostResumeRelationEntity;
import com.etplus.repository.domain.QResumeEntity;
import com.etplus.util.QuerydslRepositorySupportCustom;
import com.etplus.vo.JobPostResumeRelationVO;
import com.etplus.vo.QJobPostResumeRelationVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

  private BooleanBuilder getWhereCondition(SearchJobPostResumeRelationDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();

    if (Objects.nonNull(dto.getStatus())) {
      whereCondition.and(jobPostResumeRelation.status.eq(dto.getStatus()));
    }
    return whereCondition;
  }
}
