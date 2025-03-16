package com.etplus.vo;

import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.code.LocationType;

public record AcademyVO(
    Long id,
    String name,
    String nameEn,
    String representativeName,
    String representativeEmail,
    String businessRegistrationNumber,
    LocationType locationType,
    String detailedAddress,
    boolean byAdmin
) {

  public static AcademyVO valueOf(AcademyEntity entity) {
    return new AcademyVO(
        entity.getId(),
        entity.getName(),
        entity.getNameEn(),
        entity.getRepresentativeName(),
        entity.getRepresentativeEmail(),
        entity.getBusinessRegistrationNumber(),
        entity.getLocationType(),
        entity.getDetailedAddress(),
        entity.isByAdmin()
    );
  }

}
