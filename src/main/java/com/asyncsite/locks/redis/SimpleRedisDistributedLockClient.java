package com.asyncsite.locks.redis;

import com.asyncsite.locks.core.DistributedLockClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import java.util.UUID;

public class SimpleRedisDistributedLockClient implements DistributedLockClient {
    private final StringRedisTemplate redis;

    public SimpleRedisDistributedLockClient(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public String tryLock(String key, long leaseMs, long waitMs, long backoffMs) {
        long end = System.currentTimeMillis() + waitMs;
        String token = UUID.randomUUID().toString();
        while (System.currentTimeMillis() < end) {
            Boolean ok = redis.opsForValue().setIfAbsent(key, token, Duration.ofMillis(leaseMs));
            if (Boolean.TRUE.equals(ok)) return token;
            try { Thread.sleep(Math.max(1, backoffMs)); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); break; }
        }
        return null;
    }

    @Override
    public void unlock(String key, String token) {
        try {
            String val = redis.opsForValue().get(key);
            if (token != null && token.equals(val)) {
                redis.delete(key);
            }
        } catch (Exception ignored) { }
    }
}
