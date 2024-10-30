package com.etplus.vo;

import com.etplus.repository.domain.code.LocationType;
import java.util.List;

public record AcademyDetailVO(
    Long id,
    String name,
    String description,
    String businessRegistrationNumber,
    LocationType locationType,
    String detailedAddress,
    boolean forKindergarten,
    boolean forElementary,
    boolean forMiddleSchool,
    boolean forHighSchool,
    boolean forAdult,
    List<String> imageUrls
) {

}
