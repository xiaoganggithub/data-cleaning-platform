package com.ruoyi.system.domain.repository;

import com.ruoyi.system.domain.entity.Dataset;
import com.ruoyi.system.domain.entity.DatasetStatus;
import java.util.Optional;
import java.util.List;
import java.util.Set;

/**
 * 数据集领域仓储接口
 * 定义数据集相关的数据访问操作
 */
public interface DatasetRepository {

    /**
     * 保存数据集
     * @param dataset 数据集实体
     * @return 保存后的数据集
     */
    Dataset save(Dataset dataset);

    /**
     * 根据ID查询数据集
     * @param id 数据集ID
     * @return 数据集实体
     */
    Optional<Dataset> findById(Long id);

    /**
     * 根据编码查询数据集
     * @param code 数据集编码
     * @return 数据集实体
     */
    Optional<Dataset> findByCode(String code);

    /**
     * 根据状态查询数据集
     * @param status 状态
     * @return 数据集列表
     */
    List<Dataset> findByStatus(DatasetStatus status);

    /**
     * 根据状态列表查询数据集
     * @param statuses 状态列表
     * @return 数据集列表
     */
    List<Dataset> findByStatuses(List<DatasetStatus> statuses);

    /**
     * 查询所有数据集
     * @return 数据集列表
     */
    List<Dataset> findAll();

    /**
     * 分页查询数据集
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 数据集分页结果
     */
    List<Dataset> findByPage(int page, int size);

    /**
     * 搜索并分页查询数据集
     * @param datasetCode 数据集编码（模糊查询）
     * @param name 数据集名称（模糊查询）
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 数据集分页结果
     */
    List<Dataset> searchByPage(String datasetCode, String name, String beginTime, String endTime, int page, int size);

    /**
     * 统计搜索结果数量
     * @param datasetCode 数据集编码（模糊查询）
     * @param name 数据集名称（模糊查询）
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 符合条件的记录数
     */
    long countSearch(String datasetCode, String name, String beginTime, String endTime);

    /**
     * 统计数据集数量
     * @return 数据集总数
     */
    long count();

    /**
     * 统计各状态数据集数量
     * @return 状态统计
     */
    java.util.Map<DatasetStatus, Long> countByStatus();

    /**
     * 删除数据集
     * @param id 数据集ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 检查编码是否存在
     * @param code 数据集编码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 获取领域事件
     * @param id 数据集ID
     * @return 领域事件集合
     */
    Set<com.ruoyi.system.domain.event.DatasetEvent> getDomainEvents(Long id);

    /**
     * 清除领域事件
     * @param id 数据集ID
     */
    void clearDomainEvents(Long id);
}
