package com.etplus.scheduler;

import com.etplus.provider.EmailProvider;
import com.etplus.repository.JobPostRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.NotificationRepository;
import com.etplus.repository.domain.JobPostEntity;
import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.NotificationEntity;
import com.etplus.repository.domain.UserEntity;
import com.etplus.repository.domain.code.MessageTemplateType;
import com.etplus.scheduler.vo.JobPostDueDateNotiVO;
import com.etplus.scheduler.vo.JobPostNewApplicantNotiVO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

  private final JobPostRepository jobPostRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final NotificationRepository notificationRepository;
  private final EmailProvider emailProvider;

  @Value("${url.front}")
  private String FRONT_URL;

  @Scheduled(cron = "0 1 0 * * ?")
  @Transactional
  public void createDueDateNotification() {
    log.info("Starting creating notification for job post due date");

    // 대상 목록 조회
    LocalDate yesterday = LocalDate.now().minusDays(1);
    List<JobPostEntity> jobPostList = jobPostRepository.findByDueDate(yesterday);

    if (jobPostList.isEmpty()) {
      log.info("No job posts due yesterday ({})", yesterday);
      return;
    }

    log.info("Found job post id : {}", jobPostList.stream().map(JobPostEntity::getId).toList());

    List<NotificationEntity> createdNotificationList = new ArrayList<>();
    List<JobPostEntity> closedJobPostList = new ArrayList<>();
    for (JobPostEntity jobPost : jobPostList) {
      UserEntity user = jobPost.getAcademy().getRepresentativeUser();
      if (user != null) {
        createdNotificationList.add(new NotificationEntity(null, "마감", "Expired",
            String.format("{%s} 공고가 마감되었어요", jobPost.getTitle()),
            String.format("job posting {%s} has closed", jobPost.getTitle()),
            "/business/job-posting",
            user));
      }
      jobPost.setClosed(true);
      closedJobPostList.add(jobPost);
    }
    jobPostRepository.saveAll(closedJobPostList);
    notificationRepository.saveAll(createdNotificationList);
  }

  /***
   * 5시에 공고 마감 예정 알림.
   */
  @Scheduled(cron = "0 0 17 * * ?")
  public void sendDueDateNotifications() {
    log.info("Starting job post due date notification process");

    // 대상 목록 조회
    LocalDate today = LocalDate.now();
    List<JobPostDueDateNotiVO> jobPosts = jobPostRepository.findDueDateNotificationTarget(today);

    if (jobPosts.isEmpty()) {
      log.info("No job posts due today ({})", today);
      return;
    }
    log.info("Found {} job posts due today ({})", jobPosts.size(), today);

    // 메일 템플릿 조회
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "JOB_POST_DUE_DATE", MessageTemplateType.EMAIL).orElse(null);

    // 이메일 발송
    for (JobPostDueDateNotiVO vo : jobPosts) {
      if (!vo.academyUserAllowEmail()) {
        log.info("Skipping email notification for academy {} {} as email notifications are disabled", vo.academyId(), vo.academyName());
        continue;
      }
      Map params = new HashMap();
      params.put("name", vo.academyName());
      params.put("jobTitle", vo.title());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
      params.put("todayStr", today.format(formatter));
      params.put("jobPostResumeTotalCount", vo.jobPostResumeTotalCount());
      params.put("jobPostResumeSubmittedCount", vo.jobPostResumeSubmittedCount());
      params.put("jobPostResumeReviewedCount", vo.jobPostResumeReviewedCount());
      params.put("link", FRONT_URL + "/business/job-posting");

      StringSubstitutor sub = new StringSubstitutor(params);
      String title = sub.replace(emailTemplate.getTitle());
      String content = sub.replace(emailTemplate.getContent());

      String receiverEmail = vo.representativeEmail();
      if (vo.byAdmin()) {
        receiverEmail = vo.adminUserEmail();
      }

      emailProvider.send(receiverEmail, title, content);
    }
    log.info("Completed job post due date notification process");
  }

  /***
   * 9시에 새 지원자 알림.
   */
  @Scheduled(cron = "0 0 9 * * ?")
  public void sendNewApplicantNotifications() {
    log.info("Starting new applicant notification process");

    // 대상 목록 조회
    LocalDate today = LocalDate.now();
    List<JobPostNewApplicantNotiVO> jobPosts = jobPostRepository.findNewApplicantNotificationTarget(today);

    if (jobPosts.isEmpty()) {
      log.info("No job posts with new applicants today ({})", today);
      return;
    }
    log.info("Found {} job posts with new applicants today ({})", jobPosts.size(), today);

    // 메일 템플릿 조회
    MessageTemplateEntity emailTemplate = messageTemplateRepository.findByCodeAndType(
        "JOB_POST_NEW_APPLICANT", MessageTemplateType.EMAIL).orElse(null);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    // 이메일 발송
    for (JobPostNewApplicantNotiVO vo : jobPosts) {
      if (!vo.academyUserAllowEmail()) {
        log.info("Skipping email notification for academy {} {} as email notifications are disabled", vo.academyId(), vo.academyName());
        continue;
      }
      Map params = new HashMap();
      params.put("name", vo.academyName());
      params.put("jobTitle", vo.title());
      params.put("date", today.minusDays(1l).format(formatter));
      params.put("count", vo.yesterdayJobPostResumeTotalCount());
      params.put("link", FRONT_URL + "/business/job-posting/" + vo.id() + "/applicant-management");

      StringSubstitutor sub = new StringSubstitutor(params);
      String title = sub.replace(emailTemplate.getTitle());
      String content = sub.replace(emailTemplate.getContent());

      String receiverEmail = vo.representativeEmail();
      if (vo.byAdmin()) {
        receiverEmail = vo.adminUserEmail();
      }

      emailProvider.send(receiverEmail, title, content);
    }
    log.info("Completed new applicant notification process");
  }

}
