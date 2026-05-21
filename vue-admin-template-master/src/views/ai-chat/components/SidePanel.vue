<template>
  <div class="side-panel">
    <!-- 面板标题 -->
    <div class="panel-header">
      <h3 class="panel-title">学习设置</h3>
      <p class="panel-subtitle">自定义您的学习体验</p>
    </div>

    <!-- 聊天模式选择 -->
    <div class="panel-section">
      <div class="section-header">
        <el-icon class="section-icon"><el-icon-chat-line-round /></el-icon>
        <span class="section-title">聊天模式</span>
      </div>

      <div class="section-content">
        <el-radio-group v-model="localChatMode" @change="handleModeChange">
          <el-radio-button value="explanation">
            <el-icon><el-icon-reading /></el-icon>
            算法解释
          </el-radio-button>
          <el-radio-button value="code">
            <el-icon><el-icon-code /></el-icon>
            代码示例
          </el-radio-button>
          <el-radio-button value="advice">
            <el-icon><el-icon-guide /></el-icon>
            学习建议
          </el-radio-button>
          <el-radio-button value="qa">
            <el-icon><el-icon-question /></el-icon>
            问题解答
          </el-radio-button>
        </el-radio-group>

        <div class="mode-description">
          {{ getModeDescription(localChatMode) }}
        </div>
      </div>
    </div>

    <!-- 算法类型选择 -->
    <div class="panel-section">
      <div class="section-header">
        <el-icon class="section-icon"><el-icon-cpu /></el-icon>
        <span class="section-title">算法类型</span>
      </div>

      <div class="section-content">
        <el-select
          v-model="localAlgorithmType"
          placeholder="选择算法"
          style="width: 100%"
          @change="handleAlgorithmChange"
        >
          <el-option label="所有算法" value="all" />
          <el-option label="广度优先搜索(BFS)" value="BFS" />
          <el-option label="深度优先搜索(DFS)" value="DFS" />
          <el-option label="Dijkstra算法" value="Dijkstra" />
          <el-option label="A*算法" value="AStar" />
        </el-select>

        <div class="algorithm-info" v-if="localAlgorithmType !== 'all'">
          <div class="info-item">
            <span class="info-label">时间复杂度：</span>
            <span class="info-value">{{ getAlgorithmComplexity(localAlgorithmType) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">应用场景：</span>
            <span class="info-value">{{ getAlgorithmApplication(localAlgorithmType) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 难度级别选择 -->
    <div class="panel-section">
      <div class="section-header">
        <el-icon class="section-icon"><el-icon-trend-charts /></el-icon>
        <span class="section-title">难度级别</span>
      </div>

      <div class="section-content">
        <el-radio-group v-model="localDifficultyLevel" @change="handleDifficultyChange">
          <el-radio-button value="beginner">
            <el-icon><el-icon-star /></el-icon>
            初级
          </el-radio-button>
          <el-radio-button value="intermediate">
            <el-icon><el-icon-star /></el-icon>
            <el-icon><el-icon-star /></el-icon>
            中级
          </el-radio-button>
          <el-radio-button value="advanced">
            <el-icon><el-icon-star /></el-icon>
            <el-icon><el-icon-star /></el-icon>
            <el-icon><el-icon-star /></el-icon>
            高级
          </el-radio-button>
        </el-radio-group>

        <div class="difficulty-description">
          {{ getDifficultyDescription(localDifficultyLevel) }}
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="panel-section actions-section">
      <div class="section-content">
        <!-- 清空聊天记录 -->
        <el-button
          type="danger"
          plain
          style="width: 100%; margin-bottom: 12px;"
          @click="handleClearChat"
        >
          <el-icon><el-icon-delete /></el-icon>
          清空聊天记录
        </el-button>

        <!-- 导出聊天记录 -->
        <el-button
          type="info"
          plain
          style="width: 100%; margin-bottom: 12px;"
          @click="handleExportChat"
        >
          <el-icon><el-icon-download /></el-icon>
          导出聊天记录
        </el-button>

        <!-- 学习统计 -->
        <div class="learning-stats">
          <div class="stat-item">
            <span class="stat-label">今日对话：</span>
            <span class="stat-value">{{ todayMessages }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">累计学习：</span>
            <span class="stat-value">{{ totalMessages }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习提示 -->
    <div class="panel-section tips-section">
      <div class="section-header">
        <el-icon class="section-icon"><el-icon-lightbulb /></el-icon>
        <span class="section-title">学习提示</span>
      </div>

      <div class="section-content">
        <ul class="tips-list">
          <li>尝试不同模式来获得全面的学习体验</li>
          <li>从简单算法开始，逐步挑战更复杂的算法</li>
          <li>结合代码示例和实践练习效果更好</li>
          <li>遇到困难时可以降低难度级别</li>
          <li>定期复习已学内容巩固知识</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SidePanel',

  props: {
    chatMode: {
      type: String,
      default: 'explanation'
    },
    algorithmType: {
      type: String,
      default: 'all'
    },
    difficultyLevel: {
      type: String,
      default: 'beginner'
    }
  },

  data() {
    return {
      localChatMode: this.chatMode,
      localAlgorithmType: this.algorithmType,
      localDifficultyLevel: this.difficultyLevel,
      todayMessages: 0,
      totalMessages: 0
    }
  },

  watch: {
    chatMode(newVal) {
      this.localChatMode = newVal
    },
    algorithmType(newVal) {
      this.localAlgorithmType = newVal
    },
    difficultyLevel(newVal) {
      this.localDifficultyLevel = newVal
    }
  },

  mounted() {
    this.loadStatistics()
  },

  methods: {
    // 加载统计信息
    loadStatistics() {
      try {
        const savedMessages = localStorage.getItem('ai_chat_history')
        if (savedMessages) {
          const messages = JSON.parse(savedMessages)
          this.totalMessages = messages.length

          // 计算今日消息
          const today = new Date().toDateString()
          this.todayMessages = messages.filter(msg => {
            const msgDate = new Date(msg.timestamp).toDateString()
            return msgDate === today
          }).length
        }
      } catch (error) {
        console.error('加载统计信息失败:', error)
      }
    },

    // 处理模式变更
    handleModeChange(mode) {
      this.localChatMode = mode
      this.$emit('mode-change', mode)
    },

    // 处理算法变更
    handleAlgorithmChange(algorithm) {
      this.localAlgorithmType = algorithm
      this.$emit('algorithm-change', algorithm)
    },

    // 处理难度变更
    handleDifficultyChange(difficulty) {
      this.localDifficultyLevel = difficulty
      this.$emit('difficulty-change', difficulty)
    },

    // 处理清空聊天
    handleClearChat() {
      this.$emit('clear-chat')
      this.loadStatistics() // 重新加载统计
    },

    // 处理导出聊天
    handleExportChat() {
      try {
        const savedMessages = localStorage.getItem('ai_chat_history')
        if (!savedMessages) {
          this.$message.warning('没有聊天记录可以导出')
          return
        }

        const messages = JSON.parse(savedMessages)
        const exportData = {
          exportTime: new Date().toISOString(),
          totalMessages: messages.length,
          messages: messages
        }

        const blob = new Blob([JSON.stringify(exportData, null, 2)], {
          type: 'application/json'
        })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `ai_chat_export_${new Date().getTime()}.json`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)

        this.$message.success('聊天记录导出成功')
      } catch (error) {
        console.error('导出聊天记录失败:', error)
        this.$message.error('导出聊天记录失败')
      }
    },

    // 获取模式描述
    getModeDescription(mode) {
      const descriptions = {
        explanation: '详细解释算法原理、思想和应用场景',
        code: '提供多种语言的代码实现示例',
        advice: '根据您的水平推荐学习路径和资源',
        qa: '解答您在学习和实践中遇到的问题'
      }
      return descriptions[mode] || '选择适合您的学习模式'
    },

    // 获取算法复杂度
    getAlgorithmComplexity(algorithm) {
      const complexities = {
        BFS: 'O(V+E)',
        DFS: 'O(V+E)',
        Dijkstra: 'O((V+E)logV)',
        AStar: 'O(b^d)'
      }
      return complexities[algorithm] || '根据实现而定'
    },

    // 获取算法应用
    getAlgorithmApplication(algorithm) {
      const applications = {
        BFS: '最短路径、连通性检测',
        DFS: '拓扑排序、路径查找',
        Dijkstra: '带权图最短路径',
        AStar: '游戏AI、机器人导航'
      }
      return applications[algorithm] || '路径搜索问题'
    },

    // 获取难度描述
    getDifficultyDescription(difficulty) {
      const descriptions = {
        beginner: '适合初学者，提供基础概念和简单示例',
        intermediate: '适合有一定基础的学习者，包含进阶内容',
        advanced: '适合高级学习者，包含优化技巧和复杂应用'
      }
      return descriptions[difficulty] || '选择适合您的难度级别'
    }
  }
}
</script>

<style scoped>
.side-panel {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

/* 面板标题 */
.panel-header {
  margin-bottom: 24px;
  text-align: center;
}

.panel-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 4px;
  font-weight: 600;
}

.panel-subtitle {
  font-size: 12px;
  color: #909399;
}

/* 面板区域 */
.panel-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.panel-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

/* 区域头部 */
.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.section-icon {
  font-size: 16px;
  color: #409eff;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

/* 区域内容 */
.section-content {
  padding-left: 24px;
}

/* 模式描述 */
.mode-description {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

/* 算法信息 */
.algorithm-info {
  margin-top: 12px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #606266;
  font-weight: 500;
}

.info-value {
  color: #303133;
  text-align: right;
  max-width: 60%;
}

/* 难度描述 */
.difficulty-description {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #67c23a;
}

/* 操作按钮区域 */
.actions-section {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin: 20px -16px;
  border: 1px solid #e9ecef;
}

/* 学习统计 */
.learning-stats {
  margin-top: 16px;
  padding: 12px;
  background-color: #fff;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.stat-item:last-child {
  margin-bottom: 0;
}

.stat-label {
  color: #606266;
}

.stat-value {
  color: #409eff;
  font-weight: 600;
}

/* 学习提示区域 */
.tips-section {
  background-color: #f0f9ff;
  border-radius: 8px;
  padding: 16px;
  margin-top: 20px;
  border: 1px solid #d9ecff;
}

.tips-list {
  margin: 0;
  padding-left: 18px;
}

.tips-list li {
  margin-bottom: 6px;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* 单选按钮组样式 */
:deep(.el-radio-group) {
  width: 100%;
}

:deep(.el-radio-button) {
  flex: 1;
}

:deep(.el-radio-button__inner) {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px 4px;
  height: auto;
  min-height: 60px;
}

:deep(.el-radio-button__inner .el-icon) {
  font-size: 18px;
  margin-bottom: 4px;
}

/* 选择框样式 */
:deep(.el-select) {
  width: 100%;
}

/* 按钮样式 */
:deep(.el-button) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .side-panel {
    padding: 16px;
  }

  .panel-section {
    margin-bottom: 20px;
    padding-bottom: 16px;
  }

  .section-content {
    padding-left: 20px;
  }

  :deep(.el-radio-button__inner) {
    min-height: 50px;
    font-size: 12px;
  }

  :deep(.el-radio-button__inner .el-icon) {
    font-size: 16px;
  }
}
</style>
