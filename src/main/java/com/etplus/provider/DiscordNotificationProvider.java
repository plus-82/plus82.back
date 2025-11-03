package com.etplus.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DiscordNotificationProvider {

  @Value("${discord.notificationURL}")
  private String DISCORD_URL;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public void sendDiscordNotification(String content) {
    try {
      log.info("Discord 알림 전송 시도 - content: {}", content);

      WebClient webClient = WebClient.create(DISCORD_URL);

      // ObjectMapper를 사용하여 안전하게 JSON 생성
      Map<String, String> payload = Map.of("content", content);
      String jsonBody = objectMapper.writeValueAsString(payload);

      Mono<String> response = webClient.post()
          .uri("")
          .header("Content-Type", "application/json")
          .bodyValue(jsonBody)
          .retrieve()
          .bodyToMono(String.class);

      response.subscribe(
          result -> log.info("Discord 알림 전송 성공 - result: {}", result),
          error -> log.error("Discord 알림 전송 실패 - error: {}", error.getMessage())
      );
    } catch (JsonProcessingException e) {
      log.error("Discord 알림 JSON 생성 오류", e);
    } catch (Exception e) {
      log.error("Discord 알림 전송 오류", e);
    }
  }

}
