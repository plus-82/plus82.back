package com.etplus.repository;

import com.etplus.controller.dto.SearchUserDTO;
import com.etplus.repository.domain.QCountryEntity;
import com.etplus.repository.domain.QFileEntity;
import com.etplus.repository.domain.QUserEntity;
import com.etplus.vo.QUserVO;
import com.etplus.vo.UserVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import org.springframework.util.StringUtils;

public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory query;
  private final QUserEntity user = QUserEntity.userEntity;
  private final QCountryEntity country = QCountryEntity.countryEntity;
  private final QFileEntity profileImage = new QFileEntity("profileImage");

  public UserRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
  }

  @Override
  public Slice<UserVO> findAllUsers(SearchUserDTO dto) {
    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(dto.getEmail())) {
      builder.and(user.email.containsIgnoreCase(dto.getEmail()));
    }
    if (StringUtils.hasText(dto.getFirstName())) {
      builder.and(user.firstName.containsIgnoreCase(dto.getFirstName()));
    }
    if (StringUtils.hasText(dto.getLastName())) {
      builder.and(user.lastName.containsIgnoreCase(dto.getLastName()));
    }
    if (dto.getRoleType() != null) {
      builder.and(user.roleType.eq(dto.getRoleType()));
    }
    if (dto.getCountryId() != null) {
      builder.and(user.country.id.eq(dto.getCountryId()));
    }
    if (dto.getFromDate() != null) {
      builder.and(user.createdAt.goe(dto.getFromDate().atStartOfDay()));
    }
    if (dto.getToDate() != null) {
      builder.and(user.createdAt.loe(dto.getToDate().plusDays(1).atStartOfDay()));
    }
    if (dto.getDeleted() != null) {
      builder.and(user.deleted.eq(dto.getDeleted()));
    } else {
      builder.and(user.deleted.eq(false)); // Default to showing only active users
    }

    JPAQuery<UserVO> jpaQuery = query.select(
            new QUserVO(
                user.id,
                user.firstName,
                user.lastName,
                user.genderType,
                user.birthDate,
                user.email,
                country.id,
                country.countryNameEn,
                country.countryCode,
                country.countryCallingCode,
                country.flag,
                profileImage.path
            ))
        .from(user)
        .leftJoin(user.country, country)
        .leftJoin(user.profileImage, profileImage)
        .where(builder)
        .orderBy(user.id.desc());


    List<UserVO> content = jpaQuery
        .offset((long) dto.getPageNumber() * dto.getRowCount())
        .limit(dto.getRowCount() + 1)
        .fetch();

    boolean hasNext = false;
    if (content.size() > dto.getRowCount()) {
      content.remove(dto.getRowCount());
      hasNext = true;
    }

    return new SliceImpl<>(content, dto.toPageable(), hasNext);
  }
}