package com.etplus.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFeedCommentDTO(
    @NotBlank
    @Size(max = 200, message = "댓글은 최대 200자까지 입력 가능합니다")
    String comment
) {

} 