package com.etplus.controller;

import com.etplus.common.AuthUser;
import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateReportDTO;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/feeds/{feed-id}")
  public CommonResponse<Void> reportFeed(
      @AuthUser({RoleType.ADMIN, RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("feed-id") Long feedId,
      @RequestBody @Valid CreateReportDTO dto) {
    reportService.reportFeed(feedId, dto, loginUser.userId());
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }

  @PostMapping("/comments/{comment-id}")
  public CommonResponse<Void> reportComment(
      @AuthUser({RoleType.ADMIN, RoleType.ACADEMY, RoleType.TEACHER}) LoginUser loginUser,
      @PathVariable("comment-id") Long commentId,
      @RequestBody @Valid CreateReportDTO dto) {
    reportService.reportComment(commentId, dto, loginUser.userId());
    return new CommonResponse<>(CommonResponseCode.SUCCESS);
  }
} 