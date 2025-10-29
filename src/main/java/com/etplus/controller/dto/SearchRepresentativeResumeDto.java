package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRepresentativeResumeDto extends PagingDTO {

  private GenderType genderType;
  private List<Long> countryIdList;
  private LocalDate fromBirthDate;
  private LocalDate toBirthDate;
  private Boolean hasVisa;
  private List<VisaType> visaTypeList;

  private Boolean forKindergarten;
  private Boolean forElementary;
  private Boolean forMiddleSchool;
  private Boolean forHighSchool;
  private Boolean forAdult;

}
