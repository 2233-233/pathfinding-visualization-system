
<template>
  <div class="experiment-records-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">我的实验记录</h1>
      <p class="page-description">查看您的寻路算法实验记录与可视化结果</p>
    </div>

    <!-- 查询筛选区域 -->
    <div class="filter-section">
      <el-card class="filter-card" shadow="never">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
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
        <el-col :span="8">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #409eff;">
                <i class="el-icon-document" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.totalRecords }}</div>
                <div class="stats-label">总实验记录</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #67c23a;">
                <i class="el-icon-success" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.completedCount }}</div>
                <div class="stats-label">成功实验</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #f56c6c;">
                <i class="el-icon-star-off" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.failedCount }}</div>
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
            width="120"
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
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ currentRecord.recordId }}</el-descriptions-item>
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
import { getExperimentList, getExperimentSteps, exportExperimentRecords, experimentApi } from '@/api/experiment'
import { getInfo } from '@/api/user'

export default {
  name: 'ExperimentRecords',
  data() {
    return {
      // 筛选表单
      filterForm: {
        algorithmName: '',
        startTime: '',
        endTime: ''
      },

      // 分页参数
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },

      // 实验记录数据
      experimentRecords: [],

      // 实验步骤数据
      experimentSteps: [],

      loading: false,
      detailDialogVisible: false,
      currentRecord: null,

      // 统计信息（从后端全量统计接口获取）
      stats: {
        totalRecords: 0,
        completedCount: 0,
        failedCount: 0
      },

      // 当前用户ID
      currentUserId: null
    }
  },
  computed: {
    // 筛选后的记录（当前页数据）
    filteredRecords() {
      return this.experimentRecords
    }
  },
  async mounted() {
    await this.loadData()
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
        algorithmName: '',
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
     * 加载数据
     */
    async loadData() {
      // 先加载用户信息
      await this.loadCurrentUserInfo()
      if (this.currentUserId) {
        // 加载全量统计数据
        await this.loadUserStats()
        // 再加载分页列表
        await this.loadExperimentRecords()
      }
    },

    /**
     * 加载用户实验统计数据（从后端获取真实的全量统计数据）
     */
    async loadUserStats() {
      if (!this.currentUserId) return
      try {
        const response = await experimentApi.getUserExperimentStats(this.currentUserId)
        console.log('用户实验统计数据响应:', response)
        if (response) {
          // 后端返回的字段名：totalExperiments, completedExperiments, failedExperiments
          this.stats.totalRecords = response.totalExperiments || 0
          this.stats.completedCount = response.completedExperiments || 0
          this.stats.failedCount = response.failedExperiments || 0
        }
      } catch (error) {
        console.error('加载用户实验统计数据失败:', error)
        this.$message.error('加载统计数据失败: ' + (error.message || '未知错误'))
      }
    },

    /**
     * 加载当前用户信息
     */
    async loadCurrentUserInfo() {
      try {
        const response = await getInfo()
        console.log('用户信息响应:', response)

        // 处理不同的响应结构
        let userId = null

        if (response) {
          // 情况1: response直接包含userId字段
          if (response.userId) {
            userId = response.userId
          } else if (response.user && response.user.userId) {
            // 情况2: response包含user对象，user对象中有userId
            userId = response.user.userId
          } else if (response.data && response.data.userId) {
            // 情况3: response是data字段的内容，包含userId
            userId = response.data.userId
          } else if (response.data && response.data.user && response.data.user.userId) {
            // 情况4: response是data字段的内容，data包含user对象
            userId = response.data.user.userId
          } else if (response.id) {
            // 情况5: 从token中解析用户信息（备用方案）
            userId = response.id
          }
        }

        if (userId) {
          this.currentUserId = userId
          console.log('获取到用户ID:', userId)
        } else {
          console.warn('获取用户信息失败或用户ID不存在:', response)
          // 尝试从localStorage获取用户信息
          const userInfoStr = localStorage.getItem('user-info')
          if (userInfoStr) {
            try {
              const userInfo = JSON.parse(userInfoStr)
              if (userInfo.userId) {
                this.currentUserId = userInfo.userId
                console.log('从localStorage获取到用户ID:', userInfo.userId)
                return
              }
            } catch (e) {
              console.error('解析localStorage用户信息失败:', e)
            }
          }
          this.$message.warning('无法获取当前用户信息，实验记录列表可能为空')
          // 不设置默认用户ID，让API决定返回什么
          this.currentUserId = null
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 尝试从localStorage获取用户信息
        const userInfoStr = localStorage.getItem('user-info')
        if (userInfoStr) {
          try {
            const userInfo = JSON.parse(userInfoStr)
            if (userInfo.userId) {
              this.currentUserId = userInfo.userId
              console.log('从localStorage获取到用户ID（API失败时）:', userInfo.userId)
              return
            }
          } catch (e) {
            console.error('解析localStorage用户信息失败:', e)
          }
        }
        this.$message.error('获取用户信息失败，实验记录列表可能为空')
        // 不设置默认用户ID，让API决定返回什么
        this.currentUserId = null
      }
    },

    /**
     * 加载实验记录列表
     */
    async loadExperimentRecords() {
      // 检查是否已获取用户ID
      if (!this.currentUserId) {
        this.$message.error('无法获取用户信息，请重新登录')
        this.experimentRecords = []
        this.pagination.total = 0
        this.loading = false
        return
      }

      this.loading = true
      try {
        // 准备查询参数
        const params = {
          page: this.pagination.page,
          size: this.pagination.size,
          userId: this.currentUserId // 始终传递当前用户ID
        }

        // 添加算法筛选（后端使用algorithmName参数）
        if (this.filterForm.algorithmName) {
          params.algorithmName = this.filterForm.algorithmName
        }

        // 添加开始时间筛选（时间段搜索）
        if (this.filterForm.startTime) {
          // 使用时间段搜索：开始时间 >= 选择的开始时间
          params.startTimeFrom = this.filterForm.startTime
        }

        // 添加结束时间筛选（时间段搜索）
        if (this.filterForm.endTime) {
          // 使用时间段搜索：结束时间 <= 选择的结束时间
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
        // 检查是否是权限错误
        if (error.message && error.message.includes('需要管理员权限')) {
          this.$message.error('您没有权限查看所有实验记录，只能查看自己的记录')
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
        // 计算运行时间（毫秒）
        let runTime = null
        if (record.startTime && record.endTime) {
          const start = new Date(record.startTime)
          const end = new Date(record.endTime)
          if (!isNaN(start.getTime()) && !isNaN(end.getTime())) {
            runTime = end.getTime() - start.getTime()
          }
        }

        return {
          recordId: record.recordId || record.id,
          algorithmId: record.algorithmId,
          visitedCount: record.visitedCount,
          pathLength: record.pathLength,
          startTime: record.startTime,
          endTime: record.endTime,
          status: record.status || 'completed',
          runTime: runTime,
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
        const exportParams = {
          userId: this.currentUserId
        }

        // 复制筛选条件
        if (this.filterForm.algorithmName) {
          exportParams.algorithmName = this.filterForm.algorithmName
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
        const loadingInstances = document.querySelectorAll('.el-loading-mask')
        loadingInstances.forEach(instance => {
          if (instance.parentNode) {
            instance.parentNode.removeChild(instance)
          }
        })

        // 显示错误消息
        let errorMessage = '导出失败，请重试'
        if (error.response) {
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
          errorMessage = '网络连接失败，请检查网络设置'
        } else {
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
    }
  }
}
</script>

<style scoped>
.experiment-records-container {
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
  .experiment-records-container {
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
