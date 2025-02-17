package com.etplus.repository;

import com.etplus.controller.dto.PagingDTO;
import com.etplus.repository.domain.QFileEntity;
import com.etplus.repository.domain.QResumeEntity;
import com.etplus.vo.QResumeVO;
import com.etplus.vo.ResumeVO;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class ResumeRepositoryImpl implements ResumeRepositoryCustom {

  private final JPAQueryFactory query;
  private QResumeEntity resume;
  private QFileEntity file;

  public ResumeRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    resume = new QResumeEntity("resume");
    file = new QFileEntity("file");
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

}
