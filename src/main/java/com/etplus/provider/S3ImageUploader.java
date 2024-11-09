package com.etplus.provider;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.etplus.exception.FileException;
import com.etplus.repository.ImageFileRepository;
import com.etplus.repository.domain.ImageFileEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.util.UuidProvider;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Transactional
public class S3ImageUploader {

  private final AmazonS3Client client;
  private final ImageFileRepository imageFileRepository;

  @Value("${aws.s3.bucket}")
  private String BUCKET;
  @Value("${aws.s3.path}")
  private String PATH_ROOT;

  @Transactional
  public ImageFileEntity uploadAndSaveRepository(MultipartFile file, UserEntity owner) {
    verifyFile(file.getContentType());

    String fileExtension = file.getContentType().substring(file.getContentType().lastIndexOf('/') + 1);

    String s3PathKey = PATH_ROOT + "/" + UuidProvider.generateUuid() + "." + fileExtension;
    ObjectMetadata metaData = new ObjectMetadata();
    metaData.setContentLength(file.getSize());
    try {
      client.putObject(
          new PutObjectRequest(BUCKET, s3PathKey, file.getInputStream(), metaData));
    } catch (IOException e) {
//      log.error("Failed to upload file to S3", e);
//      throw new FileException(FileExceptionCode.FAILED_TO_STORE);
      throw new RuntimeException(e.getMessage());
    }

    return imageFileRepository.save(new ImageFileEntity(
            null,
            file.getOriginalFilename(),
            s3PathKey,
            file.getSize(),
            file.getContentType(),
            owner
    ));
  }


  private void verifyFile(String contentType) {
    // 확장자가 jpeg, png인 파일들만 받아서 처리
    if (ObjectUtils.isEmpty(contentType) | (!contentType.contains("image/jpeg") & !contentType.contains("image/png")))
      throw new FileException(FileException.FileExceptionCode.INVALID_FILE_EXTENSION);
  }

}
