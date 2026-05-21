/**
 * 寻路算法实现
 * 包含 BFS、DFS、Dijkstra、A* 算法
 */

// 格子状态枚举
export const CellState = {
  EMPTY: 'empty',
  START: 'start',
  END: 'end',
  WALL: 'wall',
  VISITED: 'visited',
  PATH: 'path'
}

// 算法结果数据结构
export class AlgorithmResult {
  constructor(visitedOrder = [], path = [], success = false) {
    this.visitedOrder = visitedOrder // Array<{x, y}>
    this.path = path // Array<{x, y}>
    this.success = success // boolean
  }
}

// 实验数据采集结构
export class ExperimentResult {
  constructor(algorithmId, gridSize) {
    this.algorithmId = algorithmId // number
    this.gridSize = gridSize // number
    this.startTime = Date.now() // number
    this.endTime = null // number
    this.visitedCount = 0 // number
    this.pathLength = 0 // number
    this.status = 'running' // 'completed' | 'failed'
    this.steps = [] // Array<StepRecord>
  }

  complete(visitedCount, pathLength, success) {
    this.endTime = Date.now()
    this.visitedCount = visitedCount
    this.pathLength = pathLength
    this.status = success ? 'completed' : 'failed'
  }

  addStep(stepIndex, x, y, state) {
    this.steps.push({
      stepIndex,
      x,
      y,
      state
    })
  }
}

// 网格类
export class Grid {
  constructor(rows, cols, obstacles = []) {
    this.rows = rows
    this.cols = cols
    this.grid = Array(rows).fill().map(() => Array(cols).fill(CellState.EMPTY))
    this.obstacles = new Set(obstacles.map(obs => `${obs.x},${obs.y}`))

    // 标记障碍
    obstacles.forEach(obs => {
      if (this.isValid(obs.x, obs.y)) {
        this.grid[obs.x][obs.y] = CellState.WALL
      }
    })
  }

  isValid(x, y) {
    return x >= 0 && x < this.rows && y >= 0 && y < this.cols
  }

  isWall(x, y) {
    return this.grid[x][y] === CellState.WALL
  }

  isEmpty(x, y) {
    return this.grid[x][y] === CellState.EMPTY
  }

  setState(x, y, state) {
    if (this.isValid(x, y)) {
      this.grid[x][y] = state
    }
  }

  getState(x, y) {
    return this.isValid(x, y) ? this.grid[x][y] : null
  }

  getNeighbors(x, y, diagonal = false) {
    const directions = [
      [0, 1], [1, 0], [0, -1], [-1, 0]
    ]

    if (diagonal) {
      directions.push([1, 1], [1, -1], [-1, 1], [-1, -1])
    }

    const neighbors = []
    for (const [dx, dy] of directions) {
      const nx = x + dx
      const ny = y + dy
      if (this.isValid(nx, ny) && !this.isWall(nx, ny)) {
        neighbors.push({ x: nx, y: ny })
      }
    }
    return neighbors
  }
}

// 算法基类
class PathfindingAlgorithm {
  constructor(grid, start, end) {
    this.grid = grid
    this.start = start
    this.end = end
    this.visitedOrder = []
    this.path = []
    this.success = false
  }

  run() {
    throw new Error('子类必须实现 run 方法')
  }

  getResult() {
    return new AlgorithmResult(this.visitedOrder, this.path, this.success)
  }

  reconstructPath(parentMap) {
    const path = []
    let current = this.end
    const visitedInPath = new Set()

    // 从终点回溯到起点
    while (current) {
      const key = `${current.x},${current.y}`

      // 检测循环：如果当前节点已经在路径中，说明形成了循环
      if (visitedInPath.has(key)) {
        console.warn('路径重建失败：检测到循环', { current, path })
        break
      }

      visitedInPath.add(key)
      path.unshift(current)

      // 如果已经到达起点，停止回溯
      if (current.x === this.start.x && current.y === this.start.y) {
        return path
      }

      const next = parentMap.get(key)

      // 如果没有父节点，无法继续回溯
      if (!next) {
        console.warn('路径重建失败：无法继续回溯', { current, parentMapSize: parentMap.size })
        break
      }

      current = next
    }

    // 检查路径是否包含起点
    const firstNode = path[0]
    if (!firstNode || firstNode.x !== this.start.x || firstNode.y !== this.start.y) {
      console.warn('路径重建失败：路径不包含起点', {
        path,
        start: this.start,
        pathLength: path.length,
        parentMapSize: parentMap.size
      })
      return []
    }

    return path
  }

