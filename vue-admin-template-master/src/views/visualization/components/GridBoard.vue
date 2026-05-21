<template>
  <div class="grid-board">
    <!-- 网格容器 -->
    <div ref="gridContainer" class="grid-container">
      <!-- 网格画布 -->
      <div class="grid-canvas" :style="gridStyleWithVar">
        <!-- 网格行 (使用0索引: rowIndex从1到gridRows, 实际坐标rowIndex-1) -->
        <div
          v-for="rowIndex in gridRows"
          :key="rowIndex"
          class="grid-row"
        >
          <!-- 网格单元格 (使用0索引: colIndex从1到gridCols, 实际坐标colIndex-1) -->
          <div
            v-for="colIndex in gridCols"
            :key="colIndex"
            class="grid-cell"
            :class="getCellClass(rowIndex - 1, colIndex - 1)"
            @click="handleCellClick(rowIndex - 1, colIndex - 1)"
            @mouseenter="handleCellHover(rowIndex - 1, colIndex - 1)"
          >
            <!-- 单元格内容 -->
            <div class="cell-content">
              <template v-if="isStartPoint(rowIndex - 1, colIndex - 1)">
                <i class="el-icon-location cell-icon start-icon" />
                <span class="cell-label">起点</span>
              </template>
              <template v-else-if="isEndPoint(rowIndex - 1, colIndex - 1)">
                <i class="el-icon-location cell-icon end-icon" />
                <span class="cell-label">终点</span>
              </template>
              <template v-else-if="isObstacle(rowIndex - 1, colIndex - 1)">
                <!-- 纯黑色方块，没有图标 -->
                <div class="obstacle-block" />
              </template>
              <template v-else-if="isVisited(rowIndex - 1, colIndex - 1)">
                <div class="visited-dot" />
              </template>
              <template v-else-if="isPath(rowIndex - 1, colIndex - 1)">
                <div class="path-dot" />
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图例 -->
    <div class="legend">
      <div class="legend-item">
        <div class="legend-color start-color" />
        <span>起点</span>
      </div>
      <div class="legend-item">
        <div class="legend-color end-color" />
        <span>终点</span>
      </div>
      <div class="legend-item">
        <div class="legend-color obstacle-color" />
        <span>障碍</span>
      </div>
      <div class="legend-item">
        <div class="legend-color visited-color" />
        <span>已访问</span>
      </div>
      <div class="legend-item">
        <div class="legend-color path-color" />
        <span>路径</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GridBoard',
  props: {
    // 网格配置
    gridSize: {
      type: String,
      default: '16x16',
      validator: (value) => ['16x16', '32x42', '42x57'].includes(value)
    },
    showGridLines: {
      type: Boolean,
      default: true
    },

    // 网格数据 (所有坐标均为0索引)
    startPoint: {
      type: Object,
      default: () => ({ x: 2, y: 2 })
    },
    endPoint: {
      type: Object,
      default: () => ({ x: 8, y: 8 })
    },
    obstacles: {
      type: Array,
      default: () => []
    },
    visitedCells: {
      type: Array,
      default: () => []
    },
    pathCells: {
      type: Array,
      default: () => []
    },

    // 编辑模式
    editMode: {
      type: String,
      default: 'start'
    },

    // 算法执行状态
    isRunning: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isMouseDown: false,
      lastCell: null,
      mouseDownTimer: null
    }
  },
  computed: {
    gridRows() {
      // 解析网格尺寸格式 "16x16", "42x32", "78x64"
      const [rows] = this.gridSize.split('x').map(Number)
      return rows
    },
    gridCols() {
      // 解析网格尺寸格式 "16x16", "42x32", "78x64"
      const [, cols] = this.gridSize.split('x').map(Number)
      return cols
    },
    cellSize() {
      // 总尺寸 528px / 最大网格数（取行和列的最大值）
      const maxSize = Math.max(this.gridRows, this.gridCols)
      return 528 / maxSize
    },
    gridStyle() {
      return {
        gridTemplateColumns: `repeat(${this.gridCols}, ${this.cellSize}px)`,
        gridTemplateRows: `repeat(${this.gridRows}, ${this.cellSize}px)`,
        width: `${this.cellSize * this.gridCols}px`,
        height: `${this.cellSize * this.gridRows}px`
      }
    },
    gridStyleWithVar() {
      return {
        ...this.gridStyle,
        '--cell-size': `${this.cellSize}px`
      }
    }
  },
  mounted() {
    console.log('GridBoard 组件已加载')
    this.setupMouseEvents()
  },
  beforeDestroy() {
    this.cleanupMouseEvents()
  },
  methods: {
    // 单元格点击事件 (row, col 均为0索引)
    handleCellClick(row, col) {
      this.$emit('cell-click', { row, col, mode: this.editMode })
    },

    handleCellHover(row, col) {
      this.$emit('cell-hover', { row, col })

      // 如果鼠标按下且处于障碍编辑模式，连续绘制
      if (this.isMouseDown && (this.editMode === 'obstacle' || this.editMode === 'erase')) {
        // 避免重复处理同一个单元格
        if (!this.lastCell || this.lastCell.row !== row || this.lastCell.col !== col) {
          this.lastCell = { row, col }
          this.$emit('cell-click', { row, col, mode: this.editMode })
        }
      }
    },

    // 鼠标事件设置
    setupMouseEvents() {
      const gridCanvas = this.$el.querySelector('.grid-canvas')
      if (gridCanvas) {
        gridCanvas.addEventListener('mousedown', this.handleMouseDown)
        gridCanvas.addEventListener('mouseup', this.handleMouseUp)
        gridCanvas.addEventListener('mouseleave', this.handleMouseLeave)
      }
    },

    cleanupMouseEvents() {
      const gridCanvas = this.$el.querySelector('.grid-canvas')
      if (gridCanvas) {
        gridCanvas.removeEventListener('mousedown', this.handleMouseDown)
        gridCanvas.removeEventListener('mouseup', this.handleMouseUp)
        gridCanvas.removeEventListener('mouseleave', this.handleMouseLeave)
      }
    },

    handleMouseDown(event) {
      // 只在障碍或擦除模式下启用长按绘制
      if (this.editMode === 'obstacle' || this.editMode === 'erase') {
        this.isMouseDown = true
        this.lastCell = null

        // 清除之前的定时器
        if (this.mouseDownTimer) {
          clearTimeout(this.mouseDownTimer)
        }

        // 设置定时器，防止误触
        this.mouseDownTimer = setTimeout(() => {
          // 长按开始，可以开始连续绘制
          console.log('开始连续绘制模式')
        }, 100)
      }
    },

    handleMouseUp() {
      this.isMouseDown = false
      this.lastCell = null

      if (this.mouseDownTimer) {
        clearTimeout(this.mouseDownTimer)
        this.mouseDownTimer = null
      }
    },

    handleMouseLeave() {
      this.isMouseDown = false
      this.lastCell = null

      if (this.mouseDownTimer) {
        clearTimeout(this.mouseDownTimer)
        this.mouseDownTimer = null
      }
    },

    // 单元格状态判断方法 (row, col 均为0索引)
    isStartPoint(row, col) {
      return this.startPoint.x === row && this.startPoint.y === col
    },

    isEndPoint(row, col) {
      return this.endPoint.x === row && this.endPoint.y === col
    },

    isObstacle(row, col) {
      return this.obstacles.some(obs => obs.x === row && obs.y === col)
    },

    isVisited(row, col) {
      return this.visitedCells.some(cell => cell.x === row && cell.y === col)
    },

    isPath(row, col) {
      return this.pathCells.some(cell => cell.x === row && cell.y === col)
    },

    getCellClass(row, col) {
      const classes = []
      if (this.isStartPoint(row, col)) classes.push('start-cell')
      if (this.isEndPoint(row, col)) classes.push('end-cell')
      if (this.isObstacle(row, col)) classes.push('obstacle-cell')
      if (this.isVisited(row, col)) classes.push('visited-cell')
      if (this.isPath(row, col)) classes.push('path-cell')
      if (this.showGridLines) classes.push('grid-border')
      return classes
    }
  }
}
</script>

