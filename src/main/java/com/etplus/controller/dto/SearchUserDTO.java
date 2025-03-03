package com.etplus.controller.dto;

import com.etplus.repository.domain.code.RoleType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchUserDTO extends PagingDTO {

  private String email;
  private String firstName;
  private String lastName;
  private RoleType roleType;
  private Long countryId;
  private LocalDate fromDate;
  private LocalDate toDate;
  private Boolean deleted;

}