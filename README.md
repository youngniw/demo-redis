# demo-redis
[SpringBoot] Redis 활용 예제 프로젝트

---

<h3>- 스택</h3>
- Spring Boot
  - Lombok
  - Spring Web
  - Spring Data Redis
  - JWT
- Gradle
- Java 11
- Redis

<br/> 
<h3>- Redis 서버</h3> 

- Url: localhost
- Port: 6379
- Database: 1
- Redis Client: Lettuce

[ redis 서버 실행 방법 ]
```
redis-cli -n 1
```

<br/>
<h3>- Redis 저장소</h3>

1. 설정
- 구성 정보: RedisConfig.java
  - @EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP) 를 통한 RedisRepository 사용 및 만료 시 인덱스 정리를 위한 키스페이스 이벤트를 수신하여 만료 감지
  - LettuceConnectionFactory로 설정

2. Refresh Token
- 포함 정보
  - refreshTokenId: 리프레시 토큰 일련번호
  - userId: 회원 일련번호
  - value: 리프레시 토큰 값
  - createdAt: 생성 및 수정 시간
  - expired: 만료 기한 (7 days)
- 예시
  ```
  {
    "refreshTokenId": 51161919463274009,
    "userId": 1,
    "value": "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ5b3VuZ25pdyIsImV4cCI6MTY2OTg3NzcxNn0.ST5taqHfqqUCQLeuJBrEdgwWc7UiXOTr5QuRlGdjOnESAazwiXRf4zA-OLish7Qzq1QYHqEReG3zhc_EcZF7Kw",
    "createdAt": "2022-11-24T15:55:16",
    "expired": 604800
  }
  ```
