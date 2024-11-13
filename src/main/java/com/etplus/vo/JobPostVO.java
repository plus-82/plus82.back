package com.etplus.vo;

import com.etplus.repository.domain.code.LocationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobPostVO {

  private Long id;
  private String title;
  private LocalDate dueDate;

  private Long academyId;
  private String academyName;
  private LocationType locationType;
  private boolean forKindergarten;
  private boolean forElementary;
  private boolean forMiddleSchool;
  private boolean forHighSchool;
  private boolean forAdult;
  @JsonIgnore
  private List<Long> imageFileIdList;
  private List<String> imageUrls;

  @QueryProjection
  public JobPostVO(Long id, String title, LocalDate dueDate, Long academyId, String academyName,
      LocationType locationType, boolean forKindergarten, boolean forElementary,
      boolean forMiddleSchool, boolean forHighSchool, boolean forAdult,
      List<Long> imageFileIdList) {
    this.id = id;
    this.title = title;
    this.dueDate = dueDate;
    this.academyId = academyId;
    this.academyName = academyName;
    this.locationType = locationType;
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
    this.imageFileIdList = imageFileIdList;
  }
}
