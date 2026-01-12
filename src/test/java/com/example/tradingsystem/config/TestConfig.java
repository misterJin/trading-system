package com.example.tradingsystem.config;

import com.example.tradingsystem.infrastructure.lock.DistributedLock;
import com.example.tradingsystem.infrastructure.lock.RedisDistributedLock;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试配置类，提供内存实现的分布式锁
 */
@Configuration
public class TestConfig {

    /**
     * 模拟的RedissonClient
     */
    @Bean
    public RedissonClient redissonClient() {
        return Mockito.mock(RedissonClient.class);
    }

    /**
     * 内存实现的分布式锁
     */
    @Bean
    @Primary
    public DistributedLock distributedLock() {
        return new InMemoryDistributedLock();
    }

    /**
     * 内存实现的分布式锁
     */
    static class InMemoryDistributedLock implements DistributedLock {

        private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

        @Override
        public boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
            ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
            try {
                return lock.tryLock(expireTime, timeUnit);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        @Override
        public void unlock(String key) {
            ReentrantLock lock = locks.get(key);
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
