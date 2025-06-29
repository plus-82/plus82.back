package com.etplus.repository;

import com.etplus.vo.FeedDetailVO.CommentVO;
import java.util.List;

interface FeedCommentRepositoryCustom {

  List<CommentVO> findAllByFeedId(long feedId);

  List<CommentVO> findAllByUserIdAndFeedId(long userId, long feedId);

} 