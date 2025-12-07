import request from '@/utils/request'

// 查询菜品分类列表
export function listCategory(query) {
  return request({
    url: '/cms/front/category/list',
    method: 'get',
    params: query
  })
}

// 查询菜品分类详细
export function getCategory(id) {
  return request({
    url: '/cms/front/category/' + id,
    method: 'get'
  })
}
