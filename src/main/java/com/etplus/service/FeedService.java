package com.etplus.service;

import com.etplus.controller.dto.CreateFeedCommentDTO;
import com.etplus.controller.dto.CreateFeedDTO;
import com.etplus.controller.dto.UpdateFeedCommentDTO;
import com.etplus.controller.dto.UpdateFeedDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.FeedException;
import com.etplus.exception.FeedException.FeedExceptionCode;
import com.etplus.exception.ResourceDeniedException;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.FeedCommentRepository;
import com.etplus.repository.FeedLikeRepository;
import com.etplus.repository.FeedRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.FeedCommentEntity;
import com.etplus.repository.domain.FeedEntity;
import com.etplus.repository.domain.FeedLike;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedService {

  private final FeedRepository feedRepository;
  private final UserRepository userRepository;
  private final FeedLikeRepository feedLikeRepository;
  private final FeedCommentRepository feedCommentRepository;
  private final S3Uploader s3Uploader;

  @Transactional
  public void createFeed(Long userId, CreateFeedDTO dto) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    FileEntity image = null;
    if (dto.image() != null) {
      image = s3Uploader.uploadImageAndSaveRepository(dto.image(), user);
    }

    FeedEntity feed = new FeedEntity(dto.content(), dto.feedVisibility(), user, image);
    feedRepository.save(feed);
  }

  @Transactional
  public void updateFeed(Long userId, Long feedId, UpdateFeedDTO dto) {
    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    // 피드 작성자만 수정 가능
    if (!feed.getCreatedUser().getId().equals(userId)) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // 이미지 업데이트
    if (dto.image() != null) {
      FileEntity newImage = s3Uploader.uploadImageAndSaveRepository(dto.image(), user);
      feed.setImage(newImage);
    } else {
      feed.setImage(null);
    }

    // 피드 내용 및 공개 설정 업데이트
    feed.setContent(dto.content());
    feed.setFeedVisibility(dto.feedVisibility());

    feedRepository.save(feed);
  }

  @Transactional
  public void deleteFeed(Long userId, RoleType userRole, Long feedId) {
    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));

    // 어드민이 아니고 피드 작성자가 아닌 경우 삭제 불가
    if (userRole != RoleType.ADMIN && !feed.getCreatedUser().getId().equals(userId)) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // Soft delete 적용
    feed.setDeleted(true);
    feedRepository.save(feed);
  }

  @Transactional
  public void addFeedLike(Long userId, Long feedId) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    
    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));

    // 이미 좋아요를 눌렀는지 확인
    if (feedLikeRepository.existsByFeedIdAndUserId(feedId, userId)) {
      throw new FeedException(FeedExceptionCode.ALREADY_LIKED_FEED);
    }

    FeedLike feedLike = new FeedLike(null, user, feed);
    feedLikeRepository.save(feedLike);
  }

  @Transactional
  public void removeFeedLike(Long userId, Long feedId) {
    FeedLike feedLike = feedLikeRepository.findByFeedIdAndUserId(feedId, userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_LIKE_NOT_FOUND));

    feedLikeRepository.delete(feedLike);
  }

  @Transactional
  public void createFeedComment(Long userId, Long feedId, CreateFeedCommentDTO dto) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));

    FeedCommentEntity feedComment = new FeedCommentEntity(null, dto.comment(), user, feed);
    feedCommentRepository.save(feedComment);
  }

  @Transactional
  public void updateFeedComment(Long userId, Long feedId, Long commentId, UpdateFeedCommentDTO dto) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    
    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));

    FeedCommentEntity feedComment = feedCommentRepository.findById(commentId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_COMMENT_NOT_FOUND));

    // 댓글이 해당 피드의 댓글이 맞는지 확인
    if (!feedComment.getFeed().getId().equals(feedId)) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_COMMENT_NOT_FOUND);
    }

    // 댓글 작성자만 수정 가능
    if (!feedComment.getUser().getId().equals(userId)) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    feedComment.setComment(dto.comment());
    feedCommentRepository.save(feedComment);
  }

  @Transactional
  public void deleteFeedComment(Long userId, Long feedId, Long commentId) {
    FeedEntity feed = feedRepository.findByIdAndDeletedIsFalse(feedId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_NOT_FOUND));

    FeedCommentEntity feedComment = feedCommentRepository.findById(commentId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_COMMENT_NOT_FOUND));

    // 댓글이 해당 피드의 댓글이 맞는지 확인
    if (!feedComment.getFeed().getId().equals(feedId)) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.FEED_COMMENT_NOT_FOUND);
    }

    // 댓글 작성자만 삭제 가능
    if (!feedComment.getUser().getId().equals(userId)) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
    // TODO 댓글 좋아요 삭제
    // TODO 댓글 신고하기 삭제 ??

    feedCommentRepository.delete(feedComment);
  }
}
