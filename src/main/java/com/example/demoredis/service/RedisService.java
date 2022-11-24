package com.example.demoredis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // string: opsForValue()
    public void setStringOps(String key, String value, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    public String getStringOps(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    // list: opsForList()
    public void setListOps(String key, List<String> values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    public List<Object> getListOps(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return (size==null || size==0) ? new ArrayList<>() : redisTemplate.opsForList().range(key, 0, size-1);
    }

    // hash: opsForHash()
    public void setHashOps(String key, HashMap<String, String> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    public String getHashOps(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    }

    // set: opsForSet()
    public void setSetOps(String key, String... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    public Set<Object> getSetOps(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // sorted set: opsForZSet()
    public void setSortedSetOps(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set getSortedSetOps(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return (size==null || size==0) ? new HashSet<String>() : redisTemplate.opsForZSet().range(key, 0, size-1);
    }
}
