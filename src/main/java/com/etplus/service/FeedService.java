package com.etplus.service;

import com.etplus.controller.dto.CreateFeedDTO;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.FeedRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.FeedEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedService {

  private final FeedRepository feedRepository;
  private final UserRepository userRepository;
  private final S3Uploader s3Uploader;

  @Transactional
  public void createFeed(Long userId, CreateFeedDTO dto) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    FileEntity image = null;
    if (dto.image() != null) {
      image = s3Uploader.uploadImageAndSaveRepository(dto.image(), user);
    }

    FeedEntity feed = new FeedEntity(null, dto.content(), dto.feedVisibility(), user, image);
    feedRepository.save(feed);
  }
}
