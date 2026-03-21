import request from '@/utils/request'

// 分类管理API

export function createRootCategory(data) {
  return request({
    url: '/system/category/root',
    method: 'post',
    data
  })
}

export function createChildCategory(data) {
  return request({
    url: '/system/category/child',
    method: 'post',
    data
  })
}

export function getCategory(categoryId) {
  return request({
    url: `/system/category/${categoryId}`,
    method: 'get'
  })
}

export function updateCategory(categoryId, data) {
  return request({
    url: `/system/category/${categoryId}`,
    method: 'put',
    data
  })
}

export function listRootCategories() {
  return request({
    url: '/system/category/root/list',
    method: 'get'
  })
}

export function listChildCategories(parentId) {
  return request({
    url: `/system/category/children/${parentId}`,
    method: 'get'
  })
}

export function listCategories() {
  return request({
    url: '/system/category/list',
    method: 'get'
  })
}

export function archiveCategory(categoryId) {
  return request({
    url: `/system/category/${categoryId}/archive`,
    method: 'post'
  })
}

export function deleteCategory(categoryId) {
  return request({
    url: `/system/category/${categoryId}`,
    method: 'delete'
  })
}

export function updateSortOrder(categoryId, sortOrder) {
  return request({
    url: `/system/category/${categoryId}/sort-order`,
    method: 'put',
    params: { sortOrder }
  })
}
