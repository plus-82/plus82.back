package com.etplus.vo;

import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.code.LocationType;
import java.util.List;

public record AcademyDetailByAdminVO(
    Long id,
    String name,
    String nameEn,
    String representativeName,
    String representativeEmail,
    String description,
    String businessRegistrationNumber,
    LocationType locationType,
    String detailedAddress,
    double lat,
    double lng,
    boolean forKindergarten,
    boolean forElementary,
    boolean forMiddleSchool,
    boolean forHighSchool,
    boolean forAdult,
    List<String> imageUrls
) {

  public static AcademyDetailByAdminVO valueOf(AcademyEntity academy, List<String> imageUrls) {
    return new AcademyDetailByAdminVO(
        academy.getId(),
        academy.getName(),
        academy.getNameEn(),
        academy.getRepresentativeName(),
        academy.getRepresentativeEmail(),
        academy.getDescription(),
        academy.getBusinessRegistrationNumber(),
        academy.getLocationType(),
        academy.getDetailedAddress(),
        academy.getLat(),
        academy.getLng(),
        academy.isForKindergarten(),
        academy.isForElementary(),
        academy.isForMiddleSchool(),
        academy.isForHighSchool(),
        academy.isForAdult(),
        imageUrls
    );
  }

}
