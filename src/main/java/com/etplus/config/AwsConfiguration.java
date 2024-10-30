package com.etplus.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfiguration {

  @Value("${aws.access-key}")
  private String ACCESS_KEY;
  @Value("${aws.secret-key}")
  private String SECRET_KEY;
  @Value("${aws.ses.region}")
  private String REGION;

  @Bean
  public AWSCredentials awsCredentials() {
    return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
  }

  @Bean
  public AmazonSimpleEmailService awsSimpleEmailService(AWSCredentials awsCredentials) {
    return AmazonSimpleEmailServiceClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(REGION)
        .build();
  }

  @Bean
  public AmazonS3Client amazonS3Client(AWSCredentials awsCredentials) {
    return (AmazonS3Client) AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(REGION)
        .build();
  }

}
