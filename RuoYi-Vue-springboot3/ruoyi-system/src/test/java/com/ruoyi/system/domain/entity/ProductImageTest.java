package com.ruoyi.system.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductImage领域实体测试
 */
class ProductImageTest {

    @Test
    void testCreateImage() {
        ProductImage image = new ProductImage(
            1L, "DS001", "SHOP001", "VENDOR001", "SN001",
            "abcd1234abcd1234abcd1234abcd1234", "https://example.com/image.jpg"
        );

        assertNotNull(image);
        assertEquals(1L, image.getDatasetId());
        assertEquals("DS001", image.getDatasetCode());
        assertEquals("SHOP001", image.getShopcode());
        assertEquals("abcd1234abcd1234abcd1234abcd1234", image.getImageMd5());
        assertEquals("https://example.com/image.jpg", image.getImageUrl());
        assertEquals(ImageStatus.PENDING_CLEANING, image.getImageStatus());
        assertFalse(image.getLocked());
    }

    @Test
    void testCleanImage() {
        ProductImage image = createTestImage();

        image.cleanImage("模糊", "需要重新拍摄");

        assertEquals(ImageStatus.CLEANED, image.getImageStatus());
        assertEquals("模糊", image.getProblemTypes());
        assertEquals("需要重新拍摄", image.getCleanRemark());
    }

    @Test
    void testCleanImageFailsIfNotPending() {
        ProductImage image = createTestImage();
        image.cleanImage("模糊", "备注");

        // 再次清洗应该失败
        assertThrows(DomainException.class, () -> {
            image.cleanImage("遮挡", "备注");
        });
    }

    @Test
    void testSubmitForReview() {
        ProductImage image = createTestImage();
        image.cleanImage("无问题", "通过");

        image.submitForReview();

        assertEquals(ImageStatus.PENDING_REVIEW, image.getImageStatus());
    }

    @Test
    void testSubmitForReviewFailsIfNotCleaned() {
        ProductImage image = createTestImage();

        // 未清洗就提交审核应该失败
        assertThrows(DomainException.class, () -> {
            image.submitForReview();
        });
    }

    @Test
    void testApprove() {
        ProductImage image = createTestImage();
        image.cleanImage("无问题", "通过");
        image.submitForReview();

        image.approve();

        assertEquals(ImageStatus.APPROVED, image.getImageStatus());
    }

    @Test
    void testApproveFailsIfNotPendingReview() {
        ProductImage image = createTestImage();
        image.cleanImage("无问题", "通过");

        // 未提交审核就批准应该失败
        assertThrows(DomainException.class, () -> {
            image.approve();
        });
    }

    @Test
    void testReject() {
        ProductImage image = createTestImage();
        image.cleanImage("模糊", "有问题");
        image.submitForReview();

        image.reject("图片模糊不清");

        assertEquals(ImageStatus.REJECTED, image.getImageStatus());
    }

    @Test
    void testRejectFailsIfNotPendingReview() {
        ProductImage image = createTestImage();
        image.cleanImage("模糊", "有问题");

        // 未提交审核就拒绝应该失败
        assertThrows(DomainException.class, () -> {
            image.reject("图片模糊不清");
        });
    }

    @Test
    void testLockAndUnlock() {
        ProductImage image = createTestImage();

        image.lock();
        assertTrue(image.getLocked());

        image.unlock();
        assertFalse(image.getLocked());
    }

    @Test
    void testSetQualityScore() {
        ProductImage image = createTestImage();

        image.setQualityScore(85.5);

        assertEquals(85.5, image.getQualityScore());
    }

    @Test
    void testIsCleaned() {
        ProductImage image = createTestImage();
        assertFalse(image.isCleaned());

        image.cleanImage("无", "通过");
        assertTrue(image.isCleaned());
    }

    @Test
    void testIsPendingReview() {
        ProductImage image = createTestImage();
        image.cleanImage("无", "通过");

        assertFalse(image.isPendingReview());

        image.submitForReview();
        assertTrue(image.isPendingReview());
    }

    @Test
    void testIsApproved() {
        ProductImage image = createTestImage();
        image.cleanImage("无", "通过");
        image.submitForReview();
        image.approve();

        assertTrue(image.isApproved());
    }

    @Test
    void testImageStatusFromValue() {
        ImageStatus status = ImageStatus.fromValue(0);
        assertEquals(ImageStatus.PENDING_CLEANING, status);

        status = ImageStatus.fromValue(1);
        assertEquals(ImageStatus.CLEANED, status);

        status = ImageStatus.fromValue(2);
        assertEquals(ImageStatus.PENDING_REVIEW, status);

        status = ImageStatus.fromValue(3);
        assertEquals(ImageStatus.APPROVED, status);

        status = ImageStatus.fromValue(4);
        assertEquals(ImageStatus.REJECTED, status);
    }

    @Test
    void testInvalidImageStatusValue() {
        assertThrows(DomainException.class, () -> {
            ImageStatus.fromValue(99);
        });
    }

    private ProductImage createTestImage() {
        return new ProductImage(
            1L, "DS001", "SHOP001", "VENDOR001", "SN001",
            "abcd1234abcd1234abcd1234abcd1234", "https://example.com/image.jpg"
        );
    }
}
