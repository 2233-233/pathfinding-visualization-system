import request from '@/utils/request'

/**
 * 获取管理员仪表板数据
 * 后端接口：GET /api/admin/dashboard
 * 需要管理员权限
 */
export function getDashboardData() {
  return request({
    url: '/api/admin/dashboard',
    method: 'get'
  })
}
