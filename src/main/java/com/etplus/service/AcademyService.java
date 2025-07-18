package com.etplus.service;

import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateAcademyDTO;
import com.etplus.controller.dto.UpdateAcademyByAdminDto;
import com.etplus.controller.dto.UpdateAcademyDto;
import com.etplus.exception.AcademyException;
import com.etplus.exception.AcademyException.AcademyExceptionCode;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
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
import com.etplus.vo.AcademyDetailByAdminVO;
import com.etplus.vo.AcademyDetailVO;
import com.etplus.vo.AcademyVO;
import com.etplus.vo.common.ImageVO;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class AcademyService {

  private final AcademyRepository academyRepository;
  private final UserRepository userRepository;
  private final S3Uploader s3Uploader;
  private final FileRepository fileRepository;

  public List<AcademyVO> getAcademiesByAdmin(long adminUserId) {
    List<AcademyEntity> academyList = academyRepository.findByAdminUserId(adminUserId);
    return academyList.stream().map(AcademyVO::valueOf).toList();
  }

  public AcademyDetailVO getAcademyDetail(Long academyId) {
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<ImageVO> imageVOList = imageFileList.stream()
        .map(fileEntity -> new ImageVO(fileEntity.getId(), fileEntity.getPath()))
        .toList();

    return AcademyDetailVO.valueOf(academy, imageVOList);
  }

  public AcademyDetailByAdminVO getAcademyDetailByAdmin(Long academyId, Long userId) {
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    if (academy.getAdminUser() != null && academy.getAdminUser().getId() != userId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<ImageVO> imageVOList = imageFileList.stream()
        .map(fileEntity -> new ImageVO(fileEntity.getId(), fileEntity.getPath()))
        .toList();

    return AcademyDetailByAdminVO.valueOf(academy, imageVOList);
  }

  public AcademyDetailByAdminVO getMyAcademy(LoginUser loginUser) {
    AcademyEntity academy = academyRepository.findByRepresentativeUserId(loginUser.userId())
        .orElse(null);

    if (!RoleType.ACADEMY.equals(loginUser.roleType()) || academy == null) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.INVALID_ROLE);
    }

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<ImageVO> imageList = imageFileList.stream()
        .map(fileEntity -> new ImageVO(fileEntity.getId(), fileEntity.getPath()))
        .toList();

    return AcademyDetailByAdminVO.valueOf(academy, imageList);
  }

  @Transactional
  public void updateMyAcademy(UpdateAcademyDto dto, LoginUser loginUser) {
    log.info("updateMyAcademy. dto : {}, loginUser : {}", dto, loginUser);
    AcademyEntity academy = academyRepository.findByRepresentativeUserId(loginUser.userId())
        .orElse(null);

    if (!RoleType.ACADEMY.equals(loginUser.roleType()) || academy == null) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.INVALID_ROLE);
    }

    academy.setName(dto.name());
    academy.setNameEn(dto.nameEn());
    academy.setRepresentativeName(dto.representativeName());
    academy.setDescription(dto.description());
    academy.setLocationType(dto.locationType());
    academy.setAddress(dto.address());
    academy.setDetailedAddress(dto.detailedAddress());
    academy.setLat(dto.lat());
    academy.setLng(dto.lng());
    academy.setForKindergarten(dto.forKindergarten());
    academy.setForElementary(dto.forElementary());
    academy.setForMiddleSchool(dto.forMiddleSchool());
    academy.setForHighSchool(dto.forHighSchool());
    academy.setForAdult(dto.forAdult());

    // 신규 이미지 업로드
    List<FileEntity> uploadedImageFiles = new ArrayList<>();
    for (MultipartFile image : dto.newImages()) {
      uploadedImageFiles.add(s3Uploader.uploadImageAndSaveRepository(image, academy.getRepresentativeUser()));
    }

    // 기존 이미지 ID 목록에 신규 이미지 ID 추가
    List<Long> imageFileIdList = dto.oldImageIds();

    List<Long> oldImageFileIdList = academy.getImageFileIdList();
    if (!oldImageFileIdList.containsAll(imageFileIdList)) {
      throw new AcademyException(AcademyExceptionCode.CHECK_OLD_IMAGE_ID);
    }

    imageFileIdList.addAll(uploadedImageFiles.stream()
        .map(FileEntity::getId)
        .toList());

    academy.setImageFileIdList(imageFileIdList);
    academyRepository.save(academy);
  }

  @Transactional
  public void updateAcademyByAdmin(long academyId, UpdateAcademyByAdminDto dto, long adminUserId) {
    log.info("updateAcademyByAdmin. academyId: {}, dto: {}, adminUserId: {}", academyId, dto, adminUserId);
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    if (academy.getAdminUser() != null && academy.getAdminUser().getId() != adminUserId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    academy.setName(dto.name());
    academy.setNameEn(dto.nameEn());
    academy.setRepresentativeName(dto.representativeName());
    academy.setRepresentativeEmail(dto.representativeEmail());
    academy.setDescription(dto.description());
    academy.setLocationType(dto.locationType());
    academy.setAddress(dto.address());
    academy.setDetailedAddress(dto.detailedAddress());
    academy.setLat(dto.lat());
    academy.setLng(dto.lng());
    academy.setForKindergarten(dto.forKindergarten());
    academy.setForElementary(dto.forElementary());
    academy.setForMiddleSchool(dto.forMiddleSchool());
    academy.setForHighSchool(dto.forHighSchool());
    academy.setForAdult(dto.forAdult());

    // 신규 이미지 업로드
    List<FileEntity> uploadedImageFiles = new ArrayList<>();
    for (MultipartFile image : dto.newImages()) {
      uploadedImageFiles.add(s3Uploader.uploadImageAndSaveRepository(image, academy.getRepresentativeUser()));
    }

    // 기존 이미지 ID 목록에 신규 이미지 ID 추가
    List<Long> imageFileIdList = dto.oldImageIds();

    List<Long> oldImageFileIdList = academy.getImageFileIdList();
    if (!oldImageFileIdList.containsAll(imageFileIdList)) {
      throw new AcademyException(AcademyExceptionCode.CHECK_OLD_IMAGE_ID);
    }

    imageFileIdList.addAll(uploadedImageFiles.stream()
        .map(FileEntity::getId)
        .toList());

    academy.setImageFileIdList(imageFileIdList);
    academyRepository.save(academy);
  }

  @Transactional
  public void createAcademy(CreateAcademyDTO dto, long adminUserId) {
    log.info("createAcademy. dto: {}, adminUserId: {}", dto, adminUserId);
    UserEntity adminUser = userRepository.findById(adminUserId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    AcademyEntity academy = academyRepository.save(
        new AcademyEntity(
            null,
            dto.name(),
            dto.nameEn(),
            dto.representativeName(),
            dto.representativeEmail(),
            dto.description(),
            null,
            dto.locationType(),
            dto.address(),
            dto.detailedAddress(),
            dto.lat(),
            dto.lng(),
            dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(), dto.forHighSchool(), dto.forAdult(),
            null,
            true,
            null,
            adminUser
        ));

    // 이미지 업로드
    List<FileEntity> uploadedImageFiles = new ArrayList<>();
    for (MultipartFile image : dto.images()) {
      uploadedImageFiles.add(s3Uploader.uploadImageAndSaveRepository(image, adminUser));
    }

    List<Long> uploadedImageFileIds = uploadedImageFiles.stream()
        .map(FileEntity::getId)
        .toList();

    academy.setImageFileIdList(uploadedImageFileIds);
    academyRepository.save(academy);
  }
}
