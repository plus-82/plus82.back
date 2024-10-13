package com.etplus.repository;

import com.etplus.repository.domain.QCountryEntity;
import com.etplus.vo.CountryVO;
import com.etplus.vo.QCountryVO;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepositoryImpl implements CountryRepositoryCustom {

  private final JPAQueryFactory query;
  private QCountryEntity country;

  public CountryRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    country = new QCountryEntity("country");
  }

  @Override
  public List<CountryVO> findAllCountry() {
    JPAQuery<CountryVO> jpaQuery = query.select(
            new QCountryVO(
                country.id,
                country.countryNameEn,
                country.countryNameLocal,
                country.countryCode,
                country.countryCallingCode,
                country.flag
            ))
        .from(country)
        .orderBy(country.id.asc());

    return jpaQuery.fetch();
  }
}