<style lang="scss" scoped>
.grid-board {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.grid-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  background-color: #f8f9fa;
  margin-bottom: 20px;
  padding: 20px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.grid-canvas {
  display: grid;
  background-color: #f0f2f5;
  border-radius: 4px;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.05);
}

.grid-row {
  display: contents;
}

.grid-cell {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #ffffff;

  &:hover {
    background-color: rgba(64, 158, 255, 0.15);
    z-index: 1;
    transform: scale(1.05);
    box-shadow: 0 0 8px rgba(64, 158, 255, 0.3);
  }

  &.grid-border {
    border: 1px solid #dcdfe6;
  }

  &.start-cell {
    background-color: #67c23a;
    box-shadow: 0 0 8px rgba(103, 194, 58, 0.5);

    &:hover {
      background-color: #5daf34;
    }
  }

  &.end-cell {
    background-color: #f56c6c;
    box-shadow: 0 0 8px rgba(245, 108, 108, 0.5);

    &:hover {
      background-color: #e64c4c;
    }
  }

  &.obstacle-cell {
    background-color: #303133;
    box-shadow: 0 0 8px rgba(48, 49, 51, 0.5);

    &:hover {
      background-color: #262626;
    }
  }

  &.visited-cell {
    background-color: #409eff;
    box-shadow: 0 0 8px rgba(64, 158, 255, 0.5);
    z-index: 1;

    &:hover {
      background-color: #3a8ee6;
    }
  }

  &.path-cell {
    background-color: #e6a23c;
    box-shadow: 0 0 8px rgba(230, 162, 60, 0.5);
    z-index: 3;

    &:hover {
      background-color: #d19236;
    }
  }
}

