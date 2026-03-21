import { defineStore } from 'pinia'
import {
  createRootTag,
  createChildTag,
  getTag,
  updateTag,
  listRootTags,
  listChildTags,
  listTagsByType,
  listTags,
  archiveTag,
  deleteTag,
  updateSortOrder,
  updatePriority,
  countTags,
  statisticsByType
} from '@/api/tag'

export const useTagStore = defineStore('tag', {
  state: () => ({
    tags: [],
    rootTags: [],
    currentTag: null,
    loading: false
  }),

  actions: {
    // 获取所有标签
    async fetchAll() {
      this.loading = true
      try {
        const result = await listTags()
        this.tags = result.data || []
        return this.tags
      } finally {
        this.loading = false
      }
    },

    // 创建根标签
    async createRoot(data) {
      this.loading = true
      try {
        const result = await createRootTag(data)
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 创建子标签
    async createChild(data) {
      this.loading = true
      try {
        const result = await createChildTag(data)
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取标签
    async fetchTag(tagId) {
      this.loading = true
      try {
        const result = await getTag(tagId)
        this.currentTag = result.data
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取根标签列表
    async fetchRootTags() {
      this.loading = true
      try {
        const result = await listRootTags()
        this.rootTags = result.data || []
        return this.rootTags
      } finally {
        this.loading = false
      }
    },

    // 获取子标签列表
    async fetchChildTags(parentId) {
      this.loading = true
      try {
        const result = await listChildTags(parentId)
        return result.data || []
      } finally {
        this.loading = false
      }
    },

    // 按类型获取标签
    async fetchTagsByType(type) {
      this.loading = true
      try {
        const result = await listTagsByType(type)
        return result.data || []
      } finally {
        this.loading = false
      }
    },

    // 更新标签
    async update(tagId, data) {
      const result = await updateTag(tagId, data)
      return result.data
    },

    // 归档标签
    async archive(tagId) {
      const result = await archiveTag(tagId)
      return result.data
    },

    // 删除标签
    async remove(tagId) {
      await deleteTag(tagId)
    },

    // 更新排序
    async updateSort(tagId, sortOrder) {
      await updateSortOrder(tagId, sortOrder)
    },

    // 更新优先级
    async updateTagPriority(tagId, priority) {
      await updatePriority(tagId, priority)
    },

    // 统计标签数量
    async fetchCount() {
      const result = await countTags()
      return result.data
    },

    // 按类型统计
    async fetchStatisticsByType() {
      const result = await statisticsByType()
      return result.data
    }
  }
})
