package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResumeContactDTO extends PagingDTO {

  private GenderType genderType;
  private Long countryId;
  private VisaType visaType;
  private LocalDate fromBirthDate;
  private LocalDate toBirthDate;

  private Boolean forKindergarten;
  private Boolean forElementary;
  private Boolean forMiddleSchool;
  private Boolean forHighSchool;
  private Boolean forAdult;
}
