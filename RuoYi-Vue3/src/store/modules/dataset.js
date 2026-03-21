import { defineStore } from 'pinia'
import {
  createDataset,
  getDataset,
  getDatasetByCode,
  listDatasets,
  listDatasetsByStatus,
  pageDatasets,
  countDatasets,
  statisticsByStatus,
  initializeDataset,
  startCleaning,
  completeCleaning,
  approveDataset,
  publishDataset,
  archiveDataset,
  deleteDataset
} from '@/api/dataset'

export const useDatasetStore = defineStore('dataset', {
  state: () => ({
    datasets: [],
    currentDataset: null,
    statistics: {},
    totalCount: 0,
    loading: false
  }),

  actions: {
    // 创建数据集
    async createDataset(data) {
      this.loading = true
      try {
        const result = await createDataset(data)
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取数据集
    async fetchDataset(datasetId) {
      this.loading = true
      try {
        const result = await getDataset(datasetId)
        this.currentDataset = result.data
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取数据集列表（支持分页和搜索）
    async fetchDatasets(params = {}) {
      this.loading = true
      try {
        // 使用分页接口，支持搜索参数
        const result = await pageDatasets(params)
        // 支持两种返回格式：直接数组或包含data/total的对象
        if (Array.isArray(result.data)) {
          this.datasets = result.data
          this.totalCount = result.total || result.data.length
        } else if (result.rows && Array.isArray(result.rows)) {
          this.datasets = result.rows
          this.totalCount = result.total || result.rows.length
        } else if (Array.isArray(result)) {
          this.datasets = result
          this.totalCount = result.length
        } else {
          this.datasets = []
          this.totalCount = 0
        }
        return this.datasets
      } finally {
        this.loading = false
      }
    },

    // 分页获取数据集
    async fetchPageDatasets(page, size) {
      this.loading = true
      try {
        const result = await pageDatasets(page, size)
        this.datasets = result.data || []
        return this.datasets
      } finally {
        this.loading = false
      }
    },

    // 获取统计数据
    async fetchStatistics() {
      try {
        const result = await statisticsByStatus()
        this.statistics = result.data || {}
        return this.statistics
      } catch (e) {
        console.error('Failed to fetch statistics:', e)
      }
    },

    // 初始化数据集
    async initialize(datasetId) {
      const result = await initializeDataset(datasetId)
      return result.data
    },

    // 开始清洗
    async startCleaning(datasetId) {
      const result = await startCleaning(datasetId)
      return result.data
    },

    // 完成清洗
    async completeCleaning(datasetId, cleanedImageCount) {
      const result = await completeCleaning(datasetId, cleanedImageCount)
      return result.data
    },

    // 审核通过
    async approve(datasetId) {
      const result = await approveDataset(datasetId)
      return result.data
    },

    // 发布数据集
    async publish(datasetId) {
      const result = await publishDataset(datasetId)
      return result.data
    },

    // 归档数据集
    async archive(datasetId) {
      const result = await archiveDataset(datasetId)
      return result.data
    },

    // 删除数据集
    async remove(datasetId) {
      await deleteDataset(datasetId)
    }
  }
})
