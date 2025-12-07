import request from '@/utils/request'


// 查询首页内容详细
export function getContent() {
  return request({
    url: '/cms/content',
    method: 'get'
  })
}

// 修改首页内容
export function updateContent(data) {
  return request({
    url: '/cms/content',
    method: 'put',
    data: data
  })
}
