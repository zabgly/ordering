import request from '@/utils/request'

// 查询我的菜单列表
export function listCart(query) {
  return request({
    url: '/ums/front/cart/list',
    method: 'get',
    params: query
  })
}

// 新增我的菜单
export function addProductInCart(data) {
  return request({
    url: '/ums/front/cart/addProductInCart',
    method: 'post',
    data: data
  })
}

// 修改我的菜单
export function removeProductInCart(data) {
  return request({
    url: '/ums/front/cart/removeProductInCart',
    method: 'post',
    data: data
  })
}
