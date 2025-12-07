import request from '@/utils/request'

// 查询菜品评价列表
export function listReview(query) {
  return request({
    url: '/ums/review/list',
    method: 'get',
    params: query
  })
}

// 查询菜品评价详细
export function getReview(id) {
  return request({
    url: '/ums/review/' + id,
    method: 'get'
  })
}

// 新增菜品评价
export function addReview(data) {
  return request({
    url: '/ums/review',
    method: 'post',
    data: data
  })
}

// 修改菜品评价
export function updateReview(data) {
  return request({
    url: '/ums/review',
    method: 'put',
    data: data
  })
}

// 删除菜品评价
export function delReview(id) {
  return request({
    url: '/ums/review/' + id,
    method: 'delete'
  })
}
