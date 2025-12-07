import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/oms/front/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(id) {
  return request({
    url: '/oms/front/order/' + id,
    method: 'get'
  })
}

// 新增订单
export function placeOrder(data) {
  return request({
    url: '/oms/front/order/placeOrder',
    method: 'post',
    data: data
  })
}
