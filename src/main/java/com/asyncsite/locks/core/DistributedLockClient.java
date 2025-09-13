package com.asyncsite.locks.core;

public interface DistributedLockClient {
    String tryLock(String key, long leaseMs, long waitMs, long backoffMs);
    void unlock(String key, String token);
}
