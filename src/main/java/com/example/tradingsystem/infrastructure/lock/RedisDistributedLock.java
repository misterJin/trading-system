package com.example.tradingsystem.infrastructure.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Redisson分布式锁实现
 */
@Component
public class RedisDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;
    private final ConcurrentHashMap<String, ReentrantLock> localLocks = new ConcurrentHashMap<>();

    public RedisDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
        if (redissonClient != null) {
            RLock lock = redissonClient.getLock(key);
            try {
                return lock.tryLock(0, expireTime, timeUnit);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                // Redisson不可用时，降级为本地锁
                return tryLocalLock(key, expireTime, timeUnit);
            }
        } else {
            // RedissonClient为null时，使用本地锁
            return tryLocalLock(key, expireTime, timeUnit);
        }
    }

    @Override
    public void unlock(String key) {
        if (redissonClient != null) {
            try {
                RLock lock = redissonClient.getLock(key);
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (Exception e) {
                // Redisson不可用时，降级为本地锁
                unlockLocalLock(key);
            }
        } else {
            // RedissonClient为null时，使用本地锁
            unlockLocalLock(key);
        }
    }

    /**
     * 尝试获取本地锁
     */
    private boolean tryLocalLock(String key, long expireTime, TimeUnit timeUnit) {
        ReentrantLock lock = localLocks.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            return lock.tryLock(expireTime, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放本地锁
     */
    private void unlockLocalLock(String key) {
        ReentrantLock lock = localLocks.get(key);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
