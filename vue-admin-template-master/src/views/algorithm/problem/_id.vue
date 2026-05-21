<template>
  <div class="problem-detail-container">
    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <!-- 左侧：题目信息 -->
      <div class="problem-info-section">
        <el-card class="problem-card">
          <div slot="header" class="clearfix">
            <span class="problem-title">{{ problem.title }}</span>
            <el-tag :type="getDifficultyType(problem.difficulty)" class="difficulty-tag">
              {{ formatDifficulty(problem.difficulty) }}
            </el-tag>
          </div>

          <div class="problem-meta">
            <div class="meta-item">
              <span class="meta-label">题目ID：</span>
              <span class="meta-value">{{ problem.problemId }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">算法ID：</span>
              <el-tag size="small">{{ problem.algorithmId || '未指定' }}</el-tag>
            </div>
            <div class="meta-item">
              <span class="meta-label">LeetCode链接：</span>
              <el-link
                v-if="problem.leetcodeLink"
                type="primary"
                :href="problem.leetcodeLink"
                target="_blank"
                class="leetcode-link"
              >
                前往LeetCode查看题目
                <i class="el-icon-link el-icon--right" />
              </el-link>
              <span v-else class="no-link">无链接</span>
            </div>
          </div>

          <div class="problem-description">
            <h3>题目描述</h3>
            <div class="description-content">
              <p>{{ problem.description || '暂无详细描述' }}</p>
              <p v-if="problem.leetcodeLink" class="leetcode-tip">
                请在LeetCode平台上完成此题，然后在此记录你的学习进度和解题思路。
              </p>
            </div>
          </div>
        </el-card>

        <!-- 学习统计 -->
        <el-card class="stats-card">
          <div slot="header">
            <span>学习统计</span>
          </div>
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-label">总尝试次数</div>
              <div class="stat-value">{{ record.attemptCount }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">累计学习时间</div>
              <div class="stat-value">{{ record.timeSpentMinutes }} 分钟</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">最近复习</div>
              <div class="stat-value">{{ formatDate(record.lastReviewDate) }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：学习记录编辑 -->
      <div class="record-edit-section">
        <el-card class="record-card">
          <div slot="header">
            <span>学习记录</span>
          </div>

          <!-- 学习状态管理 -->
          <div class="form-section">
            <h3>学习状态</h3>
            <el-form :model="record" label-width="120px">
              <el-form-item label="完成状态">
                <el-radio-group v-model="record.completionStatus">
                  <el-radio label="not_started">未开始</el-radio>
                  <el-radio label="in_progress">进行中</el-radio>
                  <el-radio label="completed">已完成</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="解题状态">
                <el-switch
                  v-model="record.isSolved"
                  active-text="已解决"
                  inactive-text="未解决"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                />
              </el-form-item>

              <el-form-item label="尝试次数">
                <el-input-number
                  v-model="record.attemptCount"
                  :min="0"
                  :max="100"
                  controls-position="right"
                  style="width: 150px;"
                />
                <el-button
                  type="text"
                  style="margin-left: 10px;"
                  @click="incrementAttempt"
                >
                  +1 次尝试
                </el-button>
              </el-form-item>

              <el-form-item label="难度自评">
                <el-rate
                  v-model="record.difficultyRating"
                  :max="5"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  show-text
                  :texts="['非常简单', '简单', '一般', '困难', '非常困难']"
                />
              </el-form-item>

              <el-form-item label="学习时间">
                <el-input-number
                  v-model="record.timeSpentMinutes"
                  :min="0"
                  :max="1000"
                  controls-position="right"
                  style="width: 150px;"
                >
                  <template slot="append">分钟</template>
                </el-input-number>
              </el-form-item>

              <el-form-item label="复习标记">
                <el-button
                  type="primary"
                  plain
                  :disabled="record.completionStatus !== 'completed'"
                  @click="markAsReviewed"
                >
                  标记为已复习
                </el-button>
                <span v-if="record.completionStatus !== 'completed'" class="tip-text">
                  （需先标记为"已完成"）
                </span>
              </el-form-item>
            </el-form>
          </div>

          <!-- 学习笔记 -->
          <div class="form-section">
            <h3>学习笔记</h3>
            <el-input
              v-model="record.notes"
              type="textarea"
              :rows="8"
              placeholder="记录你的解题思路、关键点、易错点、心得体会等..."
              resize="none"
            />
            <div class="notes-tips">
              <p class="tip-text">提示：</p>
              <ul>
                <li>记录解题的核心思路</li>
                <li>标记关键代码片段</li>
                <li>总结易错点和注意事项</li>
                <li>记录相关知识点</li>
              </ul>
            </div>

            <!-- 保存按钮 -->
            <div class="save-button-container">
              <el-button
                type="primary"
                :loading="saving"
                size="medium"
                @click="saveRecord"
              >
                <i class="el-icon-check" /> 保存学习记录
              </el-button>
              <el-button
                type="success"
                :loading="saving"
                size="medium"
                style="margin-left: 10px;"
                @click="saveAndExit"
              >
                <i class="el-icon-check" /> 保存并退出
              </el-button>
              <el-button
                type="info"
                size="medium"
                style="margin-left: 10px;"
                @click="goBack"
              >
                <i class="el-icon-back" /> 直接返回
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
// 导入API接口
import { getProblemById } from '@/api/problem'
import { getProblemCompletionByUserAndProblem, saveProblemCompletion } from '@/api/problemCompletion'

export default {
  name: 'ProblemDetail',

  data() {
    return {
      problemId: null,
      problem: {
        problemId: '',
        title: '',
        difficulty: '',
        leetcodeLink: '',
        description: '',
        algorithmId: null
      },
      // 学习记录数据，对应 ProblemCompletion 表结构
      // 注意：字段名需要与后端实体JSON序列化结果一致（驼峰式）
      record: {
        completionStatus: 'not_started',
        attemptCount: 0,
        isSolved: false,
        notes: '',
        lastReviewDate: null,
        difficultyRating: 3,
        timeSpentMinutes: 0
      },
      saving: false,
      loading: false,
      // 当前用户ID - 这里先写死，后面从登录状态获取
      currentUserId: 1,
      // 标记是否已加载过学习记录
      hasLoadedRecord: false,
      // 标记是否是第一次加载（用于控制错误提示）
      isFirstLoad: true,
      // 来源页面，用于决定返回路径
      sourcePage: null
    }
  },

  created() {
    this.problemId = this.$route.params.id
    // 获取来源页面信息
    this.sourcePage = localStorage.getItem('problemDetailSource') || 'learning'
    console.log('来源页面:', this.sourcePage)

    // 获取当前用户ID
    const userInfoStr = localStorage.getItem('user-info')
    if (userInfoStr) {
      try {
        const userInfo = JSON.parse(userInfoStr)
        this.currentUserId = userInfo.userId || 1
        console.log('当前用户ID:', this.currentUserId)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        this.currentUserId = 1
      }
    }

    this.loadProblemData()
    this.loadRecord()
  },

  methods: {
    // 加载题目数据
    async loadProblemData() {
      this.loading = true
      try {
        // 优先从路由参数或本地存储获取题目信息
        const problemData = this.$route.params.problem || JSON.parse(localStorage.getItem('currentProblem'))
        if (problemData && problemData.problemId) {
          this.problem = problemData
        } else {
          // 调用后端API获取题目详情
          const response = await getProblemById(this.problemId)
          if (response) {
            this.problem = response
          } else {
            this.problem = {
              problemId: this.problemId,
              title: '未知题目',
              difficulty: 'medium',
              leetcodeLink: 'https://leetcode.cn/problems/',
              description: '暂无描述',
              algorithmId: null
            }
          }
        }
      } catch (error) {
        console.error('加载题目数据失败:', error)
        this.$message.error('加载题目数据失败')
        this.problem = {
          problemId: this.problemId,
          title: '未知题目',
          difficulty: 'medium',
          leetcodeLink: 'https://leetcode.cn/problems/',
          description: '暂无描述',
          algorithmId: null
        }
      } finally {
        this.loading = false
      }
    },

    // 加载学习记录
    async loadRecord() {
      this.loading = true
      try {
        // 调用后端API获取当前用户对该题目的学习记录
        // 使用正确的API：getProblemCompletionByUserAndProblem(userId, problemId)
        const response = await getProblemCompletionByUserAndProblem(this.currentUserId, this.problemId)
        if (response) {
          this.record = {
            ...this.record,
            ...response
          }
          this.hasLoadedRecord = true
          console.log('加载学习记录成功:', response)
        }
      } catch (error) {
        console.error('加载学习记录失败:', error)
        // 检查是否是404错误（未找到记录）
        const isNotFoundError = error.response && error.response.status === 404
        // 检查错误消息是否包含"未找到"或"not found"
        const errorMessage = error.message || ''
        const isNotFoundMessage = errorMessage.includes('未找到') || errorMessage.includes('not found') || errorMessage.includes('Not Found')

        // 如果是404错误或未找到记录的错误，不显示错误消息（这是正常情况）
        if (!(isNotFoundError || isNotFoundMessage)) {
          // 只有不是第一次加载时才显示错误消息
          if (!this.isFirstLoad) {
            this.$message.error('加载学习记录失败')
          }
        } else {
          console.log('未找到学习记录，将创建新记录')
        }
      } finally {
        this.loading = false
        this.isFirstLoad = false
      }
    },

    // 保存学习记录
    async saveRecord() {
      this.saving = true
      try {
        // 保存当前记录的状态，用于合并服务器返回的数据
        const currentRecord = { ...this.record }

        // 准备要保存的数据，对应 ProblemCompletion 表结构
        // 注意：字段名需要与后端实体JSON序列化结果一致（驼峰式）
        const recordData = {
          userId: this.currentUserId, // 当前用户ID
          problemId: this.problemId, // 题目ID
          completionStatus: this.record.completionStatus,
          attemptCount: this.record.attemptCount,
          isSolved: this.record.isSolved,
          notes: this.record.notes,
          lastReviewDate: this.record.lastReviewDate,
          difficultyRating: this.record.difficultyRating,
          timeSpentMinutes: this.record.timeSpentMinutes
        }

        // 调用后端API保存学习记录
        // 注意：axios拦截器已经做了拆壳处理，response直接就是data字段的内容
        const response = await saveProblemCompletion(recordData)
        if (response) {
          this.$message.success('保存成功')
          // 更新本地记录，但保留用户当前输入的数据
          // 合并服务器返回的数据和当前记录，确保用户输入不会丢失
          this.record = {
            ...response,
            // 确保以下字段不会被服务器返回的数据覆盖（如果服务器返回null或undefined）
            completionStatus: response.completionStatus || currentRecord.completionStatus,
            attemptCount: response.attemptCount !== undefined ? response.attemptCount : currentRecord.attemptCount,
            isSolved: response.isSolved !== undefined ? response.isSolved : currentRecord.isSolved,
            notes: response.notes || currentRecord.notes,
            lastReviewDate: response.lastReviewDate || currentRecord.lastReviewDate,
            difficultyRating: response.difficultyRating !== undefined ? response.difficultyRating : currentRecord.difficultyRating,
            timeSpentMinutes: response.timeSpentMinutes !== undefined ? response.timeSpentMinutes : currentRecord.timeSpentMinutes
          }
          this.hasLoadedRecord = true
          return true
        } else {
          this.$message.error('保存失败')
          return false
        }
      } catch (error) {
        console.error('保存失败:', error)
        this.$message.error('保存失败')
        return false
      } finally {
        this.saving = false
      }
    },

    // 保存并退出
    async saveAndExit() {
      const success = await this.saveRecord()
      if (success) {
        this.navigateBack()
      }
    },

    // 直接返回（不保存）
    goBack() {
      this.navigateBack()
    },

    // 根据来源页面返回上一页
    navigateBack() {
      if (this.sourcePage === 'userProblemRecords') {
        // 从题目学习记录页面来的，返回题目学习记录页面
        this.$router.push('/problem-records/index')
      } else if (this.sourcePage === 'userProfile') {
        // 从个人中心来的，返回个人中心
        this.$router.push('/user/profile')
      } else {
        // 其他情况（包括算法学习页面），返回上一页
        this.$router.go(-1)
      }
    },

    // 增加尝试次数
    incrementAttempt() {
      this.record.attemptCount += 1
    },

    // 标记为已复习
    markAsReviewed() {
      this.record.lastReviewDate = new Date().toISOString().split('T')[0]
      this.$message.success('已标记为复习')
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
      return {
        easy: '简单',
        medium: '中等',
        hard: '困难'
      }[difficulty] || difficulty
    },

    // 获取算法名称
    getAlgorithmName(tag) {
      const nameMap = {
        'graph-basics': '图论基础',
        'bfs': '广度优先搜索',
        'dfs': '深度优先搜索',
        'a-star': 'A*搜索算法'
      }
      return nameMap[tag] || tag
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '从未复习'
      return new Date(date).toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style scoped>
.problem-detail-container {
  padding: 20px;
  background-color: #f5f7fa ;
  min-height: 100%;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 20px;
}

.problem-info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-edit-section {
  display: flex;
  flex-direction: column;
}

/* 题目卡片样式 */
.problem-card {
  margin-bottom: 0;
}

.problem-card .clearfix {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.problem-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.difficulty-tag {
  font-size: 14px;
  padding: 4px 10px;
}

/* 题目元信息 */
.problem-meta {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.meta-label {
  font-size: 14px;
  color: #606266;
  min-width: 80px;
}

.meta-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.leetcode-link {
  font-size: 14px;
}

/* 题目描述 */
.problem-description h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 600;
}

.description-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

/* 统计卡片 */
.stats-card {
  margin-top: 0;
}

.stats-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  text-align: center;
}

.stat-item {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
  transition: all 0.3s;
}

.stat-item:hover {
  background-color: #e9ecef;
  transform: translateY(-2px);
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

/* 记录卡片 */
.record-card {
  height: 100%;
}

.form-section {
  margin-bottom: 30px;
}

.form-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 15px;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

/* 笔记区域 */
.notes-tips {
  margin-top: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.notes-tips .tip-text {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.notes-tips ul {
  margin: 5px 0 0 20px;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}

.notes-tips li {
  margin-bottom: 3px;
}

/* 提示文本 */
.tip-text {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}

/* 保存按钮容器 */
.save-button-container {
  margin-top: 20px;
  text-align: right;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .stats-content {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-content {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .meta-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .meta-label {
    margin-bottom: 5px;
  }
}
</style>
