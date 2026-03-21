package com.ruoyi.system.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.entity.Tag;
import com.ruoyi.system.domain.repository.TagRepository;
import com.ruoyi.system.persistence.mapper.TagMapper;
import com.ruoyi.system.persistence.po.TagPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签仓储实现
 */
@Repository
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;

    public TagRepositoryImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public Tag save(Tag tag) {
        TagPO po = toPO(tag);
        if (tag.getTagId() == null) {
            tagMapper.insert(po);
        } else {
            tagMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<Tag> findById(Long id) {
        TagPO po = tagMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<Tag> findByParentId(Long parentId) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(parentId == null, TagPO::getParentId, null);
        if (parentId != null) {
            wrapper.eq(TagPO::getParentId, parentId);
        }
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByStatus(Tag.TagStatus status) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getStatus, status.getValue());
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByType(Tag.TagType type) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getType, type.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByParentIdAndType(Long parentId, Tag.TagType type) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        if (parentId == null) {
            wrapper.isNull(TagPO::getParentId);
        } else {
            wrapper.eq(TagPO::getParentId, parentId);
        }
        wrapper.eq(TagPO::getType, type.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findRootTags() {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(TagPO::getParentId);
        wrapper.eq(TagPO::getStatus, Tag.TagStatus.NORMAL.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findLeafTags() {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getImageCount, 0);
        wrapper.eq(TagPO::getStatus, Tag.TagStatus.NORMAL.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findRootTagsByType(Tag.TagType type) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(TagPO::getParentId);
        wrapper.eq(TagPO::getType, type.getValue());
        wrapper.eq(TagPO::getStatus, Tag.TagStatus.NORMAL.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findLeafTagsByType(Tag.TagType type) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getImageCount, 0);
        wrapper.eq(TagPO::getType, type.getValue());
        wrapper.eq(TagPO::getStatus, Tag.TagStatus.NORMAL.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        return tagMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tag> findByName(String name) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getName, name);
        TagPO po = tagMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public Optional<Tag> findByTypeAndName(Tag.TagType type, String name) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getType, type.getValue());
        wrapper.eq(TagPO::getName, name);
        TagPO po = tagMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<Tag> findAll() {
        return tagMapper.selectList(null).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByPage(int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<TagPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        tagMapper.selectPage(pageObj, null);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findByTypePaginated(Tag.TagType type, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<TagPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getType, type.getValue());
        wrapper.orderByAsc(TagPO::getSortOrder);
        tagMapper.selectPage(pageObj, wrapper);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return tagMapper.selectCount(null);
    }

    @Override
    public Map<Tag.TagType, Long> countByType() {
        List<TagPO> all = tagMapper.selectList(null);
        Map<Tag.TagType, Long> result = new HashMap<>();
        for (Tag.TagType type : Tag.TagType.values()) {
            result.put(type, 0L);
        }
        for (TagPO po : all) {
            Tag.TagType type = Tag.TagType.fromValue(po.getType());
            result.merge(type, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public Map<Tag.TagStatus, Long> countByStatus() {
        List<TagPO> all = tagMapper.selectList(null);
        Map<Tag.TagStatus, Long> result = new HashMap<>();
        for (Tag.TagStatus status : Tag.TagStatus.values()) {
            result.put(status, 0L);
        }
        for (TagPO po : all) {
            Tag.TagStatus status = Tag.TagStatus.fromValue(po.getStatus());
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        return tagMapper.deleteById(id) > 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return tagMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getName, name);
        if (excludeId != null) {
            wrapper.ne(TagPO::getTagId, excludeId);
        }
        return tagMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<TagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagPO::getTagCode, code);
        return tagMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Set<com.ruoyi.system.domain.event.TagEvent> getDomainEvents(Long id) {
        return Set.of();
    }

    @Override
    public void clearDomainEvents(Long id) {
    }

    // ========== Assembler Methods ==========

    private Tag toEntity(TagPO po) {
        return Tag.rebuildFromPO(po);
    }

    private TagPO toPO(Tag entity) {
        TagPO po = new TagPO();
        po.setTagId(entity.getTagId());
        po.setTagCode(entity.getTagCode() != null ? entity.getTagCode().getValue() : null);
        po.setName(entity.getName());
        po.setType(entity.getType() != null ? entity.getType().getValue() : null);
        po.setParentId(entity.getParentId());
        po.setDescription(entity.getDescription());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getValue() : null);
        po.setUsageCount(entity.getUsageCount());
        po.setImageCount(entity.getImageCount());
        po.setSortOrder(entity.getSortOrder());
        po.setIcon(entity.getIcon());
        po.setColor(entity.getColor());
        po.setPriority(entity.getPriority());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }
}
