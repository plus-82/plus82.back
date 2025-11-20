package com.etplus.service;

import com.etplus.controller.dto.SearchUserDTO;
import com.etplus.controller.dto.UpdateAcademyUserDto;
import com.etplus.controller.dto.UpdatePasswordDto;
import com.etplus.controller.dto.UpdateProfileImageDTO;
import com.etplus.controller.dto.UpdateRepresentativeResumePublicDto;
import com.etplus.controller.dto.UpdateUserDto;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.provider.PasswordProvider;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.FileRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.CountryEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.GenderType;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.UserVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final CountryRepository countryRepository;
  private final FileRepository fileRepository;
  private final PasswordProvider passwordProvider;
  private final S3Uploader s3Uploader;

  public UserVO getMe(long userId) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    CountryEntity country = user.getCountry();
    FileEntity profileImage = user.getProfileImage();

    return new UserVO(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getFullName(),
        user.getGenderType(),
        user.getBirthDate(),
        user.getEmail(),
        country == null ? null : country.getId(),
        country == null ? null : country.getCountryNameEn(),
        country == null ? null : country.getCountryCode(),
        country == null ? null : country.getCountryCallingCode(),
        country == null ? null : country.getFlag(),
        profileImage == null ? null : profileImage.getPath()
    );
  }

  public Slice<UserVO> getAllUsers(SearchUserDTO dto) {
    return userRepository.findAllUsers(dto);
  }

  @Transactional
  public void createAdminUser(String email, String password) {
    log.info("Creating admin user with email: {}", email);
    UserEntity userEntity = new UserEntity(
        null,
        null,
        null,
        null,
        "관리자",
        GenderType.MALE,
        null,
        email,
        passwordProvider.encode(password),
        true,
        RoleType.ADMIN,
        null,
        null,
        false
    );
    userRepository.save(userEntity);
  }

  @Transactional
  public void updateMe(long userId, UpdateUserDto dto) {
    log.info("updateMe. userId: {}, dto: {}", userId, dto);
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setFirstName(dto.firstName());
    user.setLastName(dto.lastName());
    user.setName(dto.firstName() + dto.lastName());
    user.setGenderType(dto.genderType());
    user.setBirthDate(dto.birthDate());

    if (dto.countryId() != null) {
      CountryEntity country = countryRepository.findById(dto.countryId()).orElseThrow(
          () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND));
      user.setCountry(country);
    }
    userRepository.save(user);
  }

  @Transactional
  public void updateMeByAcademy(long userId, UpdateAcademyUserDto dto) {
    log.info("updateMeByAcademy. userId: {}, dto: {}", userId, dto);
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setFullName(dto.fullName());
    user.setName(dto.fullName());
    user.setGenderType(dto.genderType());
    user.setBirthDate(dto.birthDate());
    userRepository.save(user);
  }

  @Transactional
  public void updateProfileImage(long userId, UpdateProfileImageDTO dto) {
    log.info("updateProfileImage. userId: {}, dto: {}", userId, dto);
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    FileEntity uploadedFile = s3Uploader.uploadImageAndSaveRepository(dto.image(), user);

    user.setProfileImage(uploadedFile);
    userRepository.save(user);
  }

  @Transactional
  public void deleteProfileImage(long userId) {
    log.info("deleteProfileImage. userId: {}", userId);
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setProfileImage(null);
    userRepository.save(user);
//    FileEntity profileImage = user.getProfileImage();
//    if (profileImage != null) {
//      s3Uploader.deleteImage(profileImage);
//      fileRepository.delete(profileImage);
//      user.setProfileImage(null);
//      userRepository.save(user);
//    }
  }

  @Transactional
  public void deleteUser(long userId) {
    log.info("deleteUser. userId: {}", userId);
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setDeleted(true);
    userRepository.save(user);
  }

  @Transactional
  public void updatePassword(long userId, UpdatePasswordDto dto) {
    log.info("updatePassword. userId: {}");
    UserEntity userEntity = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    if (!passwordProvider.matches(dto.currentPassword(), userEntity.getPassword())) {
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    userEntity.setPassword(passwordProvider.encode(dto.newPassword()));
    userRepository.save(userEntity);
  }

  @Transactional
  public void updateRepresentativeResumePublic(long userId, UpdateRepresentativeResumePublicDto dto) {
    log.info("updateRepresentativeResumePublic. userId: {}, representativeResumePublic: {}", userId, dto.representativeResumePublic());
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setRepresentativeResumePublic(dto.representativeResumePublic());
    userRepository.save(user);
  }

}
