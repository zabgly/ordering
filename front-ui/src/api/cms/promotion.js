import request from '@/utils/request'

// 查询促销活动列表
export function listPromotion(query) {
  return request({
    url: '/cms/front/promotion/list',
    method: 'get',
    params: query
  })
}

// 查询促销活动详细
export function getPromotion(id) {
  return request({
    url: '/cms/front/promotion/' + id,
    method: 'get'
  })
}
