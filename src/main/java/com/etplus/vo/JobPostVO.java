package com.etplus.vo;

import com.etplus.repository.domain.code.LocationType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.List;

public record JobPostVO(
    Long id,
    String title,
    LocalDate dueDate,

    Long academyId,
    String academyName,
    LocationType locationType,
    boolean forKindergarten,
    boolean forElementary,
    boolean forMiddleSchool,
    boolean forHighSchool,
    boolean forAdult,
    List<String> imageUrls
) {

  @QueryProjection
  public JobPostVO {
  }

}
