<template>
  <div class="ai-chat-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>AI学习助手</h1>
      <p class="page-description">与AI助手对话，深入学习路径寻路算法</p>
    </div>

    <!-- 主内容区域 -->
    <div class="chat-main-content">
      <!-- 侧边控制面板 -->
      <div class="side-panel-container">
        <side-panel
          :chat-mode="chatMode"
          :algorithm-type="algorithmType"
          :difficulty-level="difficultyLevel"
          @mode-change="handleModeChange"
          @algorithm-change="handleAlgorithmChange"
          @difficulty-change="handleDifficultyChange"
          @clear-chat="handleClearChat"
        />
      </div>

      <!-- 聊天区域 -->
      <div class="chat-area-container">
        <!-- 聊天消息列表 -->
        <div class="chat-messages" ref="chatMessages">
          <!-- 欢迎消息 -->
          <div class="welcome-message">
            <div class="welcome-content">
              <el-icon class="welcome-icon"><el-icon-chat-dot-round /></el-icon>
              <h3>欢迎使用AI学习助手！</h3>
              <p>我是您的路径寻路算法学习助手，我可以帮助您：</p>
              <ul class="welcome-features">
                <li>解释算法原理和实现</li>
                <li>提供代码示例和优化建议</li>
                <li>解答学习过程中的疑问</li>
                <li>推荐学习路径和练习题</li>
              </ul>
              <p class="welcome-tip">请从左侧选择学习模式开始对话</p>
            </div>
          </div>

          <!-- 聊天消息列表 -->
          <chat-message
            v-for="(message, index) in messages"
            :key="index"
            :message="message"
            :is-user="message.role === 'user'"
          />

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-message">
            <div class="loading-content">
              <el-icon class="loading-icon"><el-icon-loading /></el-icon>
              <span>AI正在思考中...</span>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-container">
          <chat-input
            :disabled="loading"
            @send-message="handleSendMessage"
            @quick-question="handleQuickQuestion"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SidePanel from './components/SidePanel.vue'
import ChatMessage from './components/ChatMessage.vue'
import ChatInput from './components/ChatInput.vue'
import { ElIcon } from 'element-ui'

