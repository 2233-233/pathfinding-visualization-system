<script setup>

</script>

<template>
  <div class="control-panel">
    <!-- 算法选择 -->
    <div class="control-section">
      <h4 class="section-title">算法选择</h4>
      <div class="control-group">
        <el-select
          v-model="selectedAlgorithm"
          placeholder="请选择算法"
          style="width: 100%"
          @change="handleAlgorithmChange"
        >
          <el-option label="广度优先搜索 (BFS)" value="bfs" />
          <el-option label="深度优先搜索 (DFS)" value="dfs" />
          <el-option label="Dijkstra算法" value="dijkstra" />
          <el-option label="A*算法" value="astar" />
        </el-select>
      </div>

    </div>

    <!-- 控制按钮 -->
    <div class="control-section">
      <h4 class="section-title">算法控制</h4>
      <div class="button-group">
        <el-button
          type="primary"
          icon="el-icon-video-play"
          :disabled="isRunning"
          style="width: 100%; margin-bottom: 10px"
          @click="handleStart"
        >
          开始运行
        </el-button>

        <el-row :gutter="10">
          <el-col :span="12">
            <el-button
              icon="el-icon-video-pause"
              :disabled="!isRunning"
              style="width: 100%"
              @click="handlePause"
            >
              暂停
            </el-button>
          </el-col>
          <el-col :span="12">
            <el-button
              icon="el-icon-refresh"
              style="width: 100%"
              @click="handleReset"
            >
              重置
            </el-button>
          </el-col>
        </el-row>

        <el-button
          icon="el-icon-delete"
          style="width: 100%; margin-top: 10px"
          @click="handleClearObstacles"
        >
          清空障碍
        </el-button>
      </div>
    </div>

    <!-- 起点/终点设置 -->
    <div class="control-section">
      <h4 class="section-title">起点/终点设置</h4>
      <div class="point-controls">
        <el-radio-group v-model="editMode" size="small" @change="handleEditModeChange">
          <el-radio-button label="start">设置起点</el-radio-button>
          <el-radio-button label="end">设置终点</el-radio-button>
          <el-radio-button label="obstacle">设置障碍</el-radio-button>
          <el-radio-button label="erase">擦除</el-radio-button>
        </el-radio-group>

        <div class="point-info">
          <div class="point-item">
            <span class="point-label">起点：</span>
            <span class="point-value">({{ startPoint.x }}, {{ startPoint.y }})</span>
          </div>
          <div class="point-item">
            <span class="point-label">终点：</span>
            <span class="point-value">({{ endPoint.x }}, {{ endPoint.y }})</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 算法参数设置 -->
    <div class="control-section">
      <h4 class="section-title">算法参数</h4>
      <div class="param-controls">
        <div class="param-item">
          <span class="param-label">动画速度：</span>
          <el-slider
            v-model="localAnimationSpeed"
            :min="1"
            :max="10"
            :step="1"
            show-stops
            style="width: 100%; margin-top: 8px;"
            @input="handleSpeedInput"
            @change="handleSpeedChange"
          />
          <span class="speed-value">{{ speedLabels[localAnimationSpeed - 1] }}</span>
        </div>

        <div class="param-item">
          <el-checkbox v-model="showSearchProcess" @change="handleSearchProcessChange">
            显示搜索过程
          </el-checkbox>
        </div>
        <div class="param-item">
          <el-checkbox v-model="showShortestPath" @change="handleShortestPathChange">
            显示最短路径
          </el-checkbox>
        </div>
        <div class="param-item">
          <el-checkbox v-model="showGridLines" @change="handleGridLinesChange">
            显示网格线
          </el-checkbox>
        </div>
      </div>
    </div>

    <!-- 地图设置 -->
    <div class="control-section">
      <h4 class="section-title">地图设置</h4>
      <div class="map-controls">
        <div class="param-item">
          <span class="param-label">网格尺寸：</span>
          <el-select
            v-model="localGridSize"
            size="small"
            style="width: 140px"
            @change="handleGridSizeChange"
          >
            <el-option label="16 × 16" value="16x16" />
            <el-option label="32 × 42" value="32x42" />
            <el-option label="42 × 57" value="42x57" />
          </el-select>
        </div>
        <div class="param-item">
          <el-button type="text" @click="handleGenerateRandomObstacles">
            随机生成障碍
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ControlPanel',
  props: {
    // 算法选择
    algorithm: {
      type: String,
      default: 'bfs'
    },
    problem: {
      type: String,
      default: '1'
    },

    // 编辑模式
    editMode: {
      type: String,
      default: 'start'
    },

    // 起点终点坐标
    startPoint: {
      type: Object,
      default: () => ({ x: 2, y: 2 })
    },
    endPoint: {
      type: Object,
      default: () => ({ x: 8, y: 8 })
    },

    // 算法参数
    animationSpeed: {
      type: Number,
      default: 5
    },
    showSearchProcess: {
      type: Boolean,
      default: true
    },
    showShortestPath: {
      type: Boolean,
      default: true
    },
    showGridLines: {
      type: Boolean,
      default: true
    },

    // 地图设置
    gridSize: {
      type: String,
      default: '16'
    },

    // 状态信息
    isRunning: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      selectedAlgorithm: this.algorithm,
      selectedProblem: this.problem,
      localAnimationSpeed: this.animationSpeed,
      localGridSize: this.gridSize,
      speedLabels: ['极慢', '很慢', '慢', '较慢', '中等', '较快', '快', '很快', '极快', '最快']
    }
  },
  watch: {
    algorithm(newVal) {
      this.selectedAlgorithm = newVal
    },
    problem(newVal) {
      this.selectedProblem = newVal
    },
    animationSpeed(newVal) {
      this.localAnimationSpeed = newVal
    },
    gridSize(newVal) {
      this.localGridSize = newVal
    }
  },
  methods: {
    // 事件处理方法
    handleAlgorithmChange(value) {
      this.$emit('algorithm-change', value)
    },

    handleProblemChange(value) {
      this.$emit('problem-change', value)
    },

    handleStart() {
      this.$emit('start')
    },

    handlePause() {
      this.$emit('pause')
    },

    handleReset() {
      this.$emit('reset')
    },

    handleClearObstacles() {
      this.$emit('clear-obstacles')
    },

    handleEditModeChange(value) {
      this.$emit('edit-mode-change', value)
    },

    handleSpeedInput(value) {
      // 实时更新速度，不等待 change 事件
      this.$emit('speed-input', value)
    },

    handleSpeedChange(value) {
      this.$emit('speed-change', value)
    },

    handleSearchProcessChange(value) {
      this.$emit('search-process-change', value)
    },

    handleShortestPathChange(value) {
      this.$emit('shortest-path-change', value)
    },

    handleGridLinesChange(value) {
      this.$emit('grid-lines-change', value)
    },

    handleGridSizeChange(value) {
      this.$emit('grid-size-change', value)
    },

    handleGenerateRandomObstacles() {
      this.$emit('generate-random-obstacles')
    }
  }
}
</script>

<style lang="scss" scoped>
.control-panel {
  .control-section {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .section-title {
    margin: 0 0 12px 0;
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }

  .control-group {
    margin-bottom: 15px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .button-group {
    .el-button {
      margin-bottom: 10px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .point-controls {
    .point-info {
      margin-top: 12px;

      .point-item {
        margin-bottom: 8px;
        font-size: 13px;

        &:last-child {
          margin-bottom: 0;
        }

        .point-label {
          color: #606266;
        }

        .point-value {
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }

  .param-controls,
  .map-controls {
    .param-item {
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .param-label {
      display: block;
      margin-bottom: 4px;
      font-size: 13px;
      color: #606266;
    }

    .speed-value {
      display: block;
      margin-top: 4px;
      font-size: 13px;
      color: #409EFF;
      text-align: center;
    }
  }
}
</style>
