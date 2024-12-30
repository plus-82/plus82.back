package com.etplus.util;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public abstract class QuerydslRepositorySupportCustom extends QuerydslRepositorySupport {
  protected QuerydslRepositorySupportCustom(Class<?> domainClass) {
    super(domainClass);
  }

  protected final <T> Page<T> applyPagination(JPAQuery<T> jpaQuery, Pageable pageable) {
    Querydsl querydsl = getQuerydsl();
    if (querydsl == null) {
      throw new IllegalStateException("Querydsl is null");
    }
    long totalCount = jpaQuery.fetchCount();
    List<T> result = querydsl.applyPagination(pageable, jpaQuery).fetch();
    return new PageImpl<>(result, pageable, totalCount);
  }
}
