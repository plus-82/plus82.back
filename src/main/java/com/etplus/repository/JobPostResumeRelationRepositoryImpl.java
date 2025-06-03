package com.etplus.repository;

import static com.querydsl.core.types.dsl.Expressions.constant;

import com.etplus.controller.dto.SearchJobPostResumeRelationDTO;
import com.etplus.repository.domain.JobPostResumeRelationEntity;
import com.etplus.repository.domain.QAcademyEntity;
import com.etplus.repository.domain.QCountryEntity;
import com.etplus.repository.domain.QJobPostEntity;
import com.etplus.repository.domain.QJobPostResumeRelationEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.util.QuerydslRepositorySupportCustom;
import com.etplus.vo.JobPostResumeRelationSummaryVO;
import com.etplus.vo.JobPostResumeRelationVO;
import com.etplus.vo.QJobPostResumeRelationVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class JobPostResumeRelationRepositoryImpl extends QuerydslRepositorySupportCustom
    implements JobPostResumeRelationRepositoryCustom {

  private final JPAQueryFactory query;
  private QJobPostResumeRelationEntity jobPostResumeRelation;
  private QJobPostEntity jobPost;
  private QAcademyEntity academy;
  private QCountryEntity country;

  public JobPostResumeRelationRepositoryImpl(JPAQueryFactory query) {
    super(JobPostResumeRelationEntity.class);
    this.query = query;
    jobPostResumeRelation = new QJobPostResumeRelationEntity("jobPostResumeRelation");
    jobPost = new QJobPostEntity("jobPost");
    academy = new QAcademyEntity("academy");
    country = new QCountryEntity("country");
  }

  @Override
  public Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByAcademy(
      SearchJobPostResumeRelationDTO dto, long academyId) {
    JPAQuery<JobPostResumeRelationVO> jpaQuery = query.select(
            new QJobPostResumeRelationVO(
                jobPostResumeRelation.id,
                jobPostResumeRelation.coverLetter,
                jobPostResumeRelation.status,
                jobPostResumeRelation.submittedDate,
                jobPostResumeRelation.academyMemo,
                jobPostResumeRelation.resumeTitle,
                jobPostResumeRelation.firstName,
                jobPostResumeRelation.lastName,
                country.id,
                country.countryNameEn,
                country.countryCode,
                jobPost.id,
                jobPost.title,
                academy.id,
                academy.name
            ))
        .from(jobPostResumeRelation)
        .innerJoin(jobPostResumeRelation.jobPost, jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(academy.id.eq(academyId)
            .and(getWhereCondition(dto))
        )
        .orderBy(jobPostResumeRelation.id.desc());

    return applyPagination(jpaQuery, dto.toPageable());
  }

  @Override
  public Page<JobPostResumeRelationVO> findAllJobPostResumeRelationsByAcademy(
      SearchJobPostResumeRelationDTO dto, List<Long> academyIds) {
    JPAQuery<JobPostResumeRelationVO> jpaQuery = query.select(
            new QJobPostResumeRelationVO(
                jobPostResumeRelation.id,
                jobPostResumeRelation.coverLetter,
                jobPostResumeRelation.status,
                jobPostResumeRelation.submittedDate,
                jobPostResumeRelation.academyMemo,
                jobPostResumeRelation.resumeTitle,
                jobPostResumeRelation.firstName,
                jobPostResumeRelation.lastName,
                country.id,
                country.countryNameEn,
                country.countryCode,
                jobPost.id,
                jobPost.title,
                academy.id,
                academy.name
            ))
        .from(jobPostResumeRelation)
        .innerJoin(jobPostResumeRelation.jobPost, jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(academy.id.in(academyIds)
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
                jobPostResumeRelation.coverLetter,
                jobPostResumeRelation.status,
                jobPostResumeRelation.submittedDate,
                constant(""),
                jobPostResumeRelation.resumeTitle,
                jobPostResumeRelation.firstName,
                jobPostResumeRelation.lastName,
                country.id,
                country.countryNameEn,
                country.countryCode,
                jobPost.id,
                jobPost.title,
                academy.id,
                academy.name
            ))
        .from(jobPostResumeRelation)
        .innerJoin(jobPostResumeRelation.jobPost, jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(jobPostResumeRelation.user.id.eq(teacherId)
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
    if (Objects.nonNull(dto.getJobPostId())) {
      whereCondition.and(jobPost.id.eq(dto.getJobPostId()));
    }
    return whereCondition;
  }

  @Override
  public JobPostResumeRelationSummaryVO getJobPostResumeRelationSummaryByTeacher(long teacherId) {
    return getJobPostResumeRelationSummary(jobPostResumeRelation.user.id.eq(teacherId));
  }

  @Override
  public JobPostResumeRelationSummaryVO getJobPostResumeRelationSummaryByAcademy(long academyId) {
    return getJobPostResumeRelationSummary(jobPostResumeRelation.jobPost.academy.id.eq(academyId));
  }

  private JobPostResumeRelationSummaryVO getJobPostResumeRelationSummary(BooleanExpression condition) {
    List<Tuple> results = query
        .select(jobPostResumeRelation.status, jobPostResumeRelation.count())
        .from(jobPostResumeRelation)
        .where(condition)
        .groupBy(jobPostResumeRelation.status)
        .fetch();

    Map<JobPostResumeRelationStatus, Integer> statusCounts = results.stream()
        .collect(Collectors.toMap(
            result -> result.get(jobPostResumeRelation.status),
            result -> result.get(jobPostResumeRelation.count()).intValue()
        ));

    int submitted = statusCounts.getOrDefault(JobPostResumeRelationStatus.SUBMITTED, 0);
    int reviewed = statusCounts.getOrDefault(JobPostResumeRelationStatus.REVIEWED, 0);
    int accepted = statusCounts.getOrDefault(JobPostResumeRelationStatus.ACCEPTED, 0);
    int rejected = statusCounts.getOrDefault(JobPostResumeRelationStatus.REJECTED, 0);

    int total = submitted + reviewed + accepted + rejected;

    return new JobPostResumeRelationSummaryVO(submitted, reviewed, accepted, rejected, total);
  }

}
