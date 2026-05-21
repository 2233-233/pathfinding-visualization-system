<template>
  <div class="admin-experiments-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">实验记录管理</h1>
      <p class="page-description">查看所有寻路算法实验记录与可视化结果</p>
    </div>

    <!-- 查询筛选区域 -->
    <div class="filter-section">
      <el-card class="filter-card" shadow="never">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="用户">
            <el-select
              v-model="filterForm.userId"
              placeholder="选择用户"
              clearable
              filterable
              style="width: 180px"
            >
              <el-option
                v-for="user in userOptions"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="算法">
            <el-select
              v-model="filterForm.algorithmName"
              placeholder="选择算法"
              clearable
              filterable
              style="width: 200px"
            >
              <el-option label="A*算法" value="A*" />
              <el-option label="Dijkstra算法" value="Dijkstra" />
              <el-option label="深度优先搜索 (DFS)" value="DFS" />
              <el-option label="广度优先搜索 (BFS)" value="BFS" />
            </el-select>
          </el-form-item>

          <el-form-item label="开始时间">
            <el-date-picker
              v-model="filterForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              style="width: 180px"
            />
          </el-form-item>

          <el-form-item label="结束时间">
            <el-date-picker
              v-model="filterForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              style="width: 180px"
            />
          </el-form-item>

          <el-form-item label="状态">
            <el-select
              v-model="filterForm.status"
              placeholder="选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="全部" value="" />
              <el-option label="已完成" value="completed" />
              <el-option label="失败" value="failed" />
              <el-option label="运行中" value="running" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleFilter">
              查询
            </el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">
              重置
            </el-button>
            <el-button type="success" icon="el-icon-download" @click="handleExport">
              导出Excel
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 统计信息卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #409eff;">
                <i class="el-icon-document" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ totalRecords }}</div>
                <div class="stats-label">总实验记录</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #67c23a;">
                <i class="el-icon-user" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ uniqueUsers }}</div>
                <div class="stats-label">参与用户</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #e6a23c;">
                <i class="el-icon-success" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ completedCount }}</div>
                <div class="stats-label">成功实验</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #f56c6c;">
                <i class="el-icon-star-off" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ failedCount }}</div>
                <div class="stats-label">失败实验</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <el-table
          v-loading="loading"
          :data="filteredRecords"
          style="width: 100%"
          stripe
          border
          element-loading-text="加载中..."
          element-loading-spinner="el-icon-loading"
          height="calc(100vh - 420px)"
        >
          <el-table-column
            prop="recordId"
            label="记录ID"
            width="90"
            align="center"
            sortable
          />

          <el-table-column
            label="用户信息"
            width="180"
            align="center"
          >
            <template slot-scope="scope">
              <div class="user-info">
                <div class="user-name">{{ getUserName(scope.row) }}</div>
                <div class="user-id">ID: {{ scope.row.userId }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            label="算法信息"
            min-width="200"
          >
            <template slot-scope="scope">
              <div class="algorithm-info">
                <div class="algorithm-name">{{ getAlgorithmName(scope.row.algorithmId) }}</div>
                <div class="algorithm-id">算法ID: {{ scope.row.algorithmId }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            prop="visitedCount"
            label="访问节点数"
            width="120"
            align="center"
            sortable
          />

          <el-table-column
            prop="pathLength"
            label="路径长度"
            width="100"
            align="center"
            sortable
          />

          <el-table-column
            prop="startTime"
            label="开始时间"
            width="180"
            align="center"
            sortable
          />

          <el-table-column
            prop="endTime"
            label="结束时间"
            width="180"
            align="center"
            sortable
          />

          <el-table-column
            label="操作"
            width="200"
            align="center"
            fixed="right"
          >
            <template slot-scope="scope">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="small"
                  icon="el-icon-view"
                  @click="handleViewDetail(scope.row)"
                >
                  查看详情
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  icon="el-icon-video-play"
                  @click="handleReplay(scope.row)"
                >
                  重放实验
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            :background="true"
            :current-page.sync="pagination.page"
            :page-size.sync="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 实验详情对话框 -->
    <el-dialog
      title="实验记录详情"
      :visible.sync="detailDialogVisible"
      width="800px"
      :before-close="handleCloseDetailDialog"
    >
      <div v-if="currentRecord" class="experiment-detail">
        <el-descriptions :column="2" border direction="vertical">
          <el-descriptions-item label="记录ID">{{ currentRecord.recordId }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentRecord.userId }}</el-descriptions-item>
          <el-descriptions-item label="算法">{{ getAlgorithmName(currentRecord.algorithmId) }}</el-descriptions-item>
          <el-descriptions-item label="访问节点数">{{ currentRecord.visitedCount }}</el-descriptions-item>
          <el-descriptions-item label="路径长度">{{ currentRecord.pathLength }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentRecord.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ currentRecord.endTime }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="experimentSteps.length > 0" class="steps-section">
          <h3>实验步骤 ({{ experimentSteps.length }} 步)</h3>
          <el-table
            :data="experimentSteps.slice(0, 10)"
            size="small"
            border
            style="width: 100%; margin-top: 10px;"
          >
            <el-table-column prop="stepIndex" label="步骤" width="80" align="center" />
            <el-table-column prop="nodeX" label="X坐标" width="80" align="center" />
            <el-table-column prop="nodeY" label="Y坐标" width="80" align="center" />
            <el-table-column prop="nodeState" label="状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag size="small" :type="scope.row.nodeState === 'visited' ? 'info' : 'success'">
                  {{ scope.row.nodeState === 'visited' ? '已访问' : '路径' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <p v-if="experimentSteps.length > 10" class="steps-more">
            还有 {{ experimentSteps.length - 10 }} 步未显示...
          </p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>

      </span>
    </el-dialog>
  </div>
</template>

<script>
// 导入API接口
import { getExperimentList, getExperimentSteps, exportExperimentRecords } from '@/api/experiment'
import { getUserList } from '@/api/user'

export default {
  name: 'AdminExperiments',
  data() {
    return {
      // 筛选表单
      filterForm: {
        userId: '',
        algorithmName: '',
        status: '',
        startTime: '',
        endTime: ''
      },

      // 分页参数
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },

      // 用户选项
      userOptions: [],

      // 实验记录数据
      experimentRecords: [],

      // 实验步骤数据
      experimentSteps: [],

      loading: false,
      detailDialogVisible: false,
      currentRecord: null,

      // 用户ID到用户名的映射缓存
      userNameMap: {}
    }
  },
  computed: {
    // 计算统计信息
    totalRecords() {
      return this.pagination.total
    },

    uniqueUsers() {
      const uniqueIds = new Set(this.experimentRecords.map(record => record.userId))
      return uniqueIds.size
    },

    completedCount() {
      return this.experimentRecords.filter(record => record.status === 'completed').length
    },

    failedCount() {
      return this.experimentRecords.filter(record => record.status === 'failed').length
    },

    // 筛选后的记录（当前页数据）
    filteredRecords() {
      return this.experimentRecords
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    /**
     * 处理筛选查询
     */
    handleFilter() {
      this.pagination.page = 1
      this.loadExperimentRecords()
    },

    /**
     * 重置筛选条件
     */
    handleReset() {
      this.filterForm = {
        userId: '',
        algorithmName: '',
        status: '',
        startTime: '',
        endTime: ''
      }
      this.pagination.page = 1
      this.loadExperimentRecords()
      this.$message({
        message: '已重置筛选条件',
        type: 'info',
        duration: 1500
      })
    },

    /**
     * 处理导出Excel
     */
    async handleExport() {
      try {
        // 显示加载提示
        const loading = this.$loading({
          lock: true,
          text: '正在生成Excel文件，请稍候...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        // 准备导出参数（使用当前筛选条件）
        const exportParams = {}

        // 复制筛选条件
        if (this.filterForm.userId) {
          exportParams.userId = this.filterForm.userId
        }
        if (this.filterForm.algorithmName) {
          exportParams.algorithmName = this.filterForm.algorithmName
        }
        if (this.filterForm.status) {
          exportParams.status = this.filterForm.status
        }
        if (this.filterForm.startTime) {
          exportParams.startTimeFrom = this.filterForm.startTime
        }
        if (this.filterForm.endTime) {
          exportParams.endTimeTo = this.filterForm.endTime
        }

        // 调用导出API
        const response = await exportExperimentRecords(exportParams)

        // 创建Blob对象并下载
        const blob = new Blob([response], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })

        // 创建下载链接
        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl

        // 生成文件名
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
        const fileName = `实验记录_${timestamp}.xlsx`

        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)

        // 释放URL对象
        window.URL.revokeObjectURL(downloadUrl)

        // 关闭加载提示
        loading.close()

        // 显示成功消息
        this.$message({
          message: 'Excel文件导出成功！',
          type: 'success',
          duration: 3000
        })
      } catch (error) {
        console.error('导出失败:', error)

        // 关闭加载提示（如果存在）
        if (this.$loading) {
          const loadingInstances = document.querySelectorAll('.el-loading-mask')
          loadingInstances.forEach(instance => {
            if (instance.parentNode) {
              instance.parentNode.removeChild(instance)
            }
          })
        }

        // 显示错误消息
        let errorMessage = '导出失败，请重试'
        if (error.response) {
          // 后端返回的错误
          if (error.response.status === 401) {
            errorMessage = '未授权，请重新登录'
          } else if (error.response.status === 403) {
            errorMessage = '权限不足，无法导出数据'
          } else if (error.response.status === 404) {
            errorMessage = '导出接口不存在，请联系管理员'
          } else if (error.response.status === 500) {
            errorMessage = '服务器内部错误，请稍后重试'
          }
        } else if (error.request) {
          // 请求发送但无响应
          errorMessage = '网络连接失败，请检查网络设置'
        } else {
          // 其他错误
          errorMessage = error.message || '导出失败，请重试'
        }

        this.$message({
          message: errorMessage,
          type: 'error',
          duration: 3000
        })
      }
    },

    /**
     * 加载数据
     */
    async loadData() {
      await Promise.all([
        this.loadUserOptions(),
        this.loadExperimentRecords()
      ])
    },

    /**
     * 加载用户选项
     */
    async loadUserOptions() {
      try {
        const response = await getUserList({ page: 1, size: 100 })
        if (response && response.records) {
          this.userOptions = response.records.map(user => ({
            id: user.userId,
            name: user.name || user.username,
            username: user.username
          }))
          // 构建用户ID到用户名的映射
          response.records.forEach(user => {
            this.userNameMap[user.userId] = user.name || user.username
          })
        }
      } catch (error) {
        console.error('加载用户选项失败:', error)
        this.$message.error('加载用户选项失败')
      }
    },

    /**
     * 获取用户名
     */
    getUserName(record) {
      // 优先使用缓存的用户名映射
      if (this.userNameMap[record.userId]) {
        return this.userNameMap[record.userId]
      }
      return `用户 ${record.userId}`
    },

    /**
     * 加载实验记录列表
     */
    async loadExperimentRecords() {
      this.loading = true
      try {
        // 准备查询参数
        const params = {
          page: this.pagination.page,
          size: this.pagination.size
        }

        // 添加筛选条件
        if (this.filterForm.userId) {
          params.userId = this.filterForm.userId
        }
        if (this.filterForm.algorithmName) {
          params.algorithmName = this.filterForm.algorithmName
        }
        if (this.filterForm.status) {
          params.status = this.filterForm.status
        }
        // 添加开始时间筛选（时间段搜索）
        if (this.filterForm.startTime) {
          params.startTimeFrom = this.filterForm.startTime
        }
        // 添加结束时间筛选（时间段搜索）
        if (this.filterForm.endTime) {
          params.endTimeTo = this.filterForm.endTime
        }

        // 调用API获取实验记录列表
        const response = await getExperimentList(params)

        // 根据接口规范，response已经是data字段的内容
        if (response) {
          // 检查是否是分页结构
          if (response.records !== undefined) {
            this.experimentRecords = this.transformExperimentData(response.records || [])
            this.pagination.total = response.total || 0
            this.pagination.current = response.current || 1
            this.pagination.pages = response.pages || 1
          } else if (Array.isArray(response)) {
            // 如果是数组（不分页接口）
            this.experimentRecords = this.transformExperimentData(response)
            this.pagination.total = response.length
          } else {
            console.warn('未知的响应结构:', response)
            this.experimentRecords = []
            this.pagination.total = 0
          }
        } else {
          this.experimentRecords = []
          this.pagination.total = 0
        }
      } catch (error) {
        console.error('加载实验记录失败:', error)
        console.error('错误详情:', error.response ? error.response.data : error.message)
        // 检查是否是权限错误
        if (error.response && error.response.status === 403) {
          this.$message.error('权限不足，无法查看所有实验记录')
        } else if (error.response && error.response.status === 401) {
          this.$message.error('未授权，请重新登录')
        } else {
          this.$message.error('加载实验记录失败: ' + (error.message || '未知错误'))
        }
        this.experimentRecords = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },

    /**
     * 转换实验记录数据格式
     * @param {Array} records API返回的实验记录
     * @returns {Array} 转换后的表格数据
     */
    transformExperimentData(records) {
      return records.map(record => {
        return {
          recordId: record.recordId || record.id,
          userId: record.userId,
          algorithmId: record.algorithmId,
          visitedCount: record.visitedCount,
          pathLength: record.pathLength,
          startTime: record.startTime,
          endTime: record.endTime,
          status: record.status || 'completed',
          // 保留原始数据用于详情查看
          rawData: record
        }
      })
    },

    /**
     * 查看详情
     * @param {Object} record 实验记录
     */
    async handleViewDetail(record) {
      this.currentRecord = record
      this.detailDialogVisible = true

      // 加载实验步骤
      try {
        const response = await getExperimentSteps(record.recordId)
        if (response && Array.isArray(response)) {
          this.experimentSteps = response
        } else {
          this.experimentSteps = []
        }
      } catch (error) {
        console.error('加载实验步骤失败:', error)
        this.experimentSteps = []
      }
    },

    /**
     * 重放实验
     * @param {Object} record 实验记录
     */
    handleReplay(record) {
      // 跳转到可视化页面，并传递实验记录ID
      this.$router.push({
        path: '/visualization',
        query: {
          experimentId: record.recordId,
          algorithmId: record.algorithmId,
          gridSize: record.gridSize
        }
      })
    },

    /**
     * 关闭详情对话框
     */
    handleCloseDetailDialog(done) {
      this.currentRecord = null
      this.experimentSteps = []
      done()
    },

    /**
     * 分页大小改变
     */
    handleSizeChange(val) {
      this.pagination.size = val
      this.pagination.page = 1
      this.loadExperimentRecords()
    },

    /**
     * 当前页改变
     */
    handleCurrentChange(val) {
      this.pagination.page = val
      this.loadExperimentRecords()
    },

    /**
     * 获取算法名称
     * @param {number} algorithmId 算法ID
     * @returns {string} 算法名称
     */
    getAlgorithmName(algorithmId) {
      // 根据数据库中的实际ID映射（从API获取）：
      // A*算法: algorithm_id = 1
      // Dijkstra算法: algorithm_id = 2
      // DFS: algorithm_id = 4
      // BFS: algorithm_id = 5
      const algorithmMap = {
        1: 'A*算法',
        2: 'Dijkstra算法',
        4: '深度优先搜索 (DFS)',
        5: '广度优先搜索 (BFS)'
      }
      return algorithmMap[algorithmId] || `算法 ${algorithmId}`
    },

    /**
     * 获取状态标签类型
     * @param {string} status 实验状态
     * @returns {string} 标签类型
     */
    getStatusTagType(status) {
      const typeMap = {
        'completed': 'success',
        'failed': 'danger',
        'running': 'warning'
      }
      return typeMap[status] || 'info'
    },

    /**
     * 获取状态文本
     * @param {string} status 实验状态
     * @returns {string} 状态文本
     */
    getStatusText(status) {
      const textMap = {
        'completed': '完成',
        'failed': '失败',
        'running': '运行中'
      }
      return textMap[status] || status
    }
  }
}
</script>

<style scoped>
.admin-experiments-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
  padding: 0 10px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-description {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.stats-section {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.table-section {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.user-id {
  font-size: 12px;
  color: #909399;
}

.algorithm-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.algorithm-name {
  font-weight: 500;
  color: #303133;
}

.algorithm-id {
  font-size: 12px;
  color: #909399;
}

.text-muted {
  color: #c0c4cc;
  font-style: italic;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.experiment-detail {
  padding: 10px;
}

.steps-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.steps-section h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
  font-size: 16px;
}

.steps-more {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .el-form--inline .el-form-item {
    margin-right: 10px;
    margin-bottom: 10px;
  }

  .stats-card .stats-value {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .admin-experiments-container {
    padding: 15px;
  }

  .page-title {
    font-size: 20px;
  }

  .stats-section .el-col {
    margin-bottom: 15px;
  }

  .stats-card {
    margin-bottom: 10px;
  }
}
</style>
