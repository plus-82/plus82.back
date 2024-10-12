package com.etplus.repository.domain;

import com.etplus.repository.domain.code.LocationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Entity
@Table(name = "academy_entity")
public class AcademyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;
  @Column(length = 1000)
  private String description;
  @Column(nullable = false, unique = true, length = 20)
  private String businessRegistrationNumber;  // 사업자등록번호

  @Enumerated(EnumType.STRING)
  private LocationType locationType;
  private String detailedAddress;

  // 대상
  private boolean forKindergarten;
  private boolean forElementary;
  private boolean forMiddleSchool;
  private boolean forHighSchool;
  private boolean forAdult;

  // todo image list?

  public AcademyEntity(Long id, String name, String description, String businessRegistrationNumber,
      LocationType locationType, String detailedAddress, boolean forKindergarten,
      boolean forElementary, boolean forMiddleSchool, boolean forHighSchool, boolean forAdult) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.businessRegistrationNumber = businessRegistrationNumber;
    this.locationType = locationType;
    this.detailedAddress = detailedAddress;
    this.forKindergarten = forKindergarten;
    this.forElementary = forElementary;
    this.forMiddleSchool = forMiddleSchool;
    this.forHighSchool = forHighSchool;
    this.forAdult = forAdult;
  }
}
