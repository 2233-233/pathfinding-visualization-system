<template>
  <div class="chat-input">
    <!-- 快捷问题 -->
    <div v-if="showQuickQuestions" class="quick-questions">
      <div class="quick-questions-header">
        <span class="quick-questions-title">快捷问题</span>
        <el-button type="text" size="mini" @click="toggleQuickQuestions">
          {{ showQuickQuestionsList ? '收起' : '展开' }}
        </el-button>
      </div>

      <div v-if="showQuickQuestionsList" class="quick-questions-list">
        <el-button
          v-for="question in quickQuestions"
          :key="question.id"
          class="quick-question-btn"
          size="small"
          @click="handleQuickQuestionClick(question)"
        >
          {{ question.text }}
        </el-button>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <!-- 文本输入框 -->
      <el-input
        ref="inputRef"
        v-model="inputText"
        type="textarea"
        :rows="3"
        :maxlength="1000"
        :disabled="disabled"
        placeholder="请输入您的问题，或从上方选择快捷问题..."
        resize="none"
        @keydown.enter.exact.prevent="handleEnterKey"
        @keydown.enter.shift.exact="handleShiftEnter"
      >
        <template #append>
          <!-- 发送按钮 -->
          <el-button
            class="send-btn"
            type="primary"
            :disabled="!canSend"
            :loading="sending"
            @click="handleSend"
          >
            <el-icon class="send-icon"><el-icon-send /></el-icon>
            发送
          </el-button>
        </template>
      </el-input>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <!-- 清空按钮 -->
        <el-button
          size="small"
          :disabled="!inputText"
          @click="handleClear"
        >
          <el-icon><el-icon-delete /></el-icon>
          清空
        </el-button>

        <!-- 快捷问题开关 -->
        <el-button
          size="small"
          @click="toggleQuickQuestions"
        >
          <el-icon><el-icon-collection /></el-icon>
          {{ showQuickQuestions ? '隐藏快捷问题' : '显示快捷问题' }}
        </el-button>

        <!-- 字符计数 -->
        <span class="char-count">
          {{ inputText.length }}/1000
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatInput',

  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      // 输入文本
      inputText: '',

      // 发送状态
      sending: false,

      // 是否显示快捷问题
      showQuickQuestions: true,

      // 是否显示快捷问题列表
      showQuickQuestionsList: true,

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
        },
        {
          id: 5,
          text: '如何优化寻路算法的性能？',
          mode: 'advice',
          algorithm: 'all'
        },
        {
          id: 6,
          text: '请给我一个BFS算法的Java代码示例',
          mode: 'code',
          algorithm: 'BFS'
        }
      ]
    }
  },

  computed: {
    // 是否可以发送
    canSend() {
      return this.inputText.trim().length > 0 && !this.disabled && !this.sending
    }
  },

  mounted() {
    // 加载设置
    this.loadSettings()
  },

  methods: {
    // 加载设置
    loadSettings() {
      const savedSetting = localStorage.getItem('ai_chat_show_quick_questions')
      if (savedSetting !== null) {
        this.showQuickQuestions = JSON.parse(savedSetting)
        this.showQuickQuestionsList = this.showQuickQuestions
      }
    },

    // 保存设置
    saveSettings() {
      localStorage.setItem('ai_chat_show_quick_questions', JSON.stringify(this.showQuickQuestions))
    },

    // 切换快捷问题显示
    toggleQuickQuestions() {
      this.showQuickQuestions = !this.showQuickQuestions
      this.showQuickQuestionsList = this.showQuickQuestions
      this.saveSettings()
    },

    // 处理快捷问题点击
    handleQuickQuestionClick(question) {
      this.inputText = question.text
      this.$emit('quick-question', question)
      this.$nextTick(() => {
        this.$refs.inputRef.focus()
      })
    },

    // 处理发送
    handleSend() {
      if (!this.canSend) return

      const text = this.inputText.trim()
      if (!text) return

      this.sending = true

      // 发送消息
      this.$emit('send-message', text)

      // 清空输入框
      this.inputText = ''

      // 重置发送状态
      setTimeout(() => {
        this.sending = false
        this.$nextTick(() => {
          this.$refs.inputRef.focus()
        })
      }, 300)
    },

    // 处理清空
    handleClear() {
      this.inputText = ''
      this.$nextTick(() => {
        this.$refs.inputRef.focus()
      })
    },

    // 处理回车键
    handleEnterKey() {
      if (!this.disabled && !this.sending) {
        this.handleSend()
      }
    },

    // 处理Shift+回车键
    handleShiftEnter() {
      // 插入换行
      const textarea = this.$refs.inputRef.$el.querySelector('textarea')
      if (textarea) {
        const start = textarea.selectionStart
        const end = textarea.selectionEnd
        this.inputText = this.inputText.substring(0, start) + '\n' + this.inputText.substring(end)
        this.$nextTick(() => {
          textarea.selectionStart = textarea.selectionEnd = start + 1
        })
      }
    },

    // 聚焦输入框
    focus() {
      this.$nextTick(() => {
        if (this.$refs.inputRef) {
          this.$refs.inputRef.focus()
        }
      })
    }
  }
}
</script>

<style scoped>
.chat-input {
  width: 100%;
}

/* 快捷问题区域 */
.quick-questions {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.quick-questions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.quick-questions-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

/* 快捷问题列表 */
.quick-questions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-question-btn {
  flex: 1;
  min-width: 200px;
  text-align: left;
  white-space: normal;
  height: auto;
  padding: 8px 12px;
  line-height: 1.4;
}

.quick-question-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

/* 输入区域 */
.input-area {
  position: relative;
}

/* 发送按钮 */
.send-btn {
  height: 100%;
  border-radius: 0 4px 4px 0;
  padding: 0 20px;
}

.send-icon {
  margin-right: 4px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding: 0 4px;
}

.action-buttons .el-button {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 字符计数 */
.char-count {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}

/* 禁用状态样式 */
:deep(.el-textarea.is-disabled .el-textarea__inner) {
  background-color: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
}

/* 输入框样式优化 */
:deep(.el-textarea__inner) {
  border-radius: 4px 0 0 4px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
}

:deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quick-questions-list {
    flex-direction: column;
  }

  .quick-question-btn {
    min-width: 100%;
  }

  .action-buttons {
    flex-wrap: wrap;
  }

  .char-count {
    margin-left: 0;
    width: 100%;
    text-align: right;
  }

  .send-btn {
    padding: 0 12px;
  }
}

@media (max-width: 480px) {
  .quick-questions {
    padding: 8px;
  }

  .action-buttons {
    gap: 8px;
  }

  .action-buttons .el-button {
    font-size: 12px;
    padding: 6px 10px;
  }
}
</style>
