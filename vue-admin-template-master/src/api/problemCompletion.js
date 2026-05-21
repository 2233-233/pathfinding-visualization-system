/**
 * 题目学习记录API
 * 用于管理学生在力扣题目的学习进度记录
 * 对应数据库表：ProblemCompletion
 */

import request from '@/utils/request'

/**
 * 获取学习记录列表
 * 后端接口：GET /api/problem-completions?page=1&size=10&userId=用户ID&problemId=问题ID&completionStatus=状态
 */
export function getProblemCompletionList(params) {
  return request({
    url: '/api/problem-completions',
    method: 'get',
    params
  })
}

/**
 * 获取特定题目的学习记录
 * 后端接口：GET /api/problem-completions/{userId}/{problemId}
 */
export function getProblemCompletionByUserAndProblem(userId, problemId) {
  return request({
    url: `/api/problem-completions/${userId}/${problemId}`,
    method: 'get'
  })
}

/**
 * 创建学习记录
 * 后端接口：POST /api/problem-completions
 */
export function createProblemCompletion(data) {
  return request({
    url: '/api/problem-completions',
    method: 'post',
    data
  })
}

/**
 * 更新学习记录
 * 后端接口：PUT /api/problem-completions/{completionId}
 */
export function updateProblemCompletion(completionId, data) {
  return request({
    url: `/api/problem-completions/${completionId}`,
    method: 'put',
    data
  })
}

/**
 * 删除学习记录
 * 后端接口：DELETE /api/problem-completions/{completionId}
 */
export function deleteProblemCompletion(completionId) {
  return request({
    url: `/api/problem-completions/${completionId}`,
    method: 'delete'
  })
}

/**
 * 批量删除学习记录
 * 后端接口：DELETE /api/problem-completions/batch
 */
export function batchDeleteProblemCompletions(completionIds) {
  return request({
    url: '/api/problem-completions/batch',
    method: 'delete',
    data: completionIds
  })
}

/**
 * 批量更新学习记录
 * 后端接口：PUT /api/problem-completions/batch
 */
export function batchUpdateProblemCompletions(data) {
  return request({
    url: '/api/problem-completions/batch',
    method: 'put',
    data
  })
}

/**
 * 获取用户学习统计
 * 后端接口：GET /api/problem-completions/stats/{userId}
 */
export function getUserLearningStats(userId) {
  return request({
    url: `/api/problem-completions/stats/${userId}`,
    method: 'get'
  })
}

/**
 * 获取用户学习进度
 * 后端接口：GET /api/problem-completions/progress/{userId}
 */
export function getUserLearningProgress(userId) {
  return request({
    url: `/api/problem-completions/progress/${userId}`,
    method: 'get'
  })
}

/**
 * 按照问题ID查找学习记录
 * 后端接口：GET /api/problem-completions/problem/{problemId}
 */
export function getProblemCompletionsByProblemId(problemId) {
  return request({
    url: `/api/problem-completions/problem/${problemId}`,
    method: 'get'
  })
}

/**
 * 按照用户ID查找学习记录
 * 后端接口：GET /api/problem-completions/user/{userId}
 */
export function getProblemCompletionsByUserId(userId) {
  return request({
    url: `/api/problem-completions/user/${userId}`,
    method: 'get'
  })
}

/**
 * 保存或更新学习记录（兼容接口）
 * 后端接口：POST /api/problem-completions/saveOrUpdate
 */
export function saveOrUpdateProblemCompletion(data) {
  return request({
    url: '/api/problem-completions/saveOrUpdate',
    method: 'post',
    data
  })
}

/**
 * 获取单个题目的学习记录（兼容旧接口）
 * 注意：这个函数是为了兼容现有的调用，实际应该使用 getProblemCompletionByUserAndProblem
 */
export function getProblemCompletion(problemId, userId = 1) {
  return getProblemCompletionByUserAndProblem(userId, problemId)
}

/**
 * 保存题目学习记录（兼容旧接口）
 * 注意：这个函数是为了兼容现有的调用，实际应该使用 saveOrUpdateProblemCompletion
 */
export function saveProblemCompletion(data) {
  return saveOrUpdateProblemCompletion(data)
}

/**
 * 获取用户的所有题目学习记录（兼容旧接口）
 */
export function getProblemCompletions(userId) {
  return getProblemCompletionsByUserId(userId)
}

/**
 * 获取学习统计（兼容旧接口）
 */
export function getLearningStats(userId) {
  return getUserLearningStats(userId)
}

/**
 * 搜索用户的学习记录（分页+筛选）
 * 后端接口：GET /api/problem-completions/user/{userId}/search
 * 支持按 algorithmId, difficulty, title, description 筛选
 */
export function searchUserProblemCompletions(userId, params) {
  return request({
    url: `/api/problem-completions/user/${userId}/search`,
    method: 'get',
    params
  })
}

/**
 * 导出题目学习记录Excel
 * 后端接口：GET /api/problem-completions/export
 * 导出当前用户的题目学习记录，支持筛选条件
 */
export function exportProblemCompletions(params) {
  return request({
    url: '/api/problem-completions/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
