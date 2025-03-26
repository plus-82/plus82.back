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
  private boolean forKindergarten;
  private boolean forElementary;
  private boolean forMiddleSchool;
  private boolean forHighSchool;
  private boolean forAdult;
  private boolean closed;

  private Long academyId;
  private String academyName;
  private LocationType locationType;
  @JsonIgnore
  private List<Long> imageFileIdList;
  private List<String> imageUrls;

  @QueryProjection
  public JobPostVO(Long id, String title, LocalDate dueDate, boolean forKindergarten,
      boolean forElementary, boolean forMiddleSchool, boolean forHighSchool, boolean forAdult,
      boolean closed, Long academyId, String academyName, LocationType locationType, List<Long> imageFileIdList) {
    this.id = id;
    this.title = title;
    this.dueDate = dueDate;
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
    this.closed = closed;
    this.academyId = academyId;
    this.academyName = academyName;
    this.locationType = locationType;
    this.imageFileIdList = imageFileIdList;
  }
}
