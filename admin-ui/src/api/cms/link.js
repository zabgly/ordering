import request from '@/utils/request'

// 查询菜品促销关联列表
export function listLink(query) {
  return request({
    url: '/cms/link/list',
    method: 'get',
    params: query
  })
}

// 查询菜品促销关联详细
export function getLink(id) {
  return request({
    url: '/cms/link/' + id,
    method: 'get'
  })
}

// 新增菜品促销关联
export function addLink(data) {
  return request({
    url: '/cms/link',
    method: 'post',
    data: data
  })
}

// 修改菜品促销关联
export function updateLink(data) {
  return request({
    url: '/cms/link',
    method: 'put',
    data: data
  })
}

// 删除菜品促销关联
export function delLink(id) {
  return request({
    url: '/cms/link/' + id,
    method: 'delete'
  })
}
