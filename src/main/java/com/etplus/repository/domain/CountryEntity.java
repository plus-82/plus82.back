package com.etplus.repository.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "country")
public class CountryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String countryNameEn;
  @Column(columnDefinition = "TEXT")
  private String countryNameLocal;
  @Column(length = 5)
  private String countryCode;
  @Column(length = 5)
  private String currencyCode;
  private String currencyNameEn;
  @Column(length = 10)
  private String officialLanguageCode;
  private String officialLanguageNameEn;
  private String officialLanguageNameLocal;
  @Column(length = 10)
  private String countryCallingCode;
  @Column(columnDefinition = "TEXT")
  private String areaCodes;
  private String region;
  @Column(length = 5)
  private String flag;

  public CountryEntity(Long id, String countryNameEn, String countryNameLocal, String countryCode,
      String currencyCode, String currencyNameEn, String officialLanguageCode,
      String officialLanguageNameEn, String officialLanguageNameLocal, String countryCallingCode,
      String areaCodes, String region, String flag) {
    this.id = id;
    this.countryNameEn = countryNameEn;
    this.countryNameLocal = countryNameLocal;
    this.countryCode = countryCode;
    this.currencyCode = currencyCode;
    this.currencyNameEn = currencyNameEn;
    this.officialLanguageCode = officialLanguageCode;
    this.officialLanguageNameEn = officialLanguageNameEn;
    this.officialLanguageNameLocal = officialLanguageNameLocal;
    this.countryCallingCode = countryCallingCode;
    this.areaCodes = areaCodes;
    this.region = region;
    this.flag = flag;
  }
}
