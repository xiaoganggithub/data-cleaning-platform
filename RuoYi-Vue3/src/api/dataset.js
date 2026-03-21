import request from '@/utils/request'

// 数据集管理API

export function createDataset(data) {
  return request({
    url: '/system/dataset/create',
    method: 'post',
    data
  })
}

export function getDataset(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}`,
    method: 'get'
  })
}

export function getDatasetByCode(datasetCode) {
  return request({
    url: `/system/dataset/code/${datasetCode}`,
    method: 'get'
  })
}

export function listDatasets(params) {
  return request({
    url: '/system/dataset/list',
    method: 'get',
    params
  })
}

export function listDatasetsByStatus(status) {
  return request({
    url: `/system/dataset/list/${status}`,
    method: 'get'
  })
}

export function pageDatasets(params) {
  return request({
    url: '/system/dataset/page',
    method: 'get',
    params
  })
}

export function countDatasets() {
  return request({
    url: '/system/dataset/count',
    method: 'get'
  })
}

export function statisticsByStatus() {
  return request({
    url: '/system/dataset/statistics/status',
    method: 'get'
  })
}

export function initializeDataset(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}/initialize`,
    method: 'post'
  })
}

export function startCleaning(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}/start-cleaning`,
    method: 'post'
  })
}

export function completeCleaning(datasetId, cleanedImageCount) {
  return request({
    url: `/system/dataset/${datasetId}/complete-cleaning`,
    method: 'post',
    data: { cleanedImageCount }
  })
}

export function approveDataset(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}/approve`,
    method: 'post'
  })
}

export function publishDataset(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}/publish`,
    method: 'post'
  })
}

export function archiveDataset(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}/archive`,
    method: 'post'
  })
}

export function deleteDataset(datasetId) {
  return request({
    url: `/system/dataset/${datasetId}`,
    method: 'delete'
  })
}