.cell-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.cell-icon {
  font-size: 20px;
  color: white;

  &.start-icon {
    color: white;
  }

  &.end-icon {
    color: white;
  }

  &.obstacle-icon {
    color: white;
  }
}

.cell-label {
  font-size: 10px;
  color: white;
  margin-top: 2px;
  font-weight: bold;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

    .visited-dot {
      width: calc(var(--cell-size) * 0.6);
      height: calc(var(--cell-size) * 0.6);
      max-width: 14px;
      max-height: 14px;
      min-width: 6px;
      min-height: 6px;
      border-radius: 50%;
      background-color: #409eff;
      box-shadow: 0 0 6px rgba(64, 158, 255, 0.7);
    }

    .path-dot {
      width: calc(var(--cell-size) * 0.7);
      height: calc(var(--cell-size) * 0.7);
      max-width: 16px;
      max-height: 16px;
      min-width: 8px;
      min-height: 8px;
      border-radius: 50%;
      background-color: #e6a23c;
      box-shadow: 0 0 6px rgba(230, 162, 60, 0.7);
    }

.obstacle-block {
  width: 85%;
  height: 85%;
  background-color: #000000;
  border-radius: 3px;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.5);
}

.legend {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 8px;
  border: 1px solid #dcdfe6;

  .legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #606266;
    padding: 4px 12px;
    background-color: white;
    border-radius: 4px;
    border: 1px solid #ebeef5;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }

  .legend-color {
    width: 18px;
    height: 18px;
    border-radius: 3px;
    border: 1px solid rgba(0, 0, 0, 0.1);

    &.start-color {
      background-color: #67c23a;
    }

    &.end-color {
      background-color: #f56c6c;
    }

    &.obstacle-color {
      background-color: #303133;
    }

    &.visited-color {
      background-color: #409eff;
    }

    &.path-color {
      background-color: #e6a23c;
    }
  }
}
</style>
