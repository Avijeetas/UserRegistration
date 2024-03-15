package com.example.demo.auth.services;

/**
 * @author Avijeet
 * @project UserRegistration
 * @github avijeetas
 * @date 03-15-2024
 **/
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

   private final RedisTemplate<String, String> redisTemplate;

   public RedisService(RedisTemplate<String, String> redisTemplate) {
      this.redisTemplate = redisTemplate;
   }

   public void save(String key, String value, int timeoutMinute, TimeUnit unit) {
      redisTemplate.opsForValue().set(key, value, timeoutMinute, unit);
   }

   public String get(String key) {
      return redisTemplate.opsForValue().get(key);
   }

   public boolean exists(String key) {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key));
   }

   public void delete(String key) {
      redisTemplate.delete(key);
   }
}

