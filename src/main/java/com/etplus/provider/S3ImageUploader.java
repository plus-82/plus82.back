package com.etplus.provider;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.etplus.repository.ImageFileRepository;
import com.etplus.repository.domain.ImageFileEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.util.UuidProvider;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

//  public String upload(MultipartFile multipartFile) {
//    String originalFilename = multipartFile.getOriginalFilename();
//
//    ObjectMetadata metadata = new ObjectMetadata();
//    metadata.setContentLength(multipartFile.getSize());
//    metadata.setContentType(multipartFile.getContentType());
//
//    try {
//      client.putObject(BUCKET, originalFilename, multipartFile.getInputStream(), metadata);
//    } catch (IOException exception) {
////            throw new S3ImageUploadException(exception.getMessage());
//      throw new RuntimeException(exception.getMessage());
//    }
//    return client.getUrl(BUCKET, originalFilename).toString();
//  }

//  public String upload(File file) {
//    String s3PathKey = PATH_ROOT + "/" + UuidProvider.generateUuid();
//    client.putObject(new PutObjectRequest(BUCKET, s3PathKey, file));
//    return s3PathKey;
//  }

  @Transactional
  public ImageFileEntity uploadAndSaveRepository(MultipartFile file, UserEntity owner) {
    String s3PathKey = PATH_ROOT + "/" + UuidProvider.generateUuid();
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
}
