package com.ruoyi.common.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分布式锁服务
 */
@Service
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "distributed_lock:";

    public DistributedLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁
     * @param key 锁的key
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        String value = Thread.currentThread().getId() + ":" + System.currentTimeMillis();
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, value, timeout, unit);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 释放锁
     * @param key 锁的key
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
    }

    /**
     * 检查锁是否存在
     * @param key 锁的key
     * @return 是否存在
     */
    public boolean isLocked(String key) {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * 执行加锁操作
     * @param key 锁的key
     * @param action 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T executeWithLock(String key, long timeout, TimeUnit unit, java.util.concurrent.Callable<T> action) {
        if (!tryLock(key, timeout, unit)) {
            throw new RuntimeException("获取锁失败: " + key);
        }
        try {
            return action.call();
        } catch (Exception e) {
            throw new RuntimeException("执行失败", e);
        } finally {
            unlock(key);
        }
    }

    /**
     * 执行加锁操作 (无返回值)
     * @param key 锁的key
     * @param action 要执行的操作
     */
    public void executeWithLock(String key, long timeout, TimeUnit unit, Runnable action) {
        if (!tryLock(key, timeout, unit)) {
            throw new RuntimeException("获取锁失败: " + key);
        }
        try {
            action.run();
        } finally {
            unlock(key);
        }
    }
}
