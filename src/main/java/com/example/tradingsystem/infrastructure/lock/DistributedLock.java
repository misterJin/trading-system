package com.example.tradingsystem.infrastructure.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 */
public interface DistributedLock {

    /**
     * 尝试获取分布式锁
     *
     * @param key        锁的键
     * @param expireTime 锁的过期时间
     * @param timeUnit   时间单位
     * @return 是否获取到锁
     */
    boolean tryLock(String key, long expireTime, TimeUnit timeUnit);

    /**
     * 释放分布式锁
     *
     * @param key 锁的键
     */
    void unlock(String key);
}
