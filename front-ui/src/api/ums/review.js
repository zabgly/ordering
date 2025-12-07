import request from '@/utils/request'

// 查询菜品评价列表
export function listNotReview() {
  return request({
    url: '/ums/front/review/notReviewList',
    method: 'get'
  })
}

// 查询菜品评价列表
export function listReview() {
  return request({
    url: '/ums/front/review/list',
    method: 'get'
  })
}

// 新增菜品评价
export function addReview(data) {
  return request({
    url: '/ums/front/review/addReview',
    method: 'post',
    data: data
  })
}

// 删除菜品评价
export function delReview(id) {
  return request({
    url: '/ums/front/review/' + id,
    method: 'delete'
  })
}
