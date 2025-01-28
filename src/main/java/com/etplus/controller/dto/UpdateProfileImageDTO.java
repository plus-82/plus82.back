package com.etplus.controller.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileImageDTO(
    @NotNull
    MultipartFile image
) {

}
