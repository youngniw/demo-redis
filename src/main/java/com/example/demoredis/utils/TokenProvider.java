package com.example.demoredis.utils;

import com.example.demoredis.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private final String secret;
    private final long accessTokenValidTime;
    private final long refreshTokenValidTime;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidTime,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidTime) {
        this.secret = secret;
        this.accessTokenValidTime = accessTokenValidTime;
        this.refreshTokenValidTime = refreshTokenValidTime;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Long userId) {
        LocalDateTime accessValidDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusSeconds(this.accessTokenValidTime);
        LocalDateTime refreshValidDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusSeconds(this.refreshTokenValidTime);

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("youngniw")
                .claim("user", userId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(Date.from(accessValidDate.atZone(ZoneId.of("Asia/Seoul")).toInstant()))
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuer("youngniw")
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(Date.from(refreshValidDate.atZone(ZoneId.of("Asia/Seoul")).toInstant()))
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(accessValidDate)
                .build();
    }

    // jwt 토큰 복호화하여 정보 추출
    public Long getUserId(String accessToken) {
        Claims claims = parseClaim(accessToken);

        if (claims.get("user") == null) {
            return null;
        }
        else {
            return (Long.valueOf(claims.get("user").toString()));
        }
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 jwt 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 jwt 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 jwt 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("jwt 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 토큰 정보 추출
    private Claims parseClaim(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
