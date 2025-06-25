package com.etplus.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFeedDTO extends PagingDTO {

  private String keyword; // 검색어

} 