package com.etplus.service;

import com.etplus.common.LoginUser;
import com.etplus.controller.dto.CreateAcademyDTO;
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
import com.etplus.vo.AcademyVO;
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

  public List<AcademyVO> getAllAcademies() {
    List<AcademyEntity> all = academyRepository.findAll();
    return all.stream().map(AcademyVO::valueOf).toList();
  }

  public AcademyDetailVO getAcademyDetail(Long academyId) {
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(FileEntity::getPath).toList();

    return AcademyDetailVO.valueOf(academy, imagePathList);
  }

  public AcademyDetailVO getMyAcademy(LoginUser loginUser) {
    AcademyEntity academy = academyRepository.findByRepresentativeUserId(loginUser.userId())
        .orElse(null);

    if (!RoleType.ACADEMY.equals(loginUser.roleType()) || academy == null) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.INVALID_ROLE);
    }

    List<Long> imageFileIdList = academy.getImageFileIdList();
    List<FileEntity> imageFileList = fileRepository.findAllByIdIn(imageFileIdList);

    List<String> imagePathList = imageFileList.stream().map(FileEntity::getPath).toList();

    return AcademyDetailVO.valueOf(academy, imagePathList);
  }

  @Transactional
  public void updateMyAcademy(UpdateAcademyDto dto, LoginUser loginUser) {
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
    academy.setDetailedAddress(dto.detailedAddress());
    academy.setLat(dto.lat());
    academy.setLng(dto.lng());
    academy.setForKindergarten(dto.forKindergarten());
    academy.setForElementary(dto.forElementary());
    academy.setForMiddleSchool(dto.forMiddleSchool());
    academy.setForHighSchool(dto.forHighSchool());
    academy.setForAdult(dto.forAdult());

    // 이미지 업로드
    List<FileEntity> uploadedImageFiles = new ArrayList<>();
    for (MultipartFile image : dto.images()) {
      uploadedImageFiles.add(s3Uploader.uploadImageAndSaveRepository(image, academy.getRepresentativeUser()));
    }

    List<Long> uploadedImageFileIds = uploadedImageFiles.stream()
        .map(FileEntity::getId)
        .toList();

    academy.setImageFileIdList(uploadedImageFileIds);
    academyRepository.save(academy);
  }

  @Transactional
  public void updateAcademyByAdmin(long academyId, UpdateAcademyDto dto) {
    AcademyEntity academy = academyRepository.findById(academyId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    academy.setName(dto.name());
    academy.setNameEn(dto.nameEn());
    academy.setRepresentativeName(dto.representativeName());
    academy.setDescription(dto.description());
    academy.setLocationType(dto.locationType());
    academy.setDetailedAddress(dto.detailedAddress());
    academy.setLat(dto.lat());
    academy.setLng(dto.lng());
    academy.setForKindergarten(dto.forKindergarten());
    academy.setForElementary(dto.forElementary());
    academy.setForMiddleSchool(dto.forMiddleSchool());
    academy.setForHighSchool(dto.forHighSchool());
    academy.setForAdult(dto.forAdult());

    // 이미지 업로드
    List<FileEntity> uploadedImageFiles = new ArrayList<>();
    for (MultipartFile image : dto.images()) {
      uploadedImageFiles.add(
          s3Uploader.uploadImageAndSaveRepository(image, academy.getRepresentativeUser()));
    }

    List<Long> uploadedImageFileIds = uploadedImageFiles.stream()
        .map(FileEntity::getId)
        .toList();

    academy.setImageFileIdList(uploadedImageFileIds);
    academyRepository.save(academy);
  }

  @Transactional
  public void createAcademy(CreateAcademyDTO dto, long userId) {
    UserEntity adminUser = userRepository.findById(userId)
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
            dto.detailedAddress(),
            dto.lat(),
            dto.lng(),
            false, false, false, false, false,
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
