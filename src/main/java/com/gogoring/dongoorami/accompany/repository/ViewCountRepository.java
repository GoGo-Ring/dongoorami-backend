package com.gogoring.dongoorami.accompany.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ViewCountRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    public void save(String key, String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public String findByKey(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public String increaseViewCount(String key) {
        RLock rLock = redissonClient.getLock(key + "_lock");
        String viewCount = "0";

        try {
            boolean isLocked = rLock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("Failed to acquire lock for key: " + key);
                throw new IllegalStateException("Failed to increase view count");
            }
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(key, String.valueOf(Long.parseLong(values.get(key)) + 1));
            viewCount = values.get(key);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new InterruptedException(e.getMessage());
        } finally {
            if (rLock != null && rLock.isLocked()) {
                rLock.unlock();
            }
            return viewCount;
        }
    }
}
