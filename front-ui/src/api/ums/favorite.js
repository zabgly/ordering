import request from '@/utils/request'

// 查询收藏列表
export function listFavorite(query) {
  return request({
    url: '/ums/front/favorite/list',
    method: 'get',
    params: query
  })
}

// 新增收藏
export function addCollect(data) {
  return request({
    url: '/ums/front/favorite/addCollect',
    method: 'post',
    data: data
  })
}

// 删除收藏
export function delFavorite(id) {
  return request({
    url: '/ums/front/favorite/' + id,
    method: 'delete'
  })
}
