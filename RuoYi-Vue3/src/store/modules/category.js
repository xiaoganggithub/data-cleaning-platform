import { defineStore } from 'pinia'
import {
  createRootCategory,
  createChildCategory,
  getCategory,
  updateCategory,
  listRootCategories,
  listChildCategories,
  listCategories,
  archiveCategory,
  deleteCategory
} from '@/api/category'

export const useCategoryStore = defineStore('category', {
  state: () => ({
    categories: [],
    rootCategories: [],
    currentCategory: null,
    loading: false
  }),

  actions: {
    // 创建根分类
    async createRootCategory(data) {
      this.loading = true
      try {
        const result = await createRootCategory(data)
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 创建子分类
    async createChildCategory(data) {
      this.loading = true
      try {
        const result = await createChildCategory(data)
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取分类
    async fetchCategory(categoryId) {
      this.loading = true
      try {
        const result = await getCategory(categoryId)
        this.currentCategory = result.data
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取根分类列表
    async fetchRootCategories() {
      this.loading = true
      try {
        const result = await listRootCategories()
        this.rootCategories = result.data || []
        return this.rootCategories
      } finally {
        this.loading = false
      }
    },

    // 获取子分类列表
    async fetchChildCategories(parentId) {
      this.loading = true
      try {
        const result = await listChildCategories(parentId)
        return result.data || []
      } finally {
        this.loading = false
      }
    },

    // 获取所有分类
    async fetchCategories() {
      this.loading = true
      try {
        const result = await listCategories()
        this.categories = result.data || []
        return this.categories
      } finally {
        this.loading = false
      }
    },

    // 更新分类
    async updateCategory(categoryId, data) {
      const result = await updateCategory(categoryId, data)
      return result.data
    },

    // 归档分类
    async archive(categoryId) {
      const result = await archiveCategory(categoryId)
      return result.data
    },

    // 删除分类
    async remove(categoryId) {
      await deleteCategory(categoryId)
    }
  }
})
