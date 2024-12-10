package com.etplus.controller.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateResumeWithFileDTO(
    @NotNull
    MultipartFile file
) {

}
