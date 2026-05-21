<template>
  <div class="problem-records-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">题目学习记录</h1>
      <p class="page-description">查看您的力扣题目学习进度与完成情况</p>
    </div>

    <!-- 查询筛选区域 -->
    <div class="filter-section">
      <el-card class="filter-card" shadow="never">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="算法类型">
            <el-select
              v-model="filterForm.algorithmId"
              placeholder="选择算法"
              clearable
              filterable
              style="width: 200px"
              @change="handleFilter"
            >
              <el-option label="A*" :value="1" />
              <el-option label="Dijkstra" :value="2" />
              <el-option label="DFS" :value="4" />
              <el-option label="BFS" :value="5" />
            </el-select>
          </el-form-item>

          <el-form-item label="难度">
            <el-select
              v-model="filterForm.difficulty"
              placeholder="选择难度"
              clearable
              style="width: 150px"
              @change="handleFilter"
            >
              <el-option label="简单" value="easy" />
              <el-option label="中等" value="medium" />
              <el-option label="困难" value="hard" />
            </el-select>
          </el-form-item>

          <el-form-item label="标题">
            <el-input
              v-model="filterForm.title"
              placeholder="请输入题目标题"
              style="width: 200px"
              clearable
              @keyup.enter.native="handleFilter"
            />
          </el-form-item>

          <el-form-item label="描述">
            <el-input
              v-model="filterForm.description"
              placeholder="请输入题目描述"
              style="width: 200px"
              clearable
              @keyup.enter.native="handleFilter"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleFilter">
              搜索
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
                <div class="stats-value">{{ stats.totalRecords }}</div>
                <div class="stats-label">总记录数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #67c23a;">
                <i class="el-icon-success" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.completedCount }}</div>
                <div class="stats-label">已完成</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #e6a23c;">
                <i class="el-icon-loading" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.inProgressCount }}</div>
                <div class="stats-label">进行中</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #f56c6c;">
                <i class="el-icon-time" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.totalTimeSpent }}</div>
                <div class="stats-label">学习分钟数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operation-container">
      <el-button
        type="danger"
        icon="el-icon-delete"
        :disabled="multipleSelection.length === 0"
        @click="handleBatchDelete"
      >
        批量删除
      </el-button>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <el-table
          v-loading="loading"
          :data="records"
          style="width: 100%"
          stripe
          border
          element-loading-text="加载中..."
          element-loading-spinner="el-icon-loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />

          <el-table-column
            prop="completionId"
            label="记录ID"
            width="80"
            align="center"
          />

          <el-table-column
            label="题目标题"
            min-width="180"
          >
            <template slot-scope="scope">
              <span class="problem-title">{{ scope.row.problemTitle }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="算法类型"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag size="small" :type="getAlgorithmTagType(scope.row.algorithmId)">
                {{ getAlgorithmName(scope.row.algorithmId) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="难度"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag
                size="small"
                :type="getDifficultyTagType(scope.row.difficulty)"
              >
                {{ formatDifficulty(scope.row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="完成状态"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="getCompletionStatusType(scope.row.completionStatus)" size="medium">
                {{ formatCompletionStatus(scope.row.completionStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="解题状态"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="scope.row.isSolved ? 'success' : 'danger'" size="medium">
                {{ scope.row.isSolved ? '已解决' : '未解决' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="尝试次数"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              {{ scope.row.attemptCount }}
            </template>
          </el-table-column>

          <el-table-column
            label="难度自评"
            width="130"
            align="center"
          >
            <template slot-scope="scope">
              <el-rate
                v-model="scope.row.difficultyRating"
                disabled
                :max="5"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                show-score
                text-color="#ff9900"
                score-template="{value}"
              />
            </template>
          </el-table-column>

          <el-table-column
            label="最近复习"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <div class="review-date">
                <i class="el-icon-time" />
                {{ formatDate(scope.row.lastReviewDate) }}
              </div>
            </template>
          </el-table-column>

          <el-table-column
            label="操作"
            width="180"
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
                  查看
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                >
                  删除
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
  </div>
</template>

<script>
import { searchUserProblemCompletions, getUserLearningStats, deleteProblemCompletion, batchDeleteProblemCompletions, exportProblemCompletions } from '@/api/problemCompletion'
import { getInfo } from '@/api/user'

export default {
  name: 'UserProblemRecords',
  data() {
    return {
      // 筛选表单
      filterForm: {
        algorithmId: undefined,
        difficulty: undefined,
        title: '',
        description: ''
      },

      // 分页参数
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },

      // 记录数据
      records: [],

      // 统计信息
      stats: {
        totalRecords: 0,
        completedCount: 0,
        inProgressCount: 0,
        totalTimeSpent: 0
      },

      loading: false,

      // 当前用户ID
      currentUserId: null,

      // 多选
      multipleSelection: []
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    /**
     * 加载数据
     */
    async loadData() {
      await this.loadCurrentUserInfo()
      if (this.currentUserId) {
        // 先加载用户的所有统计数据（总记录数、已完成、进行中、总学习分钟数）
        await this.loadUserStats()
        // 再加载分页列表数据
        await this.loadRecords()
      }
    },

    /**
     * 加载用户统计数据（从后端获取真实的全量统计数据）
     */
    async loadUserStats() {
      if (!this.currentUserId) return
      try {
        const response = await getUserLearningStats(this.currentUserId)
        if (response) {
          this.stats.totalRecords = response.totalProblems || 0
          this.stats.completedCount = response.completedCount || 0
          this.stats.inProgressCount = response.inProgressCount || 0
          this.stats.totalTimeSpent = response.totalTimeSpent || 0
        }
      } catch (error) {
        console.error('加载用户统计数据失败:', error)
        // 不显示错误消息，静默失败
      }
    },

    /**
     * 加载当前用户信息
     */
    async loadCurrentUserInfo() {
      try {
        const response = await getInfo()
        let userId = null

        if (response) {
          if (response.userId) {
            userId = response.userId
          } else if (response.user && response.user.userId) {
            userId = response.user.userId
          } else if (response.data && response.data.userId) {
            userId = response.data.userId
          } else if (response.data && response.data.user && response.data.user.userId) {
            userId = response.data.user.userId
          } else if (response.id) {
            userId = response.id
          }
        }

        if (userId) {
          this.currentUserId = userId
        } else {
          const userInfoStr = localStorage.getItem('user-info')
          if (userInfoStr) {
            try {
              const userInfo = JSON.parse(userInfoStr)
              if (userInfo.userId) {
                this.currentUserId = userInfo.userId
                return
              }
            } catch (e) {
              console.error('解析localStorage用户信息失败:', e)
            }
          }
          this.$message.warning('无法获取当前用户信息')
          this.currentUserId = null
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        const userInfoStr = localStorage.getItem('user-info')
        if (userInfoStr) {
          try {
            const userInfo = JSON.parse(userInfoStr)
            if (userInfo.userId) {
              this.currentUserId = userInfo.userId
              return
            }
          } catch (e) {
            console.error('解析localStorage用户信息失败:', e)
          }
        }
        this.$message.error('获取用户信息失败')
        this.currentUserId = null
      }
    },

    /**
     * 加载学习记录
     */
    async loadRecords() {
      if (!this.currentUserId) {
        this.records = []
        this.pagination.total = 0
        return
      }

      this.loading = true
      try {
        const params = {
          page: this.pagination.page,
          size: this.pagination.size
        }

        if (this.filterForm.algorithmId) {
          params.algorithmId = this.filterForm.algorithmId
        }
        if (this.filterForm.difficulty) {
          params.difficulty = this.filterForm.difficulty
        }
        if (this.filterForm.title) {
          params.title = this.filterForm.title
        }
        if (this.filterForm.description) {
          params.description = this.filterForm.description
        }

        const response = await searchUserProblemCompletions(this.currentUserId, params)

        if (response) {
          if (response.records !== undefined) {
            this.records = response.records || []
            this.pagination.total = response.total || 0
          } else if (Array.isArray(response)) {
            this.records = response
            this.pagination.total = response.length
          } else {
            this.records = []
            this.pagination.total = 0
          }
        } else {
          this.records = []
          this.pagination.total = 0
        }

      } catch (error) {
        console.error('加载学习记录失败:', error)
        this.$message.error('加载学习记录失败: ' + (error.message || '未知错误'))
        this.records = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },

    /**
     * 处理筛选查询
     */
    handleFilter() {
      this.pagination.page = 1
      this.loadRecords()
    },

    /**
     * 重置筛选条件
     */
    handleReset() {
      this.filterForm = {
        algorithmId: undefined,
        difficulty: undefined,
        title: '',
        description: ''
      }
      this.pagination.page = 1
      this.loadRecords()
      this.$message({
        message: '已重置筛选条件',
        type: 'info',
        duration: 1500
      })
    },

    /**
     * 查看题目详情
     */
    handleViewDetail(row) {
      localStorage.setItem('problemDetailSource', 'userProblemRecords')
      const problemInfo = {
        problemId: row.problemId,
        title: row.problemTitle || `题目 ${row.problemId}`,
        leetcodeLink: row.leetcodeLink || '',
        difficulty: row.difficulty || 'medium',
        description: row.description || '',
        algorithmId: row.algorithmId || null
      }
      localStorage.setItem('currentProblem', JSON.stringify(problemInfo))
      this.$router.push({
        name: 'ProblemDetail',
        params: {
          id: row.problemId,
          problem: problemInfo
        }
      })
    },

    /**
     * 分页大小改变
     */
    handleSizeChange(val) {
      this.pagination.size = val
      this.pagination.page = 1
      this.loadRecords()
    },

    /**
     * 当前页改变
     */
    handleCurrentChange(val) {
      this.pagination.page = val
      this.loadRecords()
    },

    /**
     * 表格多选
     */
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    /**
     * 删除单条记录
     */
    handleDelete(row) {
      this.$confirm('确定要删除该学习记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        try {
          await deleteProblemCompletion(row.completionId)
          this.$message.success('删除成功')
          this.loadRecords()
          this.loadUserStats()
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },

    /**
     * 批量删除
     */
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要删除的学习记录')
        return
      }

      this.$confirm(`确定要删除选中的 ${this.multipleSelection.length} 条学习记录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        const completionIds = this.multipleSelection.map(item => item.completionId)
        try {
          await batchDeleteProblemCompletions(completionIds)
          this.$message.success('批量删除成功')
          this.multipleSelection = []
          this.loadRecords()
          this.loadUserStats()
        } catch (error) {
          console.error('批量删除失败:', error)
          this.$message.error('批量删除失败')
        }
      }).catch(() => {})
    },

    /**
     * 获取算法名称
     */
    getAlgorithmName(algorithmId) {
      const algorithmMap = {
        1: 'A*',
        2: 'Dijkstra',
        4: 'DFS',
        5: 'BFS'
      }
      return algorithmMap[algorithmId] || `算法 ${algorithmId}`
    },

    /**
     * 获取算法标签类型
     */
    getAlgorithmTagType(algorithmId) {
      const typeMap = {
        1: 'danger',
        2: 'warning',
        4: 'success',
        5: 'primary'
      }
      return typeMap[algorithmId] || 'info'
    },

    /**
     * 获取难度标签类型
     */
    getDifficultyTagType(difficulty) {
      const typeMap = {
        'easy': 'success',
        'medium': 'warning',
        'hard': 'danger'
      }
      return typeMap[difficulty] || 'info'
    },

    /**
     * 格式化难度显示
     */
    formatDifficulty(difficulty) {
      const map = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难'
      }
      return map[difficulty] || difficulty
    },

    /**
     * 获取完成状态标签类型
     */
    getCompletionStatusType(status) {
      const typeMap = {
        'not_started': 'info',
        'in_progress': 'warning',
        'completed': 'success'
      }
      return typeMap[status] || 'info'
    },

    /**
     * 格式化完成状态显示
     */
    formatCompletionStatus(status) {
      const statusMap = {
        'not_started': '未开始',
        'in_progress': '进行中',
        'completed': '已完成'
      }
      return statusMap[status] || status
    },

    /**
     * 导出Excel
     */
    async handleExport() {
      try {
        const loading = this.$loading({
          lock: true,
          text: '正在生成Excel文件，请稍候...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        // 使用当前筛选条件
        const exportParams = {}
        if (this.filterForm.algorithmId) {
          exportParams.algorithmId = this.filterForm.algorithmId
        }
        if (this.filterForm.difficulty) {
          exportParams.difficulty = this.filterForm.difficulty
        }
        if (this.filterForm.title) {
          exportParams.title = this.filterForm.title
        }
        if (this.filterForm.description) {
          exportParams.description = this.filterForm.description
        }

        const response = await exportProblemCompletions(exportParams)

        const blob = new Blob([response], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })

        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
        link.download = `题目学习记录_${timestamp}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(downloadUrl)

        loading.close()

        this.$message({
          message: 'Excel文件导出成功！',
          type: 'success',
          duration: 3000
        })
      } catch (error) {
        console.error('导出失败:', error)
        const loadingInstances = document.querySelectorAll('.el-loading-mask')
        loadingInstances.forEach(instance => {
          if (instance.parentNode) {
            instance.parentNode.removeChild(instance)
          }
        })

        let errorMessage = '导出失败，请重试'
        if (error.response) {
          if (error.response.status === 401) {
            errorMessage = '未授权，请重新登录'
          } else if (error.response.status === 403) {
            errorMessage = '权限不足，无法导出数据'
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
     * 格式化日期
     */
    formatDate(date) {
      if (!date) return '从未复习'
      try {
        return new Date(date).toLocaleDateString('zh-CN')
      } catch (error) {
        return date
      }
    }
  }
}
</script>

<style scoped>
.problem-records-container {
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

.operation-container {
  margin-bottom: 20px;
}

.table-section {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

.problem-title {
  font-weight: 500;
  color: #303133;
}

.review-date {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #606266;
}

.review-date i {
  color: #909399;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
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
  .problem-records-container {
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
