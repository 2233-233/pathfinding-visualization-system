<script setup>

</script>

<template>
  <div class="step-info">
    <!-- 当前状态 -->
    <div class="status-section">
      <h4 class="section-title">当前状态</h4>
      <div class="status-content">
        <div class="status-item">
          <span class="status-label">状态：</span>
          <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
        </div>
        <div class="status-item">
          <span class="status-label">当前步骤：</span>
          <span class="status-value">{{ currentStep }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">算法：</span>
          <span class="status-value">{{ algorithmName }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">题目：</span>
          <span class="status-value">{{ problemTitle }}</span>
        </div>
      </div>
    </div>

    <!-- 实时统计 -->
    <div class="stats-section">
      <h4 class="section-title">实时统计</h4>
      <div class="stats-content">
        <el-row :gutter="10">
          <el-col :span="12">
            <div class="stat-card">
              <div class="stat-icon">
                <i class="el-icon-location-outline" />
              </div>
              <div class="stat-info">
                <div class="stat-label">已访问节点</div>
                <div class="stat-value">{{ visitedCount }}</div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-card">
              <div class="stat-icon">
                <i class="el-icon-position" />
              </div>
              <div class="stat-info">
                <div class="stat-label">路径长度</div>
                <div class="stat-value">{{ pathLength || '--' }}</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="10" style="margin-top: 10px">
          <el-col :span="12">
            <div class="stat-card">
              <div class="stat-icon">
                <i class="el-icon-time" />
              </div>
              <div class="stat-info">
                <div class="stat-label">运行时间</div>
                <div class="stat-value">{{ runTime || '--' }} ms</div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-card">
              <div class="stat-icon">
                <i class="el-icon-cpu" />
              </div>
              <div class="stat-info">
                <div class="stat-label">算法复杂度</div>
                <div class="stat-value">{{ complexity || '--' }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 操作日志 -->
    <div class="log-section">
      <h4 class="section-title">操作日志</h4>
      <div class="log-content">
        <div class="log-list">
          <div
            v-for="(log, index) in logs"
            :key="index"
            class="log-item"
            :class="log.type"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
          <div v-if="logs.length === 0" class="empty-log">
            暂无操作记录
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StepInfo',
  props: {
    // 状态信息
    statusText: {
      type: String,
      default: '就绪'
    },
    statusType: {
      type: String,
      default: 'success'
    },
    currentStep: {
      type: Number,
      default: 0
    },

    // 算法信息
    algorithmName: {
      type: String,
      default: 'BFS'
    },
    problemTitle: {
      type: String,
      default: '简单迷宫'
    },

    // 统计信息
    visitedCount: {
      type: Number,
      default: 0
    },
    pathLength: {
      type: Number,
      default: null
    },
    runTime: {
      type: Number,
      default: null
    },
    complexity: {
      type: String,
      default: null
    },

    // 操作日志
    logs: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style lang="scss" scoped>
.step-info {
  .section-title {
    margin: 0 0 12px 0;
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }

  .status-section {
    margin-bottom: 20px;

    .status-content {
      .status-item {
        display: flex;
        align-items: center;
        margin-bottom: 10px;

        &:last-child {
          margin-bottom: 0;
        }

        .status-label {
          width: 80px;
          font-size: 13px;
          color: #606266;
        }

        .status-value {
          flex: 1;
          font-size: 13px;
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }

  .stats-section {
    margin-bottom: 20px;

    .stats-content {
      .stat-card {
        display: flex;
        align-items: center;
        padding: 10px;
        background-color: #f5f7fa;
        border-radius: 4px;
        height: 60px;

        .stat-icon {
          width: 40px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #409eff;
          border-radius: 50%;
          margin-right: 10px;

          i {
            font-size: 20px;
            color: white;
          }
        }

        .stat-info {
          flex: 1;
          min-width: 0; /* 防止flex item溢出 */

          .stat-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 4px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .stat-value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            word-break: break-word;
            overflow-wrap: break-word;
            line-height: 1.2;
          }
        }
      }
    }
  }

  .log-section {
    .log-content {
      .log-list {
        max-height: 200px;
        overflow-y: auto;
        border: 1px solid #EBEEF5;
        border-radius: 4px;
        padding: 10px;
        background-color: #fafafa;

        .log-item {
          padding: 5px 0;
          border-bottom: 1px solid #f0f0f0;
          font-size: 12px;

          &:last-child {
            border-bottom: none;
          }

          &.info {
            .log-message {
              color: #409eff;
            }
          }

          &.success {
            .log-message {
              color: #67c23a;
            }
          }

          &.warning {
            .log-message {
              color: #e6a23c;
            }
          }

          &.error {
            .log-message {
              color: #f56c6c;
            }
          }

          .log-time {
            color: #909399;
            margin-right: 10px;
          }

          .log-message {
            font-weight: 500;
          }
        }

        .empty-log {
          text-align: center;
          color: #909399;
          font-size: 12px;
          padding: 20px 0;
        }
      }
    }
  }
}
</style>
