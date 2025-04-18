package com.etplus.controller.dto;

import com.etplus.controller.dto.code.OrderType;
import com.etplus.repository.domain.code.LocationType;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchJobPostDTO extends PagingDTO{

  private String searchText; // job title, academy name
  private List<LocationType> locationTypeList;
  private Boolean forKindergarten;
  private Boolean forElementary;
  private Boolean forMiddleSchool;
  private Boolean forHighSchool;
  private Boolean forAdult;
  private Boolean closed;

  private LocalDate fromDueDate;
  private LocalDate toDueDate;

  private OrderType orderType = OrderType.ASC;
  private String sortBy = "id";  // id, dueDate

}
