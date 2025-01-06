package com.etplus;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EtplusApplication {

  public static void main(String[] args) {
    Locale.setDefault(Locale.ENGLISH);
    SpringApplication.run(EtplusApplication.class, args);
  }

}
