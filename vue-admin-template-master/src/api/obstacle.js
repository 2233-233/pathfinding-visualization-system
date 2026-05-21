import request from '@/utils/request'

// 障碍物管理API
export const obstacleApi = {
  // 获取实验记录的障碍物列表
  getObstaclesByRecordId(recordId) {
    return request({
      url: `/api/experiments/${recordId}/obstacles`,
      method: 'get'
    })
  },

  // 批量添加障碍物
  addObstaclesBatch(recordId, obstacles) {
    return request({
      url: `/api/experiments/${recordId}/obstacles/batch`,
      method: 'post',
      data: obstacles
    })
  },

  // 删除实验记录的所有障碍物
  deleteObstaclesByRecordId(recordId) {
    return request({
      url: `/api/experiments/${recordId}/obstacles`,
      method: 'delete'
    })
  },

  // 删除单个障碍物
  deleteObstacle(recordId, obstacleId) {
    return request({
      url: `/api/experiments/${recordId}/obstacles/${obstacleId}`,
      method: 'delete'
    })
  }
}

// 为了方便使用，也导出单独的函数
export function getObstaclesByRecordId(recordId) {
  return obstacleApi.getObstaclesByRecordId(recordId)
}

export function addObstaclesBatch(recordId, obstacles) {
  return obstacleApi.addObstaclesBatch(recordId, obstacles)
}

export function deleteObstaclesByRecordId(recordId) {
  return obstacleApi.deleteObstaclesByRecordId(recordId)
}

export function deleteObstacle(recordId, obstacleId) {
  return obstacleApi.deleteObstacle(recordId, obstacleId)
}

export default obstacleApi
