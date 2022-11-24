package com.example.demoredis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RedisHash(value = "refreshToken")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    private Long refreshTokenId;    // 토큰 일련번호 (키 값)

    @Indexed
    private Long userId;    // 사용자 일련번호

    private String value;   // 토큰 값

    private LocalDateTime createdAt;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private long expired;    // 만료 시간 (단위: sec, 설정: 7일)

    @JsonIgnore
    public void updateRefreshToken(String value, LocalDateTime createdAt) {
        this.value = value;
        this.createdAt = createdAt;
    }
}
