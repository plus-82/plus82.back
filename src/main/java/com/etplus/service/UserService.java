package com.etplus.service;

import com.etplus.controller.dto.SearchUserDTO;
import com.etplus.controller.dto.UpdateAcademyUserDto;
import com.etplus.controller.dto.UpdatePasswordDto;
import com.etplus.controller.dto.UpdateProfileImageDTO;
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
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

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
    UserEntity userEntity = new UserEntity(
        null,
        null,
        null,
        null,
        GenderType.MALE,
        null,
        email,
        passwordProvider.encode(password),
        true,
        RoleType.ADMIN,
        null,
        null
    );
    userRepository.save(userEntity);
  }

  @Transactional
  public void updateMe(long userId, UpdateUserDto dto) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setFirstName(dto.firstName());
    user.setLastName(dto.lastName());
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
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setFullName(dto.fullName());
    user.setGenderType(dto.genderType());
    user.setBirthDate(dto.birthDate());
    userRepository.save(user);
  }

  @Transactional
  public void updateProfileImage(long userId, UpdateProfileImageDTO dto) {
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    FileEntity uploadedFile = s3Uploader.uploadImageAndSaveRepository(dto.image(), user);

    user.setProfileImage(uploadedFile);
    userRepository.save(user);
  }

  @Transactional
  public void deleteProfileImage(long userId) {
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
    UserEntity user = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    user.setDeleted(true);
    userRepository.save(user);
  }

  @Transactional
  public void updatePassword(long userId, UpdatePasswordDto dto) {
    UserEntity userEntity = userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(
        () -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.USER_NOT_FOUND));

    if (!passwordProvider.matches(dto.currentPassword(), userEntity.getPassword())) {
      throw new AuthException(AuthExceptionCode.PW_NOT_CORRECT);
    }

    userEntity.setPassword(passwordProvider.encode(dto.newPassword()));
    userRepository.save(userEntity);
  }

}
