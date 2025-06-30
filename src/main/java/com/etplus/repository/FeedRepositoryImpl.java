package com.etplus.repository;

import com.etplus.controller.dto.SearchFeedDTO;
import com.etplus.repository.domain.QFeedCommentEntity;
import com.etplus.repository.domain.QFeedEntity;
import com.etplus.repository.domain.QFeedLikeEntity;
import com.etplus.repository.domain.QFileEntity;
import com.etplus.repository.domain.QUserEntity;
import com.etplus.repository.domain.code.FeedVisibility;
import com.etplus.vo.FeedVO;
import com.etplus.vo.QFeedVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import java.util.List;

public class FeedRepositoryImpl implements FeedRepositoryCustom {

  private final JPAQueryFactory query;
  private QFeedEntity feed;
  private QUserEntity creator;
  private QFileEntity creatorProfileImage;
  private QFileEntity image;
  private QFeedLikeEntity userLike;
  private QFeedCommentEntity userComment;

  public FeedRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    feed = new QFeedEntity("feed");
    creator = new QUserEntity("creator");
    creatorProfileImage = new QFileEntity("creatorProfileImage");
    image = new QFileEntity("image");
    userLike = new QFeedLikeEntity("userLike");
    userComment = new QFeedCommentEntity("userComment");
  }

  @Override
  public Slice<FeedVO> findAllFeeds(long userId, SearchFeedDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();
    if (StringUtils.hasText(dto.getKeyword())) {
      whereCondition.and(feed.content.containsIgnoreCase(dto.getKeyword())
          .or(creator.name.containsIgnoreCase(dto.getKeyword())));
    }

    JPAQuery<FeedVO> jpaQuery = query.select(
            new QFeedVO(
                feed.id,
                feed.content,
                feed.createdAt,
                creator.name,
                creatorProfileImage.path,
                image.path,
                feed.commentCount,
                feed.likeCount,
                JPAExpressions
                    .selectOne()
                    .from(userLike)
                    .where(userLike.user.id.eq(userId)
                        .and(userLike.feed.id.eq(feed.id)))
                    .exists(),
                JPAExpressions
                    .selectOne()
                    .from(userComment)
                    .where(userComment.user.id.eq(userId)
                        .and(userComment.feed.id.eq(feed.id)))
                    .exists()
            ))
        .from(feed)
        .innerJoin(feed.createdUser, creator)
        .leftJoin(creator.profileImage, creatorProfileImage)
        .leftJoin(feed.image, image)
        .where(whereCondition
            .and(feed.deleted.isFalse()))
        .orderBy(feed.createdAt.desc());

    List<FeedVO> content = jpaQuery
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
  public Slice<FeedVO> findAllPublicFeeds(SearchFeedDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();
    if (StringUtils.hasText(dto.getKeyword())) {
      whereCondition.and(feed.content.containsIgnoreCase(dto.getKeyword())
          .or(creator.name.containsIgnoreCase(dto.getKeyword())));
    }

    JPAQuery<FeedVO> jpaQuery = query.select(
            new QFeedVO(
                feed.id,
                feed.content,
                feed.createdAt,
                creator.name,
                creatorProfileImage.path,
                image.path,
                feed.commentCount,
                feed.likeCount,
                Expressions.constant(false),
                Expressions.constant(false)
            ))
        .from(feed)
        .innerJoin(feed.createdUser, creator)
        .leftJoin(creator.profileImage, creatorProfileImage)
        .leftJoin(feed.image, image)
        .where(whereCondition
            .and(feed.deleted.isFalse())
            .and(feed.feedVisibility.eq(FeedVisibility.PUBLIC)))
        .orderBy(feed.createdAt.desc());

    List<FeedVO> content = jpaQuery
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