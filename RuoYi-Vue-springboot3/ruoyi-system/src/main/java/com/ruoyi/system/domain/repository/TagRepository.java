package com.ruoyi.system.domain.repository;

import com.ruoyi.system.domain.entity.Tag;
import com.ruoyi.system.domain.entity.TagType;
import com.ruoyi.system.domain.entity.TagStatus;
import java.util.Optional;
import java.util.List;
import java.util.Set;

/**
 * 标签领域仓储接口
 * 定义标签相关的数据访问操作
 */
public interface TagRepository {

    /**
     * 保存标签
     * @param tag 标签实体
     * @return 保存后的标签
     */
    Tag save(Tag tag);

    /**
     * 根据ID查询标签
     * @param id 标签ID
     * @return 标签实体
     */
    Optional<Tag> findById(Long id);

    /**
     * 根据父标签ID查询子标签
     * @param parentId 父标签ID
     * @return 标签列表
     */
    List<Tag> findByParentId(Long parentId);

    /**
     * 根据状态查询标签
     * @param status 状态
     * @return 标签列表
     */
    List<Tag> findByStatus(TagStatus status);

    /**
     * 根据类型查询标签
     * @param type 类型
     * @return 标签列表
     */
    List<Tag> findByType(TagType type);

    /**
     * 根据父标签ID和类型查询标签
     * @param parentId 父标签ID
     * @param type 类型
     * @return 标签列表
     */
    List<Tag> findByParentIdAndType(Long parentId, TagType type);

    /**
     * 查询所有根标签
     * @return 根标签列表
     */
    List<Tag> findRootTags();

    /**
     * 查询所有叶子标签
     * @return 叶子标签列表
     */
    List<Tag> findLeafTags();

    /**
     * 根据类型查询根标签
     * @param type 类型
     * @return 标签列表
     */
    List<Tag> findRootTagsByType(TagType type);

    /**
     * 根据类型查询叶子标签
     * @param type 类型
     * @return 标签列表
     */
    List<Tag> findLeafTagsByType(TagType type);

    /**
     * 根据名称查询标签
     * @param name 标签名称
     * @return 标签实体
     */
    Optional<Tag> findByName(String name);

    /**
     * 根据类型和名称查询标签
     * @param type 类型
     * @param name 标签名称
     * @return 标签实体
     */
    Optional<Tag> findByTypeAndName(TagType type, String name);

    /**
     * 查询所有标签
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 分页查询标签
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 标签分页结果
     */
    List<Tag> findByPage(int page, int size);

    /**
     * 根据类型分页查询标签
     * @param type 类型
     * @param page 页码
     * @param size 每页大小
     * @return 标签列表
     */
    List<Tag> findByTypePaginated(TagType type, int page, int size);

    /**
     * 统计标签数量
     * @return 标签总数
     */
    long count();

    /**
     * 统计各类型标签数量
     * @return 类型统计
     */
    java.util.Map<TagType, Long> countByType();

    /**
     * 统计各状态标签数量
     * @return 状态统计
     */
    java.util.Map<TagStatus, Long> countByStatus();

    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除标签
     * @param ids 标签ID列表
     * @return 删除数量
     */
    int deleteByIds(List<Long> ids);

    /**
     * 检查名称是否存在
     * @param name 标签名称
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 检查编码是否存在
     * @param code 标签编码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 获取领域事件
     * @param id 标签ID
     * @return 领域事件集合
     */
    Set<com.ruoyi.system.domain.event.TagEvent> getDomainEvents(Long id);

    /**
     * 清除领域事件
     * @param id 标签ID
     */
    void clearDomainEvents(Long id);
}
