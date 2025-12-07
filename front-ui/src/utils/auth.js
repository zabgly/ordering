// import Cookies from 'js-cookie'

const TokenKey = 'Front-Token'

const UserKey = 'Front-User'

const ExpiresInKey = 'Front-Expires-In'

export function getToken() {
  return localStorage.getItem(TokenKey)
}

export function setToken(token) {
  return localStorage.setItem(TokenKey, token)
}

export function getUser() {
  return JSON.parse(localStorage.getItem(UserKey))
}

export function setUser(user) {
  return localStorage.setItem(UserKey, JSON.stringify(user))
}

export function removeToken() {
  return localStorage.removeItem(TokenKey)
}

export function getExpiresIn() {
  return localStorage.getItem(ExpiresInKey) || -1
}

export function setExpiresIn(time) {
  return localStorage.setItem(ExpiresInKey, time)
}

export function removeExpiresIn() {
  return localStorage.removeItem(ExpiresInKey)
}
