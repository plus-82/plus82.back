package com.etplus.vo;

import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.code.LocationType;
import com.etplus.vo.common.ImageVO;
import java.util.List;

public record AcademyDetailVO(
    Long id,
    String name,
    String nameEn,
    String representativeName,
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
    List<ImageVO> imageList
) {

  public static AcademyDetailVO valueOf(AcademyEntity academy, List<ImageVO> imageList) {
    return new AcademyDetailVO(
        academy.getId(),
        academy.getName(),
        academy.getNameEn(),
        academy.getRepresentativeName(),
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
        imageList
    );
  }

}
