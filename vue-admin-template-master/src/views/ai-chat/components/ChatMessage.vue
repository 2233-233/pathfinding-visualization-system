<template>
  <div :class="['chat-message', { 'user-message': isUser, 'ai-message': !isUser }]">
    <!-- 头像 -->
    <div class="message-avatar">
      <el-avatar :size="36" :src="avatarUrl" :icon="avatarIcon" />
    </div>

    <!-- 消息内容 -->
    <div class="message-content">
      <!-- 消息头部 -->
      <div class="message-header">
        <span class="message-sender">{{ senderName }}</span>
        <span class="message-time">{{ formattedTime }}</span>
      </div>

      <!-- 消息主体 -->
      <div class="message-body">
        <!-- 文本内容 -->
        <div class="message-text" v-html="formattedContent"></div>

        <!-- 消息元信息 -->
        <div v-if="message.mode || message.algorithm" class="message-meta">
          <el-tag v-if="message.mode" size="mini" :type="getModeTagType(message.mode)">
            {{ getModeName(message.mode) }}
          </el-tag>
          <el-tag v-if="message.algorithm && message.algorithm !== 'all'" size="mini" type="info">
            {{ getAlgorithmName(message.algorithm) }}
          </el-tag>
          <el-tag v-if="message.difficulty" size="mini" :type="getDifficultyTagType(message.difficulty)">
            {{ getDifficultyName(message.difficulty) }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { formatTime } from '@/utils'

export default {
  name: 'ChatMessage',

  props: {
    message: {
      type: Object,
      required: true
    },
    isUser: {
      type: Boolean,
      default: false
    }
  },

  computed: {
    // 头像URL
    avatarUrl() {
      if (this.isUser) {
        return ''
      } else {
        return ''
      }
    },

    // 头像图标
    avatarIcon() {
      if (this.isUser) {
        return 'el-icon-user'
      } else {
        return 'el-icon-chat-dot-round'
      }
    },

    // 发送者名称
    senderName() {
      if (this.isUser) {
        return '您'
      } else if (this.message.role === 'system') {
        return '系统'
      } else {
        return 'AI助手'
      }
    },

    // 格式化时间
    formattedTime() {
      if (this.message.timestamp) {
        return formatTime(this.message.timestamp, '{h}:{i}:{s}')
      }
      return ''
    },

    // 格式化内容（支持Markdown和代码高亮）
    formattedContent() {
      let content = this.message.content || ''

      // 转义HTML
      content = content
        .replace(/&/g, '&')
        .replace(/</g, '<')
        .replace(/>/g, '>')
        .replace(/"/g, '"')
        .replace(/'/g, '&#039;')

      // 处理代码块
      content = content.replace(/```(\w+)?\n([\s\S]*?)```/g, (match, lang, code) => {
        const language = lang || 'text'
        return `<pre class="code-block"><code class="language-${language}">${this.escapeHtml(code)}</code></pre>`
      })

      // 处理行内代码
      content = content.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>')

      // 处理粗体
      content = content.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')

      // 处理斜体
      content = content.replace(/\*([^*]+)\*/g, '<em>$1</em>')

      // 处理列表
      content = content.replace(/^\s*[-*+]\s+(.+)$/gm, '<li>$1</li>')
      content = content.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')

      // 处理数字列表
      content = content.replace(/^\s*\d+\.\s+(.+)$/gm, '<li>$1</li>')
      content = content.replace(/(<li>.*<\/li>)/s, '<ol>$1</ol>')

      // 处理换行
      content = content.replace(/\n/g, '<br>')

      return content
    }
  },

  methods: {
    // 转义HTML
    escapeHtml(text) {
      return text
        .replace(/&/g, '&')
        .replace(/</g, '<')
        .replace(/>/g, '>')
        .replace(/"/g, '"')
        .replace(/'/g, '&#039;')
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

    // 获取模式标签类型
    getModeTagType(mode) {
      const types = {
        explanation: 'primary',
        code: 'success',
        advice: 'warning',
        qa: 'danger'
      }
      return types[mode] || 'info'
    },

    // 获取算法名称
    getAlgorithmName(algorithm) {
      const algorithmNames = {
        BFS: 'BFS',
        DFS: 'DFS',
        Dijkstra: 'Dijkstra',
        AStar: 'A*'
      }
      return algorithmNames[algorithm] || algorithm
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

    // 获取难度标签类型
    getDifficultyTagType(difficulty) {
      const types = {
        beginner: 'success',
        intermediate: 'warning',
        advanced: 'danger'
      }
      return types[difficulty] || 'info'
    }
  }
}
</script>

<style scoped>
.chat-message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 用户消息 */
.user-message {
  flex-direction: row-reverse;
}

.user-message .message-content {
  align-items: flex-end;
}

.user-message .message-body {
  background-color: #409eff;
  color: white;
  border-radius: 12px 12px 0 12px;
}

/* AI消息 */
.ai-message .message-body {
  background-color: #f0f2f5;
  color: #303133;
  border-radius: 12px 12px 12px 0;
}

/* 系统消息 */
.ai-message.system-message .message-body {
  background-color: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fde2e2;
}

/* 消息头像 */
.message-avatar {
  flex-shrink: 0;
}

/* 消息内容区域 */
.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

/* 消息头部 */
.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
}

.message-sender {
  font-weight: 600;
  color: #606266;
}

.message-time {
  color: #909399;
}

/* 消息主体 */
.message-body {
  padding: 12px 16px;
  line-height: 1.6;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

/* 消息文本 */
.message-text {
  font-size: 14px;
}

.message-text >>> .code-block {
  margin: 8px 0;
  padding: 12px;
  background-color: #2d2d2d;
  color: #f8f8f2;
  border-radius: 6px;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
}

.message-text >>> .inline-code {
  padding: 2px 6px;
  background-color: #f0f2f5;
  color: #e74c3c;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
}

.message-text >>> strong {
  font-weight: 600;
  color: #303133;
}

.message-text >>> em {
  font-style: italic;
  color: #606266;
}

.message-text >>> ul,
.message-text >>> ol {
  margin: 8px 0;
  padding-left: 20px;
}

.message-text >>> li {
  margin-bottom: 4px;
}

/* 消息元信息 */
.message-meta {
  margin-top: 8px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }

  .message-body {
    padding: 10px 14px;
  }

  .message-text {
    font-size: 13px;
  }
}
</style>
