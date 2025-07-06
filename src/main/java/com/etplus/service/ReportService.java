package com.etplus.service;

import com.etplus.controller.dto.CreateReportDTO;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.repository.FeedCommentRepository;
import com.etplus.repository.FeedRepository;
import com.etplus.repository.ReportRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.FeedCommentEntity;
import com.etplus.repository.domain.FeedEntity;
import com.etplus.repository.domain.ReportEntity;
import com.etplus.repository.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

  private final ReportRepository reportRepository;
  private final FeedRepository feedRepository;
  private final FeedCommentRepository feedCommentRepository;
  private final UserRepository userRepository;

  @Transactional
  public void reportFeed(Long feedId, CreateReportDTO dto, Long userId) {
    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));

    UserEntity reporter = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    reportRepository.save(new ReportEntity(dto.reason(), dto.otherReason(), reporter, feed));
  }

  @Transactional
  public void reportComment(Long commentId, CreateReportDTO dto, Long userId) {
    FeedCommentEntity comment = feedCommentRepository.findById(commentId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_COMMENT_NOT_FOUND));

    UserEntity reporter = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    reportRepository.save(new ReportEntity(dto.reason(), dto.otherReason(), reporter, comment));
  }
} 