import request from '@/utils/request'

// 查询浏览记录列表
export function listHistory(query) {
  return request({
    url: '/ums/history/list',
    method: 'get',
    params: query
  })
}

// 查询浏览记录详细
export function getHistory(id) {
  return request({
    url: '/ums/history/' + id,
    method: 'get'
  })
}

// 新增浏览记录
export function addHistory(data) {
  return request({
    url: '/ums/history',
    method: 'post',
    data: data
  })
}

// 修改浏览记录
export function updateHistory(data) {
  return request({
    url: '/ums/history',
    method: 'put',
    data: data
  })
}

// 删除浏览记录
export function delHistory(id) {
  return request({
    url: '/ums/history/' + id,
    method: 'delete'
  })
}
