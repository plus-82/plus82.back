package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateFeedCommentDTO;
import com.etplus.controller.dto.CreateFeedDTO;
import com.etplus.controller.dto.SearchFeedDTO;
import com.etplus.controller.dto.UpdateFeedCommentDTO;
import com.etplus.controller.dto.UpdateFeedDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.FeedService;
import com.etplus.vo.FeedDetailVO;
import com.etplus.vo.FeedVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feeds")
public class FeedController {

  private final FeedService feedService;

  @GetMapping
  public CommonResponse<Slice<FeedVO>> getFeeds(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @Valid SearchFeedDTO dto) {
    Slice<FeedVO> result = feedService.getFeeds(loginUser.userId(), dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/public")
  public CommonResponse<Slice<FeedVO>> getPublicFeeds(
      @Valid SearchFeedDTO dto) {
    Slice<FeedVO> result = feedService.getPublicFeeds(dto);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/{feed-id}")
  public CommonResponse<FeedDetailVO> getFeedDetail(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId) {
    FeedDetailVO result = feedService.getFeedDetail(loginUser.userId(), feedId);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

  @GetMapping("/public/{feed-id}")
  public CommonResponse<FeedDetailVO> getPublicFeedDetail(
      @PathVariable("feed-id") Long feedId) {
    FeedDetailVO result = feedService.getFeedDetail(null, feedId);
    return new CommonResponse<>(result, CommonResponseCode.SUCCESS);
  }

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

  @PostMapping("/{feed-id}/like")
  public CommonResponse<Void> addFeedLike(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId) {
    feedService.addFeedLike(loginUser.userId(), feedId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/{feed-id}/like")
  public CommonResponse<Void> removeFeedLike(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId) {
    feedService.removeFeedLike(loginUser.userId(), feedId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/{feed-id}/comments")
  public CommonResponse<Void> createFeedComment(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @Valid @RequestBody CreateFeedCommentDTO dto) {
    feedService.createFeedComment(loginUser.userId(), feedId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PutMapping("/{feed-id}/comments/{comment-id}")
  public CommonResponse<Void> updateFeedComment(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @PathVariable("comment-id") Long commentId,
      @Valid @RequestBody UpdateFeedCommentDTO dto) {
    feedService.updateFeedComment(loginUser.userId(), feedId, commentId, dto);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/{feed-id}/comments/{comment-id}")
  public CommonResponse<Void> deleteFeedComment(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @PathVariable("comment-id") Long commentId) {
    feedService.deleteFeedComment(loginUser.userId(), feedId, commentId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/{feed-id}/comments/{comment-id}/like")
  public CommonResponse<Void> addFeedCommentLike(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @PathVariable("comment-id") Long commentId) {
    feedService.addFeedCommentLike(loginUser.userId(), feedId, commentId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @DeleteMapping("/{feed-id}/comments/{comment-id}/like")
  public CommonResponse<Void> removeFeedCommentLike(
      @AuthUser({RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @PathVariable("comment-id") Long commentId) {
    feedService.removeFeedCommentLike(loginUser.userId(), feedId, commentId);
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

}
