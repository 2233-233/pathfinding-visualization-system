/**
 * 题目管理API
 * 连接真实后端接口，替换mock数据
 */

import request from '@/utils/request'

// 根据算法ID获取问题列表
export function listProblemsByAlgorithmId(algorithmId) {
  return request({
    url: '/api/problem/listByAlgorithm',
    method: 'get',
    params: { algorithmId }
  })
}

// 获取问题列表（分页+搜索）
export function getProblemList(params) {
  return request({
    url: '/api/problem/problems',
    method: 'get',
    params: params // { page, size, title, description, difficulty, algorithmId }
  })
}

// 获取问题详情
export function getProblemById(problemId) {
  return request({
    url: `/api/problem/problems/${problemId}`,
    method: 'get'
  })
}

// 创建问题
export function createProblem(data) {
  return request({
    url: '/api/problem/problems',
    method: 'post',
    data: data
  })
}

// 更新问题
export function updateProblem(problemId, data) {
  return request({
    url: `/api/problem/problems/${problemId}`,
    method: 'put',
    data: data
  })
}

// 删除单个问题
export function deleteProblem(problemId) {
  return request({
    url: `/api/problem/problems/${problemId}`,
    method: 'delete'
  })
}

// 批量删除问题
export function batchDeleteProblems(problemIds) {
  return request({
    url: '/api/problem/problems/batch',
    method: 'delete',
    data: problemIds
  })
}

// 搜索问题
export function searchProblems(keyword) {
  return request({
    url: '/api/problem/problems/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取所有问题（不分页）
export function getAllProblems() {
  return request({
    url: '/api/problem/problems/all',
    method: 'get'
  })
}

// 获取题目统计信息
export function getProblemStats() {
  return request({
    url: '/api/problem/problems/stats',
    method: 'get'
  })
}

// 导出题目Excel
export function exportProblems(params) {
  return request({
    url: '/api/problem/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
