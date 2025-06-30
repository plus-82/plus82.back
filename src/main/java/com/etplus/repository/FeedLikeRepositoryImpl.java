package com.etplus.repository;

import com.etplus.repository.domain.QFeedLikeEntity;
import com.etplus.repository.domain.QFileEntity;
import com.etplus.repository.domain.QUserEntity;
import com.etplus.vo.FeedLikeVO;
import com.etplus.vo.QFeedLikeVO;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public class FeedLikeRepositoryImpl implements FeedLikeRepositoryCustom {

  private final JPAQueryFactory query;
  private QFeedLikeEntity feedLike;
  private QUserEntity user;
  private QFileEntity profileImage;

  public FeedLikeRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
    feedLike = new QFeedLikeEntity("feedLike");
    user = new QUserEntity("user");
    profileImage = new QFileEntity("profileImage");
  }

  @Override
  public List<FeedLikeVO> findAllByFeedId(long feedId) {
    JPAQuery<FeedLikeVO> jpaQuery = query.select(
            new QFeedLikeVO(
                feedLike.id,
                feedLike.createdAt,
                user.id,
                user.name,
                profileImage.path
            ))
        .from(feedLike)
        .innerJoin(feedLike.user, user)
        .leftJoin(user.profileImage, profileImage)
        .where(feedLike.feed.id.eq(feedId))
        .orderBy(feedLike.id.asc());
    return jpaQuery.fetch();
  }

}