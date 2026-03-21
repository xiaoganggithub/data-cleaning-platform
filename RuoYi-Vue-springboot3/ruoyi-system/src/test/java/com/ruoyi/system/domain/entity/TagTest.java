package com.ruoyi.system.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tag领域实体测试
 */
class TagTest {

    @Test
    void testCreateRootTag() {
        Tag tag = new Tag("模糊", Tag.TagType.BLUR);

        assertNotNull(tag);
        assertEquals("模糊", tag.getName());
        assertEquals(Tag.TagType.BLUR, tag.getType());
        assertEquals(Tag.TagStatus.NORMAL, tag.getStatus());
        assertNull(tag.getParentId());
        assertEquals(0, tag.getUsageCount());
        assertEquals(0, tag.getImageCount());
    }

    @Test
    void testCreateChildTag() {
        Tag parentTag = new Tag("质量问题", Tag.TagType.QUALITY);
        Tag childTag = new Tag(parentTag.getTagId(), "模糊", Tag.TagType.BLUR);

        assertNotNull(childTag);
        assertEquals(parentTag.getTagId(), childTag.getParentId());
        assertEquals("模糊", childTag.getName());
    }

    @Test
    void testCreateTagFailsWithEmptyName() {
        assertThrows(DomainException.class, () -> {
            new Tag("", Tag.TagType.QUALITY);
        });
    }

    @Test
    void testCreateTagFailsWithNullType() {
        assertThrows(DomainException.class, () -> {
            new Tag("测试标签", null);
        });
    }

    @Test
    void testArchiveTag() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);

        tag.archive();

        assertEquals(Tag.TagStatus.ARCHIVED, tag.getStatus());
    }

    @Test
    void testArchiveAlreadyArchivedFails() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);
        tag.archive();

        assertThrows(DomainException.class, () -> {
            tag.archive();
        });
    }

    @Test
    void testIncrementUsageCount() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);

        tag.incrementUsageCount();

        assertEquals(1, tag.getUsageCount());
    }

    @Test
    void testDecrementUsageCount() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);
        tag.incrementUsageCount();
        tag.incrementUsageCount();

        tag.decrementUsageCount();

        assertEquals(1, tag.getUsageCount());
    }

    @Test
    void testDecrementUsageCountDoesNotGoBelowZero() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);

        tag.decrementUsageCount();

        assertEquals(0, tag.getUsageCount());
    }

    @Test
    void testUpdateSortOrder() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);

        tag.updateSortOrder(10);

        assertEquals(10, tag.getSortOrder());
    }

    @Test
    void testUpdatePriority() {
        Tag tag = new Tag("测试标签", Tag.TagType.QUALITY);

        tag.updatePriority(5);

        assertEquals(5, tag.getPriority());
    }

    @Test
    void testIsRoot() {
        Tag rootTag = new Tag("根标签", Tag.TagType.QUALITY);
        assertTrue(rootTag.isRoot());

        Tag childTag = new Tag(rootTag.getTagId(), "子标签", Tag.TagType.BLUR);
        assertFalse(childTag.isRoot());
    }

    @Test
    void testIsActive() {
        Tag activeTag = new Tag("活跃标签", Tag.TagType.QUALITY);
        assertTrue(activeTag.isActive());

        Tag archivedTag = new Tag("已归档标签", Tag.TagType.QUALITY);
        archivedTag.archive();
        assertFalse(archivedTag.isActive());
    }

    @Test
    void testTagCodeValidation() {
        Tag.TagCode validCode = new Tag.TagCode("ABCD1234");
        assertEquals("ABCD1234", validCode.getValue());

        assertThrows(DomainException.class, () -> {
            new Tag.TagCode("AB");
        });

        assertThrows(DomainException.class, () -> {
            new Tag.TagCode("");
        });
    }

    @Test
    void testTagTypeFromValue() {
        Tag.TagType type = Tag.TagType.fromValue(0);
        assertEquals(Tag.TagType.QUALITY, type);

        type = Tag.TagType.fromValue(1);
        assertEquals(Tag.TagType.CLARITY, type);

        type = Tag.TagType.fromValue(2);
        assertEquals(Tag.TagType.BLUR, type);

        type = Tag.TagType.fromValue(3);
        assertEquals(Tag.TagType.OCCLUSION, type);

        type = Tag.TagType.fromValue(4);
        assertEquals(Tag.TagType.BACKGROUND, type);

        type = Tag.TagType.fromValue(9);
        assertEquals(Tag.TagType.OTHER, type);
    }

    @Test
    void testInvalidTagTypeValue() {
        assertThrows(DomainException.class, () -> {
            Tag.TagType.fromValue(99);
        });
    }

    @Test
    void testTagStatusFromValue() {
        Tag.TagStatus status = Tag.TagStatus.fromValue(0);
        assertEquals(Tag.TagStatus.NORMAL, status);

        status = Tag.TagStatus.fromValue(1);
        assertEquals(Tag.TagStatus.ARCHIVED, status);
    }

    @Test
    void testInvalidTagStatusValue() {
        assertThrows(DomainException.class, () -> {
            Tag.TagStatus.fromValue(99);
        });
    }
}
