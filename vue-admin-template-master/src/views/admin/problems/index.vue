<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>题库管理</h1>
      <p class="page-description">管理路径寻路算法相关的练习题</p>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-container">
        <!-- 算法筛选 -->
        <div class="filter-item">
          <span class="filter-label">算法类型：</span>
          <el-select
            v-model="listQuery.algorithmId"
            placeholder="请选择算法"
            clearable
            style="width: 200px;"
            @change="handleFilter"
          >
            <el-option
              v-for="algorithm in algorithmOptions"
              :key="algorithm.algorithmId"
              :label="algorithm.name"
              :value="algorithm.algorithmId"
            />
          </el-select>
        </div>

        <!-- 难度筛选 -->
        <div class="filter-item">
          <span class="filter-label">难度：</span>
          <el-select
            v-model="listQuery.difficulty"
            placeholder="请选择难度"
            clearable
            style="width: 150px;"
            @change="handleFilter"
          >
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </div>

        <!-- 标题搜索 -->
        <div class="filter-item">
          <span class="filter-label">标题：</span>
          <el-input
            v-model="listQuery.title"
            placeholder="请输入题目标题"
            style="width: 200px;"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </div>

        <!-- 描述搜索 -->
        <div class="filter-item">
          <span class="filter-label">描述：</span>
          <el-input
            v-model="listQuery.description"
            placeholder="请输入题目描述"
            style="width: 200px;"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </div>

        <!-- 搜索按钮 -->
        <div class="filter-item">
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleFilter"
          >
            搜索
          </el-button>
          <el-button
            icon="el-icon-refresh"
            @click="resetFilter"
          >
            重置
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 统计信息卡片 -->
    <div class="stats-section">
      <!-- 第一行：按难度统计 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #409eff;">
                <i class="el-icon-document" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.total }}</div>
                <div class="stats-label">总题目数</div>
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
                <div class="stats-value">{{ problemStats.easy }}</div>
                <div class="stats-label">简单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #e6a23c;">
                <i class="el-icon-warning" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.medium }}</div>
                <div class="stats-label">中等</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #f56c6c;">
                <i class="el-icon-error" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.hard }}</div>
                <div class="stats-label">困难</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 第二行：按算法统计 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #909399;">
                <i class="el-icon-s-operation" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.byAlgorithm['1'] || 0 }}</div>
                <div class="stats-label">A* 算法</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #909399;">
                <i class="el-icon-s-operation" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.byAlgorithm['2'] || 0 }}</div>
                <div class="stats-label">Dijkstra 算法</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #909399;">
                <i class="el-icon-s-operation" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.byAlgorithm['4'] || 0 }}</div>
                <div class="stats-label">DFS</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #909399;">
                <i class="el-icon-s-operation" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ problemStats.byAlgorithm['5'] || 0 }}</div>
                <div class="stats-label">BFS</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operation-container">
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate"
      >
        新增题目
      </el-button>
      <el-button
        type="danger"
        icon="el-icon-delete"
        :disabled="multipleSelection.length === 0"
        @click="handleBatchDelete"
      >
        批量删除
      </el-button>
      <el-button
        type="success"
        icon="el-icon-download"
        @click="handleExport"
      >
        导出Excel
      </el-button>
    </div>

    <!-- 题目表格 -->
    <el-table
      v-loading="listLoading"
      :data="problemList"
      border
      fit
      highlight-current-row
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />

      <el-table-column label="ID" prop="problemId" width="80" align="center">
        <template slot-scope="{row}">
          <span>{{ row.problemId }}</span>
        </template>
      </el-table-column>

      <el-table-column label="标题" min-width="200">
        <template slot-scope="{row}">
          <span class="problem-title">{{ row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column label="算法类型" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag type="info" size="small">
            {{ getAlgorithmName(row.algorithmId) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="难度" width="100" align="center">
        <template slot-scope="{row}">
          <el-tag
            :type="getDifficultyType(row.difficulty)"
            size="small"
          >
            {{ formatDifficulty(row.difficulty) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="描述" min-width="250">
        <template slot-scope="{row}">
          <span class="problem-description">{{ row.description }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template slot-scope="{row}">
          <div class="action-buttons">
            <el-button
              type="primary"
              size="small"
              icon="el-icon-edit"
              class="action-btn edit-btn"
              @click="handleUpdate(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              icon="el-icon-delete"
              class="action-btn delete-btn"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 使用Element UI自带分页 -->
    <div v-show="total>0" class="pagination-container">
      <el-pagination
        :background="true"
        :current-page.sync="listQuery.page"
        :page-size.sync="listQuery.size"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="problemForm"
        :model="problemForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="problemForm.title"
            placeholder="请输入题目标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="算法类型" prop="algorithmId">
          <el-select
            v-model="problemForm.algorithmId"
            placeholder="请选择算法类型"
            style="width: 100%;"
          >
            <el-option
              v-for="algorithm in algorithmOptions"
              :key="algorithm.algorithmId"
              :label="algorithm.name"
              :value="algorithm.algorithmId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="难度" prop="difficulty">
          <el-select
            v-model="problemForm.difficulty"
            placeholder="请选择难度"
            style="width: 100%;"
          >
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="problemForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入题目描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="力扣链接" prop="leetcodeLink">
          <el-input
            v-model="problemForm.leetcodeLink"
            placeholder="请输入力扣题目链接"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProblemList, createProblem, updateProblem, deleteProblem, batchDeleteProblems, getProblemStats, exportProblems } from '@/api/problem'
// 算法模块已移除，使用模拟数据

export default {
  name: 'ProblemManagement',

  data() {
    return {
      // 题目列表数据
      problemList: [],
      total: 0,
      listLoading: true,

      // 算法选项（用于筛选下拉框）
      algorithmOptions: [],

      // 筛选条件
      listQuery: {
        page: 1,
        size: 10,
        algorithmId: undefined,
        difficulty: undefined,
        title: undefined,
        description: undefined
      },

      // 多选
      multipleSelection: [],

      // 对话框
      dialogVisible: false,
      dialogTitle: '',

      // 表单数据
      problemForm: {
        problemId: undefined,
        title: '',
        algorithmId: undefined,
        difficulty: '',
        description: '',
        leetcodeLink: ''
      },

      // 题目统计信息
      problemStats: {
        total: 0,
        easy: 0,
        medium: 0,
        hard: 0,
        byAlgorithm: {}
      },

      // 表单验证规则
      rules: {
        title: [
          { required: true, message: '请输入题目标题', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        algorithmId: [
          { required: true, message: '请选择算法类型', trigger: 'change' }
        ],
        difficulty: [
          { required: true, message: '请选择难度', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入题目描述', trigger: 'blur' },
          { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
        ],
        leetcodeLink: [
          { required: true, message: '请输入力扣题目链接', trigger: 'blur' },
          { max: 255, message: '长度不能超过 255 个字符', trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    this.getAlgorithmList()
    this.getList()
    this.loadProblemStats()
  },

  methods: {
    // 获取算法列表（用于筛选下拉框）- 使用模拟数据
    async getAlgorithmList() {
      try {
        // 模拟算法数据（与数据库algorithm表ID对应：1=A*, 2=Dijkstra, 4=DFS, 5=BFS）
        this.algorithmOptions = [
          { algorithmId: 1, name: 'A*', description: '启发式搜索算法', complexity: 'O(b^d)' },
          { algorithmId: 2, name: 'Dijkstra', description: '迪杰斯特拉最短路径算法', complexity: 'O((V+E)logV)' },
          { algorithmId: 4, name: 'DFS', description: '深度优先搜索算法', complexity: 'O(V+E)' },
          { algorithmId: 5, name: 'BFS', description: '广度优先搜索算法', complexity: 'O(V+E)' }
        ]
      } catch (error) {
        console.error('获取算法列表失败:', error)
        this.algorithmOptions = []
      }
    },

    // 获取题目列表（分页数据）
    async getList() {
      this.listLoading = true
      try {
        // 准备查询参数
        const params = {
          page: this.listQuery.page,
          size: this.listQuery.size
        }

        // 添加筛选条件
        if (this.listQuery.algorithmId) {
          params.algorithmId = this.listQuery.algorithmId
        }

        if (this.listQuery.difficulty) {
          params.difficulty = this.listQuery.difficulty
        }

        if (this.listQuery.title) {
          params.title = this.listQuery.title
        }

        if (this.listQuery.description) {
          params.description = this.listQuery.description
        }

        console.log('题库管理页面请求参数:', params)
        const response = await getProblemList(params)
        console.log('题库管理页面API响应:', response)

        // 后端返回 R<Page<Problem>>，拦截器已拆壳，response 为 Page 对象
        // Page 对象包含: records, total, current, size, pages 等字段
        if (response && response.records !== undefined) {
          // 标准分页结构
          let records = response.records || []
          // 前端过滤：过滤掉标题为"默认题目"的记录
          records = records.filter(item => item.title !== '默认题目')
          this.problemList = records
          this.total = response.total || 0
          console.log('分页数据加载成功，total:', this.total, '当前页记录数:', records.length)
        } else if (Array.isArray(response)) {
          // 如果是数组（不分页接口兜底）
          this.problemList = response.filter(item => item.title !== '默认题目')
          this.total = this.problemList.length
          console.log('使用数组结构，length:', this.total)
        } else {
          console.warn('未知的响应结构:', response)
          this.problemList = []
          this.total = 0
        }
      } catch (error) {
        console.error('获取题目列表失败:', error)
        this.$message.error('获取题目列表失败')
        this.problemList = []
        this.total = 0
      } finally {
        this.listLoading = false
      }
    },

    // 根据算法ID获取算法名称
    getAlgorithmName(algorithmId) {
      const algorithm = this.algorithmOptions.find(item => item.algorithmId === algorithmId)
      return algorithm ? algorithm.name : '未知算法'
    },

    // 获取难度标签类型
    getDifficultyType(difficulty) {
      const typeMap = {
        'easy': 'success',
        'medium': 'warning',
        'hard': 'danger'
      }
      return typeMap[difficulty] || 'info'
    },

    // 格式化难度显示
    formatDifficulty(difficulty) {
      const map = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难'
      }
      return map[difficulty] || difficulty
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.listQuery.size = val
      this.listQuery.page = 1
      this.getList()
    },

    // 当前页改变
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },

    // 筛选处理
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    // 重置筛选
    resetFilter() {
      this.listQuery = {
        page: 1,
        size: 10,
        algorithmId: undefined,
        difficulty: undefined,
        title: undefined,
        description: undefined
      }
      this.getList()
    },

    // 表格多选
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    // 新增题目
    handleCreate() {
      this.dialogTitle = '新增题目'
      this.dialogVisible = true
    },

    // 编辑题目
    handleUpdate(row) {
      this.dialogTitle = '编辑题目'
      this.dialogVisible = true
      // 深拷贝，避免表单修改影响表格数据
      this.problemForm = Object.assign({}, row)
    },

    // 删除题目
    handleDelete(row) {
      this.$confirm('确定要删除该题目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        try {
          await deleteProblem(row.problemId)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },

    // 批量删除
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请选择要删除的题目')
        return
      }

      this.$confirm(`确定要删除选中的 ${this.multipleSelection.length} 个题目吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        const problemIds = this.multipleSelection.map(item => item.problemId)
        try {
          await batchDeleteProblems(problemIds)
          this.$message.success('批量删除成功')
          this.getList()
          this.multipleSelection = []
        } catch (error) {
          console.error('批量删除失败:', error)
          this.$message.error('批量删除失败')
        }
      }).catch(() => {})
    },

    // 提交表单（新增/编辑）
    handleSubmit() {
      this.$refs['problemForm'].validate(async(valid) => {
        if (valid) {
          try {
            if (this.problemForm.problemId) {
              // 编辑
              await updateProblem(this.problemForm.problemId, this.problemForm)
              this.$message.success('更新成功')
            } else {
              // 新增
              await createProblem(this.problemForm)
              this.$message.success('创建成功')
            }

            this.dialogVisible = false
            this.getList()
          } catch (error) {
            console.error('操作失败:', error)
            this.$message.error('操作失败')
          }
        }
      })
    },

    // 加载题目统计信息
    async loadProblemStats() {
      try {
        const response = await getProblemStats()
        if (response) {
          this.problemStats = {
            total: response.total || 0,
            easy: response.easy || 0,
            medium: response.medium || 0,
            hard: response.hard || 0,
            byAlgorithm: response.byAlgorithm || {}
          }
        }
      } catch (error) {
        console.error('获取题目统计信息失败:', error)
      }
    },

    // 导出Excel
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
        if (this.listQuery.algorithmId) {
          exportParams.algorithmId = this.listQuery.algorithmId
        }
        if (this.listQuery.difficulty) {
          exportParams.difficulty = this.listQuery.difficulty
        }
        if (this.listQuery.title) {
          exportParams.title = this.listQuery.title
        }
        if (this.listQuery.description) {
          exportParams.description = this.listQuery.description
        }

        const response = await exportProblems(exportParams)

        const blob = new Blob([response], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })

        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
        link.download = `题目列表_${timestamp}.xlsx`
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

    // 重置表单
    resetForm() {
      this.$refs['problemForm'].resetFields()
      this.problemForm = {
        problemId: undefined,
        title: '',
        algorithmId: undefined,
        difficulty: '',
        description: '',
        leetcodeLink: ''
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.page-description {
  font-size: 14px;
  color: #909399;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 15px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
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

.problem-title {
  font-weight: 500;
  color: #303133;
}

.problem-description {
  color: #606266;
  font-size: 13px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-btn {
  min-width: 60px;
  padding: 7px 12px;
  font-size: 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.edit-btn {
  background-color: #409eff;
  border-color: #409eff;
}

.edit-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.delete-btn {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.delete-btn:hover {
  background-color: #f78989;
  border-color: #f78989;
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  text-align: center;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-item {
    width: 100%;
    margin-bottom: 10px;
  }

  .filter-label {
    min-width: 60px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 5px;
  }

  .action-btn {
    width: 100%;
  }

  .pagination-container {
    padding: 15px 10px;
  }

  .pagination-container ::v-deep .el-pagination {
    font-size: 12px;
  }

  .pagination-container ::v-deep .el-pagination .btn-prev,
  .pagination-container ::v-deep .el-pagination .btn-next,
  .pagination-container ::v-deep .el-pagination .el-pager li {
    min-width: 28px;
    height: 28px;
    line-height: 28px;
  }
}
</style>
