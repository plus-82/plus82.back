package com.etplus.provider;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.etplus.exception.FileException;
import com.etplus.repository.FileRepository;
import com.etplus.repository.domain.FileEntity;
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
public class S3Uploader {

  private final AmazonS3Client client;
  private final FileRepository fileRepository;

  @Value("${aws.s3.bucket}")
  private String BUCKET;
  @Value("${aws.s3.path}")
  private String PATH_ROOT;

  @Transactional
  public FileEntity uploadImageAndSaveRepository(MultipartFile file, UserEntity owner) {
    verifyFileSize(file.getSize());
    verifyImageFile(file.getContentType());
    return uploadAndSaveRepository(file, owner);
  }

  @Transactional
  public FileEntity uploadResumeAndSaveRepository(MultipartFile file, UserEntity owner) {
    verifyFileSize(file.getSize());
    verifyResumeFile(file.getContentType());
    return uploadAndSaveRepository(file, owner);
  }
  
  private void verifyFileSize(long fileSize) {
    // 5MB in bytes
    long maxSize = 5 * 1024 * 1024;
    if (fileSize > maxSize) {
      throw new FileException(FileException.FileExceptionCode.FILE_SIZE_EXCEEDED);
    }
  }

  private FileEntity uploadAndSaveRepository(MultipartFile file, UserEntity owner) {
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

    return fileRepository.save(new FileEntity(
            null,
            file.getOriginalFilename(),
            s3PathKey,
            file.getSize(),
            file.getContentType(),
            owner
    ));
  }

  private void verifyImageFile(String contentType) {
    // 확장자가 jpeg, jpg, png인 파일들만 받아서 처리
    if (ObjectUtils.isEmpty(contentType) || 
        (!contentType.contains("image/jpeg") && 
         !contentType.contains("image/jpg") && 
         !contentType.contains("image/png")))
      throw new FileException(FileException.FileExceptionCode.INVALID_FILE_EXTENSION);
  }

  private void verifyResumeFile(String contentType) {
    // 확장자가 pdf인 파일들만 받아서 처리
    if (ObjectUtils.isEmpty(contentType) || (!contentType.contains("application/pdf")))
      throw new FileException(FileException.FileExceptionCode.INVALID_FILE_EXTENSION);
  }

}
