package com.etplus.scheduler;

import com.etplus.provider.EmailProvider;
import com.etplus.repository.JobPostRepository;
import com.etplus.repository.UserRepository;
import com.etplus.repository.domain.AcademyEntity;
import com.etplus.repository.domain.JobPostEntity;
import com.etplus.scheduler.vo.JobPostDueDateNotiVO;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

  private final JobPostRepository jobPostRepository;
  private final UserRepository userRepository;
  private final EmailProvider emailProvider;

  /***
   * 5시에 공고 마감 예정 알림.
   */
  @Scheduled(cron = "0 0 17 * * ?")
  @Transactional(readOnly = true)
  public void sendDueDateNotifications() {
    log.info("Starting job post due date notification process");

    LocalDate today = LocalDate.now();
    List<JobPostDueDateNotiVO> jobPosts = jobPostRepository.findDueDateNotificationTarget(today);

    log.info("Found {} job posts due today ({})", jobPosts.size(), today);

    if (jobPosts.isEmpty()) {
      return;
    }

    // send email
    for (JobPostDueDateNotiVO vo : jobPosts) {
      // to representative



      if (vo.byAdmin()) {
        // to admin

      }
    }


//안녕하세요 {Name}님,
//귀하가 등록한 채용 공고 "[Job Posting Title]"이(가) 오늘(YYYY.MM.DD) 12시에 마감될 예정입니다
//
//현재까지 지원자 통계:
//
//총 지원자 수: {Total Applicants}
//
//접수 : {Document Passed Count}
//
//검토 중 : {Final Accepted Count}
//
//대시보드에서 자세한 내용을 확인해 주세요.
//감사합니다,
//Plus82 팀



    // Send notification emails for each job post
//    for (JobPostEntity jobPost : jobPosts) {
//      try {
//        AcademyEntity academy = jobPost.getAcademy();
//
//        // Find academy admin users
//        Specification<UserEntity> userSpec = (root, query, criteriaBuilder) ->
//            criteriaBuilder.and(
//                criteriaBuilder.equal(root.get("academy"), academy),
//                criteriaBuilder.equal(root.get("roleType"), RoleType.ACADEMY),
//                criteriaBuilder.equal(root.get("deleted"), false),
//                criteriaBuilder.equal(root.get("allowEmail"), true)
//            );
//
//        List<UserEntity> academyAdmins = userRepository.findAll(userSpec);
//
//        if (academyAdmins.isEmpty()) {
//          log.warn("No admin users found for academy ID: {}, Name: {}",
//              academy.getId(), academy.getName());
//          continue;
//        }
//
//        // Get all admin emails
//        List<String> recipientEmails = academyAdmins.stream()
//            .map(UserEntity::getEmail)
//            .collect(Collectors.toList());
//
//        String subject = "[Plus82] Job Post Due Date Notification";
//
//        for (String email : recipientEmails) {
//          String content = buildEmailContent(jobPost, academy);
//          emailProvider.send(email, subject, content);
//
//          log.info("Sent due date notification email to {} for job post ID: {}, Title: {}",
//              email, jobPost.getId(), jobPost.getTitle());
//        }
//
//      } catch (Exception e) {
//        log.error("Failed to send due date notification for job post ID: {}",
//            jobPost.getId(), e);
//      }
//    }
//
//    log.info("Completed job post due date notification process");
  }

  /**
   * Builds the HTML content for the notification email.
   */
  private String buildEmailContent(JobPostEntity jobPost, AcademyEntity academy) {
    return String.format("""
        <html>
        <body>
            <h2>Job Post Due Date Notification</h2>
            <p>Dear %s Administrator,</p>
            <p>Your job post "<strong>%s</strong>" is due today.</p>
            <p>If you wish to extend the due date or make changes to your job post, 
            please log in to your Plus82 account.</p>
            <p>Thank you for using Plus82!</p>
            <p>Best regards,<br>The Plus82 Team</p>
        </body>
        </html>
        """, academy.getName(), jobPost.getTitle());
  }
}
