package com.ruoyi.system.application.service;

import com.ruoyi.system.domain.entity.Tag;
import com.ruoyi.system.domain.repository.TagRepository;
import com.ruoyi.system.domain.service.TagDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * 标签应用服务
 */
@Service
public class TagApplicationService {

    private final TagRepository tagRepository;

    public TagApplicationService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * 创建根标签
     */
    @Transactional
    public Tag createRootTag(String name, Tag.TagType type, String description) {
        TagDomainService.validateTagName(name);
        TagDomainService.validateTagDescription(description);
        TagDomainService.validateTagType(type);

        Tag tag = new Tag(name, type);
        tag.setDescription(description);

        return tagRepository.save(tag);
    }

    /**
     * 创建子标签
     */
    @Transactional
    public Tag createChildTag(Long parentId, String name, Tag.TagType type, String description) {
        TagDomainService.validateTagName(name);
        TagDomainService.validateTagDescription(description);
        TagDomainService.validateTagType(type);
        TagDomainService.validateParentId(parentId);

        Tag tag = new Tag(parentId, name, type);
        tag.setDescription(description);

        return tagRepository.save(tag);
    }

    /**
     * 更新标签
     */
    @Transactional
    public Tag updateTag(Long tagId, String name, String description) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        TagDomainService.validateTagName(name);
        TagDomainService.validateTagDescription(description);

        tag.setName(name);
        tag.setDescription(description);

        return tagRepository.save(tag);
    }

    /**
     * 归档标签
     */
    @Transactional
    public Tag archiveTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        tag.archive();

        return tagRepository.save(tag);
    }

    /**
     * 增加使用次数
     */
    @Transactional
    public void incrementUsageCount(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        tag.incrementUsageCount();
        tagRepository.save(tag);
    }

    /**
     * 减少使用次数
     */
    @Transactional
    public void decrementUsageCount(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        tag.decrementUsageCount();
        tagRepository.save(tag);
    }

    /**
     * 更新排序权重
     */
    @Transactional
    public void updateSortOrder(Long tagId, Integer sortOrder) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        tag.updateSortOrder(sortOrder);
        tagRepository.save(tag);
    }

    /**
     * 更新优先级
     */
    @Transactional
    public void updatePriority(Long tagId, Integer priority) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        tag.updatePriority(priority);
        tagRepository.save(tag);
    }

    /**
     * 查询标签
     */
    public Optional<Tag> findById(Long tagId) {
        return tagRepository.findById(tagId);
    }

    /**
     * 查询所有根标签
     */
    public List<Tag> findRootTags() {
        return tagRepository.findRootTags();
    }

    /**
     * 查询子标签
     */
    public List<Tag> findByParentId(Long parentId) {
        return tagRepository.findByParentId(parentId);
    }

    /**
     * 根据类型查询标签
     */
    public List<Tag> findByType(Tag.TagType type) {
        return tagRepository.findByType(type);
    }

    /**
     * 查询所有标签
     */
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    /**
     * 根据状态查询
     */
    public List<Tag> findByStatus(Tag.TagStatus status) {
        return tagRepository.findByStatus(status);
    }

    /**
     * 删除标签
     */
    @Transactional
    public boolean deleteTag(Long tagId) {
        return tagRepository.deleteById(tagId);
    }

    /**
     * 统计数量
     */
    public long count() {
        return tagRepository.count();
    }

    /**
     * 按类型统计
     */
    public Map<Tag.TagType, Long> countByType() {
        return tagRepository.countByType();
    }
}
