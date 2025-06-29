package com.etplus.repository;

import com.etplus.repository.domain.QFeedCommentEntity;
import com.etplus.repository.domain.QFeedCommentLike;
import com.etplus.repository.domain.QUserEntity;
import com.etplus.vo.FeedDetailVO;
import com.etplus.vo.FeedDetailVO.CommentVO;
import com.etplus.vo.QFeedDetailVO_CommentVO;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public class FeedCommentRepositoryImpl implements FeedCommentRepositoryCustom {

  private final JPAQueryFactory query;
  private QFeedCommentEntity feedComment;
  private QUserEntity creator;
  private QFeedCommentLike feedCommentLike;

  public FeedCommentRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    feedComment = new QFeedCommentEntity("feedComment");
    creator = new QUserEntity("creator");
    feedCommentLike = new QFeedCommentLike("feedCommentLike");
  }

  @Override
  public List<CommentVO> findAllByFeedId(long feedId) {
    JPAQuery<FeedDetailVO.CommentVO> jpaQuery = query.select(
            new QFeedDetailVO_CommentVO(
                feedComment.id,
                feedComment.comment,
                feedComment.createdAt,
                feedComment.likeCount,
                creator.id,
                creator.name,
                Expressions.constant(false)
            ))
        .from(feedComment)
        .innerJoin(feedComment.user, creator)
        .where(feedComment.feed.id.eq(feedId))
        .orderBy(feedComment.createdAt.asc());

    return jpaQuery.fetch();
  }

  @Override
  public List<CommentVO> findAllByUserIdAndFeedId(long userId, long feedId) {
    JPAQuery<FeedDetailVO.CommentVO> jpaQuery = query.select(
            new QFeedDetailVO_CommentVO(
                feedComment.id,
                feedComment.comment,
                feedComment.createdAt,
                feedComment.likeCount,
                creator.id,
                creator.name,
                JPAExpressions
                    .selectOne()
                    .from(feedCommentLike)
                    .where(feedCommentLike.user.id.eq(userId))
                    .exists()
            ))
        .from(feedComment)
        .innerJoin(feedComment.user, creator)
        .where(feedComment.feed.id.eq(feedId))
        .orderBy(feedComment.createdAt.asc());

    return jpaQuery.fetch();
  }

}