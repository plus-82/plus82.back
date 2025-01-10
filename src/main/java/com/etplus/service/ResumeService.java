package com.etplus.service;

import com.etplus.controller.dto.CreateResumeDTO;
import com.etplus.controller.dto.CreateResumeWithFileDTO;
import com.etplus.controller.dto.PagingDTO;
import com.etplus.exception.AuthException;
import com.etplus.exception.AuthException.AuthExceptionCode;
import com.etplus.exception.ResourceNotFoundException;
import com.etplus.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode;
import com.etplus.exception.ResumeException;
import com.etplus.exception.ResumeException.ResumeExceptionCode;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.JobPostResumeRelationRepository;
import com.etplus.repository.ResumeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.CountryEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.RoleType;
import com.etplus.vo.ResumeDetailVO;
import com.etplus.vo.ResumeVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;
  private final CountryRepository countryRepository;
  private final JobPostResumeRelationRepository jobPostResumeRelationRepository;
  private final S3Uploader s3Uploader;

  public Slice<ResumeVO> getMyResumes(long userId, PagingDTO dto) {
    return resumeRepository.findAllByUserId(userId, dto);
  }

  public ResumeDetailVO getResumeDetail(RoleType roleType, long userId, long resumeId) {
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    if (RoleType.TEACHER.equals(roleType)) {
      // 본인 이력서만 조회 가능
      if (resume.getUser().getId() != userId) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }
    } else if (RoleType.ACADEMY.equals(roleType)) {
      UserEntity user = userRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException(
              ResourceNotFoundExceptionCode.USER_NOT_FOUND));
      AcademyEntity academy = user.getAcademy();

      // 해당 학원에 지원한 이력서만 조회 가능
      if (!jobPostResumeRelationRepository.existsByResumeIdAndAcademyId(resumeId, academy.getId())) {
        throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
      }
    } else {
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    return ResumeDetailVO.valueOf(resume);
  }

  @Transactional
  public void createResume(long userId, CreateResumeDTO dto) {
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

    resumeRepository.save(
        new ResumeEntity(null, dto.title(), dto.personalIntroduction(), dto.firstName(),
            dto.lastName(), dto.email(), dto.degree(), dto.major(), dto.genderType(),
            dto.birthDate(), dto.hasVisa(), dto.visaType(), dto.isRepresentative(),
            dto.forKindergarten(), dto.forElementary(), dto.forMiddleSchool(), dto.forHighSchool(),
            dto.forAdult(), country, residenceCountry, user, null));
  }

  @Transactional
  public void createResumeWithFile(long userId, CreateResumeWithFileDTO dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    FileEntity file = s3Uploader.uploadResumeAndSaveRepository(dto.file(), user);

    resumeRepository.save(new ResumeEntity(file.getFileName(), user, file));
  }
}
