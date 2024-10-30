package com.etplus.service;

import com.etplus.config.security.LoginUser;
import com.etplus.controller.dto.UpdateAcademyDto;
import com.etplus.exception.ResourceDeniedException;
import com.etplus.exception.ResourceDeniedException.ResourceDeniedExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.S3ImageUploader;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
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
  private final S3ImageUploader s3ImageUploader;

  public AcademyDetailVO getMyAcademy(LoginUser loginUser) {
    UserEntity user = userRepository.findById(loginUser.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    AcademyEntity academy = user.getAcademy();

    if (!RoleType.ACADEMY.equals(user.getRoleType()) || academy == null) {
      throw new ResourceDeniedException(ResourceDeniedExceptionCode.INVALID_ROLE);
    }

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
        academy.getImageUrls()
    );
  }

  @Transactional
  public void updateMyAcademy(UpdateAcademyDto dto, LoginUser loginUser) {
    UserEntity user = userRepository.findById(loginUser.getUserId())
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
    List<String> uploadedImageUrls = new ArrayList<>();
    for (MultipartFile image : dto.images()) {
      uploadedImageUrls.add(s3ImageUploader.upload(image));
    }

    academy.setImageUrls(uploadedImageUrls);
    academyRepository.save(academy);
  }
}
