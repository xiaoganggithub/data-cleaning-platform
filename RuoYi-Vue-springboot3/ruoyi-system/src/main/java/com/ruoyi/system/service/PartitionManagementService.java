package com.ruoyi.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 分区表管理服务
 */
@Service
public class PartitionManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建年度分区
     * @param tableName 表名
     * @param year 年份
     */
    @Transactional
    public void createYearlyPartition(String tableName, int year) {
        // 创建主分区表（如果不存在）
        String createMainTable = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                id BIGSERIAL
            ) PARTITION BY RANGE (create_time)
            """, tableName);
        jdbcTemplate.execute(createMainTable);

        // 创建当年分区
        String partitionName = tableName + "_" + year;
        String startDate = year + "-01-01";
        String endDate = (year + 1) + "-01-01";

        String createPartition = String.format("""
            CREATE TABLE IF NOT EXISTS %s PARTITION OF %s
            FOR VALUES FROM ('%s') TO ('%s')
            """, partitionName, tableName, startDate, endDate);

        jdbcTemplate.execute(createPartition);
    }

    /**
     * 创建月度分区
     * @param tableName 表名
     * @param year 年份
     * @param month 月份
     */
    @Transactional
    public void createMonthlyPartition(String tableName, int year, int month) {
        // 创建主分区表（如果不存在）
        String createMainTable = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                id BIGSERIAL
            ) PARTITION BY RANGE (create_time)
            """, tableName);
        jdbcTemplate.execute(createMainTable);

        // 创建当月分区
        String partitionName = String.format("%s_%d_%02d", tableName, year, month);
        String startDate = String.format("%d-%02d-01", year, month);
        java.time.LocalDate nextMonth = java.time.LocalDate.of(year, month, 1).plusMonths(1);
        String endDate = nextMonth.toString();

        String createPartition = String.format("""
            CREATE TABLE IF NOT EXISTS %s PARTITION OF %s
            FOR VALUES FROM ('%s') TO ('%s')
            """, partitionName, tableName, startDate, endDate);

        jdbcTemplate.execute(createPartition);
    }

    /**
     * 检查分区是否存在
     * @param partitionName 分区名
     * @return 是否存在
     */
    public boolean partitionExists(String partitionName) {
        String sql = """
            SELECT COUNT(*) FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name = ?
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, partitionName);
        return count != null && count > 0;
    }

    /**
     * 获取表的所有分区
     * @param tableName 表名
     * @return 分区列表
     */
    public List<String> getPartitions(String tableName) {
        String sql = """
            SELECT child.relname AS partition_name
            FROM pg_inherits
            JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
            JOIN pg_class child ON pg_inherits.inhrelid = child.oid
            WHERE parent.relname = ?
            ORDER BY child.relname
            """;
        return jdbcTemplate.queryForList(sql, String.class, tableName);
    }

    /**
     * 删除旧分区 (保留最近N年)
     * @param tableName 表名
     * @param retentionYears 保留年数
     */
    @Transactional
    public int dropOldPartitions(String tableName, int retentionYears) {
        List<String> partitions = getPartitions(tableName);
        int cutoffYear = java.time.LocalDate.now().getYear() - retentionYears;
        int droppedCount = 0;

        for (String partition : partitions) {
            // 尝试从分区名中提取年份
            String yearStr = partition.replace(tableName + "_", "");
            try {
                int year = Integer.parseInt(yearStr);
                if (year < cutoffYear) {
                    String dropSql = String.format("DROP TABLE IF EXISTS %s", partition);
                    jdbcTemplate.execute(dropSql);
                    droppedCount++;
                }
            } catch (NumberFormatException e) {
                // 忽略无法解析的分区名
            }
        }

        return droppedCount;
    }

    /**
     * 自动管理分区 - 创建当前和下一年的分区
     * @param tableName 表名
     */
    @Transactional
    public void autoManagePartitions(String tableName) {
        int currentYear = java.time.LocalDate.now().getYear();

        // 创建当前年份分区
        createYearlyPartition(tableName, currentYear);

        // 创建下一年分区
        createYearlyPartition(tableName, currentYear + 1);
    }

    /**
     * 获取分区统计信息
     * @param tableName 表名
     * @return 统计信息
     */
    public List<Map<String, Object>> getPartitionStatistics(String tableName) {
        String sql = """
            SELECT
                child.relname AS partition_name,
                pg_size_pretty(pg_relation_size(child.oid)) AS size,
                pg_relation_size(child.oid) AS size_bytes,
                (SELECT COUNT(*) FROM %s WHERE create_time >= TO_DATE(child.relname, 'YYYY')) AS row_count
            FROM pg_inherits
            JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
            JOIN pg_class child ON pg_inherits.inhrelid = child.oid
            WHERE parent.relname = ?
            ORDER BY child.relname
            """;

        String formattedSql = String.format(sql, tableName);
        return jdbcTemplate.queryForList(formattedSql, tableName);
    }

    /**
     * 分析分区表
     * @param tableName 表名
     */
    @Transactional
    public void analyzePartition(String tableName) {
        String sql = String.format("ANALYZE %s", tableName);
        jdbcTemplate.execute(sql);
    }
}
