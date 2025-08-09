package com.etplus.repository;

import com.etplus.repository.domain.QFeedCommentEntity;
import com.etplus.repository.domain.QFeedCommentLike;
import com.etplus.repository.domain.QFileEntity;
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
  private QFileEntity creatorProfileImage;

  public FeedCommentRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    feedComment = new QFeedCommentEntity("feedComment");
    creator = new QUserEntity("creator");
    feedCommentLike = new QFeedCommentLike("feedCommentLike");
    creatorProfileImage = new QFileEntity("creatorProfileImage");
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
                Expressions.constant(false),
                creatorProfileImage.path
            ))
        .from(feedComment)
        .innerJoin(feedComment.user, creator)
        .leftJoin(creator.profileImage, creatorProfileImage)
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
                    .where(feedCommentLike.user.id.eq(userId)
                        .and(feedCommentLike.feedComment.id.eq(feedComment.id)))
                    .exists(),
                creatorProfileImage.path
            ))
        .from(feedComment)
        .innerJoin(feedComment.user, creator)
        .leftJoin(creator.profileImage, creatorProfileImage)
        .where(feedComment.feed.id.eq(feedId))
        .orderBy(feedComment.createdAt.asc());

    return jpaQuery.fetch();
  }

}