-- ==========================================
-- 瑶池智浣数据清洗平台 - 分区表管理
-- ==========================================

-- 1. 创建分区子表函数
CREATE OR REPLACE FUNCTION create_partition_by_year(year_param INTEGER)
RETURNS VARCHAR AS $$
DECLARE
    partition_name VARCHAR(20);
BEGIN
    partition_name := 'product_image_' || year_param;

    -- 检查分区是否已存在
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = partition_name
    ) THEN
        RAISE NOTICE '分区表 % 已存在，跳过创建', partition_name;
        RETURN partition_name;
    END IF;

    -- 创建分区表
    EXECUTE format('
        CREATE TABLE %I PARTITION OF product_image
        FOR VALUES FROM (''%L-01-01'') TO (''%L-01-01'')
    ', partition_name, year_param, year_param + 1);

    -- 创建索引
    EXECUTE format('
        CREATE INDEX idx_%I_shopcode ON %I(shopcode)
    ', partition_name, partition_name);

    EXECUTE format('
        CREATE INDEX idx_%I_image_md5 ON %I(image_md5)
    ', partition_name, partition_name);

    EXECUTE format('
        CREATE INDEX idx_%I_image_time ON %I(image_time DESC)
    ', partition_name, partition_name);

    EXECUTE format('
        CREATE INDEX idx_%I_status ON %I(image_status)
    ', partition_name, partition_name);

    RAISE NOTICE '分区表 % 创建成功', partition_name;
    RETURN partition_name;
END;
$$ LANGUAGE plpgsql;

-- 2. 创建当前年份和下一年度的分区
-- 注意：需要在运行时修改年份参数
DO $$
DECLARE
    current_year INTEGER := EXTRACT(YEAR FROM CURRENT_DATE);
    next_year INTEGER := current_year + 1;
BEGIN
    RAISE NOTICE '正在创建分区表：', current_year, ' 和 ', next_year;

    -- 创建当前年份分区
    PERFORM create_partition_by_year(current_year);

    -- 创建下一年分区
    PERFORM create_partition_by_year(next_year);

    RAISE NOTICE '分区表创建完成';
END $$;

-- 3. 获取所有分区表列表
SELECT
    table_name,
    (SELECT MIN(image_time) FROM table_name) AS min_time,
    (SELECT MAX(image_time) FROM table_name) AS max_time,
    (SELECT COUNT(*) FROM table_name) AS record_count
FROM information_schema.tables
WHERE table_schema = 'public'
AND table_name LIKE 'product_image_%'
ORDER BY table_name;

-- 4. 查看分区表统计信息
SELECT
    schemaname AS schema,
    tablename AS table_name,
    parenttablename AS parent_table,
    pgeschemaname AS partition_schema,
    pgtable AS partition_table,
    pgrelid::regclass AS partition_relation,
    pg_inherits.relfilenode,
    pg_stats.tablename AS table_name_stats,
    pg_stats.n_live_tup AS live_tuples,
    pg_stats.n_dead_tup AS dead_tuples,
    pg_stats.n_tune_tup AS tunable_tuples,
    pg_stats.last_analyze AS last_analyze_time,
    pg_stats.last_vacuum AS last_vacuum_time
FROM pg_inherits
JOIN pg_class AS pgrel ON pg_inherits.inhrelid = pgrel.oid
JOIN pg_class AS pgesch ON pg_inherits.inhparent = pgesch.oid
JOIN pg_tables ON pg_tables.schemaname = pgesch.schemaname AND pg_tables.tablename = pgesch.relname
LEFT JOIN pg_stats ON pg_stats.tablename = pgrel.relname
WHERE pgrel.relname LIKE 'product_image%'
ORDER BY pgrel.relname;

-- 5. 自动管理：删除旧分区（保留最近2年）
CREATE OR REPLACE FUNCTION auto_manage_partitions()
RETURNS TABLE (
    deleted_partition_name VARCHAR(100),
    delete_date TIMESTAMP
) AS $$
DECLARE
    current_year INTEGER := EXTRACT(YEAR FROM CURRENT_DATE);
    year_to_delete INTEGER;
    partition_name VARCHAR(100);
BEGIN
    -- 删除3年前的分区
    year_to_delete := current_year - 3;

    FOR partition_name IN
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name LIKE 'product_image_%'
        AND SUBSTRING(table_name FROM 14) = year_to_delete::VARCHAR
    LOOP
        -- 删除分区
        EXECUTE format('DROP TABLE IF EXISTS %I CASCADE', partition_name);

        RAISE NOTICE '已删除旧分区表：%', partition_name;
        RETURN QUERY SELECT partition_name, CURRENT_TIMESTAMP;
    END LOOP;

    RETURN QUERY SELECT NULL, NULL;
END;
$$ LANGUAGE plpgsql;

-- 6. 清理所有旧分区（测试用）
-- SELECT * FROM auto_manage_partitions();

-- 7. 创建主表和分区的视图
CREATE OR REPLACE VIEW product_image_with_partition AS
SELECT
    image_id,
    dataset_id,
    dataset_code,
    category_id,
    plu_code,
    plu_name,
    shard_id,
    shopcode,
    vendorcode,
    sn,
    image_md5,
    image_url,
    image_status,
    locked,
    create_time,
    update_time,
    status,
    'product_image'::VARCHAR || ' (' || create_time::DATE::TEXT || ')' AS partition_name
FROM product_image;

-- 8. 统计各分区数据量
SELECT
    'product_image_' || EXTRACT(YEAR FROM create_time)) AS partition_name,
    COUNT(*) AS record_count,
    MIN(create_time) AS earliest,
    MAX(create_time) AS latest
FROM product_image
GROUP BY EXTRACT(YEAR FROM create_time)
ORDER BY EXTRACT(YEAR FROM create_time);

-- 9. 分区表维护：重新分析表
-- ANALYZE product_image;
-- ANALYZE product_image_2026;
-- ANALYZE product_image_2027;

-- 10. 分区表维护：清空表（删除所有数据，保留表结构）
-- TRUNCATE TABLE product_image CASCADE;
-- TRUNCATE TABLE product_image_2026 CASCADE;
-- TRUNCATE TABLE product_image_2027 CASCADE;

-- ==========================================
-- 分区表管理完成
-- ==========================================
-- 注意：需要根据实际年份动态调整 current_year 参数
