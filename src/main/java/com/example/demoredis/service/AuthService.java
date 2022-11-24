package com.example.demoredis.service;

import com.example.demoredis.domain.RefreshToken;
import com.example.demoredis.dto.TokenDto;
import com.example.demoredis.dto.TokenRequestDto;
import com.example.demoredis.repository.RedisRefreshTokenRepository;
import com.example.demoredis.utils.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenProvider tokenProvider;
    private final RedisRefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiredTime;

    // 토큰(access, refresh) 생성
    public TokenDto generateToken(Long userId) {
        TokenDto tokenDto = tokenProvider.generateToken(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .value(tokenDto.getRefreshToken())
                .createdAt(LocalDateTime.now())
                .expired(refreshTokenExpiredTime)
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("refresh token 일련번호: {}", refreshTokenRepository.findById(refreshToken.getRefreshTokenId()));
        log.info("refresh token 개수: {}", refreshTokenRepository.count());

        return tokenDto;
    }

    // 토큰 유효성 검사 후, 토큰(access, refresh) 갱신
    public TokenDto updateToken(TokenRequestDto tokenRequestDto) {
        if (tokenProvider.validateToken(tokenRequestDto.getAccessToken()) &&
                tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            Long userId = tokenProvider.getUserId(tokenRequestDto.getAccessToken());

            TokenDto tokenDto = tokenProvider.generateToken(userId);

            // 저장소에서 refresh 토큰 값 수정
            RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));
            refreshToken.updateRefreshToken(tokenDto.getRefreshToken(), LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);

            log.info("refresh token 일련번호: {}", refreshTokenRepository.findById(refreshToken.getRefreshTokenId()));
            log.info("refresh token 개수: {}", refreshTokenRepository.count());

            return tokenDto;
        }
        else {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
    }

    // 토큰(refresh) 삭제
    public void deleteToken(Long userId) {
        // 저장소에서 refresh 토큰 값 삭제 위함
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("옳지 않은 회원 번호입니다."));

        refreshTokenRepository.delete(refreshToken);

        log.info("refresh token 개수: {}", refreshTokenRepository.count());
    }
}
