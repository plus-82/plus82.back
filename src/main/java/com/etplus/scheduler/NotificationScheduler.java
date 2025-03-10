package com.etplus.scheduler;

import com.etplus.provider.EmailProvider;
import com.etplus.repository.JobPostRepository;
import com.etplus.repository.MessageTemplateRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.JobPostEntity;
import com.etplus.repository.domain.MessageTemplateEntity;
import com.etplus.repository.domain.code.MessageTemplateType;
import com.etplus.scheduler.vo.JobPostDueDateNotiVO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

  private final JobPostRepository jobPostRepository;
  private final MessageTemplateRepository messageTemplateRepository;
  private final UserRepository userRepository;
  private final EmailProvider emailProvider;

  /***
   * 5시에 공고 마감 예정 알림.
   */
  @Scheduled(cron = "0 0 17 * * ?")
  @Transactional(readOnly = true)
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
      Map params = new HashMap();
      params.put("name", vo.academyName());
      params.put("jobTitle", vo.title());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
      params.put("todayStr", today.format(formatter));
      params.put("jobPostResumeTotalCount", vo.jobPostResumeTotalCount());
      params.put("jobPostResumeSubmittedCount", vo.jobPostResumeSubmittedCount());
      params.put("jobPostResumeReviewedCount", vo.jobPostResumeReviewedCount());
      params.put("link", "https://plus82.co/my-job-posts");

      StringSubstitutor sub = new StringSubstitutor(params);
      String title = sub.replace(emailTemplate.getTitle());
      String content = sub.replace(emailTemplate.getContent());

      emailProvider.send(vo.representativeEmail(), title, content);
    }
    log.info("Completed job post due date notification process");
  }
}
