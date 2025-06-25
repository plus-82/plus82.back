package com.etplus.repository;

import com.etplus.controller.dto.SearchFeedDTO;
import com.etplus.repository.domain.QFeedCommentEntity;
import com.etplus.repository.domain.QFeedEntity;
import com.etplus.repository.domain.QFeedLike;
import com.etplus.repository.domain.QFileEntity;
import com.etplus.repository.domain.QUserEntity;
import com.etplus.repository.domain.code.FeedVisibility;
import com.etplus.vo.FeedVO;
import com.etplus.vo.QFeedVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
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
  private QFeedCommentEntity comment;
  private QFeedLike like;
  private QFeedLike userLike;
  private QFeedCommentEntity userComment;

  public FeedRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    feed = new QFeedEntity("feed");
    creator = new QUserEntity("creator");
    creatorProfileImage = new QFileEntity("creatorProfileImage");
    image = new QFileEntity("image");
    comment = new QFeedCommentEntity("comment");
    like = new QFeedLike("like");
    userLike = new QFeedLike("userLike");
    userComment = new QFeedCommentEntity("userComment");
  }

  @Override
  public Slice<FeedVO> findAllFeeds(long userId, SearchFeedDTO dto) {
    BooleanBuilder whereCondition = new BooleanBuilder();
    if (StringUtils.hasText(dto.getKeyword())) {
      whereCondition.and(feed.content.containsIgnoreCase(dto.getKeyword())
          .or(creator.fullName.containsIgnoreCase(dto.getKeyword()))
          .or(creator.firstName.containsIgnoreCase(dto.getKeyword()))
          .or(creator.lastName.containsIgnoreCase(dto.getKeyword())));
    }

    JPAQuery<FeedVO> jpaQuery = query.select(
            new QFeedVO(
                feed.id,
                feed.content,
                feed.createdAt,
                creator.firstName,
                creator.lastName,
                creator.fullName,
                creatorProfileImage.path,
                image.path,
                comment.count(),
                like.count(),
                userLike.id.isNotNull(),
                userComment.id.isNotNull()
            ))
        .from(feed)
        .innerJoin(feed.createdUser, creator)
        .leftJoin(creator.profileImage, creatorProfileImage)
        .leftJoin(feed.image, image)
        .leftJoin(comment).on(comment.feed.eq(feed))
        .leftJoin(like).on(like.feed.eq(feed))
        .leftJoin(userLike).on(userLike.feed.eq(feed).and(userLike.user.id.eq(userId)))
        .leftJoin(userComment).on(userComment.feed.eq(feed).and(userComment.user.id.eq(userId)))
        .where(whereCondition
            .and(feed.deleted.isFalse()))
        .groupBy(feed.id, feed.content, image.path, feed.feedVisibility, feed.createdAt, userLike.id, userComment.id)
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
          .or(creator.fullName.containsIgnoreCase(dto.getKeyword()))
          .or(creator.firstName.containsIgnoreCase(dto.getKeyword()))
          .or(creator.lastName.containsIgnoreCase(dto.getKeyword())));
    }

    JPAQuery<FeedVO> jpaQuery = query.select(
            new QFeedVO(
                feed.id,
                feed.content,
                feed.createdAt,
                creator.firstName,
                creator.lastName,
                creator.fullName,
                creatorProfileImage.path,
                image.path,
                comment.count(),
                like.count(),
                Expressions.constant(false),
                Expressions.constant(false)
            ))
        .from(feed)
        .innerJoin(feed.createdUser, creator)
        .leftJoin(creator.profileImage, creatorProfileImage)
        .leftJoin(feed.image, image)
        .leftJoin(comment).on(comment.feed.eq(feed))
        .leftJoin(like).on(like.feed.eq(feed))
        .where(whereCondition
            .and(feed.deleted.isFalse())
            .and(feed.feedVisibility.eq(FeedVisibility.PUBLIC)))
        .groupBy(feed.id, feed.content, image.path, feed.feedVisibility, feed.createdAt)
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