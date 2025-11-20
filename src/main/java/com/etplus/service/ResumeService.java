package com.etplus.service;

import com.etplus.controller.dto.ContactResumeDTO;
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
import com.etplus.provider.DiscordNotificationProvider;
import com.etplus.provider.EmailProvider;
import com.etplus.provider.S3Uploader;
import com.etplus.repository.AcademyRepository;
import com.etplus.repository.CountryRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.ResumeContactRepository;
import com.etplus.repository.ResumeRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.CountryEntity;
import com.etplus.repository.domain.FileEntity;
import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.ResumeContactEntity;
import com.etplus.repository.domain.ResumeEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.MessageTemplateType;
import com.etplus.vo.RepresentativeResumeVO;
import com.etplus.vo.ResumeDetailVO;
import com.etplus.vo.ResumeVO;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResumeService {

  private final AcademyRepository academyRepository;
  @Value("${url.front}")
  private String FRONT_URL;

  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;
  private final CountryRepository countryRepository;
  private final ResumeContactRepository resumeContactRepository;
  private final S3Uploader s3Uploader;
  private final DiscordNotificationProvider discordNotificationProvider;
  private final MessageTemplateRepository messageTemplateRepository;
  private final EmailProvider emailProvider;

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
    log.info("createResume 시작 - userId: {}, dto: {}", userId, dto);
    
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
        log.warn("이미 대표 이력서 존재 - userId: {}", userId);
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

    // Discord 알림 전송
    String teacherName = user.getName() != null ? user.getName() : 
        (user.getFirstName() + " " + user.getLastName());

    String message = String.format("📝 새로운 이력서 생성 📝\n\n" +
        "선생님: %s\n" +
        "이력서제목: %s\n" +
        "대표이력서: %s\n" +
        "선생님 이메일: %s",
        teacherName,
        dto.title() != null ? dto.title() : "제목 없음",
        dto.isRepresentative() ? "예" : "아니오",
        user.getEmail()
    );

    discordNotificationProvider.sendDiscordNotification(message);
    log.info("createResume 완료 - userId: {}, resumeId: {}", userId);
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
    log.info("createResumeWithFile 시작 - userId: {}", userId);
    
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    FileEntity file = s3Uploader.uploadResumeAndSaveRepository(dto.file(), user);

    ResumeEntity savedResume = resumeRepository.save(new ResumeEntity(file.getFileName(), user, file, false));

    // Discord 알림 전송
    String teacherName = user.getName() != null ? user.getName() : 
        (user.getFirstName() + " " + user.getLastName());

    String message = String.format("📝 파일 이력서 업로드 📝\n\n" +
        "선생님: %s\n" +
        "파일명: %s\n" +
        "선생님 이메일: %s",
        teacherName,
        savedResume.getTitle() != null ? savedResume.getTitle() : "제목 없음",
        user.getEmail()
    );

    discordNotificationProvider.sendDiscordNotification(message);
    
    log.info("createResumeWithFile 완료 - userId: {}, resumeId: {}", userId, savedResume.getId());
  }

  @Transactional
  public void updateResume(long userId, long resumeId, UpdateResumeDTO dto) {
    log.info("updateResume 시작 - userId: {}, resumeId: {}", userId, resumeId);
    
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));

    // 본인 이력서만 수정 가능
    if (resume.getUser().getId() != userId) {
      log.warn("이력서 수정 권한 없음 - resumeId: {}, resumeUserId: {}, requestUserId: {}", 
          resumeId, resume.getUser().getId(), userId);
      throw new AuthException(AuthExceptionCode.ACCESS_DENIED);
    }

    // 파일 이력서는 수정 불가
    if (resume.getFile() != null) {
      log.warn("파일 이력서는 수정 불가 - resumeId: {}", resumeId);
      throw new ResumeException(ResumeExceptionCode.FILE_RESUME_CANNOT_BE_MODIFIED);
    }

    // 대표 이력서 중복되는지 확인
    if (dto.isRepresentative() && !resume.getIsRepresentative()) {
      if (resumeRepository.existsByUserIdAndIsRepresentativeIsTrue(userId)) {
        log.warn("이미 대표 이력서 존재 - userId: {}", userId);
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
    
    log.info("updateResume 완료 - userId: {}, resumeId: {}", userId, resumeId);
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
    return ResumeDetailVO.valueOfWithMasking(resume);
  }

  @Transactional
  public Long contactResume(long resumeId, long userId, ContactResumeDTO dto) {
    log.info("Contacting resume - resumeId: {}, userId: {}", resumeId, userId);
    ResumeEntity resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.RESUME_NOT_FOUND));
    if (!Boolean.TRUE.equals(resume.getIsRepresentative())) {
      log.warn("Not representative resume - resumeId: {}", resumeId);
      throw new ResourceNotFoundException(ResourceNotFoundExceptionCode.RESUME_NOT_FOUND);
    }

    UserEntity academyUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(
            ResourceNotFoundExceptionCode.USER_NOT_FOUND));
    AcademyEntity academy = academyRepository.findByRepresentativeUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundExceptionCode.ACADEMY_NOT_FOUND));

    // 중복 컨택 확인
    if (resumeContactRepository.existsByResumeIdAndAcademyUser(resumeId, academyUser)) {
      log.warn("Resume already contacted - resumeId: {}, academyUserId: {}", resumeId, userId);
      throw new ResumeException(ResumeExceptionCode.RESUME_ALREADY_CONTACTED);
    }

    try {
      log.info("선생님 컨택 이메일 전송 시도 - resumeId: {}, userId: {}, email: {}",
          resumeId, userId, resume.getEmail());
      MessageTemplateEntity emailTemplate = messageTemplateRepository
          .findByCodeAndType("TEACHER_CONTACT", MessageTemplateType.EMAIL).orElse(null);

      Map params = new HashMap();

      params.put("name", resume.getLastName() + " " + resume.getFirstName());
      params.put("academyName", academy.getNameEn());
      params.put("interestReason", dto.interestReason());
      params.put("appealMessage", dto.appealMessage());
      params.put("additionalMessage", dto.additionalMessage());
      params.put("academyEmail", academyUser.getEmail());
      params.put("link", FRONT_URL);

      StringSubstitutor sub = new StringSubstitutor(params);
      String emailTitle = sub.replace(emailTemplate.getTitle());
      String emailContent = sub.replace(emailTemplate.getContent());

      emailProvider.send(resume.getEmail(), emailTitle, emailContent);
      log.info("선생님 컨택 이메일 전송 성공 - userId: {}, email: {}", userId, resume.getEmail());
    } catch (Exception e) {
      log.error("선생님 컨택 이메일 전송 실패 - userId: {}, email: {}", userId, resume.getEmail(), e);
    }

    ResumeContactEntity contact = ResumeContactEntity.create(resume, academyUser,
        dto.interestReason(), dto.appealMessage(), dto.additionalMessage(), dto.contactEmail());
    resumeContactRepository.save(contact);
    log.info("Resume contact created successfully - contactId: {}, resumeId: {}, academyUserId: {}",
        contact.getId(), resumeId, userId);
    return contact.getId();
  }
}
