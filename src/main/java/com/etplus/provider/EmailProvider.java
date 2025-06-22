package com.etplus.provider;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

  private final AmazonSimpleEmailService amazonSimpleEmailService;

  @Value("${aws.ses.sender.email}")
  private String defaultSenderEmail;

  public String send(String to, String subject, String content) {
    log.info("Sending email to: {}, subject: {}, content: {}", to, subject, content);
    SendEmailRequest request = new SendEmailRequest()
        .withSource(defaultSenderEmail)
        .withDestination(new Destination().withToAddresses(to))
        .withReturnPath(defaultSenderEmail)
        .withMessage(new Message()
            .withSubject(new Content().withCharset("UTF-8").withData(subject))
            .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(content)))
        );
    SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(request);
    return sendEmailResult.getMessageId();
  }

}
