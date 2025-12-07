import request from '@/utils/request'

// 菜品列表
export function listProduct() {
  return request({
    url: '/cms/front/product/list',
    method: 'get'
  })
}

// 搜索
export function searchProduct(data) {
  return request({
    url: '/cms/front/product/searchProduct',
    method: 'get',
    params: data
  })
}

// 查询菜品详细
export function getProduct(id) {
  return request({
    url: '/cms/front/product/getById/' + id,
    method: 'get'
  })
}
