import request from '@/utils/request'

// 查询浏览记录列表
export function listHistory(query) {
  return request({
    url: '/ums/front/history/list',
    method: 'get',
    params: query
  })
}

// 删除浏览记录
export function delHistory(id) {
  return request({
    url: '/ums/front/history/' + id,
    method: 'delete'
  })
}

