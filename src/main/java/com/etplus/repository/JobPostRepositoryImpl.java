package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.controller.dto.code.OrderType;
import com.etplus.repository.domain.QAcademyEntity;
import com.etplus.repository.domain.QJobPostEntity;
import com.etplus.repository.domain.QJobPostResumeRelationEntity;
import com.etplus.repository.domain.QUserEntity;
import com.etplus.repository.domain.code.JobPostResumeRelationStatus;
import com.etplus.scheduler.vo.JobPostDueDateNotiVO;
import com.etplus.scheduler.vo.JobPostNewApplicantNotiVO;
import com.etplus.scheduler.vo.QJobPostDueDateNotiVO;
import com.etplus.scheduler.vo.QJobPostNewApplicantNotiVO;
import com.etplus.vo.JobPostVO;
import com.etplus.vo.QJobPostVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class JobPostRepositoryImpl implements JobPostRepositoryCustom {

  private final JPAQueryFactory query;
  private QJobPostEntity jobPost;
  private QAcademyEntity academy;

  public JobPostRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    jobPost = new QJobPostEntity("jobPost");
    academy = new QAcademyEntity("academy");
  }

  @Override
  public Slice<JobPostVO> findAllJobPost(SearchJobPostDTO dto) {
    BooleanBuilder whereCondition = getWhereCondition(dto);
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(dto);

    JPAQuery<JobPostVO> jpaQuery = query.select(
        new QJobPostVO(
            jobPost.id,
            jobPost.title,
            jobPost.dueDate,
            jobPost.forKindergarten,
            jobPost.forElementary,
            jobPost.forMiddleSchool,
            jobPost.forHighSchool,
            jobPost.forAdult,
            academy.id,
            academy.name,
            academy.locationType,
            academy.imageFileIdList
        ))
        .from(jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(whereCondition)
        .orderBy(orderSpecifier);

    List<JobPostVO> content = jpaQuery
        .offset(dto.getPageNumber() * dto.getRowCount())
        .limit(dto.getRowCount() + 1)
        .fetch();

    boolean hasNext = false;
    if (content.size() > dto.getRowCount()) {
      content.remove(dto.getRowCount());
      hasNext = true;
    }

    return new SliceImpl<>(content, dto.toPageable(), hasNext);
  }

  @Override
  public List<JobPostDueDateNotiVO> findDueDateNotificationTarget(LocalDate today) {
    QUserEntity adminUser = new QUserEntity("adminUser");
    QJobPostResumeRelationEntity jobPostResumeRelation = new QJobPostResumeRelationEntity("jobPostResumeRelation");

    JPAQuery<JobPostDueDateNotiVO> jpaQuery = query.select(
            new QJobPostDueDateNotiVO(
                jobPost.id,
                jobPost.title,
                academy.id,
                academy.name,
                academy.representativeName,
                academy.representativeEmail,
                academy.byAdmin,
                adminUser.id,
                adminUser.email,
                JPAExpressions
                    .select(jobPostResumeRelation.count())
                    .from(jobPostResumeRelation)
                    .where(jobPostResumeRelation.jobPost.id.eq(jobPost.id)),
                JPAExpressions
                    .select(jobPostResumeRelation.count())
                    .from(jobPostResumeRelation)
                    .where(jobPostResumeRelation.jobPost.id.eq(jobPost.id)
                        .and(jobPostResumeRelation.status.eq(JobPostResumeRelationStatus.SUBMITTED))),
                JPAExpressions
                    .select(jobPostResumeRelation.count())
                    .from(jobPostResumeRelation)
                    .where(jobPostResumeRelation.jobPost.id.eq(jobPost.id)
                        .and(jobPostResumeRelation.status.eq(JobPostResumeRelationStatus.REVIEWED)))
            ))
        .from(jobPost)
        .innerJoin(jobPost.academy, academy)
        .leftJoin(academy.adminUser, adminUser)
        .where(jobPost.dueDate.eq(today)
            .and(academy.byAdmin.isFalse()));

    return jpaQuery.fetch();
  }

  @Override
  public List<JobPostNewApplicantNotiVO> findNewApplicantNotificationTarget(LocalDate today) {
    QUserEntity adminUser = new QUserEntity("adminUser");
    QJobPostResumeRelationEntity jobPostResumeRelation = new QJobPostResumeRelationEntity("jobPostResumeRelation");

    JPAQuery<JobPostNewApplicantNotiVO> jpaQuery = query.select(
        new QJobPostNewApplicantNotiVO(
            jobPost.id,
            jobPost.title,
            academy.id,
            academy.name,
            academy.representativeName,
            academy.representativeEmail,
            academy.byAdmin,
            adminUser.id,
            adminUser.email,
            jobPostResumeRelation.count()
        ))
        .from(jobPost)
        .innerJoin(jobPost.academy, academy)
        .leftJoin(academy.adminUser, adminUser)
        .leftJoin(jobPostResumeRelation)
        .on(jobPost.id.eq(jobPostResumeRelation.jobPost.id)
            .and(jobPostResumeRelation.createdAt.between(
                today.minusDays(1).atStartOfDay(),
                today.atStartOfDay())
            )
        )
        .groupBy(jobPost.id, jobPost.title, academy.id, academy.name,
            academy.representativeName, academy.representativeEmail,
            adminUser.id, adminUser.email)
        .having(jobPostResumeRelation.id.count().gt(0)); // 1개 이상인 경우만 가져오기

    return jpaQuery.fetch();
  }

  private BooleanBuilder getWhereCondition(SearchJobPostDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();

    if (Objects.nonNull(dto.getSearchText())) {
      whereCondition.and(
          jobPost.title.like("%" + dto.getSearchText() + "%")
              .or(academy.name.like("%" + dto.getSearchText() + "%"))
      );
    }
    if (Objects.nonNull(dto.getLocationTypeList()) && !dto.getLocationTypeList().isEmpty()) {
      whereCondition.and(academy.locationType.in(dto.getLocationTypeList()));
    }
    if (Objects.nonNull(dto.getForKindergarten())) {
      whereCondition.and(jobPost.forKindergarten.eq(dto.getForKindergarten()));
    }
    if (Objects.nonNull(dto.getForElementary())) {
      whereCondition.and(jobPost.forElementary.eq(dto.getForElementary()));
    }
    if (Objects.nonNull(dto.getForMiddleSchool())) {
      whereCondition.and(jobPost.forMiddleSchool.eq(dto.getForMiddleSchool()));
    }
    if (Objects.nonNull(dto.getForHighSchool())) {
      whereCondition.and(jobPost.forHighSchool.eq(dto.getForHighSchool()));
    }
    if (Objects.nonNull(dto.getForAdult())) {
      whereCondition.and(jobPost.forAdult.eq(dto.getForAdult()));
    }
    if (Objects.nonNull(dto.getClosed())) {
      whereCondition.and(jobPost.closed.eq(dto.getClosed()));
    }
    if (Objects.nonNull(dto.getFromDueDate())) {
      whereCondition.and(jobPost.dueDate.goe(dto.getFromDueDate()));
    }
    if (Objects.nonNull(dto.getToDueDate())) {
      whereCondition.and(jobPost.dueDate.loe(dto.getToDueDate()));
    }
    return whereCondition;
  }

  private OrderSpecifier createOrderSpecifier(SearchJobPostDTO dto) {
    Order order = Order.ASC ;
    if (OrderType.DESC.equals(dto.getOrderType())) {
      order = Order.DESC;
    }

    Path path = jobPost.id;
    if ("dueDate".equals(dto.getSortBy())) {
      path = jobPost.dueDate;
    }
    return new OrderSpecifier(order, path);
  }
}