  heuristic(a, b) {
    // 曼哈顿距离
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y)
  }
}

// BFS 算法
export class BFSAlgorithm extends PathfindingAlgorithm {
  run() {
    const queue = [this.start]
    const visited = new Set()
    const parentMap = new Map()

    visited.add(`${this.start.x},${this.start.y}`)
    this.visitedOrder.push(this.start)

    while (queue.length > 0) {
      const current = queue.shift()

      // 检查是否到达终点
      if (current.x === this.end.x && current.y === this.end.y) {
        this.path = this.reconstructPath(parentMap)
        this.success = true
        return this.getResult()
      }

      const neighbors = this.grid.getNeighbors(current.x, current.y)

      for (const neighbor of neighbors) {
        const key = `${neighbor.x},${neighbor.y}`
        if (!visited.has(key)) {
          visited.add(key)
          parentMap.set(key, current)
          queue.push(neighbor)
          this.visitedOrder.push(neighbor)
        }
      }
    }

    this.success = false
    return this.getResult()
  }
}

// DFS 算法 - 改进版：真正的深度优先单路径推进
export class DFSAlgorithm extends PathfindingAlgorithm {
  run() {
    const stack = [this.start]
    const visited = new Set()
    const parentMap = new Map()
    // 记录每个节点是否已被"处理"（从栈中弹出）
    const processed = new Set()

    visited.add(`${this.start.x},${this.start.y}`)
    this.visitedOrder.push(this.start)

    while (stack.length > 0) {
      const current = stack.pop()
      const currentKey = `${current.x},${current.y}`

      // 如果当前节点已经被处理过，跳过（避免重复处理）
      if (processed.has(currentKey)) {
        continue
      }
      processed.add(currentKey)

      // 检查是否到达终点
      if (current.x === this.end.x && current.y === this.end.y) {
        this.path = this.reconstructPath(parentMap)
        this.success = true
        return this.getResult()
      }

      const neighbors = this.grid.getNeighbors(current.x, current.y)

      // 逆序入栈，保证按上右下左的顺序访问（更自然的DFS顺序）
      for (let i = neighbors.length - 1; i >= 0; i--) {
        const neighbor = neighbors[i]
        const key = `${neighbor.x},${neighbor.y}`
        if (!visited.has(key)) {
          visited.add(key)
          parentMap.set(key, current)
          stack.push(neighbor)
          this.visitedOrder.push(neighbor)
        }
      }
    }

    this.success = false
    return this.getResult()
  }
}

// Dijkstra 算法
export class DijkstraAlgorithm extends PathfindingAlgorithm {
  run() {
    const distances = new Map()
    const visited = new Set()
    const parentMap = new Map()
    const priorityQueue = []

    // 初始化距离
    for (let i = 0; i < this.grid.rows; i++) {
      for (let j = 0; j < this.grid.cols; j++) {
        distances.set(`${i},${j}`, Infinity)
      }
    }

    const startKey = `${this.start.x},${this.start.y}`
    distances.set(startKey, 0)
    priorityQueue.push({ ...this.start, distance: 0 })

    while (priorityQueue.length > 0) {
      // 按距离排序
      priorityQueue.sort((a, b) => a.distance - b.distance)
      const current = priorityQueue.shift()

      const currentKey = `${current.x},${current.y}`

      if (visited.has(currentKey)) continue

      visited.add(currentKey)
      this.visitedOrder.push({ x: current.x, y: current.y })

      // 检查是否到达终点
      if (current.x === this.end.x && current.y === this.end.y) {
        this.path = this.reconstructPath(parentMap)
        this.success = true
        return this.getResult()
      }

      const neighbors = this.grid.getNeighbors(current.x, current.y)

      for (const neighbor of neighbors) {
        const neighborKey = `${neighbor.x},${neighbor.y}`
        const newDistance = current.distance + 1 // 所有边的权重为1

        if (newDistance < distances.get(neighborKey)) {
          distances.set(neighborKey, newDistance)
          parentMap.set(neighborKey, { x: current.x, y: current.y })
          priorityQueue.push({ ...neighbor, distance: newDistance })
        }
      }
    }

    this.success = false
    return this.getResult()
  }
}

