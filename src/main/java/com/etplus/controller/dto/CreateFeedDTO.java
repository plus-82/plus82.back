package com.etplus.controller.dto;

import com.etplus.repository.domain.code.FeedVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateFeedDTO(
    @NotBlank
    String content,
    @NotNull
    FeedVisibility feedVisibility,
    MultipartFile image
) {

} 