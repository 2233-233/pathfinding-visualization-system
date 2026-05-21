import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/api/user/logout',
    method: 'post'
  })
}

// 用户管理API
export function getUserList(params) {
  return request({
    url: '/api/user/users',
    method: 'get',
    params
  })
}

export function getUserById(userId) {
  return request({
    url: `/api/user/users/${userId}`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/api/user/users',
    method: 'post',
    data
  })
}

export function updateUser(userId, data) {
  return request({
    url: `/api/user/users/${userId}`,
    method: 'put',
    data
  })
}

export function deleteUser(userId) {
  return request({
    url: `/api/user/users/${userId}`,
    method: 'delete'
  })
}

// 通过学号获取用户详情（需要管理员权限）
export function getUserByStudentId(studentId) {
  return request({
    url: `/api/user/users/student/${studentId}`,
    method: 'get'
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

// 修改密码（使用通用更新用户接口）
export function updatePassword(userId, data) {
  return request({
    url: `/api/user/users/${userId}`,
    method: 'put',
    data
  })
}

// 修改邮箱（使用通用更新用户接口）
export function updateEmail(userId, data) {
  return request({
    url: `/api/user/users/${userId}`,
    method: 'put',
    data
  })
}
