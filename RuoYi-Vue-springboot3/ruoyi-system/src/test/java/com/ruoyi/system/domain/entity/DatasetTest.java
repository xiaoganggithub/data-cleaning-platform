package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.valueobject.DatasetCode;
import com.ruoyi.system.domain.valueobject.ImageCount;
import com.ruoyi.system.domain.valueobject.QualityGrade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Dataset领域实体测试
 */
class DatasetTest {

    @Test
    void testCreateDataset() {
        Dataset dataset = new Dataset("测试数据集", "这是一个测试数据集");

        assertNotNull(dataset);
        assertEquals("测试数据集", dataset.getName());
        assertEquals("这是一个测试数据集", dataset.getDescription());
        assertEquals(DatasetStatus.INITIALIZED, dataset.getStatus());
        assertEquals(0, dataset.getTotalImageCount().getValue());
        assertEquals(0, dataset.getCleanedImageCount().getValue());
    }

    @Test
    void testDatasetCodeValidation() {
        // 有效编码
        DatasetCode validCode = new DatasetCode("ABC123");
        assertEquals("ABC123", validCode.getValue());

        // 无效编码 - 长度不对
        assertThrows(DomainException.class, () -> {
            new DatasetCode("AB");
        });

        // 无效编码 - 为空
        assertThrows(DomainException.class, () -> {
            new DatasetCode("");
        });
    }

    @Test
    void testInitializeDataset() {
        Dataset dataset = new Dataset("测试数据集", "描述");

        dataset.initialize();

        assertEquals(DatasetStatus.PENDING_CLEANING, dataset.getStatus());
    }

    @Test
    void testInitializeFailsIfNotInitialized() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();

        // 再次初始化应该失败
        assertThrows(DomainException.class, () -> {
            dataset.initialize();
        });
    }

    @Test
    void testStartCleaning() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);

        dataset.startCleaning();

        assertEquals(DatasetStatus.CLEANING, dataset.getStatus());
    }

    @Test
    void testStartCleaningFailsWithoutImages() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();

        // 没有图片时开始清洗应该失败
        assertThrows(DomainException.class, () -> {
            dataset.startCleaning();
        });
    }

    @Test
    void testCompleteCleaning() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);

        dataset.startCleaning();
        dataset.completeCleaning();

        assertEquals(DatasetStatus.APPROVED, dataset.getStatus());
    }

    @Test
    void testApprove() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning();

        dataset.approve();

        assertEquals(DatasetStatus.APPROVED, dataset.getStatus());
    }

    @Test
    void testArchive() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning();
        dataset.approve();

        dataset.archive();

        assertEquals(DatasetStatus.ARCHIVED, dataset.getStatus());
    }

    @Test
    void testDelete() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning();
        dataset.approve();
        dataset.archive();

        dataset.delete();

        assertEquals(DatasetStatus.DELETED, dataset.getStatus());
    }

    @Test
    void testDeleteFailsIfNotArchived() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);
        dataset.startCleaning();

        // 未归档就删除应该失败
        assertThrows(DomainException.class, () -> {
            dataset.delete();
        });
    }

    @Test
    void testAddTotalImageCount() {
        Dataset dataset = new Dataset("测试数据集", "描述");

        dataset.addTotalImageCount(100);

        assertEquals(100, dataset.getTotalImageCount().getValue());
    }

    @Test
    void testAddCleanedImageCount() {
        Dataset dataset = new Dataset("测试数据集", "描述");

        dataset.addCleanedImageCount(80);

        assertEquals(80, dataset.getCleanedImageCount().getValue());
    }

    @Test
    void testUpdateStatistics() {
        Dataset dataset = new Dataset("测试数据集", "描述");

        dataset.addTotalImageCount(100);
        dataset.addCleanedImageCount(80);
        dataset.updateStatistics();

        assertEquals(100, dataset.getTotalImageCount().getValue());
        assertEquals(80, dataset.getCleanedImageCount().getValue());
    }

    @Test
    void testQualityScoreCalculation() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.addTotalImageCount(100);
        dataset.addCleanedImageCount(80);

        double qualityScore = dataset.calculateQualityScore();

        assertEquals(80.0, qualityScore, 0.01);
    }

    @Test
    void testGetQualityGrade() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.addTotalImageCount(100);
        dataset.addCleanedImageCount(80);

        QualityGrade grade = dataset.getQualityGrade();

        assertEquals("优秀", grade.getDescription());
    }

    @Test
    void testDatasetStatusFromValue() {
        DatasetStatus status = DatasetStatus.fromValue(0);
        assertEquals(DatasetStatus.INITIALIZED, status);

        status = DatasetStatus.fromValue(1);
        assertEquals(DatasetStatus.PENDING_CLEANING, status);
    }

    @Test
    void testInvalidStatusValue() {
        assertThrows(DomainException.class, () -> {
            DatasetStatus.fromValue(99);
        });
    }

    @Test
    void testIsActive() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        assertTrue(dataset.isActive());

        dataset.initialize();
        assertTrue(dataset.isActive());
    }

    @Test
    void testIsCleaningCompleted() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        dataset.initialize();
        dataset.addTotalImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning();

        assertTrue(dataset.isCleaningCompleted());
    }

    @Test
    void testIsDeletable() {
        Dataset dataset = new Dataset("测试数据集", "描述");
        assertFalse(dataset.isDeletable());

        dataset.initialize();
        dataset.addTotalImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning();
        dataset.approve();
        dataset.archive();

        assertTrue(dataset.isDeletable());
    }
}
