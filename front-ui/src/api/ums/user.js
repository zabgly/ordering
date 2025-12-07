import request from '@/utils/request'

// 注册
export function register(data) {
  return request({
    url: '/ums/register',
    method: 'post',
    data: data
  })
}

// 登录
export function login(data) {
  return request({
    url: '/ums/login',
    method: 'post',
    data: data
  })
}

// 登出
export function logout() {
  return request({
    url: '/ums/logout',
    method: 'post'
  })
}

// 登录后查看个人信息
export function getById() {
  return request({
    url: '/ums/getById',
    method: 'get'
  })
}

// 登录后更改个人信息
export function updatePersonalData(data) {
  return request({
    url: '/ums/updatePersonalData',
    method: 'post',
    data: data
  })
}
