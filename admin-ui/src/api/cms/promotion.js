import request from '@/utils/request'

// 查询促销活动列表
export function listPromotion(query) {
  return request({
    url: '/cms/promotion/list',
    method: 'get',
    params: query
  })
}

// 促销活动分配菜品
export function distributeProductByPromotionId(data) {
  return request({
    url: '/cms/promotion/distributeProductByPromotionId',
    method: 'post',
    data: data
  })
}

// 查询促销活动详细
export function getPromotion(id) {
  return request({
    url: '/cms/promotion/' + id,
    method: 'get'
  })
}

// 新增促销活动
export function addPromotion(data) {
  return request({
    url: '/cms/promotion',
    method: 'post',
    data: data
  })
}

// 修改促销活动
export function updatePromotion(data) {
  return request({
    url: '/cms/promotion',
    method: 'put',
    data: data
  })
}

// 删除促销活动
export function delPromotion(id) {
  return request({
    url: '/cms/promotion/' + id,
    method: 'delete'
  })
}
