package com.etplus.controller;

import com.etplus.controller.dto.ZepToDiscordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webhook")
public class WebhookController {

  @Value("${discord.webhookURL}")
  private String DISCORD_URL;

  @PostMapping("/zep-to-discord")
  public String zepToDiscord(@RequestBody ZepToDiscordDto dto) {
    System.out.println(dto.toString());

    String content;
    if ("enter".equals(dto.body().type())) {
      content = String.format("%s님이 ZEP에 입장하셨습니다.", dto.body().nickname());
    } else {
      content = String.format("%s님이 ZEP에서 퇴장하셨습니다.", dto.body().nickname());
    }

    WebClient webClient = WebClient.create(DISCORD_URL); // API base URL

    // 비동기 Post 요청
    Mono<String> response = webClient.post()
        .uri("")
        .header("Content-Type", "application/json")
        .bodyValue("{\"content\": \"" + content + "\"}")
        .retrieve()
        .bodyToMono(String.class);

    // 응답 출력
    response.subscribe(System.out::println);

    return "success";
  }

}
