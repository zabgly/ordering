import request from '@/utils/request'

// 查询菜品列表
export function listProduct(query) {
  return request({
    url: '/cms/product/list',
    method: 'get',
    params: query
  })
}

// 查询菜品详细
export function getProduct(id) {
  return request({
    url: '/cms/product/' + id,
    method: 'get'
  })
}

// 新增菜品
export function addProduct(data) {
  return request({
    url: '/cms/product',
    method: 'post',
    data: data
  })
}

// 修改菜品
export function updateProduct(data) {
  return request({
    url: '/cms/product',
    method: 'put',
    data: data
  })
}

// 删除菜品
export function delProduct(id) {
  return request({
    url: '/cms/product/' + id,
    method: 'delete'
  })
}
