package com.etplus.controller.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateRepresentativeResumePublicDto(
    @NotNull
    Boolean representativeResumePublic
) {

}

