import { defineStore } from 'pinia'
import {
  createImage,
  getImage,
  listImagesByDataset,
  listPendingCleaningImages,
  listCleanedImages,
  listPendingReviewImages,
  listApprovedImages,
  pageImages,
  filterImages,
  cleanImage,
  submitForReview,
  approveImage,
  rejectImage,
  lockImage,
  unlockImage,
  setQualityScore,
  deleteImage
} from '@/api/image'

export const useImageStore = defineStore('image', {
  state: () => ({
    images: [],
    currentImage: null,
    loading: false,
    statistics: {}
  }),

  actions: {
    // 创建图片记录
    async createImage(data) {
      this.loading = true
      try {
        const result = await createImage(data)
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 获取图片
    async fetchImage(imageId) {
      this.loading = true
      try {
        const result = await getImage(imageId)
        this.currentImage = result.data
        return result.data
      } finally {
        this.loading = false
      }
    },

    // 根据数据集获取图片
    async fetchImagesByDataset(datasetId) {
      this.loading = true
      try {
        const result = await listImagesByDataset(datasetId)
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 获取待清洗图片
    async fetchPendingCleaningImages() {
      this.loading = true
      try {
        const result = await listPendingCleaningImages()
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 获取已清洗图片
    async fetchCleanedImages() {
      this.loading = true
      try {
        const result = await listCleanedImages()
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 获取待审核图片
    async fetchPendingReviewImages() {
      this.loading = true
      try {
        const result = await listPendingReviewImages()
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 获取已通过图片
    async fetchApprovedImages() {
      this.loading = true
      try {
        const result = await listApprovedImages()
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 分页获取图片
    async fetchPageImages(page, size) {
      this.loading = true
      try {
        const result = await pageImages(page, size)
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 带过滤条件获取图片
    async fetchFilteredImages(params) {
      this.loading = true
      try {
        const result = await filterImages(params)
        this.images = result.data || []
        return this.images
      } finally {
        this.loading = false
      }
    },

    // 清洗图片
    async clean(imageId, data) {
      const result = await cleanImage(imageId, data)
      return result.data
    },

    // 提交审核
    async submitReview(imageId) {
      const result = await submitForReview(imageId)
      return result.data
    },

    // 审核通过
    async approve(imageId) {
      const result = await approveImage(imageId)
      return result.data
    },

    // 审核拒绝
    async reject(imageId, data) {
      const result = await rejectImage(imageId, data)
      return result.data
    },

    // 锁定图片
    async lock(imageId) {
      await lockImage(imageId)
    },

    // 解锁图片
    async unlock(imageId) {
      await unlockImage(imageId)
    },

    // 设置质量评分
    async updateQualityScore(imageId, qualityScore) {
      await setQualityScore(imageId, qualityScore)
    },

    // 删除图片
    async remove(imageId) {
      await deleteImage(imageId)
    }
  }
})
