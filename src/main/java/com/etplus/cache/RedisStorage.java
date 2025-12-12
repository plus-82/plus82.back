//package com.etplus.cache;
//
//import java.util.concurrent.TimeUnit;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class RedisStorage {
//
//   @Value("${spring.data.redis.key-prefix}")
//   private String REDIS_KEY_PREFIX;
//
//   private final RedisTemplate<String, String> redisTemplate;
//
//   public String get(String key) {
//     return redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + "::" + key);
//   }
//
//   public void save(String key, String value) {
//     redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + "::" + key, value);
//   }
//
//   public void save(String key, String value, long expireTime) {
//     redisTemplate.opsForValue()
//         .set(REDIS_KEY_PREFIX + "::" + key, value, expireTime, TimeUnit.MILLISECONDS);
//   }
//
//   public void delete(String key) {
//     redisTemplate.delete(REDIS_KEY_PREFIX + "::" + key);
//   }
//}