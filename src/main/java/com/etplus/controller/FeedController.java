package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateFeedDTO;
import com.etplus.controller.dto.UpdateFeedDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feeds")
public class FeedController {

  private final FeedService feedService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> createFeed(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @Valid @ModelAttribute CreateFeedDTO dto) {
    feedService.createFeed(loginUser.userId(), dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping(value = "/{feed-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommonResponse<Void> updateFeed(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @Valid @ModelAttribute UpdateFeedDTO dto) {
    feedService.updateFeed(loginUser.userId(), feedId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/{feed-id}")
  public CommonResponse<Void> deleteFeed(
      @AuthUser({RoleType.ADMIN, RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId) {
    feedService.deleteFeed(loginUser.userId(), loginUser.roleType(), feedId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

}
