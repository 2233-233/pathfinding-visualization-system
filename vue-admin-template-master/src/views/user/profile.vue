<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-description">查看您的个人信息与学习统计</p>
    </div>

    <!-- 个人信息区域 -->
    <div class="info-section">
      <el-card class="info-card" shadow="never">
        <div slot="header" class="clearfix">
          <span class="card-title">个人信息</span>
          <div style="float: right;">
            <el-button
              type="primary"
              size="small"
              icon="el-icon-edit"
              @click="handleEditProfile"
            >
              修改个人信息
            </el-button>
          </div>
        </div>

        <div class="user-info-content">
          <div class="info-row">
            <span class="info-label">用户名：</span>
            <span class="info-value">{{ userInfo.username }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">姓名：</span>
            <span class="info-value">{{ userInfo.name }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">学号：</span>
            <span class="info-value">{{ userInfo.studentId || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">邮箱：</span>
            <span class="info-value">{{ userInfo.email || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">注册时间：</span>
            <span class="info-value">{{ userInfo.enrollmentDate }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 学习统计区域 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #409eff;">
                <i class="el-icon-s-flag" />
              </div>
              <div class="stats-info">
                <div class="stats-title">已完成实验次数</div>
                <div class="stats-value">{{ stats.completedExperiments }}</div>
                <div class="stats-desc">总计完成实验</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #67c23a;">
                <i class="el-icon-document" />
              </div>
              <div class="stats-info">
                <div class="stats-title">写过的题目</div>
                <div class="stats-value">{{ stats.totalProblems }}</div>
                <div class="stats-desc">题目学习记录总数</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="stats-card" shadow="hover">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #e6a23c;">
                <i class="el-icon-success" />
              </div>
              <div class="stats-info">
                <div class="stats-title">已完成的题目</div>
                <div class="stats-value">{{ stats.completedProblems }}</div>
                <div class="stats-desc">已完成题目数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- ProblemCompletion记录 -->
    <div class="records-section">
      <el-card class="records-card" shadow="never">
        <div slot="header" class="clearfix">
          <span class="card-title">题目学习记录</span>
          <el-button
            type="text"
            size="small"
            icon="el-icon-refresh"
            style="float: right;"
            :loading="loadingCompletions"
            @click="fetchProblemCompletions"
          >
            刷新
          </el-button>
        </div>

        <el-table
          :data="problemCompletions"
          style="width: 100%"
          stripe
          border
          size="medium"
          @row-click="handleProblemCompletionClick"
        >
          <el-table-column
            prop="completionId"
            label="记录ID"
            width="80"
            align="center"
          />

          <el-table-column
            prop="problemId"
            label="题目ID"
            width="100"
            align="center"
          />

          <el-table-column
            label="题目名称"
            min-width="180"
          >
            <template slot-scope="scope">
              <div class="problem-title">
                <i class="el-icon-document" />
                <span class="problem-link" @click.stop="handleViewProblemDetail(scope.row)">
                  {{ scope.row.problemTitle || `题目 ${scope.row.problemId}` }}
                </span>
              </div>
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
            width="120"
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
            width="180"
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
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                size="small"
                icon="el-icon-view"
                @click.stop="handleViewProblemDetail(scope.row)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态提示 -->
        <div v-if="problemCompletions.length === 0 && !loadingCompletions" class="empty-state">
          <i class="el-icon-document" />
          <p>暂无学习记录</p>
          <el-button type="text" @click="goToLearningPage">前往学习页面</el-button>
        </div>
      </el-card>
    </div>

    <!-- 修改个人信息对话框 -->
    <el-dialog
      title="修改个人信息"
      :visible.sync="profileDialogVisible"
      width="480px"
      :close-on-click-modal="false"
      @closed="resetProfileForm"
    >
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="profileForm.name"
            placeholder="请输入姓名"
            prefix-icon="el-icon-user"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="profileForm.email"
            placeholder="请输入新邮箱地址"
            prefix-icon="el-icon-message"
          />
        </el-form-item>

        <el-divider content-position="center">密码修改（可选）</el-divider>

        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="profileForm.password"
            type="password"
            placeholder="留空则不修改密码"
            show-password
            prefix-icon="el-icon-key"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="profileForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            prefix-icon="el-icon-check"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="profileDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="profileSubmitting" @click="submitProfileChange">
          确 定
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUserById, updateUser } from '@/api/user'
import { getProblemCompletionsByUserId } from '@/api/problemCompletion'
import { getProblemById } from '@/api/problem'
import { experimentApi } from '@/api/experiment'

export default {
  name: 'UserProfile',
  data() {
    // 密码验证规则（编辑时密码可选）
    const validatePassword = (rule, value, callback) => {
      if (value && value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.profileForm.confirmPassword !== '') {
          this.$refs.profileFormRef.validateField('confirmPassword')
        }
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (this.profileForm.password && value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.profileForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      // 用户信息
      userInfo: {
        username: '',
        name: '',
        studentId: '',
        email: '',
        enrollmentDate: ''
      },
      loading: false,

      // 学习统计数据
      stats: {
        completedExperiments: 0,
        totalProblems: 0,
        completedProblems: 0
      },

      // ProblemCompletion记录
      problemCompletions: [],
      loadingCompletions: false,

      // 当前用户ID
      currentUserId: null,

      // 修改个人信息对话框
      profileDialogVisible: false,
      profileSubmitting: false,
      profileForm: {
        name: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      profileRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  async mounted() {
    console.log('个人中心页面加载完成')
    await this.fetchUserInfo()
    await this.fetchProblemCompletions()
    await this.fetchExperimentStats()
  },
  methods: {
    /**
     * 获取用户信息
     */
    async fetchUserInfo() {
      this.loading = true
      try {
        // 从localStorage获取当前用户ID
        const userInfoStr = localStorage.getItem('user-info')
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr)
          this.currentUserId = userInfo.userId

          if (this.currentUserId) {
            const response = await getUserById(this.currentUserId)
            // 后端返回 R<User>，响应拦截器返回 res.data（即 User 对象）
            // 但为了兼容不同响应格式，也处理 records 包裹的情况
            const userData = response && response.records ? response.records[0] : response

            // 更新用户信息
            this.userInfo = {
              username: userData.username || '',
              name: userData.name || userData.username || '',
              studentId: userData.studentId || '',
              email: userData.email || '',
              enrollmentDate: userData.createdAt || userData.enrollmentDate || '未设置'
            }

            console.log('获取用户信息成功:', userData)
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.$message({
          message: '获取用户信息失败，请稍后重试',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.loading = false
      }
    },

    /**
     * 获取ProblemCompletion记录
     */
    async fetchProblemCompletions() {
      if (!this.currentUserId) {
        console.log('未获取到用户ID，无法获取学习记录')
        return
      }

      this.loadingCompletions = true
      try {
        const response = await getProblemCompletionsByUserId(this.currentUserId)
        // 根据后端API响应格式调整
        const completions = response.records || response.data || response || []
        console.log('获取ProblemCompletion记录成功:', completions)

        // 如果记录不为空，尝试获取题目信息
        if (completions.length > 0) {
          // 获取所有题目的详细信息
          const enrichedCompletions = await this.enrichProblemCompletions(completions)
          this.problemCompletions = enrichedCompletions
        } else {
          this.problemCompletions = []
        }

        // 更新学习统计数据
        this.updateLearningStats()
      } catch (error) {
        console.error('获取ProblemCompletion记录失败:', error)
        this.$message({
          message: '获取学习记录失败，请稍后重试',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.loadingCompletions = false
      }
    },

    /**
     * 丰富ProblemCompletion记录，获取题目信息
     */
    async enrichProblemCompletions(completions) {
      const enriched = []

      for (const completion of completions) {
        try {
          // 获取题目详细信息
          const problemResponse = await getProblemById(completion.problemId)
          const problem = problemResponse.records ? problemResponse.records[0] : problemResponse

          // 合并ProblemCompletion和Problem信息
          enriched.push({
            ...completion,
            // Problem信息
            problemTitle: problem?.title || `题目 ${completion.problemId}`,
            leetcodeLink: problem?.leetcodeLink || '',
            difficulty: problem?.difficulty || 'medium',
            description: problem?.description || '',
            algorithmId: problem?.algorithmId || null
          })
        } catch (error) {
          console.error(`获取题目 ${completion.problemId} 信息失败:`, error)
          // 如果获取题目信息失败，使用基本信息
          enriched.push({
            ...completion,
            problemTitle: `题目 ${completion.problemId}`,
            leetcodeLink: '',
            difficulty: 'medium',
            description: '',
            algorithmId: null
          })
        }
      }

      return enriched
    },

    /**
     * 更新学习统计数据
     */
    updateLearningStats() {
      // 统计题目学习记录总数
      this.stats.totalProblems = this.problemCompletions.length
      // 统计已完成（completion_status为completed）的题目数量
      const completedCount = this.problemCompletions.filter(item => item.completionStatus === 'completed').length
      this.stats.completedProblems = completedCount
    },

    /**
     * 获取实验统计数据
     */
    async fetchExperimentStats() {
      if (!this.currentUserId) {
        console.log('未获取到用户ID，无法获取实验统计')
        return
      }

      try {
        const response = await experimentApi.getUserExperimentStats(this.currentUserId)
        // 后端返回 R<Map>，响应拦截器返回 res.data（即 Map 对象）
        const statsData = response && response.records ? response.records[0] : response
        if (statsData) {
          this.stats.completedExperiments = statsData.completedExperiments || 0
          console.log('获取实验统计成功:', statsData)
        }
      } catch (error) {
        console.error('获取实验统计失败:', error)
        // 不显示错误消息，避免干扰用户
      }
    },

    /**
     * 处理ProblemCompletion表格行点击
     */
    handleProblemCompletionClick(row) {
      this.handleViewProblemDetail(row)
    },

    /**
     * 查看题目详情
     */
    handleViewProblemDetail(row) {
      // 保存来源页面信息到localStorage
      localStorage.setItem('problemDetailSource', 'userProfile')

      // 保存题目信息到localStorage，以便详情页使用
      const problemInfo = {
        problemId: row.problemId,
        title: row.problemTitle || `题目 ${row.problemId}`,
        // 其他字段可能为空，但algorithm/problem/_id.vue会通过API获取完整信息
        leetcodeLink: row.leetcodeLink || '',
        difficulty: row.difficulty || 'medium',
        description: row.description || '',
        algorithmId: row.algorithmId || null
      }
      localStorage.setItem('currentProblem', JSON.stringify(problemInfo))

      // 跳转到题目详情页面
      this.$router.push({
        name: 'ProblemDetail',
        params: {
          id: row.problemId,
          problem: problemInfo
        }
      })
    },

    /**
     * 编辑ProblemCompletion记录
     */
    handleEditProblemCompletion(row) {
      // 保存来源页面信息到localStorage
      localStorage.setItem('problemDetailSource', 'userProfile')

      // 跳转到题目详情页面
      this.$router.push({
        name: 'ProblemDetail',
        params: {
          id: row.problemId,
          problem: {
            problemId: row.problemId,
            title: row.problemTitle || `题目 ${row.problemId}`
          }
        }
      })
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
     * 格式化日期
     */
    formatDate(date) {
      if (!date) return '从未复习'
      try {
        return new Date(date).toLocaleDateString('zh-CN')
      } catch (error) {
        return date
      }
    },

    /**
     * 跳转到学习页面
     */
    goToLearningPage() {
      this.$router.push('/algorithm/learning')
    },

    /**
     * 打开修改个人信息对话框
     */
    handleEditProfile() {
      // 将当前用户信息填入表单
      this.profileForm = {
        name: this.userInfo.name,
        email: this.userInfo.email,
        password: '',
        confirmPassword: ''
      }
      this.profileDialogVisible = true
    },

    /**
     * 提交修改个人信息
     */
    submitProfileChange() {
      this.$refs.profileFormRef.validate(async(valid) => {
        if (!valid) return

        this.profileSubmitting = true
        try {
          // 准备提交数据
          const submitData = {
            name: this.profileForm.name,
            email: this.profileForm.email
          }

          // 如果密码不为空，则包含密码字段
          if (this.profileForm.password) {
            submitData.password = this.profileForm.password
          }

          // 使用 updateUser API 更新用户信息
          await updateUser(this.currentUserId, submitData)

          this.$message({
            message: '个人信息修改成功',
            type: 'success',
            duration: 3000
          })
          this.profileDialogVisible = false

          // 如果修改了密码，让用户重新登录
          if (this.profileForm.password) {
            setTimeout(() => {
              this.$store.dispatch('user/logout').then(() => {
                this.$router.push('/login')
              })
            }, 1500)
          } else {
            // 仅修改姓名/邮箱，刷新用户信息
            this.fetchUserInfo()
          }
        } catch (error) {
          console.error('修改个人信息失败:', error)
          this.$message({
            message: '修改个人信息失败，请稍后重试',
            type: 'error',
            duration: 3000
          })
        } finally {
          this.profileSubmitting = false
        }
      })
    },

    /**
     * 重置个人信息表单
     */
    resetProfileForm() {
      this.profileForm = {
        name: '',
        email: '',
        password: '',
        confirmPassword: ''
      }
      this.profileSubmitting = false
      if (this.$refs.profileFormRef) {
        this.$refs.profileFormRef.clearValidate()
      }
    },

    /**
     * 处理查看全部题目操作
     */
    handleViewAllProblems() {
      this.$message({
        message: '跳转到题目列表页面（模拟）',
        type: 'info',
        duration: 1500
      })
    },

    /**
     * 跳转到力扣页面
     * @param {Object} problem 题目数据
     */
    handleGoToLeetCode(problem) {
      window.open(problem.leetcodeUrl, '_blank')
      this.$message({
        message: `正在跳转到力扣题目: ${problem.title}`,
        type: 'success',
        duration: 1500
      })
    },

    /**
     * 获取算法标签类型
     * @param {string} algorithm 算法名称
     * @returns {string} 标签类型
     */
    getAlgorithmTagType(algorithm) {
      const typeMap = {
        'BFS': 'primary',
        'DFS': 'success',
        'Dijkstra': 'warning',
        'A*': 'danger'
      }
      return typeMap[algorithm] || 'info'
    },

    /**
     * 获取难度标签类型
     * @param {string} difficulty 难度等级
     * @returns {string} 标签类型
     */
    getDifficultyTagType(difficulty) {
      const typeMap = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return typeMap[difficulty] || 'info'
    }
  }
}
</script>

<style scoped>
.profile-container {
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

.info-section {
  margin-bottom: 20px;
}

.info-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background-color: #ffffff;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 个人信息内容样式 */
.user-info-content {
  padding: 15px 0;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding: 0 10px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  width: 100px;
  font-weight: 600;
  color: #606266;
  text-align: right;
  margin-right: 15px;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  color: #303133;
  font-weight: 500;
  word-break: break-word;
}

/* 学习统计区域样式 */
.stats-section {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background-color: #ffffff;
  height: 120px;
  overflow: hidden;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
}

.stats-icon i {
  font-size: 28px;
  color: white;
}

.stats-info {
  flex: 1;
}

.stats-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stats-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
  line-height: 1;
}

.stats-desc {
  font-size: 12px;
  color: #c0c4cc;
}

/* 记录区域样式 */
.records-section {
  margin-bottom: 20px;
}

.records-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background-color: #ffffff;
}

.problem-link {
  color: #409eff;
  text-decoration: none;
  transition: color 0.3s;
}

.problem-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.solve-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #606266;
}

.solve-time i {
  color: #909399;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .profile-container {
    padding: 15px;
  }

  .page-title {
    font-size: 20px;
  }

  .stats-card {
    height: auto;
    margin-bottom: 15px;
  }

  .stats-content {
    flex-direction: column;
    text-align: center;
    padding: 15px 0;
  }

  .stats-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }

  .el-col {
    margin-bottom: 15px;
  }

  .info-row {
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 12px;
  }

  .info-label {
    width: auto;
    text-align: left;
    margin-right: 0;
    margin-bottom: 5px;
  }

  .info-value {
    width: 100%;
  }

  .el-table__body-wrapper .el-table__cell {
    padding: 8px 4px;
  }

  .problem-link {
    font-size: 13px;
  }
}
</style>
