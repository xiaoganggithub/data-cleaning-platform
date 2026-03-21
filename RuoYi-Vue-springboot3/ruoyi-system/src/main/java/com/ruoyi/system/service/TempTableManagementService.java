package com.ruoyi.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 临时表管理服务
 */
@Service
public class TempTableManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建数据集临时表
     * @param datasetCode 数据集编码
     */
    @Transactional
    public void createTempTable(String datasetCode) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                id BIGSERIAL PRIMARY KEY,
                shopcode VARCHAR(64) NOT NULL,
                vendorcode VARCHAR(64),
                sn VARCHAR(64),
                image_md5 VARCHAR(32) NOT NULL,
                image_url TEXT NOT NULL,
                image_time TIMESTAMP,
                create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """, tableName);

        jdbcTemplate.execute(sql);

        // 创建索引
        String indexSql = String.format("""
            CREATE INDEX IF NOT EXISTS idx_%s_shopcode ON %s(shopcode);
            CREATE INDEX IF NOT EXISTS idx_%s_image_md5 ON %s(image_md5);
            """, datasetCode, tableName, datasetCode, tableName);

        jdbcTemplate.execute(indexSql);
    }

    /**
     * 删除数据集临时表
     * @param datasetCode 数据集编码
     */
    @Transactional
    public void dropTempTable(String datasetCode) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = String.format("DROP TABLE IF EXISTS %s CASCADE", tableName);
        jdbcTemplate.execute(sql);
    }

    /**
     * 检查临时表是否存在
     * @param datasetCode 数据集编码
     * @return 是否存在
     */
    public boolean tempTableExists(String datasetCode) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = """
            SELECT COUNT(*) FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name = ?
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }

    /**
     * 获取临时表记录数
     * @param datasetCode 数据集编码
     * @return 记录数
     */
    public long getTempTableCount(String datasetCode) {
        String tableName = "dataset_temp_" + datasetCode;
        if (!tempTableExists(datasetCode)) {
            return 0;
        }
        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    /**
     * 向临时表插入数据
     * @param datasetCode 数据集编码
     * @param shopcode 店铺编码
     * @param vendorcode 供应商编码
     * @param sn 序列号
     * @param imageMd5 图片MD5
     * @param imageUrl 图片URL
     * @param imageTime 图片时间
     */
    @Transactional
    public void insertTempData(String datasetCode, String shopcode, String vendorcode,
                               String sn, String imageMd5, String imageUrl, java.sql.Timestamp imageTime) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = String.format("""
            INSERT INTO %s (shopcode, vendorcode, sn, image_md5, image_url, image_time)
            VALUES (?, ?, ?, ?, ?, ?)
            """, tableName);

        jdbcTemplate.update(sql, shopcode, vendorcode, sn, imageMd5, imageUrl, imageTime);
    }

    /**
     * 批量插入临时表数据
     * @param datasetCode 数据集编码
     * @param dataList 数据列表
     */
    @Transactional
    public void batchInsertTempData(String datasetCode, List<Map<String, Object>> dataList) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = String.format("""
            INSERT INTO %s (shopcode, vendorcode, sn, image_md5, image_url, image_time)
            VALUES (?, ?, ?, ?, ?, ?)
            """, tableName);

        for (Map<String, Object> data : dataList) {
            jdbcTemplate.update(sql,
                    data.get("shopcode"),
                    data.get("vendorcode"),
                    data.get("sn"),
                    data.get("image_md5"),
                    data.get("image_url"),
                    data.get("image_time"));
        }
    }

    /**
     * 清空临时表数据
     * @param datasetCode 数据集编码
     */
    @Transactional
    public void truncateTempTable(String datasetCode) {
        String tableName = "dataset_temp_" + datasetCode;
        if (tempTableExists(datasetCode)) {
            String sql = String.format("TRUNCATE TABLE %s", tableName);
            jdbcTemplate.execute(sql);
        }
    }

    /**
     * 获取临时表数据
     * @param datasetCode 数据集编码
     * @param limit 限制条数
     * @param offset 偏移量
     * @return 数据列表
     */
    public List<Map<String, Object>> getTempData(String datasetCode, int limit, int offset) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = String.format("""
            SELECT shopcode, vendorcode, sn, image_md5, image_url, image_time
            FROM %s
            ORDER BY id
            LIMIT ? OFFSET ?
            """, tableName);

        return jdbcTemplate.queryForList(sql, limit, offset);
    }

    /**
     * 检查临时表是否过期 (超过7天)
     * @param datasetCode 数据集编码
     * @return 是否过期
     */
    public boolean isTempTableExpired(String datasetCode) {
        String tableName = "dataset_temp_" + datasetCode;
        String sql = String.format("""
            SELECT MAX(create_time) FROM %s
            """, tableName);

        java.sql.Timestamp maxTime = jdbcTemplate.queryForObject(sql, java.sql.Timestamp.class);
        if (maxTime == null) {
            return false;
        }

        long diff = System.currentTimeMillis() - maxTime.getTime();
        return diff > 7 * 24 * 60 * 60 * 1000; // 7天
    }

    /**
     * 清理所有过期临时表
     * @return 清理的表数量
     */
    @Transactional
    public int cleanExpiredTempTables() {
        String sql = """
            SELECT table_name FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name LIKE 'dataset_temp_%'
            """;

        List<String> tableNames = jdbcTemplate.queryForList(sql, String.class);
        int cleanedCount = 0;

        for (String tableName : tableNames) {
            String datasetCode = tableName.replace("dataset_temp_", "");
            if (isTempTableExpired(datasetCode)) {
                dropTempTable(datasetCode);
                cleanedCount++;
            }
        }

        return cleanedCount;
    }
}
