package com.etplus.repository;

import com.etplus.vo.FeedLikeVO;
import java.util.List;

interface FeedLikeRepositoryCustom {

  List<FeedLikeVO> findAllByFeedId(long feedId);

} 