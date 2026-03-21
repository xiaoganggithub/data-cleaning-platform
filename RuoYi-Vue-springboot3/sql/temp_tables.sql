-- ==========================================
-- 瑶池智浣数据清洗平台 - 临时表管理函数
-- ==========================================

-- 1. 创建函数：根据数据集编码创建临时表
CREATE OR REPLACE FUNCTION create_dataset_temp_table(dataset_code_param VARCHAR(64))
RETURNS VARCHAR AS $$
DECLARE
    temp_table_name VARCHAR(100);
BEGIN
    temp_table_name := 'dataset_temp_' || dataset_code_param;

    -- 检查临时表是否已存在
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = temp_table_name
    ) THEN
        RAISE NOTICE '临时表 % 已存在，跳过创建', temp_table_name;
        RETURN temp_table_name;
    END IF;

    -- 创建临时表
    EXECUTE format('
        CREATE TABLE %I (
            shopcode VARCHAR(64) NOT NULL,
            vendorcode VARCHAR(64) NOT NULL,
            sn VARCHAR(64) NOT NULL,
            image_md5 VARCHAR(32) NOT NULL,
            image_url VARCHAR(1024) NOT NULL,
            image_time TIMESTAMP NOT NULL,
            CONSTRAINT %I PRIMARY KEY (shopcode, image_md5)
        )
    ', temp_table_name, temp_table_name);

    EXECUTE format('
        CREATE INDEX idx_%I_shopcode ON %I(shopcode)
    ', temp_table_name, temp_table_name);

    EXECUTE format('
        CREATE INDEX idx_%I_image_md5 ON %I(image_md5)
    ', temp_table_name, temp_table_name);

    EXECUTE format('
        CREATE INDEX idx_%I_image_time ON %I(image_time DESC)
    ', temp_table_name, temp_table_name);

    RAISE NOTICE '临时表 % 创建成功', temp_table_name;
    RETURN temp_table_name;
END;
$$ LANGUAGE plpgsql;

-- 2. 创建函数：删除临时表
CREATE OR REPLACE FUNCTION drop_dataset_temp_table(dataset_code_param VARCHAR(64))
RETURNS BOOLEAN AS $$
DECLARE
    temp_table_name VARCHAR(100);
BEGIN
    temp_table_name := 'dataset_temp_' || dataset_code_param;

    -- 检查临时表是否存在
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = temp_table_name
    ) THEN
        RAISE NOTICE '临时表 % 不存在', temp_table_name;
        RETURN false;
    END IF;

    -- 删除临时表
    EXECUTE format('DROP TABLE IF EXISTS %I CASCADE', temp_table_name);

    RAISE NOTICE '临时表 % 删除成功', temp_table_name;
    RETURN true;
END;
$$ LANGUAGE plpgsql;

-- 3. 创建函数：检查临时表是否过期（7天未使用）
CREATE OR REPLACE FUNCTION check_temp_table_expiry(dataset_code_param VARCHAR(64))
RETURNS BOOLEAN AS $$
DECLARE
    temp_table_name VARCHAR(100);
    last_used_time TIMESTAMP;
    days_diff INTEGER;
BEGIN
    temp_table_name := 'dataset_temp_' || dataset_code_param;

    -- 获取最后更新时间
    SELECT MAX(image_time) INTO last_used_time
    FROM temp_table_name;

    -- 如果表为空或没有image_time字段，视为过期
    IF last_used_time IS NULL THEN
        RETURN true;
    END IF;

    -- 计算天数差
    days_diff := EXTRACT(DAY FROM (CURRENT_TIMESTAMP - last_used_time));

    -- 超过7天未使用，返回true
    RETURN days_diff >= 10; -- 实际使用中设置为7天
END;
$$ LANGUAGE plpgsql;

-- 4. 创建函数：清理过期临时表
CREATE OR REPLACE FUNCTION clean_expired_temp_tables()
RETURNS TABLE (
    cleaned_count INTEGER,
    not_found_count INTEGER
) AS $$
DECLARE
    temp_table_name TEXT;
    last_used_time TIMESTAMP;
    days_diff INTEGER;
    cleaned_count INTEGER := 0;
    not_found_count INTEGER := 0;
BEGIN
    -- 查询所有以dataset_temp_开头的表
    FOR temp_table_name IN
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name LIKE 'dataset_temp_%'
    LOOP
        -- 检查是否过期
        EXECUTE format('SELECT check_temp_table_expiry(%L)', temp_table_name)
        INTO days_diff;

        IF days_diff THEN
            -- 删除过期临时表
            EXECUTE format('DROP TABLE IF EXISTS %I CASCADE', temp_table_name);
            cleaned_count := cleaned_count + 1;
        END IF;
    END LOOP;

    -- 统计未找到的表数量（假设最多10个）
    not_found_count := 10 - cleaned_count;

    RETURN QUERY SELECT cleaned_count, not_found_count;
END;
$$ LANGUAGE plpgsql;

-- 5. 查询所有临时表
SELECT
    table_name,
    (SELECT MAX(image_time) FROM table_name) AS last_used_time,
    (SELECT COUNT(*) FROM table_name) AS record_count
FROM information_schema.tables
WHERE table_schema = 'public'
AND table_name LIKE 'dataset_temp_%'
ORDER BY table_name;

-- 6. 清理过期临时表（测试用）
-- SELECT * FROM clean_expired_temp_tables();

-- ==========================================
-- 临时表管理函数完成
-- ==========================================
