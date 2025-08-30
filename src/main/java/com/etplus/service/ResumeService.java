package com.etplus.service;

import com.etplus.controller.dto.CreateResumeDTO;
import com.etplus.controller.dto.CreateResumeWithFileDTO;
import com.etplus.controller.dto.PagingDTO;
import com.etplus.controller.dto.SearchRepresentativeResumeDto;
import com.etplus.controller.dto.UpdateResumeDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.ResumeException;
import com.etplus.exception.ResumeException.ResumeExceptionCode;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.ResumeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.CountryEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.vo.RepresentativeResumeVO;
import com.etplus.vo.ResumeDetailVO;
import com.etplus.vo.ResumeVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;
  private final CountryRepository countryRepository;
  private final S3Uploader s3Uploader;

  public Slice<ResumeVO> getMyResumes(long userId, PagingDTO dto) {
    return resumeRepository.findAllByUserId(userId, dto);
  }

  public ResumeDetailVO getResumeDetail(long userId, long resumeId) {
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 본인 이력서만 조회 가능
    if (resume.getUser().getId() != userId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    return ResumeDetailVO.valueOf(resume);
  }

  @Transactional
  public void createResume(long userId, CreateResumeDTO dto) {
    log.info("Creating resume for userId: {}, dto: {}", userId, dto);
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    CountryEntity country = countryRepository.findById(dto.countryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND));
    CountryEntity residenceCountry = countryRepository.findById(dto.residenceCountryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND));

    // 대표 이력서 중복되는지 확인
    if (dto.isRepresentative()) {
      if (resumeRepository.existsByUserIdAndIsRepresentativeIsTrue(userId)) {
        throw new ResumeException(ResumeExceptionCode.REPRESENTATIVE_RESUME_EXISTS);
      }
    }

    // 프로필 이미지
    FileEntity fileEntity;
    if (dto.profileImage() != null) {
      fileEntity = s3Uploader.uploadImageAndSaveRepository(dto.profileImage(), user);
    } else {
      fileEntity = user.getProfileImage();
    }

    resumeRepository.save(
        new ResumeEntity(null, dto.title(), dto.personalIntroduction(), dto.firstName(),
            dto.lastName(), dto.email(), dto.degree(), dto.major(), dto.genderType(),
            dto.birthDate(), dto.hasVisa(), dto.visaType(), dto.isRepresentative(),
            dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(), dto.forHighSchool(),
            dto.forAdult(), false, country, residenceCountry, user, fileEntity, null));
  }

  @Transactional
  public void createDraftResume(long userId, CreateResumeDTO dto) {
    log.info("Creating draft resume for userId: {}, dto: {}", userId, dto);
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    CountryEntity country = dto.countryId() != null ? countryRepository.findById(dto.countryId())
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND)) : null;
    CountryEntity residenceCountry =
        dto.residenceCountryId() != null ? countryRepository.findById(dto.residenceCountryId())
            .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND)) : null;

    // 프로필 이미지
    FileEntity fileEntity;
    if (dto.profileImage() != null) {
      fileEntity = s3Uploader.uploadImageAndSaveRepository(dto.profileImage(), user);
    } else {
      fileEntity = user.getProfileImage();
    }

    resumeRepository.save(
        new ResumeEntity(null, dto.title(), dto.personalIntroduction(), dto.firstName(),
            dto.lastName(), dto.email(), dto.degree(), dto.major(), dto.genderType(),
            dto.birthDate(), dto.hasVisa(), dto.visaType(), dto.isRepresentative(),
            dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(), dto.forHighSchool(),
            dto.forAdult(), true, country, residenceCountry, user, fileEntity, null));
  }

  @Transactional
  public void copyResume(long userId, long resumeId) {
    log.info("Copying resume for userId: {}, resumeId: {}", userId, resumeId);
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 본인 이력서만 수정 가능
    if (resume.getUser().getId() != userId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // 파일 이력서는 수정 불가
    if (resume.getFile() != null) {
      throw new ResumeException(ResumeExceptionCode.FILE_RESUME_CANNOT_BE_MODIFIED);
    }

    resumeRepository.save(
        new ResumeEntity(
            null, "(copy)" + resume.getTitle(), resume.getPersonalIntroduction(), resume.getFirstName(),
            resume.getLastName(), resume.getEmail(), resume.getDegree(), resume.getMajor(),
            resume.getGenderType(), resume.getBirthDate(), resume.getHasVisa(), resume.getVisaType(),
            false, // 복사 시 대표 이력서 X
            resume.getForKindergarten(), resume.getForElementary(), resume.getForMiddleSchool(),
            resume.getForHighSchool(), resume.getForAdult(), resume.isDraft(), resume.getCountry(),
            resume.getResidenceCountry(), resume.getUser(), resume.getProfileImage(), null));
  }

  @Transactional
  public void createResumeWithFile(long userId, CreateResumeWithFileDTO dto) {
    log.info("Creating resume with file for userId: {}, dto: {}", userId, dto);
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    FileEntity file = s3Uploader.uploadResumeAndSaveRepository(dto.file(), user);

    resumeRepository.save(new ResumeEntity(file.getFileName(), user, file, false));
  }

  @Transactional
  public void updateResume(long userId, long resumeId, UpdateResumeDTO dto) {
    log.info("Updating resume for userId: {}, resumeId: {}, dto: {}", userId, resumeId, dto);
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 본인 이력서만 수정 가능
    if (resume.getUser().getId() != userId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // 파일 이력서는 수정 불가
    if (resume.getFile() != null) {
      throw new ResumeException(ResumeExceptionCode.FILE_RESUME_CANNOT_BE_MODIFIED);
    }

    // 대표 이력서 중복되는지 확인
    if (dto.isRepresentative() && !resume.getIsRepresentative()) {
      if (resumeRepository.existsByUserIdAndIsRepresentativeIsTrue(userId)) {
        throw new ResumeException(ResumeExceptionCode.REPRESENTATIVE_RESUME_EXISTS);
      }
    }

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    CountryEntity country = countryRepository.findById(dto.countryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND));
    CountryEntity residenceCountry = countryRepository.findById(dto.residenceCountryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND));

    resume.setTitle(dto.title());
    resume.setPersonalIntroduction(dto.personalIntroduction());
    resume.setFirstName(dto.firstName());
    resume.setLastName(dto.lastName());
    resume.setEmail(dto.email());
    resume.setDegree(dto.degree());
    resume.setMajor(dto.major());
    resume.setGenderType(dto.genderType());
    resume.setBirthDate(dto.birthDate());
    resume.setHasVisa(dto.hasVisa());
    resume.setVisaType(dto.visaType());
    resume.setIsRepresentative(dto.isRepresentative());
    resume.setForKindergarten(dto.forKindergarten());
    resume.setForElementary(dto.forElementary());
    resume.setForMiddleSchool(dto.forMiddleSchool());
    resume.setForHighSchool(dto.forHighSchool());
    resume.setForAdult(dto.forAdult());
    resume.setDraft(false);

    // 프로필 이미지
    if (dto.profileImage() != null) {
      resume.setProfileImage(s3Uploader.uploadImageAndSaveRepository(dto.profileImage(), user));
    }
    resume.setCountry(country);
    resume.setResidenceCountry(residenceCountry);

    resumeRepository.save(resume);
  }

  @Transactional
  public void updateDraftResume(long userId, long resumeId, UpdateResumeDTO dto) {
    log.info("Updating draft resume for userId: {}, resumeId: {}, dto: {}", userId, resumeId, dto);
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 본인 이력서, 임시저장 이력서만 수정 가능
    if (resume.getUser().getId() != userId || (!resume.isDraft())) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }
    // 파일 이력서는 수정 불가
    if (resume.getFile() != null) {
      throw new ResumeException(ResumeExceptionCode.FILE_RESUME_CANNOT_BE_MODIFIED);
    }

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    CountryEntity country = dto.countryId() != null ? countryRepository.findById(dto.countryId())
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND)) : null;
    CountryEntity residenceCountry =
        dto.residenceCountryId() != null ? countryRepository.findById(dto.residenceCountryId())
            .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.COUNTRY_NOT_FOUND)) : null;

    resume.setTitle(dto.title());
    resume.setPersonalIntroduction(dto.personalIntroduction());
    resume.setFirstName(dto.firstName());
    resume.setLastName(dto.lastName());
    resume.setEmail(dto.email());
    resume.setDegree(dto.degree());
    resume.setMajor(dto.major());
    resume.setGenderType(dto.genderType());
    resume.setBirthDate(dto.birthDate());
    resume.setHasVisa(dto.hasVisa());
    resume.setVisaType(dto.visaType());
    resume.setIsRepresentative(dto.isRepresentative());
    resume.setForKindergarten(dto.forKindergarten());
    resume.setForElementary(dto.forElementary());
    resume.setForMiddleSchool(dto.forMiddleSchool());
    resume.setForHighSchool(dto.forHighSchool());
    resume.setForAdult(dto.forAdult());

    // 프로필 이미지
    if (dto.profileImage() != null) {
      resume.setProfileImage(s3Uploader.uploadImageAndSaveRepository(dto.profileImage(), user));
    }
    resume.setCountry(country);
    resume.setResidenceCountry(residenceCountry);

    resumeRepository.save(resume);
  }

  @Transactional
  public void deleteResume(long userId, long resumeId) {
    log.info("Deleting resume for userId: {}, resumeId: {}", userId, resumeId);
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 본인 이력서만 삭제 가능
    if (resume.getUser().getId() != userId) {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    resumeRepository.delete(resume);
  }

  public Page<RepresentativeResumeVO> getRepresentativeResumes(SearchRepresentativeResumeDto dto) {
    return resumeRepository.findAllRepresentativeResumes(dto);
  }

  public ResumeDetailVO getRepresentativeResumeDetail(long resumeId) {
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));
    if (!Boolean.TRUE.equals(resume.getIsRepresentative())) {
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.RESUME_NOT_FOUND);
    }
    return ResumeDetailVO.valueOf(resume);
  }
}