export default {
  name: 'AIChat',

  components: {
    SidePanel,
    ChatMessage,
    ChatInput,
    ElIcon
  },

  data() {
    return {
      // 聊天消息列表
      messages: [],

      // 加载状态
      loading: false,

      // 聊天模式
      chatMode: 'explanation',

      // 算法类型
      algorithmType: 'all',

      // 难度级别
      difficultyLevel: 'beginner',

      // 快捷问题列表
      quickQuestions: [
        {
          id: 1,
          text: '请解释BFS算法的基本原理',
          mode: 'explanation',
          algorithm: 'BFS'
        },
        {
          id: 2,
          text: '给我一个DFS算法的Python实现示例',
          mode: 'code',
          algorithm: 'DFS'
        },
        {
          id: 3,
          text: '学习A*算法的最佳路径是什么？',
          mode: 'advice',
          algorithm: 'A*'
        },
        {
          id: 4,
          text: 'Dijkstra和A*算法有什么区别？',
          mode: 'explanation',
          algorithm: 'all'
        }
      ]
    }
  },

  mounted() {
    // 加载历史消息（如果有）
    this.loadHistory()
  },

  methods: {
    // 加载历史消息
    loadHistory() {
      const savedMessages = localStorage.getItem('ai_chat_history')
      if (savedMessages) {
        try {
          this.messages = JSON.parse(savedMessages)
        } catch (error) {
          console.error('加载聊天历史失败:', error)
        }
      }
    },

    // 保存消息到本地存储
    saveHistory() {
      try {
        localStorage.setItem('ai_chat_history', JSON.stringify(this.messages))
      } catch (error) {
        console.error('保存聊天历史失败:', error)
      }
    },

    // 处理模式变更
    handleModeChange(mode) {
      this.chatMode = mode
      this.addSystemMessage(`已切换到${this.getModeName(mode)}模式`)
    },

    // 处理算法变更
    handleAlgorithmChange(algorithm) {
      this.algorithmType = algorithm
      this.addSystemMessage(`已选择${this.getAlgorithmName(algorithm)}算法`)
    },

    // 处理难度变更
    handleDifficultyChange(difficulty) {
      this.difficultyLevel = difficulty
      this.addSystemMessage(`已设置难度为${this.getDifficultyName(difficulty)}`)
    },

    // 处理清空聊天
    handleClearChat() {
      this.$confirm('确定要清空所有聊天记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.messages = []
        localStorage.removeItem('ai_chat_history')
        this.$message.success('聊天记录已清空')
      })
    },

    // 处理发送消息
    async handleSendMessage(content) {
      if (!content.trim()) return

      // 添加用户消息
      const userMessage = {
        id: Date.now(),
        role: 'user',
        content: content.trim(),
        timestamp: new Date(),
        mode: this.chatMode,
        algorithm: this.algorithmType,
        difficulty: this.difficultyLevel
      }

      this.messages.push(userMessage)
      this.saveHistory()
      this.scrollToBottom()

      // 模拟AI回复（实际应该调用API）
      this.loading = true
      await this.simulateAIResponse(content)
      this.loading = false
      this.scrollToBottom()
    },

    // 处理快捷问题
    handleQuickQuestion(question) {
      this.handleSendMessage(question.text)
    },

    // 模拟AI回复
    simulateAIResponse(userMessage) {
      return new Promise(resolve => {
        setTimeout(() => {
          const responses = {
            explanation: `我已经理解了您的问题。关于${this.getAlgorithmName(this.algorithmType)}算法，我可以从以下几个方面为您解释：

1. **算法原理**：${this.getAlgorithmName(this.algorithmType)}是一种${this.getAlgorithmDescription(this.algorithmType)}

2. **核心思想**：${this.getAlgorithmCoreIdea(this.algorithmType)}

3. **时间复杂度**：${this.getAlgorithmComplexity(this.algorithmType)}

4. **应用场景**：${this.getAlgorithmApplication(this.algorithmType)}

您希望我详细讲解哪个部分？或者有其他相关问题吗？`,

            code: `以下是一个${this.getAlgorithmName(this.algorithmType)}算法的${this.getLanguageByDifficulty()}实现示例：

\`\`\`${this.getLanguageByDifficulty()}
${this.getCodeExample(this.algorithmType, this.difficultyLevel)}
\`\`\`

**代码说明**：
${this.getCodeExplanation(this.algorithmType)}

**使用示例**：
${this.getUsageExample(this.algorithmType)}

您对这段代码有什么疑问吗？或者需要其他语言的实现？`,

            advice: `基于您当前的学习情况（${this.getDifficultyName(this.difficultyLevel)}级别，学习${this.getAlgorithmName(this.algorithmType)}算法），我为您推荐以下学习路径：

1. **基础知识**：
   - 理解图的基本概念
   - 学习${this.getPrerequisiteAlgorithms(this.algorithmType)}
   - 掌握${this.getAlgorithmName(this.algorithmType)}的核心思想

2. **实践练习**：
   - 从简单题目开始，逐步增加难度
   - 尝试实现算法的不同变体
   - 分析算法的时间和空间复杂度

3. **深入学习**：
   - 研究算法的优化方法
   - 学习相关算法的比较和选择
   - 解决实际应用问题

4. **推荐资源**：
   - 经典教材和在线课程
   - LeetCode相关题目
   - 开源项目代码学习

您希望我从哪个方面开始详细指导？`,

            qa: `感谢您的提问！关于"${userMessage}"，我的回答如下：

${this.getQAAnswer(userMessage)}

**关键要点**：
${this.getQAKeyPoints(userMessage)}

**常见误区**：
${this.getQAMisconceptions(userMessage)}

**扩展学习**：
${this.getQAExtensions(userMessage)}

这个回答对您有帮助吗？如果有其他问题，请随时提问。`
          }

          const aiMessage = {
            id: Date.now() + 1,
            role: 'assistant',
            content: responses[this.chatMode] || '我目前专注于路径寻路算法的学习指导。请问您想了解哪个算法的相关内容？',
            timestamp: new Date(),
            mode: this.chatMode,
            algorithm: this.algorithmType,
            difficulty: this.difficultyLevel
          }

          this.messages.push(aiMessage)
          this.saveHistory()
          resolve()
        }, 1500) // 模拟1.5秒延迟
      })
    },

    // 添加系统消息
    addSystemMessage(content) {
      const systemMessage = {
        id: Date.now(),
        role: 'system',
        content: content,
        timestamp: new Date()
      }
      this.messages.push(systemMessage)
      this.saveHistory()
      this.scrollToBottom()
    },

    // 滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatMessages
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    // 获取模式名称
    getModeName(mode) {
      const modeNames = {
        explanation: '算法解释',
        code: '代码示例',
        advice: '学习建议',
        qa: '问题解答'
      }
      return modeNames[mode] || mode
    },

    // 获取算法名称
    getAlgorithmName(algorithm) {
      const algorithmNames = {
        all: '所有算法',
        BFS: '广度优先搜索(BFS)',
        DFS: '深度优先搜索(DFS)',
        Dijkstra: 'Dijkstra算法',
        AStar: 'A*算法'
      }
      return algorithmNames[algorithm] || algorithm
    },

    // 获取算法描述
    getAlgorithmDescription(algorithm) {
      const descriptions = {
        BFS: '基于队列的图遍历算法，逐层扩展搜索',
        DFS: '基于栈或递归的图遍历算法，深度优先搜索',
        Dijkstra: '单源最短路径算法，使用贪心策略',
        AStar: '启发式搜索算法，结合Dijkstra和最佳优先搜索'
      }
      return descriptions[algorithm] || '路径寻路算法'
    },

    // 获取算法核心思想
    getAlgorithmCoreIdea(algorithm) {
      const ideas = {
        BFS: '从起点开始，逐层向外扩展，先访问距离起点近的节点',
        DFS: '沿着一条路径深入搜索到底，然后回溯继续搜索',
        Dijkstra: '维护一个到起点的最短距离集合，每次选择距离最小的节点扩展',
        AStar: '使用估价函数 f(n)=g(n)+h(n) 指导搜索方向，g(n)是实际代价，h(n)是启发函数'
      }
      return ideas[algorithm] || '根据具体算法特性而定'
    },

    // 获取算法复杂度
    getAlgorithmComplexity(algorithm) {
      const complexities = {
        BFS: '时间复杂度O(V+E)，空间复杂度O(V)',
        DFS: '时间复杂度O(V+E)，空间复杂度O(V)',
        Dijkstra: '时间复杂度O((V+E)logV)（使用优先队列），空间复杂度O(V)',
        AStar: '时间复杂度取决于启发函数质量，最坏情况O(b^d)，空间复杂度O(V)'
      }
      return complexities[algorithm] || '根据具体实现而定'
    },

    // 获取算法应用
    getAlgorithmApplication(algorithm) {
      const applications = {
        BFS: '最短路径问题（无权图）、连通性检测、层级遍历',
        DFS: '拓扑排序、连通分量、路径查找、回溯问题',
        Dijkstra: '带权图的最短路径、网络路由、地图导航',
        AStar: '游戏AI路径规划、机器人导航、地图寻路'
      }
      return applications[algorithm] || '路径搜索和图遍历问题'
    },

    // 根据难度获取编程语言
    getLanguageByDifficulty() {
      const languages = {
        beginner: 'Python',
        intermediate: 'Java',
        advanced: 'C++'
      }
      return languages[this.difficultyLevel] || 'Python'
    },

    // 获取代码示例
    getCodeExample(algorithm, difficulty) {
      // 这里应该返回实际的代码示例
      // 为了简化，返回一个通用模板
      return `// ${this.getAlgorithmName(algorithm)} 算法实现
// 难度：${this.getDifficultyName(difficulty)}
// 语言：${this.getLanguageByDifficulty()}

// 这里应该是具体的代码实现
// 实际开发中需要根据算法和难度生成不同的代码`
    },

    // 获取代码解释
    getCodeExplanation(algorithm) {
      return `1. 算法初始化：设置起点和数据结构
2. 核心循环：按照算法特性进行节点扩展
3. 终止条件：找到目标或遍历完所有节点
4. 结果返回：路径或搜索状态`
    },

    // 获取使用示例
    getUsageExample(algorithm) {
      return `// 创建图或网格
// 设置起点和终点
// 调用算法函数
// 获取并处理结果`
    },

    // 获取先修算法
    getPrerequisiteAlgorithms(algorithm) {
      const prerequisites = {
        BFS: '图的基本概念、队列数据结构',
        DFS: '递归思想、栈数据结构',
        Dijkstra: '图论基础、优先队列、贪心算法',
        AStar: 'Dijkstra算法、启发式搜索概念'
      }
      return prerequisites[algorithm] || '基础数据结构和算法知识'
    },

    // 获取难度名称
    getDifficultyName(difficulty) {
      const names = {
        beginner: '初级',
        intermediate: '中级',
        advanced: '高级'
      }
      return names[difficulty] || difficulty
    },

    // 获取QA回答
    getQAAnswer(question) {
      return '这是一个关于路径寻路算法的专业问题。基于我的知识库，我可以为您提供详细的解答。'
    },

    // 获取QA关键点
    getQAKeyPoints(question) {
      return '- 理解算法核心思想\n- 掌握实现细节\n- 分析时间空间复杂度\n- 了解应用场景'
    },

    // 获取QA常见误区
    getQAMisconceptions(question) {
      return '- 混淆不同算法的适用场景\n- 错误估计算法复杂度\n- 忽略边界条件处理\n- 不理解算法优化方法'
    },

    // 获取QA扩展学习
    getQAExtensions(question) {
      return '- 学习相关算法的变体\n- 研究实际应用案例\n- 参与开源项目\n- 阅读学术论文'
    }
  }
}
</script>

