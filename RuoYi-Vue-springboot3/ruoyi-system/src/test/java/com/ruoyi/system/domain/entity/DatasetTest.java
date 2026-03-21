package com.ruoyi.system.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Dataset领域实体测试
 */
class DatasetTest {

    @Test
    void testCreateDataset() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "这是一个测试数据集");

        assertNotNull(dataset);
        assertEquals("TEST001", dataset.getDatasetCode().getValue());
        assertEquals("测试数据集", dataset.getName());
        assertEquals("这是一个测试数据集", dataset.getDescription());
        assertEquals(Dataset.DatasetStatus.INITIALIZED, dataset.getStatus());
        assertEquals(0, dataset.getTotalImageCount().getValue());
        assertEquals(0, dataset.getCleanedImageCount().getValue());
    }

    @Test
    void testDatasetCodeValidation() {
        // 有效编码
        Dataset.DatasetCode validCode = new Dataset.DatasetCode("ABC123");
        assertEquals("ABC123", validCode.getValue());

        // 无效编码 - 长度不对
        assertThrows(DomainException.class, () -> {
            new Dataset.DatasetCode("AB");
        });

        // 无效编码 - 为空
        assertThrows(DomainException.class, () -> {
            new Dataset.DatasetCode("");
        });
    }

    @Test
    void testInitializeDataset() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");

        dataset.initialize();

        assertEquals(Dataset.DatasetStatus.PENDING_CLEANING, dataset.getStatus());
    }

    @Test
    void testInitializeFailsIfNotInitialized() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();

        // 再次初始化应该失败
        assertThrows(DomainException.class, () -> {
            dataset.initialize();
        });
    }

    @Test
    void testStartCleaning() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();

        dataset.addImageCount(100);

        dataset.startCleaning();

        assertEquals(Dataset.DatasetStatus.CLEANING, dataset.getStatus());
    }

    @Test
    void testStartCleaningFailsWithoutImages() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();

        // 没有图片时开始清洗应该失败
        assertThrows(DomainException.class, () -> {
            dataset.startCleaning();
        });
    }

    @Test
    void testCompleteCleaning() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();
        dataset.addImageCount(100);

        dataset.startCleaning();
        dataset.completeCleaning(100);

        assertEquals(Dataset.DatasetStatus.APPROVED, dataset.getStatus());
        assertEquals(100, dataset.getCleanedImageCount().getValue());
    }

    @Test
    void testApprove() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();
        dataset.addImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning(100);

        dataset.approve();

        assertEquals(Dataset.DatasetStatus.APPROVED, dataset.getStatus());
    }

    @Test
    void testPublish() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();
        dataset.addImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning(100);
        dataset.approve();

        dataset.publish();

        assertEquals(Dataset.DatasetStatus.PUBLISHED, dataset.getStatus());
    }

    @Test
    void testArchive() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();
        dataset.addImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning(100);
        dataset.approve();
        dataset.publish();

        dataset.archive();

        assertEquals(Dataset.DatasetStatus.ARCHIVED, dataset.getStatus());
    }

    @Test
    void testDelete() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();
        dataset.addImageCount(100);
        dataset.startCleaning();
        dataset.completeCleaning(100);
        dataset.approve();
        dataset.publish();
        dataset.archive();

        dataset.delete();

        assertEquals(Dataset.DatasetStatus.DELETED, dataset.getStatus());
    }

    @Test
    void testDeleteFailsIfNotArchived() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.initialize();
        dataset.addImageCount(100);
        dataset.startCleaning();

        // 未归档就删除应该失败
        assertThrows(DomainException.class, () -> {
            dataset.delete();
        });
    }

    @Test
    void testAddImageCount() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");

        dataset.addImageCount(100);

        assertEquals(100, dataset.getTotalImageCount().getValue());
    }

    @Test
    void testUpdateStatistics() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");

        dataset.updateStatistics(100, 80);

        assertEquals(100, dataset.getTotalImageCount().getValue());
        assertEquals(80, dataset.getCleanedImageCount().getValue());
    }

    @Test
    void testQualityScoreCalculation() {
        Dataset dataset = new Dataset("TEST001", "测试数据集", "描述");
        dataset.updateStatistics(100, 80);

        double qualityScore = dataset.calculateQualityScore();

        assertEquals(80.0, qualityScore, 0.01);
    }

    @Test
    void testGetQualityGrade() {
        Dataset.QualityGrade gradeA = Dataset.QualityGrade.A;
        assertEquals("A", gradeA.getValue());
        assertEquals("优秀", gradeA.getDescription());

        Dataset.QualityGrade gradeB = Dataset.QualityGrade.B;
        assertEquals("B", gradeB.getValue());
        assertEquals("良好", gradeB.getDescription());
    }

    @Test
    void testDatasetStatusFromValue() {
        Dataset.DatasetStatus status = Dataset.DatasetStatus.fromValue(0);
        assertEquals(Dataset.DatasetStatus.INITIALIZED, status);

        status = Dataset.DatasetStatus.fromValue(1);
        assertEquals(Dataset.DatasetStatus.PENDING_CLEANING, status);
    }

    @Test
    void testInvalidStatusValue() {
        assertThrows(DomainException.class, () -> {
            Dataset.DatasetStatus.fromValue(99);
        });
    }
}
