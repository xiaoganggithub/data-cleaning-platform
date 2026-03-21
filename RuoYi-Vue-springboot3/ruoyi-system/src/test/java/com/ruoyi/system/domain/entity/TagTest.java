package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.valueobject.TagCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tag领域实体测试
 */
class TagTest {

    @Test
    void testCreateRootTag() {
        Tag tag = new Tag("模糊", TagType.BLUR);

        assertNotNull(tag);
        assertEquals("模糊", tag.getName());
        assertEquals(TagType.BLUR, tag.getType());
        assertEquals(TagStatus.NORMAL, tag.getStatus());
        assertNull(tag.getParentId());
        assertEquals(0, tag.getUsageCount());
        assertEquals(0, tag.getImageCount());
    }

    @Test
    void testCreateChildTag() {
        Tag parentTag = new Tag("质量问题", TagType.QUALITY);
        Tag childTag = new Tag(parentTag.getTagId(), "模糊", TagType.BLUR);

        assertNotNull(childTag);
        assertEquals(parentTag.getTagId(), childTag.getParentId());
        assertEquals("模糊", childTag.getName());
    }

    @Test
    void testCreateTagFailsWithEmptyName() {
        assertThrows(DomainException.class, () -> {
            new Tag("", TagType.QUALITY);
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
        Tag tag = new Tag("测试标签", TagType.QUALITY);

        tag.archive();

        assertEquals(TagStatus.ARCHIVED, tag.getStatus());
    }

    @Test
    void testArchiveAlreadyArchivedFails() {
        Tag tag = new Tag("测试标签", TagType.QUALITY);
        tag.archive();

        assertThrows(DomainException.class, () -> {
            tag.archive();
        });
    }

    @Test
    void testIncrementUsageCount() {
        Tag tag = new Tag("测试标签", TagType.QUALITY);

        tag.incrementUsageCount();

        assertEquals(1, tag.getUsageCount());
    }

    @Test
    void testDecrementUsageCount() {
        Tag tag = new Tag("测试标签", TagType.QUALITY);
        tag.incrementUsageCount();
        tag.incrementUsageCount();

        tag.decrementUsageCount();

        assertEquals(1, tag.getUsageCount());
    }

    @Test
    void testDecrementUsageCountDoesNotGoBelowZero() {
        Tag tag = new Tag("测试标签", TagType.QUALITY);

        tag.decrementUsageCount();

        assertEquals(0, tag.getUsageCount());
    }

    @Test
    void testUpdateSortOrder() {
        Tag tag = new Tag("测试标签", TagType.QUALITY);

        tag.updateSortOrder(10);

        assertEquals(10, tag.getSortOrder());
    }

    @Test
    void testUpdatePriority() {
        Tag tag = new Tag("测试标签", TagType.QUALITY);

        tag.updatePriority(5);

        assertEquals(5, tag.getPriority());
    }

    @Test
    void testIsRoot() {
        Tag rootTag = new Tag("根标签", TagType.QUALITY);
        assertTrue(rootTag.isRoot());

        Tag childTag = new Tag(rootTag.getTagId(), "子标签", TagType.BLUR);
        assertFalse(childTag.isRoot());
    }

    @Test
    void testIsActive() {
        Tag activeTag = new Tag("活跃标签", TagType.QUALITY);
        assertTrue(activeTag.isActive());

        Tag archivedTag = new Tag("已归档标签", TagType.QUALITY);
        archivedTag.archive();
        assertFalse(archivedTag.isActive());
    }

    @Test
    void testTagCodeValidation() {
        TagCode validCode = new TagCode("ABCD1234");
        assertEquals("ABCD1234", validCode.getValue());

        assertThrows(DomainException.class, () -> {
            new TagCode("AB");
        });

        assertThrows(DomainException.class, () -> {
            new TagCode("");
        });
    }

    @Test
    void testTagTypeFromValue() {
        TagType type = TagType.fromValue(0);
        assertEquals(TagType.QUALITY, type);

        type = TagType.fromValue(1);
        assertEquals(TagType.CLARITY, type);

        type = TagType.fromValue(2);
        assertEquals(TagType.BLUR, type);

        type = TagType.fromValue(3);
        assertEquals(TagType.OCCLUSION, type);

        type = TagType.fromValue(4);
        assertEquals(TagType.BACKGROUND, type);

        type = TagType.fromValue(9);
        assertEquals(TagType.OTHER, type);
    }

    @Test
    void testInvalidTagTypeValue() {
        assertThrows(DomainException.class, () -> {
            TagType.fromValue(99);
        });
    }

    @Test
    void testTagStatusFromValue() {
        TagStatus status = TagStatus.fromValue(0);
        assertEquals(TagStatus.NORMAL, status);

        status = TagStatus.fromValue(1);
        assertEquals(TagStatus.ARCHIVED, status);
    }

    @Test
    void testInvalidTagStatusValue() {
        assertThrows(DomainException.class, () -> {
            TagStatus.fromValue(99);
        });
    }
}
