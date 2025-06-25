package com.etplus.repository;

import com.etplus.controller.dto.SearchFeedDTO;
import com.etplus.vo.FeedVO;
import org.springframework.data.domain.Slice;

interface FeedRepositoryCustom {

  Slice<FeedVO> findAllFeeds(long userId, SearchFeedDTO dto);
  Slice<FeedVO> findAllPublicFeeds(SearchFeedDTO dto);

} 