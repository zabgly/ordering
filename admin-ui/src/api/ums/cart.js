import request from '@/utils/request'

// 查询我的菜单列表
export function listCart(query) {
  return request({
    url: '/ums/cart/list',
    method: 'get',
    params: query
  })
}

// 查询我的菜单详细
export function getCart(id) {
  return request({
    url: '/ums/cart/' + id,
    method: 'get'
  })
}

// 新增我的菜单
export function addCart(data) {
  return request({
    url: '/ums/cart',
    method: 'post',
    data: data
  })
}

// 修改我的菜单
export function updateCart(data) {
  return request({
    url: '/ums/cart',
    method: 'put',
    data: data
  })
}

// 删除我的菜单
export function delCart(id) {
  return request({
    url: '/ums/cart/' + id,
    method: 'delete'
  })
}