<style scoped>
.ai-chat-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
  display: flex;
  flex-direction: column;
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

/* 主内容区域 */
.chat-main-content {
  display: flex;
  flex: 1;
  gap: 20px;
  min-height: 600px;
}

/* 侧边面板容器 */
.side-panel-container {
  width: 280px;
  flex-shrink: 0;
}

/* 聊天区域容器 */
.chat-area-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 聊天消息区域 */
.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  max-height: 500px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  text-align: center;
}

.welcome-content {
  max-width: 500px;
  padding: 40px;
  background-color: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.welcome-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 20px;
}

.welcome-content h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 16px;
}

.welcome-content p {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.6;
}

.welcome-features {
  text-align: left;
  margin: 16px 0;
  padding-left: 20px;
}

.welcome-features li {
  margin-bottom: 8px;
  color: #606266;
  line-height: 1.5;
}

.welcome-features li:last-child {
  margin-bottom: 0;
}

.welcome-tip {
  font-size: 13px;
  color: #909399;
  font-style: italic;
  margin-top: 16px;
}

/* 加载消息 */
.loading-message {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 24px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.loading-icon {
  font-size: 18px;
  color: #409eff;
  animation: rotate 1.5s linear infinite;
}

.loading-content span {
  font-size: 14px;
  color: #606266;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 聊天输入容器 */
.chat-input-container {
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background-color: #fff;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .chat-main-content {
    flex-direction: column;
  }

  .side-panel-container {
    width: 100%;
    margin-bottom: 20px;
  }

  .chat-messages {
    max-height: 400px;
  }
}

  .ai-chat-container {
    padding: 15px;
  }

  .welcome-content {
    padding: 30px 20px;
  }

  .welcome-icon {
    font-size: 36px;
  }

  .welcome-content h3 {
    font-size: 18px;
  }

  .chat-messages {
    padding: 15px;
    max-height: 350px;
  }

  .chat-input-container {
    padding: 15px;
  }
</style>
