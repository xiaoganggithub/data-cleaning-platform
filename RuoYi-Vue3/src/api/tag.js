import request from '@/utils/request'

// 标签管理API

export function createRootTag(data) {
  return request({
    url: '/system/tag/root',
    method: 'post',
    data
  })
}

export function createChildTag(data) {
  return request({
    url: '/system/tag/child',
    method: 'post',
    data
  })
}

export function getTag(tagId) {
  return request({
    url: `/system/tag/${tagId}`,
    method: 'get'
  })
}

export function updateTag(tagId, data) {
  return request({
    url: `/system/tag/${tagId}`,
    method: 'put',
    data
  })
}

export function listRootTags() {
  return request({
    url: '/system/tag/root/list',
    method: 'get'
  })
}

export function listChildTags(parentId) {
  return request({
    url: `/system/tag/children/${parentId}`,
    method: 'get'
  })
}

export function listTagsByType(type) {
  return request({
    url: `/system/tag/type/${type}`,
    method: 'get'
  })
}

export function listTags() {
  return request({
    url: '/system/tag/list',
    method: 'get'
  })
}

export function archiveTag(tagId) {
  return request({
    url: `/system/tag/${tagId}/archive`,
    method: 'post'
  })
}

export function deleteTag(tagId) {
  return request({
    url: `/system/tag/${tagId}`,
    method: 'delete'
  })
}

export function updateSortOrder(tagId, sortOrder) {
  return request({
    url: `/system/tag/${tagId}/sort-order`,
    method: 'put',
    params: { sortOrder }
  })
}

export function updatePriority(tagId, priority) {
  return request({
    url: `/system/tag/${tagId}/priority`,
    method: 'put',
    params: { priority }
  })
}

export function countTags() {
  return request({
    url: '/system/tag/count',
    method: 'get'
  })
}

export function statisticsByType() {
  return request({
    url: '/system/tag/statistics/type',
    method: 'get'
  })
}
