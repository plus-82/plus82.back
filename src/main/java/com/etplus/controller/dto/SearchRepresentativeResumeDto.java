package com.etplus.controller.dto;

import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.VisaType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRepresentativeResumeDto extends PagingDTO {

  private GenderType genderType;
  private Long countryId;
  private LocalDate fromBirthDate;
  private LocalDate toBirthDate;
  private Boolean hasVisa;
  private VisaType visaType;

}
