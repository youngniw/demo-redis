package com.example.demoredis.controller;

import com.example.demoredis.dto.LoginDto;
import com.example.demoredis.dto.TokenDto;
import com.example.demoredis.dto.TokenRequestDto;
import com.example.demoredis.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    // userId로 토큰 생성 (테스트용 - RedisTemplate 이용)
    @PostMapping("/login")
    public ResponseEntity<TokenDto> getNewToken(@RequestBody LoginDto loginDto) {
        TokenDto token = authService.generateToken(loginDto.getUserId());

        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    // userId로 토큰 생성 (테스트용 - RedisTemplate 이용)
    

    // access token 과 refresh token 을 이용한 토큰재발급
    @PatchMapping("/token/user")
    public ResponseEntity<TokenDto> reissueRefreshToken(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenDto token = authService.updateToken(tokenRequestDto);

        return ResponseEntity.ok(token);
    }

    // refreshToken 값으로 삭제
    @DeleteMapping("/token/user/{userId}")
    public ResponseEntity<String> deleteRefreshToken(@PathVariable("userId") Long userId) {
        authService.deleteToken(userId);

        return ResponseEntity.ok("success");
    }
}
