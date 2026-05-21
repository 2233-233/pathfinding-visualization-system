import request from '@/utils/request'

// 实验记录管理API
export const experimentApi = {
  // 获取实验记录列表（分页）
  getExperimentList(params) {
    return request({
      url: '/api/experiments',
      method: 'get',
      params
    })
  },

  // 获取实验记录列表（不分页）
  getExperimentListAll(params) {
    return request({
      url: '/api/experiments/all',
      method: 'get',
      params
    })
  },

  // 获取实验记录详情
  getExperimentById(id) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'get'
    })
  },

  // 创建实验记录
  createExperiment(data) {
    return request({
      url: '/api/experiments',
      method: 'post',
      data
    })
  },

  // 更新实验记录
  updateExperiment(id, data) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'put',
      data
    })
  },

  // 删除实验记录
  deleteExperiment(id) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'delete'
    })
  },

  // 获取实验步骤列表
  getExperimentSteps(recordId) {
    return request({
      url: `/api/experiments/${recordId}/steps`,
      method: 'get'
    })
  },

  // 添加实验步骤
  addExperimentStep(recordId, data) {
    return request({
      url: `/api/experiments/${recordId}/steps`,
      method: 'post',
      data
    })
  },

  // 更新实验步骤
  updateExperimentStep(recordId, stepId, data) {
    return request({
      url: `/api/experiments/${recordId}/steps/${stepId}`,
      method: 'put',
      data
    })
  },

  // 获取用户实验统计
  getUserExperimentStats(userId) {
    return request({
      url: `/api/experiments/stats/user/${userId}`,
      method: 'get'
    })
  },

  // 获取算法实验统计
  getAlgorithmExperimentStats(algorithmName) {
    return request({
      url: `/api/experiments/stats/algorithm/${algorithmName}`,
      method: 'get'
    })
  },

  // 兼容接口：保存实验记录
  saveRecord(data) {
    return request({
      url: '/api/experimentRecord/saveRecord',
      method: 'post',
      data
    })
  },

  // 兼容接口：获取用户实验记录列表
  listByUser(userId) {
    return request({
      url: '/api/experimentRecord/listByUser',
      method: 'get',
      params: { userId }
    })
  },

  // 导出实验记录Excel
  exportExperimentRecords(params) {
    return request({
      url: '/api/experiments/export',
      method: 'get',
      params,
      responseType: 'blob' // 重要：指定响应类型为blob
    })
  }
}

// 为了方便使用，也导出单独的函数
export function getExperimentList(params) {
  return experimentApi.getExperimentList(params)
}

export function getExperimentById(id) {
  return experimentApi.getExperimentById(id)
}

export function createExperiment(data) {
  return experimentApi.createExperiment(data)
}

export function updateExperiment(id, data) {
  return experimentApi.updateExperiment(id, data)
}

export function deleteExperiment(id) {
  return experimentApi.deleteExperiment(id)
}

export function getExperimentSteps(recordId) {
  return experimentApi.getExperimentSteps(recordId)
}

// 导出实验记录Excel
export function exportExperimentRecords(params) {
  return experimentApi.exportExperimentRecords(params)
}

export default experimentApi
