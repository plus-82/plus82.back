package com.etplus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

  @GetMapping
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("OK");
  }

  @GetMapping("/logs")
  public ResponseEntity<String> insertLogs() {
    log.trace("[APP_LOG] trace log");
    log.debug("[APP_LOG] debug log");
    log.info("[APP_LOG] info log");
    log.warn("[APP_LOG] warn log");
    log.error("[APP_LOG] error log");
    return ResponseEntity.ok("Log Inserted");
  }

}
