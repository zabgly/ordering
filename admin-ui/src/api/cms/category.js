import request from '@/utils/request'

// 查询菜品分类列表
export function listCategory(query) {
  return request({
    url: '/cms/category/list',
    method: 'get',
    params: query
  })
}

// 获取叶子节点
export function listLeafNodes(query) {
  return request({
    url: '/cms/category/listLeafNodes',
    method: 'get',
    params: query
  })
}

// 查询菜品分类详细
export function getCategory(id) {
  return request({
    url: '/cms/category/' + id,
    method: 'get'
  })
}

// 新增菜品分类
export function addCategory(data) {
  return request({
    url: '/cms/category',
    method: 'post',
    data: data
  })
}

// 修改菜品分类
export function updateCategory(data) {
  return request({
    url: '/cms/category',
    method: 'put',
    data: data
  })
}

// 删除菜品分类
export function delCategory(id) {
  return request({
    url: '/cms/category/' + id,
    method: 'delete'
  })
}
