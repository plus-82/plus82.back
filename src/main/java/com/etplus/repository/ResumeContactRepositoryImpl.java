package com.etplus.repository;

import com.etplus.controller.dto.SearchResumeContactDTO;
import com.etplus.repository.domain.QCountryEntity;
import com.etplus.repository.domain.QResumeContactEntity;
import com.etplus.repository.domain.ResumeContactEntity;
import com.etplus.util.QuerydslRepositorySupportCustom;
import com.etplus.vo.QResumeContactVO;
import com.etplus.vo.ResumeContactVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

public class ResumeContactRepositoryImpl extends QuerydslRepositorySupportCustom
    implements ResumeContactRepositoryCustom {

  private final JPAQueryFactory query;
  private QResumeContactEntity resumeContact;
  private QCountryEntity country;

  public ResumeContactRepositoryImpl(JPAQueryFactory query) {
    super(ResumeContactEntity.class);
    this.query = query;
    resumeContact = new QResumeContactEntity("resumeContact");
    country = new QCountryEntity("country");
  }

  @Override
  public Page<ResumeContactVO> findAllByAcademyUserId(long academyUserId,
      SearchResumeContactDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();
    whereCondition.and(resumeContact.academyUser.id.eq(academyUserId));

    if (dto.getGenderType() != null) {
      whereCondition.and(resumeContact.genderType.eq(dto.getGenderType()));
    }
    if (dto.getCountryIdList() != null && !dto.getCountryIdList().isEmpty()) {
      whereCondition.and(country.id.in(dto.getCountryIdList()));
    }
    if (dto.getFromAge() != null) {
      // fromAge 이상: 현재 날짜에서 fromAge년 전 이전에 태어난 사람들
      whereCondition.and(resumeContact.birthDate.loe(LocalDate.now().minusYears(dto.getFromAge())));
    }
    if (dto.getToAge() != null) {
      // toAge 이하: 현재 날짜에서 (toAge+1)년 전 이후에 태어난 사람들
      whereCondition.and(resumeContact.birthDate.goe(LocalDate.now().minusYears(dto.getToAge() + 1)));
    }
    if (dto.getVisaTypeList() != null && !dto.getVisaTypeList().isEmpty()) {
      whereCondition.and(resumeContact.visaType.in(dto.getVisaTypeList()));
    }
    if (dto.getForKindergarten() != null) {
      whereCondition.and(resumeContact.forKindergarten.eq(dto.getForKindergarten()));
    }
    if (dto.getForElementary() != null) {
      whereCondition.and(resumeContact.forElementary.eq(dto.getForElementary()));
    }
    if (dto.getForMiddleSchool() != null) {
      whereCondition.and(resumeContact.forMiddleSchool.eq(dto.getForMiddleSchool()));
    }
    if (dto.getForHighSchool() != null) {
      whereCondition.and(resumeContact.forHighSchool.eq(dto.getForHighSchool()));
    }
    if (dto.getForAdult() != null) {
      whereCondition.and(resumeContact.forAdult.eq(dto.getForAdult()));
    }

    JPAQuery<ResumeContactVO> jpaQuery = query.select(
        new QResumeContactVO(
            resumeContact.id,
            resumeContact.interestReason,
            resumeContact.appealMessage,
            resumeContact.additionalMessage,
            resumeContact.contactEmail,
            resumeContact.resumeId,
            resumeContact.resumeTitle,
            resumeContact.firstName,
            resumeContact.lastName,
            resumeContact.email,
            resumeContact.degree,
            resumeContact.major,
            resumeContact.genderType,
            resumeContact.birthDate,
            resumeContact.hasVisa,
            resumeContact.visaType,
            resumeContact.forKindergarten,
            resumeContact.forElementary,
            resumeContact.forMiddleSchool,
            resumeContact.forHighSchool,
            resumeContact.forAdult,
            country.id,
            country.countryNameEn,
            resumeContact.teacher.id,
            resumeContact.academyUser.id,
            resumeContact.createdAt,
            resumeContact.updatedAt
        ))
        .from(resumeContact)
        .leftJoin(resumeContact.country, country)
        .where(whereCondition)
        .orderBy(resumeContact.createdAt.desc());

    return applyPagination(jpaQuery, dto.toPageable());
  }
}

