package com.etplus.service;

import com.etplus.common.LoginUser;
import com.etplus.controller.dto.UpdateAcademyDto;
import com.etplus.exception.ResourceDeniedException;
import com.etplus.exception.ResourceDeniedException.ResourceDeniedExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.FileRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.AcademyDetailVO;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AcademyService {

  private final AcademyRepository academyRepository;
  private final UserRepository userRepository;
  private final S3Uploader s3Uploader;
  private final FileRepository fileRepository;

  public AcademyDetailVO getAcademyDetail(Long academyId) {
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(FileEntity::getPath).toList();

    return new AcademyDetailVO(
        academy.getId(),
        academy.getName(),
        academy.getDescription(),
        academy.getBusinessRegistrationNumber(),
        academy.getLocationType(),
        academy.getDetailedAddress(),
        academy.isForKindergarten(),
        academy.isForElementary(),
        academy.isForMiddleSchool(),
        academy.isForHighSchool(),
        academy.isForAdult(),
        imagePathList
    );
  }

  public AcademyDetailVO getMyAcademy(LoginUser loginUser) {
    UserEntity user = userRepository.findById(loginUser.userId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    AcademyEntity academy = user.getAcademy();

    if (!RoleType.ACADEMY.equals(user.getRoleType()) || academy == null) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.INVALID_ROLE);
    }

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(FileEntity::getPath).toList();

    return new AcademyDetailVO(
        academy.getId(),
        academy.getName(),
        academy.getDescription(),
        academy.getBusinessRegistrationNumber(),
        academy.getLocationType(),
        academy.getDetailedAddress(),
        academy.isForKindergarten(),
        academy.isForElementary(),
        academy.isForMiddleSchool(),
        academy.isForHighSchool(),
        academy.isForAdult(),
        imagePathList
    );
  }

  @Transactional
  public void updateMyAcademy(UpdateAcademyDto dto, LoginUser loginUser) {
    UserEntity user = userRepository.findById(loginUser.userId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    AcademyEntity academy = user.getAcademy();

    if (!RoleType.ACADEMY.equals(user.getRoleType()) || academy == null) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.INVALID_ROLE);
    }

    academy.setName(dto.name());
    academy.setDescription(dto.description());
    academy.setForKindergarten(dto.forKindergarten());
    academy.setForElementary(dto.forElementary());
    academy.setForMiddleSchool(dto.forMiddleSchool());
    academy.setForHighSchool(dto.forHighSchool());
    academy.setForAdult(dto.forAdult());

    // 이미지 업로드
    List<FileEntity> uploadedImageFiles = new ArrayList<>();
    for (MultipartFile image : dto.images()) {
      uploadedImageFiles.add(s3Uploader.uploadImageAndSaveRepository(image, user));
    }

    List<Long> uploadedImageFileIds = uploadedImageFiles.stream()
        .map(FileEntity::getId)
        .toList();

    academy.setImageFileIdList(uploadedImageFileIds);
    academyRepository.save(academy);
  }
}