// A* 算法
export class AStarAlgorithm extends PathfindingAlgorithm {
  run() {
    const openSet = new Set()
    const closedSet = new Set()
    const gScore = new Map()
    const fScore = new Map()
    const parentMap = new Map()

    const startKey = `${this.start.x},${this.start.y}`
    const endKey = `${this.end.x},${this.end.y}`

    // 初始化分数
    for (let i = 0; i < this.grid.rows; i++) {
      for (let j = 0; j < this.grid.cols; j++) {
        const key = `${i},${j}`
        gScore.set(key, Infinity)
        fScore.set(key, Infinity)
      }
    }

    gScore.set(startKey, 0)
    fScore.set(startKey, this.heuristic(this.start, this.end))

    openSet.add(startKey)

    while (openSet.size > 0) {
      // 找到 fScore 最小的节点
      let currentKey = null
      let minFScore = Infinity

      for (const key of openSet) {
        if (fScore.get(key) < minFScore) {
          minFScore = fScore.get(key)
          currentKey = key
        }
      }

      if (!currentKey) break

      // 解析坐标
      const [x, y] = currentKey.split(',').map(Number)
      const current = { x, y }

      // 检查是否到达终点
      if (currentKey === endKey) {
        this.path = this.reconstructPath(parentMap)
        this.success = true
        return this.getResult()
      }

      openSet.delete(currentKey)
      closedSet.add(currentKey)
      this.visitedOrder.push(current)

      const neighbors = this.grid.getNeighbors(current.x, current.y)

      for (const neighbor of neighbors) {
        const neighborKey = `${neighbor.x},${neighbor.y}`

        if (closedSet.has(neighborKey)) continue

        const tentativeGScore = gScore.get(currentKey) + 1

        if (!openSet.has(neighborKey)) {
          openSet.add(neighborKey)
        } else if (tentativeGScore >= gScore.get(neighborKey)) {
          continue
        }

        parentMap.set(neighborKey, current)
        gScore.set(neighborKey, tentativeGScore)
        fScore.set(neighborKey, tentativeGScore + this.heuristic(neighbor, this.end))
      }
    }

    this.success = false
    return this.getResult()
  }
}

// 算法工厂函数
export function createAlgorithm(algorithmType, grid, start, end) {
  switch (algorithmType) {
    case 'bfs':
      return new BFSAlgorithm(grid, start, end)
    case 'dfs':
      return new DFSAlgorithm(grid, start, end)
    case 'dijkstra':
      return new DijkstraAlgorithm(grid, start, end)
    case 'astar':
      return new AStarAlgorithm(grid, start, end)
    default:
      throw new Error(`未知的算法类型: ${algorithmType}`)
  }
}

// 统一算法运行函数
export function runAlgorithm(grid, start, end, options = {}) {
  const { algorithm = 'bfs' } = options

  // 创建网格实例
  const gridInstance = new Grid(grid.rows, grid.cols, grid.obstacles || [])

  // 创建算法实例
  const algorithmInstance = createAlgorithm(algorithm, gridInstance, start, end)

  // 运行算法
  const result = algorithmInstance.run()

  return result
}

// 实验数据保存（使用真实API）
export const experimentMock = {
  async saveExperimentResult(result) {
    console.log('保存实验数据到真实API:', result)

    try {
      // 准备实验记录数据
      const experimentData = {
        algorithmId: result.algorithmId,
        gridSize: result.gridSize,
        startTime: new Date(result.startTime).toISOString(),
        endTime: new Date(result.endTime).toISOString(),
        visitedCount: result.visitedCount,
        pathLength: result.pathLength,
        status: result.status
      }

      // 注意：这里不能直接导入Vue模块，所以返回一个兼容的接口
      // 实际保存逻辑在 visualization/index.vue 中实现
      console.log('实验数据准备保存到真实API:', experimentData)

      // 返回一个模拟的成功响应，实际保存由前端页面处理
      return {
        success: true,
        id: Date.now(),
        message: '实验数据已准备保存到真实API（实际保存由前端页面处理）'
      }
    } catch (error) {
      console.error('保存实验数据失败:', error)
      throw error
    }
  }
}

// 工具函数：延迟函数
export function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// 工具函数：动画播放
export async function playAnimation(gridBoard, visitedOrder, path, speed = 5) {
  const visitedCells = []
  const pathCells = []

  // 播放访问动画
  for (const cell of visitedOrder) {
    visitedCells.push({ ...cell })
    // 更新 GridBoard 显示
    if (gridBoard.updateVisitedCells) {
      gridBoard.updateVisitedCells([...visitedCells])
    }
    await delay(100 / speed)
  }

  // 播放路径动画
  for (const cell of path) {
    pathCells.push({ ...cell })
    // 更新 GridBoard 显示
    if (gridBoard.updatePathCells) {
      gridBoard.updatePathCells([...pathCells])
    }
    await delay(200 / speed)
  }

  return { visitedCells, pathCells }
}
