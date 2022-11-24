package com.example.demoredis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpireDate;
}