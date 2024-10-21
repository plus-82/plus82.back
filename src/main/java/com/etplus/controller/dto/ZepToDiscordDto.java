package com.etplus.controller.dto;


public record ZepToDiscordDto(
    ZepToDiscordBody body
) {

  public record ZepToDiscordBody(
      String map_hashID,
      String userKey,
      String nickname,
      String type,
      String date,
      String userId
  ) {

  }

}
