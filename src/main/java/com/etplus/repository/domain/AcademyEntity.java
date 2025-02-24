package com.etplus.repository.domain;

import com.etplus.repository.domain.code.LocationType;
import com.etplus.repository.domain.converter.LongListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Entity
@Table(name = "academy")
public class AcademyEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;                        // 학원 이름
  @Column(nullable = false)
  private String representativeName;          // 대표자명
  @Column(length = 1000)
  private String description;                 // 학원 설명
  @Column(nullable = false, unique = true, length = 20)
  private String businessRegistrationNumber;  // 사업자등록번호

  @Enumerated(EnumType.STRING)
  private LocationType locationType;          // 위치 (시,도)
  private String detailedAddress;             // 상세 주소
  private double lat;
  private double lng;

  // 대상
  private boolean forKindergarten;
  private boolean forElementary;
  private boolean forMiddleSchool;
  private boolean forHighSchool;
  private boolean forAdult;

  // Image List
  @Convert(converter = LongListConverter.class)
  private List<Long> imageFileIdList;

  public AcademyEntity(Long id, String name, String representativeName, String description,
      String businessRegistrationNumber, LocationType locationType, String detailedAddress,
      double lat, double lng, boolean forKindergarten, boolean forElementary,
      boolean forMiddleSchool,
      boolean forHighSchool, boolean forAdult, List<Long> imageFileIdList) {
    this.id = id;
    this.name = name;
    this.representativeName = representativeName;
    this.description = description;
    this.businessRegistrationNumber = businessRegistrationNumber;
    this.locationType = locationType;
    this.detailedAddress = detailedAddress;
    this.lat = lat;
    this.lng = lng;
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
    this.imageFileIdList = imageFileIdList;
  }
}
