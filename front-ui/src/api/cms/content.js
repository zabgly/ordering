import request from '@/utils/request'


// 查询首页内容详细
export function getContent() {
  return request({
    url: '/cms/front/content',
    method: 'get'
  })
}
