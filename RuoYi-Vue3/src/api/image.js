import request from '@/utils/request'

// 图片管理API

export function createImage(data) {
  return request({
    url: '/system/image/create',
    method: 'post',
    data
  })
}

export function getImage(imageId) {
  return request({
    url: `/system/image/${imageId}`,
    method: 'get'
  })
}

export function listImagesByDataset(datasetId) {
  return request({
    url: `/system/image/dataset/${datasetId}`,
    method: 'get'
  })
}

export function listPendingCleaningImages() {
  return request({
    url: '/system/image/pending-cleaning',
    method: 'get'
  })
}

export function listCleanedImages() {
  return request({
    url: '/system/image/cleaned',
    method: 'get'
  })
}

export function listPendingReviewImages() {
  return request({
    url: '/system/image/pending-review',
    method: 'get'
  })
}

export function listApprovedImages() {
  return request({
    url: '/system/image/approved',
    method: 'get'
  })
}

export function pageImages(page, size) {
  return request({
    url: '/system/image/page',
    method: 'get',
    params: { page, size }
  })
}

export function filterImages(params) {
  return request({
    url: '/system/image/filter',
    method: 'get',
    params
  })
}

export function cleanImage(imageId, data) {
  return request({
    url: `/system/image/${imageId}/clean`,
    method: 'post',
    data
  })
}

export function submitForReview(imageId) {
  return request({
    url: `/system/image/${imageId}/submit-review`,
    method: 'post'
  })
}

export function approveImage(imageId) {
  return request({
    url: `/system/image/${imageId}/approve`,
    method: 'post'
  })
}

export function rejectImage(imageId, data) {
  return request({
    url: `/system/image/${imageId}/reject`,
    method: 'post',
    data
  })
}

export function lockImage(imageId) {
  return request({
    url: `/system/image/${imageId}/lock`,
    method: 'post'
  })
}

export function unlockImage(imageId) {
  return request({
    url: `/system/image/${imageId}/unlock`,
    method: 'post'
  })
}

export function setQualityScore(imageId, qualityScore) {
  return request({
    url: `/system/image/${imageId}/quality-score`,
    method: 'put',
    params: { qualityScore }
  })
}

export function deleteImage(imageId) {
  return request({
    url: `/system/image/${imageId}`,
    method: 'delete'
  })
}
