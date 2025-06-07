package com.etplus.vo;

import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.code.LocationType;
import java.time.LocalDateTime;

public record AcademyVO(
    Long id,
    String name,
    String nameEn,
    String representativeName,
    String representativeEmail,
    String businessRegistrationNumber,
    LocationType locationType,
    String address,
    String detailedAddress,
    LocalDateTime updatedAt,
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
        entity.getAddress(),
        entity.getDetailedAddress(),
        entity.getUpdatedAt(),
        entity.isByAdmin()
    );
  }

}
