<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>寻路算法可视化</h2>
      <p class="page-description">寻路算法演示平台</p>
    </div>

    <!-- 顶部控制栏 -->
    <el-card class="top-control-bar" shadow="never">
      <div class="control-row">
        <div class="control-group">
          <span class="control-label">算法选择：</span>
          <el-select v-model="selectedAlgorithm" placeholder="请选择算法" style="width: 200px">
            <el-option label="广度优先搜索 (BFS)" value="bfs" />
            <el-option label="深度优先搜索 (DFS)" value="dfs" />
            <el-option label="Dijkstra算法" value="dijkstra" />
            <el-option label="A*算法" value="astar" />
          </el-select>
        </div>

        <div class="button-group">
          <el-button type="primary" icon="el-icon-video-play" @click="startAlgorithm">
            开始运行
          </el-button>
          <el-button icon="el-icon-video-pause" @click="pauseAlgorithm">
            暂停
          </el-button>
          <el-button icon="el-icon-refresh" @click="resetGrid">
            重置地图
          </el-button>
          <el-button icon="el-icon-delete" @click="clearObstacles">
            清空障碍
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧控制面板 -->
      <el-card class="left-panel" shadow="never">
        <div slot="header" class="panel-header">
          <span>控制面板</span>
        </div>

        <!-- 控制面板组件 -->
        <ControlPanel
          :algorithm="selectedAlgorithm"
          :problem="selectedProblem"
          :edit-mode="editMode"
          :start-point="startPoint"
          :end-point="endPoint"
          :animation-speed="animationSpeed"
          :show-search-process="showSearchProcess"
          :show-shortest-path="showShortestPath"
          :show-grid-lines="showGridLines"
          :grid-size="gridSize"
          :is-running="isRunning"
          @algorithm-change="handleAlgorithmChange"
          @problem-change="handleProblemChange"
          @start="startAlgorithm"
          @pause="pauseAlgorithm"
          @reset="resetGrid"
          @clear-obstacles="clearObstacles"
          @edit-mode-change="handleEditModeChange"
          @speed-input="handleSpeedInput"
          @speed-change="handleSpeedChange"
          @search-process-change="handleSearchProcessChange"
          @shortest-path-change="handleShortestPathChange"
          @grid-lines-change="handleGridLinesChange"
          @grid-size-change="handleGridSizeChange"
          @generate-random-obstacles="generateRandomObstacles"
        />
      </el-card>

      <!-- 中央可视化区域 -->
      <div class="center-area">
        <el-card class="visualization-area" shadow="never">
          <div slot="header" class="panel-header">
            <span>算法可视化</span>
          </div>

          <!-- 网格/地图组件 -->
          <GridBoard
            :grid-size="gridSize"
            :show-grid-lines="showGridLines"
            :start-point="startPoint"
            :end-point="endPoint"
            :obstacles="obstacles"
            :visited-cells="visitedCells"
            :path-cells="pathCells"
            :edit-mode="editMode"
            :is-running="isRunning"
            @cell-click="handleCellClick"
            @cell-hover="handleCellHover"
          />
        </el-card>
      </div>

      <!-- 右侧步骤信息 -->
      <el-card class="right-panel" shadow="never">
        <div slot="header" class="panel-header">
          <span>步骤信息</span>
        </div>

        <!-- 步骤信息组件 -->
        <StepInfo
          :status-text="statusText"
          :status-type="statusType"
          :current-step="currentStep"
          :algorithm-name="getAlgorithmName(selectedAlgorithm)"
          :problem-title="getProblemTitle(selectedProblem)"
          :visited-count="visitedCells.length"
          :path-length="stats.pathLength"
          :run-time="stats.runTime"
          :complexity="stats.complexity"
          :logs="operationLogs"
        />
      </el-card>
    </div>

    <!-- 底部统计信息 -->
    <el-card class="stats-card" shadow="never">
      <div slot="header" class="panel-header">
        <span>实验统计信息</span>
      </div>
      <div class="stats-content">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-label">路径长度</div>
              <div class="stat-value">{{ stats.pathLength || '--' }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-label">访问节点数</div>
              <div class="stat-value">{{ stats.visitedCount || '--' }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-label">运行时间</div>
              <div class="stat-value">{{ stats.runTime || '--' }} ms</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-label">算法复杂度</div>
              <div class="stat-value">{{ stats.complexity || '--' }}</div>
            </div>
          </el-col>
        </el-row>

        <!-- 详细统计 -->
        <div class="detailed-stats">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="detail-stat">
                <span class="detail-label">开始时间：</span>
                <span class="detail-value">{{ stats.startTime || '--' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-stat">
                <span class="detail-label">结束时间：</span>
                <span class="detail-value">{{ stats.endTime || '--' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-stat">
                <span class="detail-label">实验状态：</span>
                <el-tag size="small" type="info">{{ stats.status || '未开始' }}</el-tag>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
// 导入组件
import GridBoard from './components/GridBoard.vue'
import ControlPanel from './components/ControlPanel.vue'
import StepInfo from './components/StepInfo.vue'

// 导入算法
import {
  runAlgorithm,
  ExperimentResult,
  delay
} from './algorithms.js'

// 导入实验记录API
import { createExperiment, getExperimentById, getExperimentSteps, addExperimentStep } from '@/api/experiment'

export default {
  name: 'Visualization',
  components: {
    GridBoard,
    ControlPanel,
    StepInfo
  },
  data() {
    return {
      // 算法选择
      selectedAlgorithm: 'bfs',
      selectedProblem: '1',

      // 编辑模式
      editMode: 'start',

      // 起点终点坐标 (0索引)
      startPoint: { x: 2, y: 2 },
      endPoint: { x: 8, y: 8 },


      // 算法参数
      animationSpeed: 5,
      showSearchProcess: true,
      showShortestPath: true,
      showGridLines: true,

      // 地图设置 - 使用对象存储网格尺寸 {rows, cols}
      gridSize: '16x16',
      gridRows: 16,
      gridCols: 16,

      // 状态信息
      statusText: '就绪',
      statusType: 'success',
      currentStep: 0,
      isRunning: false,
      isPaused: false,
      shouldStop: false,
      animationTask: null,

      // 网格数据
      obstacles: [
        { x: 5, y: 5 }, { x: 5, y: 6 }, { x: 5, y: 7 },
        { x: 6, y: 5 }, { x: 7, y: 5 },
        { x: 3, y: 3 }, { x: 3, y: 4 }, { x: 4, y: 3 },
        { x: 10, y: 10 }, { x: 10, y: 11 }, { x: 11, y: 10 }
      ],
      visitedCells: [],
      pathCells: [],

      // 算法执行数据
      algorithmResult: null,
      experimentResult: null,

      // 实验记录ID（用于重放）
      experimentId: null,
      isReplayMode: false,
      replaySteps: [],

      // 操作日志
      operationLogs: [
        { time: '10:00:00', message: '页面加载成功', type: 'info' },
        { time: '10:00:05', message: '设置起点 (2, 2)', type: 'success' },
        { time: '10:00:10', message: '设置终点 (8, 8)', type: 'success' }
      ],

      // 统计信息
      stats: {
        pathLength: null,
        visitedCount: null,
        runTime: null,
        complexity: null,
        startTime: null,
        endTime: null,
        status: '未开始'
      }
    }
  },
  mounted() {
    console.log('可视化页面已加载')
    this.loadExperimentFromQuery()
  },
  methods: {
    // 算法控制方法
    async startAlgorithm() {
      if (this.isRunning) {
        // 如果已经在运行，先停止当前算法
        this.stopAlgorithm()
        await delay(100) // 等待一小段时间确保停止完成
      }
      // 重置状态
      this.isRunning = true
      this.isPaused = false
      this.shouldStop = false
      this.statusText = '运行中'
      this.statusType = 'warning'
      this.currentStep = 0
      this.visitedCells = []
      this.pathCells = []

      // 创建实验记录
      const algorithmId = this.getAlgorithmId(this.selectedAlgorithm)
      // 使用网格单元格总数作为gridSize参数
      const gridCellCount = this.gridRows * this.gridCols
      this.experimentResult = new ExperimentResult(algorithmId, gridCellCount)

      this.stats.status = '运行中'
      this.stats.startTime = new Date().toLocaleTimeString()
      this.stats.startTimestamp = Date.now()

      // 添加操作日志
      this.operationLogs.push({
        time: new Date().toLocaleTimeString(),
        message: `开始执行 ${this.getAlgorithmName(this.selectedAlgorithm)} 算法`,
        type: 'info'
      })

      console.log(`开始执行 ${this.selectedAlgorithm} 算法`)

      try {
        // 准备网格数据
        const gridData = {
          rows: this.gridRows,
          cols: this.gridCols,
          obstacles: this.obstacles
        }

        // 运行算法
        const startTime = Date.now()
        this.algorithmResult = runAlgorithm(
          gridData,
          this.startPoint,
          this.endPoint,
          { algorithm: this.selectedAlgorithm }
        )
        const endTime = Date.now()

        // 检查是否被停止
        if (this.shouldStop) {
          console.log('算法执行被中断')
          return
        }

        // 更新统计信息
        this.stats.runTime = endTime - startTime
        this.stats.visitedCount = this.algorithmResult.visitedOrder.length
        this.stats.pathLength = this.algorithmResult.path.length
        this.stats.complexity = this.getAlgorithmComplexity(this.selectedAlgorithm)

        // 完成实验记录
        this.experimentResult.complete(
          this.algorithmResult.visitedOrder.length,
          this.algorithmResult.path.length,
          this.algorithmResult.success
        )

        // 播放动画
        if (this.showSearchProcess) {
          await this.playAnimation()
        } else {
          // 如果不显示搜索过程，直接显示结果
          this.visitedCells = [...this.algorithmResult.visitedOrder]
          this.pathCells = [...this.algorithmResult.path]
        }

        // 检查是否被停止
        if (this.shouldStop) {
          console.log('动画播放被中断')
          return
        }

        // 更新状态
        if (this.algorithmResult.success) {
          this.statusText = '完成'
          this.statusType = 'success'
          this.stats.status = '完成'

          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: `算法执行成功，找到路径长度: ${this.algorithmResult.path.length}`,
            type: 'success'
          })
        } else {
          this.statusText = '失败'
          this.statusType = 'error'
          this.stats.status = '失败'

          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: '算法执行失败，未找到路径',
            type: 'error'
          })
        }

        // 保存实验数据
        await this.saveExperimentData()
      } catch (error) {
        console.error('算法执行错误:', error)
        this.statusText = '错误'
        this.statusType = 'error'
        this.stats.status = '错误'

        this.operationLogs.push({
          time: new Date().toLocaleTimeString(),
          message: `算法执行错误: ${error.message}`,
          type: 'error'
        })
      } finally {
        if (!this.shouldStop) {
          this.isRunning = false
          this.stats.endTime = new Date().toLocaleTimeString()
        }
      }
    },

    // 停止算法
    stopAlgorithm() {
      this.shouldStop = true
      this.isRunning = false
      this.isPaused = false

      this.operationLogs.push({
        time: new Date().toLocaleTimeString(),
        message: '算法已停止',
        type: 'warning'
      })

      console.log('算法已停止')
    },

    pauseAlgorithm() {
      if (!this.isRunning) return

      this.isPaused = !this.isPaused

      if (this.isPaused) {
        this.statusText = '已暂停'
        this.statusType = 'info'

        this.operationLogs.push({
          time: new Date().toLocaleTimeString(),
          message: '算法已暂停',
          type: 'warning'
        })
      } else {
        this.statusText = '运行中'
        this.statusType = 'warning'

        this.operationLogs.push({
          time: new Date().toLocaleTimeString(),
          message: '算法已继续',
          type: 'info'
        })
      }
    },

    resetGrid() {
      this.startPoint = { x: 2, y: 2 }
      this.endPoint = { x: 8, y: 8 }
      this.obstacles = [
        { x: 5, y: 5 }, { x: 5, y: 6 }, { x: 5, y: 7 },
        { x: 6, y: 5 }, { x: 7, y: 5 },
        { x: 3, y: 3 }, { x: 3, y: 4 }, { x: 4, y: 3 },
        { x: 10, y: 10 }, { x: 10, y: 11 }, { x: 11, y: 10 }
      ]
      this.visitedCells = []
      this.pathCells = []
      this.currentStep = 0
      this.statusText = '就绪'
      this.statusType = 'success'
      this.isRunning = false
      this.stats = {
        pathLength: null,
        visitedCount: null,
        runTime: null,
        complexity: null,
        startTime: null,
        endTime: null,
        status: '未开始'
      }

      this.operationLogs.push({
        time: new Date().toLocaleTimeString(),
        message: '网格已重置',
        type: 'success'
      })

      console.log('网格已重置')
    },

    clearObstacles() {
      this.obstacles = []

      this.operationLogs.push({
        time: new Date().toLocaleTimeString(),
        message: '障碍已清空',
        type: 'success'
      })

      console.log('障碍已清空')
    },

    // 事件处理方法 - 添加缺失的方法
    handleAlgorithmChange(value) {
      this.selectedAlgorithm = value
      console.log('算法已更改为:', value)
    },

    handleProblemChange(value) {
      this.selectedProblem = value
      console.log('题目已更改为:', value)
    },

    handleEditModeChange(value) {
      this.editMode = value
      console.log('编辑模式已更改为:', value)
    },

    handleSpeedInput(value) {
      // 实时更新动画速度
      this.animationSpeed = value
      console.log('动画速度实时更新为:', value)
    },

    handleSpeedChange(value) {
      this.animationSpeed = value
      console.log('动画速度已更改为:', value)
    },

    handleSearchProcessChange(value) {
      this.showSearchProcess = value
      console.log('显示搜索过程已更改为:', value)
    },

    handleShortestPathChange(value) {
      this.showShortestPath = value
      console.log('显示最短路径已更改为:', value)
    },

    handleGridLinesChange(value) {
      this.showGridLines = value
      console.log('显示网格线已更改为:', value)
    },

    handleGridSizeChange(value) {
      this.gridSize = value
      // 解析网格尺寸格式 "16x16", "42x32", "78x64"
      const [rows, cols] = value.split('x').map(Number)
      this.gridRows = rows
      this.gridCols = cols
      console.log('网格大小已更改为:', value, `(${rows}行 x ${cols}列)`)
    },

    // 网格交互方法
    handleCellClick(data) {
      const { row, col, mode } = data
      switch (mode) {
        case 'start':
          this.startPoint = { x: row, y: col }
          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: `设置起点: (${row}, ${col})`,
            type: 'success'
          })
          console.log(`设置起点: (${row}, ${col})`)
          break
        case 'end':
          this.endPoint = { x: row, y: col }
          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: `设置终点: (${row}, ${col})`,
            type: 'success'
          })
          console.log(`设置终点: (${row}, ${col})`)
          break
        case 'obstacle':
          this.addObstacle(row, col)
          break
        case 'erase':
          this.removeObstacle(row, col)
          break
      }
    },

    handleCellHover(data) {
      // 鼠标悬停效果
      // console.log('鼠标悬停在单元格:', data)
    },

    addObstacle(row, col) {
      if (!this.isStartPoint(row, col) && !this.isEndPoint(row, col)) {
        const obstacle = { x: row, y: col }
        if (!this.isObstacle(row, col)) {
          this.obstacles.push(obstacle)
          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: `添加障碍: (${row}, ${col})`,
            type: 'info'
          })
          console.log(`添加障碍: (${row}, ${col})`)
        }
      }
    },

    removeObstacle(row, col) {
      this.obstacles = this.obstacles.filter(obs => !(obs.x === row && obs.y === col))
      this.operationLogs.push({
        time: new Date().toLocaleTimeString(),
        message: `移除障碍: (${row}, ${col})`,
        type: 'warning'
      })
      console.log(`移除障碍: (${row}, ${col})`)
    },

    generateRandomObstacles() {
      this.obstacles = []
      const obstacleCount = Math.floor(this.gridRows * this.gridCols * 0.2) // 20%的障碍

      for (let i = 0; i < obstacleCount; i++) {
        // 使用0索引坐标 (0 到 gridRows-1, 0 到 gridCols-1)
        const x = Math.floor(Math.random() * this.gridRows)
        const y = Math.floor(Math.random() * this.gridCols)

        // 避免起点和终点位置
        if (!(x === this.startPoint.x && y === this.startPoint.y) &&
            !(x === this.endPoint.x && y === this.endPoint.y)) {
          this.obstacles.push({ x, y })
        }
      }

      this.operationLogs.push({
        time: new Date().toLocaleTimeString(),
        message: `生成了 ${obstacleCount} 个随机障碍`,
        type: 'info'
      })

      console.log(`生成了 ${obstacleCount} 个随机障碍`)
    },


    // 单元格状态判断方法
    isStartPoint(row, col) {
      return this.startPoint.x === row && this.startPoint.y === col
    },

    isEndPoint(row, col) {
      return this.endPoint.x === row && this.endPoint.y === col
    },

    isObstacle(row, col) {
      return this.obstacles.some(obs => obs.x === row && obs.y === col)
    },

    // 辅助方法 - 添加缺失的方法
    getAlgorithmName(algorithm) {
      const algorithmNames = {
        'bfs': '广度优先搜索 (BFS)',
        'dfs': '深度优先搜索 (DFS)',
        'dijkstra': 'Dijkstra算法',
        'astar': 'A*算法'
      }
      return algorithmNames[algorithm] || algorithm
    },

    getProblemTitle(problem) {
      const problemTitles = {
        '1': '简单迷宫 - 难度：简单',
        '2': '复杂障碍 - 难度：中等',
        '3': '随机地图 - 难度：困难'
      }
      return problemTitles[problem] || `题目 ${problem}`
    },

    // 动画播放方法
    async  playAnimation() {
      if (!this.algorithmResult) return

      this.visitedCells = []
      this.pathCells = []
      // 计算动画延迟：使用指数级变化，让速度差异更明显
      // 速度级别 1-10 对应延迟 200ms 到 1ms（最快）
      const baseDelay = 200
      const minDelay = 1
      // 指数计算：速度=1时延迟=200ms，速度=10时延迟=1ms
      const speedFactor = Math.pow(minDelay / baseDelay, 1 / 9) // 9个间隔
      const visitedDelay = baseDelay * Math.pow(speedFactor, this.animationSpeed - 1)
      const pathDelay = visitedDelay * 2 // 路径动画延迟加倍
      // 网格大小调整：网格越大，动画越快（减少延迟）
      const gridSizeFactor = Math.max(1, this.gridRows * this.gridCols / 256) // 以16x16=256为基准
      const adjustedVisitedDelay = Math.max(minDelay, visitedDelay / Math.sqrt(gridSizeFactor))
      const adjustedPathDelay = Math.max(minDelay * 2, pathDelay / Math.sqrt(gridSizeFactor))

      console.log(`动画参数: 速度=${this.animationSpeed}, 网格=${this.gridRows}x${this.gridCols}, 访问延迟=${adjustedVisitedDelay.toFixed(1)}ms, 路径延迟=${adjustedPathDelay.toFixed(1)}ms`)

      // 性能优化：当网格很大时，使用批量更新减少Vue响应式开销
      const batchSize = Math.max(1, Math.floor(this.algorithmResult.visitedOrder.length / 100))
      let batchCells = []

      // 播放访问动画
      for (let i = 0; i < this.algorithmResult.visitedOrder.length; i++) {
        if (this.shouldStop) break

        if (this.isPaused) {
          // 等待暂停状态结束
          while (this.isPaused && this.isRunning && !this.shouldStop) {
            await delay(100)
          }
          if (!this.isRunning || this.shouldStop) break
        }

        const cell = this.algorithmResult.visitedOrder[i]
        batchCells.push({ ...cell })
        this.currentStep = i + 1

        // 添加实验步骤记录
        if (this.experimentResult) {
          this.experimentResult.addStep(i + 1, cell.x, cell.y, 'visited')
        }

        // 批量更新：每batchSize个单元格更新一次，或者到达最后一个单元格
        if (batchCells.length >= batchSize || i === this.algorithmResult.visitedOrder.length - 1) {
          this.visitedCells = [...this.visitedCells, ...batchCells]
          batchCells = []

          // 根据调整后的延迟播放动画
          await delay(adjustedVisitedDelay)
        }
      }

      // 检查是否被停止
      if (this.shouldStop) {
        console.log('访问动画播放被中断')
        return
      }

      // 播放路径动画
      if (this.showShortestPath && this.algorithmResult.success) {
        batchCells = []
        const pathBatchSize = Math.max(1, Math.floor(this.algorithmResult.path.length / 50))

        for (let i = 0; i < this.algorithmResult.path.length; i++) {
          if (this.shouldStop) break

          if (this.isPaused) {
            // 等待暂停状态结束
            while (this.isPaused && this.isRunning && !this.shouldStop) {
              await delay(100)
            }
            if (!this.isRunning || this.shouldStop) break
          }

          const cell = this.algorithmResult.path[i]
          batchCells.push({ ...cell })

          // 添加实验步骤记录
          if (this.experimentResult) {
            this.experimentResult.addStep(
              this.algorithmResult.visitedOrder.length + i + 1,
              cell.x,
              cell.y,
              'path'
            )
          }

          // 批量更新：每pathBatchSize个单元格更新一次，或者到达最后一个单元格
          if (batchCells.length >= pathBatchSize || i === this.algorithmResult.path.length - 1) {
            this.pathCells = [...this.pathCells, ...batchCells]
            batchCells = []

            // 根据调整后的延迟播放动画
            await delay(adjustedPathDelay)
          }
        }
      }
    },

    // 获取算法ID（用于创建ExperimentResult对象）
    getAlgorithmId(algorithm) {
      // 根据数据库中的实际ID映射（从API获取）：
      // A*算法: algorithm_id = 1
      // Dijkstra算法: algorithm_id = 2
      // DFS: algorithm_id = 4
      // BFS: algorithm_id = 5
      const algorithmIds = {
        'astar': 1, // A*算法
        'dijkstra': 2, // Dijkstra算法
        'dfs': 4, // DFS
        'bfs': 5 // BFS
      }
      return algorithmIds[algorithm] || 1
    },

    // 获取算法名称（用于保存实验记录）
    getAlgorithmNameForSave(algorithm) {
      // 根据数据库中的算法名称映射：
      const algorithmNames = {
        'astar': 'A*', // A*算法
        'dijkstra': 'Dijkstra', // Dijkstra算法
        'dfs': 'DFS', // DFS
        'bfs': 'BFS' // BFS
      }
      return algorithmNames[algorithm] || 'A*'
    },

    // 获取算法复杂度
    getAlgorithmComplexity(algorithm) {
      const complexities = {
        'bfs': 'O(V + E)',
        'dfs': 'O(V + E)',
        'dijkstra': 'O((V + E) log V)',
        'astar': 'O(E)'
      }
      return complexities[algorithm] || '未知'
    },

    // 保存实验数据
    async saveExperimentData() {
      if (!this.experimentResult) return

      try {
        // 准备实验记录数据
        // 注意：后端期望LocalDateTime格式，使用 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        const formatLocalDateTime = (date) => {
          const d = new Date(date)
          // 格式化为 yyyy-MM-dd HH:mm:ss (有空格，不是T)
          const year = d.getFullYear()
          const month = String(d.getMonth() + 1).padStart(2, '0')
          const day = String(d.getDate()).padStart(2, '0')
          const hours = String(d.getHours()).padStart(2, '0')
          const minutes = String(d.getMinutes()).padStart(2, '0')
          const seconds = String(d.getSeconds()).padStart(2, '0')
          return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
        }

        // 根据后端实体类，需要以下字段：
        // userId, problemId, algorithmId, startTime, endTime, pathLength, visitedCount, status
        // 注意：userId应该从token中获取，后端会自动处理
        // problemId从selectedProblem获取
        const experimentData = {
          algorithmId: this.getAlgorithmId(this.selectedAlgorithm),
          problemId: parseInt(this.selectedProblem) || 1, // 将字符串转换为整数
          startTime: formatLocalDateTime(this.experimentResult.startTime),
          endTime: formatLocalDateTime(this.experimentResult.endTime),
          visitedCount: this.experimentResult.visitedCount,
          pathLength: this.experimentResult.pathLength,
          status: this.experimentResult.status
        }

        console.log('发送的实验数据:', experimentData)
        console.log('算法ID:', experimentData.algorithmId)
        console.log('算法ID类型:', typeof experimentData.algorithmId)
        console.log('算法ID是否为空:', !experimentData.algorithmId)

        // 调用真实API保存实验记录
        const response = await createExperiment(experimentData)

        // 根据接口规范，response已经是data字段的内容
        // 后端可能返回字符串消息或包含recordId的对象
        console.log('API响应:', response)

        if (response) {
          if (typeof response === 'object' && response.recordId) {
            // 返回的是包含recordId的对象
            this.experimentId = response.recordId

            this.operationLogs.push({
              time: new Date().toLocaleTimeString(),
              message: `实验数据保存成功，记录ID: ${response.recordId}`,
              type: 'success'
            })

            console.log('实验数据保存成功:', response)

            // 保存实验步骤
            await this.saveExperimentSteps(response.recordId)
          } else if (typeof response === 'string') {
            // 返回的是字符串消息，说明保存成功但没有返回ID
            // 我们可以通过查询最新记录来获取ID，或者显示成功消息
            this.operationLogs.push({
              time: new Date().toLocaleTimeString(),
              message: `实验数据保存成功: ${response}`,
              type: 'success'
            })

            console.log('实验数据保存成功:', response)

            // 这里可以添加逻辑来查询最新记录获取ID
            // 暂时不设置experimentId，因为不知道具体ID
          } else {
            // 其他情况，认为保存成功
            this.operationLogs.push({
              time: new Date().toLocaleTimeString(),
              message: '实验数据保存成功',
              type: 'success'
            })

            console.log('实验数据保存成功:', response)
          }
        } else {
          throw new Error('保存实验记录失败：未返回有效响应')
        }
      } catch (error) {
        console.error('保存实验数据失败:', error)

        this.operationLogs.push({
          time: new Date().toLocaleTimeString(),
          message: `保存实验数据失败: ${error.message}`,
          type: 'error'
        })
      }
    },

    // 保存实验步骤
    async saveExperimentSteps(recordId) {
      if (!this.experimentResult || !this.experimentResult.steps || this.experimentResult.steps.length === 0) {
        return
      }

      try {
        console.log(`实验记录 ${recordId} 有 ${this.experimentResult.steps.length} 个步骤需要保存`)

        // 准备障碍物信息
        const obstaclesInfo = {
          obstacles: this.obstacles,
          gridSize: {
            rows: this.gridRows,
            cols: this.gridCols
          },
          startPoint: this.startPoint,
          endPoint: this.endPoint
        }

        // 保存第一个步骤时包含障碍物信息
        if (this.experimentResult.steps.length > 0) {
          const firstStep = this.experimentResult.steps[0]
          // 创建包含障碍物信息的步骤数据
          // 注意: addStep存储的字段名为x, y, state, 但后端API期望nodeX, nodeY, nodeState
          const stepData = {
            stepIndex: firstStep.stepIndex,
            nodeX: firstStep.x,
            nodeY: firstStep.y,
            nodeState: firstStep.state,
            gridState: JSON.stringify(obstaclesInfo) // 将障碍物信息存储在gridState字段中
          }


          // 调用API保存步骤
          const response = await addExperimentStep(recordId, stepData)
          if (response) {
            console.log('保存实验步骤成功（包含障碍物信息）:', response)
          }
        }

        this.operationLogs.push({
          time: new Date().toLocaleTimeString(),
          message: `实验步骤已记录，共 ${this.experimentResult.steps.length} 步（包含障碍物信息）`,
          type: 'info'
        })
      } catch (error) {
        console.error('保存实验步骤失败:', error)
        // 步骤保存失败不影响主流程
        this.operationLogs.push({
          time: new Date().toLocaleTimeString(),
          message: `保存实验步骤失败: ${error.message}`,
          type: 'warning'
        })
      }
    },

    // 从URL参数加载实验记录
    async loadExperimentFromQuery() {
      const query = this.$route.query
      const experimentId = query.experimentId
      const algorithmId = query.algorithmId
      const gridSize = query.gridSize

      if (experimentId) {
        try {
          this.isReplayMode = true
          this.statusText = '加载实验记录...'
          this.statusType = 'info'

          // 加载实验记录
          const response = await getExperimentById(experimentId)
          if (response) {
            this.experimentId = experimentId

            // 设置算法
            if (response.algorithmId) {
              this.selectedAlgorithm = this.getAlgorithmKeyById(response.algorithmId)
            } else if (algorithmId) {
              this.selectedAlgorithm = this.getAlgorithmKeyById(algorithmId)
            }

            // 设置网格大小
            if (response.gridSize) {
              this.gridSize = response.gridSize.toString()
              // 解析网格尺寸
              if (typeof response.gridSize === 'number') {
                // 如果是数字，假设是正方形网格
                const size = Math.sqrt(response.gridSize)
                if (Number.isInteger(size)) {
                  this.gridRows = size
                  this.gridCols = size
                }
              }
            } else if (gridSize) {
              this.gridSize = gridSize.toString()
            }

            // 更新统计信息
            this.stats = {
              pathLength: response.pathLength || null,
              visitedCount: response.visitedCount || null,
              runTime: null, // 需要计算
              complexity: this.getAlgorithmComplexity(this.selectedAlgorithm),
              startTime: response.startTime || null,
              endTime: response.endTime || null,
              status: response.status || '完成'
            }

            // 加载实验步骤
            await this.loadExperimentSteps(experimentId)

            this.statusText = '实验记录加载完成'
            this.statusType = 'success'

            this.operationLogs.push({
              time: new Date().toLocaleTimeString(),
              message: `实验记录 ${experimentId} 加载完成`,
              type: 'success'
            })

            console.log('实验记录加载完成:', response)
          }
        } catch (error) {
          console.error('加载实验记录失败:', error)
          this.statusText = '加载实验记录失败'
          this.statusType = 'error'

          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: `加载实验记录失败: ${error.message}`,
            type: 'error'
          })
        }
      }
    },

    // 加载实验步骤
    async loadExperimentSteps(recordId) {
      try {
        const response = await getExperimentSteps(recordId)
        if (response && Array.isArray(response)) {
          this.replaySteps = response

          // 从步骤中提取访问节点和路径
          this.visitedCells = response
            .filter(step => step.nodeState === 'visited')
            .map(step => ({ x: step.nodeX, y: step.nodeY }))

          this.pathCells = response
            .filter(step => step.nodeState === 'path')
            .map(step => ({ x: step.nodeX, y: step.nodeY }))

          this.operationLogs.push({
            time: new Date().toLocaleTimeString(),
            message: `加载了 ${response.length} 个实验步骤`,
            type: 'info'
          })
        }
      } catch (error) {
        console.error('加载实验步骤失败:', error)
        // 步骤加载失败不影响主流程
      }
    },

    // 根据算法ID获取算法key
    getAlgorithmKeyById(algorithmId) {
      // 根据数据库中的实际ID映射（从API获取）：
      // A*算法: algorithm_id = 1
      // Dijkstra算法: algorithm_id = 2
      // DFS: algorithm_id = 4
      // BFS: algorithm_id = 5
      const algorithmMap = {
        1: 'astar', // A*算法
        2: 'dijkstra', // Dijkstra算法
        4: 'dfs', // DFS
        5: 'bfs' // BFS
      }
      return algorithmMap[algorithmId] || 'bfs'
    },

    // 其他方法
    toggleFullscreen() {
      console.log('切换全屏模式')
      // 这里可以实现全屏切换逻辑
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  h2 {
    margin: 0;
    color: #303133;
    font-size: 24px;
  }

  .page-description {
    margin: 5px 0 0;
    color: #909399;
    font-size: 14px;
  }
}

.top-control-bar {
  margin-bottom: 20px;

  .control-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 20px;
  }

  .control-group {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .control-label {
    font-size: 14px;
    color: #606266;
  }

  .button-group {
    display: flex;
    gap: 10px;
  }
}

.main-content {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  @media (max-width: 1200px) {
    flex-direction: column;
  }
}

.left-panel {
  width: 300px;
  flex-shrink: 0;

  @media (max-width: 1200px) {
    width: 100%;
  }
}

.center-area {
  flex: 1;
  min-width: 0;
}

.right-panel {
  width: 300px;
  flex-shrink: 0;

  @media (max-width: 1200px) {
    width: 100%;
  }
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  span {
    font-weight: 600;
    color: #303133;
  }
}

.visualization-area {
  height: 700px;
  display: flex;
  flex-direction: column;
}

.stats-card {
  margin-top: 20px;

  .stats-content {
    .stat-item {
      text-align: center;
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 4px;

      .stat-label {
        font-size: 13px;
        color: #909399;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }
    }
  }

  .detailed-stats {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #EBEEF5;

    .detail-stat {
      font-size: 13px;

      .detail-label {
        color: #909399;
      }

      .detail-value {
        color: #303133;
        font-weight: 500;
      }
    }
  }
}
</style>
