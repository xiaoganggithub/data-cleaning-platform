package com.ruoyi.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 分布式ID生成器
 * 基于Snowflake算法实现
 */
@Service
@RequiredArgsConstructor
public class IdGeneratorService {

    // 时间戳起点 (2020-01-01)
    private static final long EPOCH = 1577836800000L;

    // 机器ID位数 (5位)
    private static final long WORKER_ID_BITS = 5L;

    // 数据中心ID位数 (5位)
    private static final long DATACENTER_ID_BITS = 5L;

    // 序列号位数 (12位)
    private static final long SEQUENCE_BITS = 12L;

    // 机器ID最大值 (31)
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    // 数据中心ID最大值 (31)
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    // 机器ID左移位数
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    // 数据中心ID左移位数
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // 时间戳左移位数
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    // 序列号掩码 (4095)
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成下一个ID (字符串形式)
     */
    public String nextIdStr() {
        return String.valueOf(nextId());
    }

    /**
     * 阻塞到下一个毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 从ID中解析时间戳
     */
    public long getTimestamp(long id) {
        return ((id >> TIMESTAMP_SHIFT) & (~(-1L << 41))) + EPOCH;
    }

    /**
     * 从ID中解析机器ID
     */
    public long getWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 从ID中解析数据中心ID
     */
    public long getDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * 从ID中解析序列号
     */
    public long getSequence(long id) {
        return id & SEQUENCE_MASK;
    }
}
