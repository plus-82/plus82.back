package com.etplus.vo;

public record TokenVO (
    String accessToken,
    Long accessTokenExpireTime,

    String refreshToken,
    Long refreshTokenExpireTime
) {

}
