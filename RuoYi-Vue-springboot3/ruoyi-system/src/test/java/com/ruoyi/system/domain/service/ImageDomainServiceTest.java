package com.ruoyi.system.domain.service;

import com.ruoyi.system.domain.entity.ImageStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ImageDomainService测试
 */
class ImageDomainServiceTest {

    @Test
    void testIsValidImageUrl() {
        assertTrue(ImageDomainService.isValidImageUrl("http://example.com/image.jpg"));
        assertTrue(ImageDomainService.isValidImageUrl("https://example.com/image.jpg"));
        assertFalse(ImageDomainService.isValidImageUrl("ftp://example.com/image.jpg"));
        assertFalse(ImageDomainService.isValidImageUrl(""));
        assertFalse(ImageDomainService.isValidImageUrl(null));
    }

    @Test
    void testIsValidImageMd5() {
        assertTrue(ImageDomainService.isValidImageMd5("abcd1234abcd1234abcd1234abcd1234"));
        assertTrue(ImageDomainService.isValidImageMd5("ABCD1234ABCD1234ABCD1234ABCD1234"));
        assertFalse(ImageDomainService.isValidImageMd5("abcd1234")); // 长度不对
        assertFalse(ImageDomainService.isValidImageMd5(""));
        assertFalse(ImageDomainService.isValidImageMd5(null));
    }

    @Test
    void testValidateCleanRemark() {
        // 有效备注
        ImageDomainService.validateCleanRemark("这是一个有效的备注");

        // null备注也允许
        ImageDomainService.validateCleanRemark(null);

        // 太长的备注应该抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            ImageDomainService.validateCleanRemark("a".repeat(501));
        });
    }

    @Test
    void testValidateQualityScore() {
        // 有效评分
        ImageDomainService.validateQualityScore(0.0);
        ImageDomainService.validateQualityScore(50.0);
        ImageDomainService.validateQualityScore(100.0);
        ImageDomainService.validateQualityScore(null); // null也允许

        // 无效评分
        assertThrows(IllegalArgumentException.class, () -> {
            ImageDomainService.validateQualityScore(-1.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ImageDomainService.validateQualityScore(101.0);
        });
    }

    @Test
    void testValidateStatusTransition() {
        // 有效转换
        ImageDomainService.validateStatusTransition(ImageStatus.PENDING_CLEANING, ImageStatus.CLEANED);
        ImageDomainService.validateStatusTransition(ImageStatus.CLEANED, ImageStatus.PENDING_REVIEW);
        ImageDomainService.validateStatusTransition(ImageStatus.PENDING_REVIEW, ImageStatus.APPROVED);
        ImageDomainService.validateStatusTransition(ImageStatus.PENDING_REVIEW, ImageStatus.REJECTED);

        // 无效转换
        assertThrows(IllegalArgumentException.class, () -> {
            ImageDomainService.validateStatusTransition(ImageStatus.PENDING_CLEANING, ImageStatus.APPROVED);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ImageDomainService.validateStatusTransition(ImageStatus.APPROVED, ImageStatus.PENDING_CLEANING);
        });
    }

    @Test
    void testIsCleaned() {
        assertTrue(ImageDomainService.isCleaned(ImageStatus.CLEANED));
        assertFalse(ImageDomainService.isCleaned(ImageStatus.PENDING_CLEANING));
        assertFalse(ImageDomainService.isCleaned(ImageStatus.APPROVED));
    }

    @Test
    void testIsPendingReview() {
        assertTrue(ImageDomainService.isPendingReview(ImageStatus.PENDING_REVIEW));
        assertFalse(ImageDomainService.isPendingReview(ImageStatus.CLEANED));
        assertFalse(ImageDomainService.isPendingReview(ImageStatus.APPROVED));
    }

    @Test
    void testIsApproved() {
        assertTrue(ImageDomainService.isApproved(ImageStatus.APPROVED));
        assertFalse(ImageDomainService.isApproved(ImageStatus.PENDING_REVIEW));
        assertFalse(ImageDomainService.isApproved(ImageStatus.REJECTED));
    }

    @Test
    void testIsRejected() {
        assertTrue(ImageDomainService.isRejected(ImageStatus.REJECTED));
        assertFalse(ImageDomainService.isRejected(ImageStatus.APPROVED));
        assertFalse(ImageDomainService.isRejected(ImageStatus.PENDING_REVIEW));
    }

    @Test
    void testIsPendingCleaning() {
        assertTrue(ImageDomainService.isPendingCleaning(ImageStatus.PENDING_CLEANING));
        assertFalse(ImageDomainService.isPendingCleaning(ImageStatus.CLEANED));
        assertFalse(ImageDomainService.isPendingCleaning(ImageStatus.APPROVED));
    }

    @Test
    void testIsLocked() {
        assertTrue(ImageDomainService.isLocked(true));
        assertFalse(ImageDomainService.isLocked(false));
        assertFalse(ImageDomainService.isLocked(null));
    }

    @Test
    void testIsActive() {
        assertTrue(ImageDomainService.isActive(ImageStatus.PENDING_CLEANING));
        assertTrue(ImageDomainService.isActive(ImageStatus.CLEANED));
        assertTrue(ImageDomainService.isActive(ImageStatus.PENDING_REVIEW));
        assertFalse(ImageDomainService.isActive(ImageStatus.APPROVED));
        assertFalse(ImageDomainService.isActive(ImageStatus.REJECTED));
    }

    @Test
    void testGenerateCleanRemark() {
        assertEquals("无问题", ImageDomainService.generateCleanRemark(""));
        assertEquals("模糊", ImageDomainService.generateCleanRemark("模糊"));
    }

    @Test
    void testCalculateAverageQualityScore() {
        assertEquals(0.0, ImageDomainService.calculateAverageQualityScore(null, null));
        assertEquals(50.0, ImageDomainService.calculateAverageQualityScore(50.0, null));
        assertEquals(50.0, ImageDomainService.calculateAverageQualityScore(null, 50.0));
        assertEquals(75.0, ImageDomainService.calculateAverageQualityScore(50.0, 100.0));
    }
}
