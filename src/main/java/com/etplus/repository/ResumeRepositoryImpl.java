package com.etplus.repository;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.controller.dto.SearchRepresentativeResumeDto;
import com.etplus.repository.domain.QCountryEntity;
import com.etplus.repository.domain.QFileEntity;
import com.etplus.repository.domain.QResumeEntity;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.util.QuerydslRepositorySupportCustom;
import com.etplus.vo.QRepresentativeResumeVO;
import com.etplus.vo.QResumeVO;
import com.etplus.vo.RepresentativeResumeVO;
import com.etplus.vo.ResumeVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class ResumeRepositoryImpl extends QuerydslRepositorySupportCustom
    implements ResumeRepositoryCustom {

  private final JPAQueryFactory query;
  private QResumeEntity resume;
  private QFileEntity file;
  private QCountryEntity country;

  public ResumeRepositoryImpl(JPAQueryFactory query) {
    super(ResumeEntity.class);
    this.query = query;
    resume = new QResumeEntity("resume");
    file = new QFileEntity("file");
    country = new QCountryEntity("country");
  }

  @Override
  public Slice<ResumeVO> findAllByUserId(long userId, PagingDTO dto) {
    JPAQuery<ResumeVO> jpaQuery = query.select(
        new QResumeVO(
            resume.id,
            resume.title,
            resume.firstName,
            resume.lastName,
            resume.email,
            resume.hasVisa,
            resume.visaType,
            resume.isRepresentative,
            resume.isDraft,
            resume.createdAt,
            resume.updatedAt,
            file.path,
            file.fileName
        ))
        .where(resume.user.id.eq(userId))
        .from(resume)
        .leftJoin(resume.file, file)
        .orderBy(resume.id.desc());

    List<ResumeVO> content = jpaQuery
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
  public Page<RepresentativeResumeVO> findAllRepresentativeResumes(
      SearchRepresentativeResumeDto dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();

    if (dto.getGenderType() != null) {
      whereCondition.and(resume.genderType.eq(dto.getGenderType()));
    }
    if (dto.getCountryId() != null) {
      whereCondition.and(country.id.eq(dto.getCountryId()));
    }
    if (dto.getFromBirthDate() != null) {
      whereCondition.and(resume.birthDate.goe(dto.getFromBirthDate()));
    }
    if (dto.getToBirthDate() != null) {
      whereCondition.and(resume.birthDate.loe(dto.getToBirthDate()));
    }
    if (dto.getHasVisa() != null) {
      whereCondition.and(resume.hasVisa.eq(dto.getHasVisa()));
    }
    if (dto.getVisaType() != null) {
      whereCondition.and(resume.visaType.eq(dto.getVisaType()));
    }

    JPAQuery<RepresentativeResumeVO> jpaQuery = query.select(
        new QRepresentativeResumeVO(
            resume.id,
            resume.title,
            resume.firstName,
            resume.lastName,
            resume.email,
            resume.hasVisa,
            resume.visaType,
            resume.genderType,
            resume.birthDate,
            resume.createdAt,
            resume.updatedAt,
            country.id,
            country.countryNameEn,
            country.countryCode,
            resume.user.id
        ))
        .from(resume)
        .where(whereCondition)
        .leftJoin(resume.country, country)
        .orderBy(resume.updatedAt.desc());

    return applyPagination(jpaQuery, dto.toPageable());
  }
}
