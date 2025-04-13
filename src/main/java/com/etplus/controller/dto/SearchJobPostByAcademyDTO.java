package com.etplus.controller.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchJobPostByAcademyDTO extends PagingDTO {

  private Boolean closed;
  private LocalDate fromDueDate;
  private LocalDate toDueDate;

}
