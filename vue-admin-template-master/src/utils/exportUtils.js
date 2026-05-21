import { Message } from 'element-ui'

/**
 * 通用导出函数
 * @param {string} url - 导出API地址
 * @param {object} params - 查询参数
 * @param {string} filename - 默认文件名
 * @returns {Promise<{success: boolean, error?: string}>}
 */
export const exportToExcel = async(url, params = {}, filename = 'export.xlsx') => {
  try {
    // 构建查询字符串，过滤空值
    const queryParams = {}
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
        queryParams[key] = params[key]
      }
    })

    // 使用axios下载文件
    const response = await fetch(url + '?' + new URLSearchParams(queryParams).toString(), {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer ' + (localStorage.getItem('token') || sessionStorage.getItem('token') || ''),
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    // 获取blob数据
    const blob = await response.blob()

    // 创建下载链接
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)

    return { success: true }
  } catch (error) {
    console.error('导出失败:', error)

    // 尝试读取错误信息
    let errorMessage = '导出失败，请检查网络连接或权限'
    if (error.response) {
      try {
        const reader = new FileReader()
        reader.onload = () => {
          console.error('导出错误详情:', reader.result)
        }
        reader.readAsText(error.response.data)
        errorMessage = '导出失败，服务器返回错误'
      } catch (e) {
        // 忽略读取错误
      }
    } else if (error.message) {
      errorMessage = error.message
    }

    return {
      success: false,
      error: errorMessage
    }
  }
}

/**
 * 导出实验记录
 * @param {object} filters - 筛选条件
 * @returns {Promise<{success: boolean, error?: string}>}
 */
export const exportExperimentRecords = async(filters = {}) => {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
  const filename = `实验记录_${timestamp}.xlsx`

  // 构建导出参数，确保参数名与后端API一致
  const exportParams = {
    userId: filters.userId,
    algorithmId: filters.algorithmId,
    status: filters.status,
    startTimeFrom: filters.startTimeFrom || filters.startTime,
    endTimeTo: filters.endTimeTo || filters.endTime
  }

  return await exportToExcel('/api/experiments/export', exportParams, filename)
}

/**
 * 处理导出错误并显示消息
 * @param {object} result - 导出结果
 * @param {string} successMessage - 成功消息
 */
export const handleExportResult = (result, successMessage = '导出成功') => {
  if (result.success) {
    Message({
      message: successMessage,
      type: 'success',
      duration: 3000
    })
  } else {
    Message({
      message: result.error || '导出失败',
      type: 'error',
      duration: 3000
    })
  }
}

export default {
  exportToExcel,
  exportExperimentRecords,
  handleExportResult
}
