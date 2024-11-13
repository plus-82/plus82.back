package com.etplus.repository;

import com.etplus.controller.dto.SearchJobPostDTO;
import com.etplus.repository.domain.QAcademyEntity;
import com.etplus.repository.domain.QJobPostEntity;
import com.etplus.vo.JobPostVO;
import com.etplus.vo.QJobPostVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    JPAQuery<JobPostVO> jpaQuery = query.select(
        new QJobPostVO(
            jobPost.id,
            jobPost.title,
            jobPost.dueDate,
            academy.id,
            academy.name,
            academy.locationType,
            academy.forKindergarten,
            academy.forElementary,
            academy.forMiddleSchool,
            academy.forHighSchool,
            academy.forAdult,
            academy.imageFileIdList
        ))
        .from(jobPost)
        .innerJoin(jobPost.academy, academy)
        .where(whereCondition)
        .orderBy(jobPost.dueDate.desc(), jobPost.id.desc());

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
      whereCondition.and(academy.forKindergarten.eq(dto.getForKindergarten()));
    }
    if (Objects.nonNull(dto.getForElementary())) {
      whereCondition.and(academy.forElementary.eq(dto.getForElementary()));
    }
    if (Objects.nonNull(dto.getForMiddleSchool())) {
      whereCondition.and(academy.forMiddleSchool.eq(dto.getForMiddleSchool()));
    }
    if (Objects.nonNull(dto.getForHighSchool())) {
      whereCondition.and(academy.forHighSchool.eq(dto.getForHighSchool()));
    }
    if (Objects.nonNull(dto.getForAdult())) {
      whereCondition.and(academy.forAdult.eq(dto.getForAdult()));
    }
    return whereCondition;
  }
}
