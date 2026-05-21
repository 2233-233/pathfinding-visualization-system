<template>
  <div class="algorithm-learning-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>算法学习</h1>
      <p class="page-description">学习路径寻路算法的核心概念与实现原理</p>
    </div>

    <!-- 算法模块切换 Tabs -->
    <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
      <el-tab-pane
        v-for="algorithm in backendAlgorithms"
        :key="algorithm.algorithmId"
        :label="algorithm.name"
        :name="algorithm.algorithmId.toString()"
      >
        <div class="algorithm-content">
          <!-- 算法讲解部分 -->
          <div class="algorithm-explanation">
            <h2>{{ algorithm.name }}</h2>

            <div class="section">
              <h3>算法简介</h3>
              <p>{{ algorithm.description || '暂无详细描述' }}</p>
            </div>

            <div class="section">
              <h3>核心概念</h3>
              <ul class="core-ideas">
                <li>时间复杂度：{{ algorithm.complexity || '未指定' }}</li>
                <li>算法类型：路径寻路算法</li>
                <li>适用场景：网格路径搜索、图遍历</li>
                <li>关键特点：{{ getAlgorithmFeatures(algorithm.algorithmId) }}</li>
              </ul>
            </div>

            <div class="section">
              <h3>基本原理</h3>
              <div class="principle-content" v-html="getAlgorithmPrinciple(algorithm.algorithmId)"></div>
            </div>

            <div class="section">
              <h3>算法特点</h3>
              <pre class="pseudo-code">{{ getAlgorithmPseudoCode(algorithm.algorithmId) }}</pre>
            </div>
          </div>

          <!-- 相关题目部分 -->
          <div class="related-problems">
            <h3>相关练习题</h3>

            <!-- 难度筛选 -->
            <div class="filter-section">
              <span class="filter-label">难度筛选：</span>
              <el-select
                v-model="selectedDifficulty"
                placeholder="请选择难度"
                size="medium"
                style="width: 150px;"
                @change="handleFilter"
              >
                <el-option label="全部" value="all" />
                <el-option label="简单" value="easy" />
                <el-option label="中等" value="medium" />
                <el-option label="困难" value="hard" />
              </el-select>
            </div>

            <!-- 题目表格 -->
            <el-table
              v-loading="loading"
              :data="filteredProblems"
              stripe
              style="width: 100%; margin-top: 20px;"
              :default-sort="{prop: 'problemId', order: 'ascending'}"
            >
              <el-table-column
                prop="problemId"
                label="ID"
                width="80"
                sortable
              />

              <el-table-column
                prop="title"
                label="题目名称"
                min-width="200"
              >
                <template slot-scope="scope">
                  <span class="problem-title" @click="handleProblemClick(scope.row)">
                    {{ scope.row.title }}
                  </span>
                </template>
              </el-table-column>

              <el-table-column
                prop="difficulty"
                label="难度"
                width="120"
              >
                <template slot-scope="scope">
                  <el-tag
                    :type="getDifficultyType(scope.row.difficulty)"
                    size="medium"
                  >
                    {{ formatDifficulty(scope.row.difficulty) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column
                label="LeetCode链接"
                width="150"
                align="center"
              >
                <template slot-scope="scope">
                  <el-link
                    v-if="scope.row.leetcodeLink"
                    :href="scope.row.leetcodeLink"
                    target="_blank"
                    type="primary"
                  >
                    前往LeetCode
                  </el-link>
                  <span v-else>无链接</span>
                </template>
              </el-table-column>

              <el-table-column
                label="操作"
                width="120"
                align="center"
              >
                <template slot-scope="scope">
                  <el-button
                    type="primary"
                    size="mini"
                    @click="handleStartPractice(scope.row)"
                  >
                    开始练习
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div v-show="total>0" class="pagination-container">
              <el-pagination
                :background="true"
                :current-page.sync="listQuery.page"
                :page-size.sync="listQuery.size"
                :page-sizes="[5, 10, 20, 30]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
// 算法数据改为使用模拟数据，因为算法模块已移除
import { getProblemList } from '@/api/problem'

export default {
  name: 'AlgorithmLearning',

  data() {
    return {
      // 当前激活的 Tab（算法ID）
      activeTab: null,

      // 选择的难度
      selectedDifficulty: 'all',

      // 加载状态
      loading: false,

      // 从后端获取的算法数据
      backendAlgorithms: [],

      // 当前算法的问题列表
      problemList: [],

      // 分页参数
      listQuery: {
        page: 1,
        size: 10,
        algorithmId: null,
        difficulty: null
      },

      // 分页总数
      total: 0,

      // 静态算法内容（用于展示，与数据库algorithm表ID对应：1=A*, 2=Dijkstra, 4=DFS, 5=BFS）
      staticAlgorithmContent: {
        1: {
          features: ['启发式搜索算法', '结合Dijkstra和贪心算法', '使用估价函数 f(n)=g(n)+h(n)', '保证找到最优解（可采纳启发函数）'],
          principle: `<p><strong>A*（A-Star）算法</strong>是一种启发式搜索算法，结合了 Dijkstra 算法的优点和贪心最佳优先搜索的效率。其基本原理如下：</p>
<ol>
  <li><strong>估价函数：</strong>使用 <strong>f(n) = g(n) + h(n)</strong> 来评估每个节点的优先级：
    <ul>
      <li><strong>g(n)：</strong>从起点到当前节点 n 的实际代价（已走过的距离）。</li>
      <li><strong>h(n)：</strong>从当前节点 n 到目标节点的估计代价（启发式函数），如曼哈顿距离或欧几里得距离。</li>
      <li><strong>f(n)：</strong>综合评估，值越小优先级越高，优先被探索。</li>
    </ul>
  </li>
  <li><strong>开放列表与关闭列表：</strong>
    <ul>
      <li><strong>开放列表（Open Set）：</strong>存储待评估的节点，按 f(n) 值排序。</li>
      <li><strong>关闭列表（Closed Set）：</strong>存储已评估过的节点，避免重复处理。</li>
    </ul>
  </li>
  <li><strong>启发式搜索：</strong>通过启发函数 h(n) 引导搜索方向，优先探索更有可能通向目标的路径，从而减少搜索范围。</li>
  <li><strong>最优性保证：</strong>当启发函数 h(n) 是<strong>可采纳的</strong>（即 h(n) ≤ 实际代价）且<strong>一致的</strong>时，A* 保证找到最短路径。</li>
</ol>
<p><strong>核心思想：</strong>既考虑已经走过的实际距离（g(n)），又预估到目标还有多远（h(n)），两者结合做出最明智的下一步选择。</p>`,
          pseudoCode: `function AStar(start, target):
  openSet = new PriorityQueue()
  gScore = new Map()
  fScore = new Map()

  gScore[start] = 0
  fScore[start] = heuristic(start, target)
  openSet.enqueue(start, fScore[start])

  while openSet is not empty:
    current = openSet.dequeue()

    if current == target:
      return reconstructPath(cameFrom, current)

    for neighbor in current.neighbors:
      tentative_gScore = gScore[current] + distance(current, neighbor)

      if tentative_gScore < gScore.get(neighbor, Infinity):
        cameFrom[neighbor] = current
        gScore[neighbor] = tentative_gScore
        fScore[neighbor] = gScore[neighbor] + heuristic(neighbor, target)

        if neighbor not in openSet:
          openSet.enqueue(neighbor, fScore[neighbor])

  return null`
        },
        2: {
          features: ['使用优先队列（最小堆）', '贪心策略：每次选择距离起点最近的节点', '保证找到最短路径（带权图）', '时间复杂度：O((V+E)logV)'],
          principle: `<p><strong>Dijkstra（迪杰斯特拉）算法</strong>是一种用于计算图中单源最短路径的经典算法。其基本原理如下：</p>
<ol>
  <li><strong>贪心策略：</strong>每次从<strong>未处理</strong>的节点中选择距离起点<strong>最近</strong>的节点进行扩展，确保该节点的最短路径已被确定。</li>
  <li><strong>距离更新：</strong>对于当前选中的节点，检查其所有邻居，如果通过当前节点到达邻居的路径比已知路径更短，则更新邻居的最短距离（松弛操作）。</li>
  <li><strong>优先队列：</strong>使用<strong>最小堆（优先队列）</strong>来高效地获取当前距离起点最近的未处理节点，时间复杂度为 O(logV)。</li>
  <li><strong>路径记录：</strong>使用 prev 数组记录每个节点的前驱节点，算法结束后可以通过回溯得到完整的最短路径。</li>
</ol>
<p><strong>核心思想：</strong>从起点出发，不断选择当前已知最短距离的节点进行扩展，像涟漪一样逐步确定所有节点的最短路径。适用于<strong>带权图</strong>（权重非负）。</p>`,
          pseudoCode: `function Dijkstra(start, target):
  dist = new Map()
  prev = new Map()
  pq = new PriorityQueue()

  dist[start] = 0
  pq.enqueue(start, 0)

  while pq is not empty:
    current = pq.dequeue()

    if current == target:
      return reconstructPath(prev, current)

    for neighbor in current.neighbors:
      alt = dist[current] + weight(current, neighbor)

      if alt < dist.get(neighbor, Infinity):
        dist[neighbor] = alt
        prev[neighbor] = current
        pq.enqueue(neighbor, alt)

  return null`
        },
        4: {
          features: ['使用栈数据结构或递归', '深度优先搜索', '适合解决连通性问题', '时间复杂度：O(V+E)'],
          principle: `<p><strong>深度优先搜索（DFS）</strong>是一种用于遍历或搜索树或图的算法。其基本原理如下：</p>
<ol>
  <li><strong>深度优先：</strong>从起始节点开始，沿着一条路径尽可能深地探索，直到无法继续或找到目标，然后回溯到上一个分支节点继续探索。</li>
  <li><strong>栈结构：</strong>使用<strong>后进先出（LIFO）</strong>的栈（或递归调用栈）来管理待访问节点，后发现的节点优先被探索。</li>
  <li><strong>回溯机制：</strong>当当前路径无法继续前进时，算法会回溯到上一个有未探索分支的节点，继续搜索其他方向。</li>
  <li><strong>访问标记：</strong>使用 visited 集合记录已访问的节点，防止重复访问和无限循环。</li>
</ol>
<p><strong>核心思想：</strong>不撞南墙不回头——沿着一条路走到底，走不通了再回头换一条路继续探索，直到找到目标或遍历完所有节点。</p>`,
          pseudoCode: `function DFS(node, target, visited):
  if node == target:
    return true

  visited.add(node)

  for neighbor in node.neighbors:
    if neighbor not in visited:
      if DFS(neighbor, target, visited):
        return true

  return false`
        },
        5: {
          features: ['使用队列数据结构', '逐层扩展搜索', '保证找到最短路径（无权图）', '时间复杂度：O(V+E)'],
          principle: `<p><strong>广度优先搜索（BFS）</strong>是一种用于遍历或搜索树或图的算法。其基本原理如下：</p>
<ol>
  <li><strong>逐层扩展：</strong>从起始节点开始，先访问所有距离为1的邻居节点，然后访问距离为2的节点，以此类推，逐层向外扩展。</li>
  <li><strong>队列结构：</strong>使用<strong>先进先出（FIFO）</strong>的队列来管理待访问节点，确保先访问的节点先被处理。</li>
  <li><strong>访问标记：</strong>使用 visited 集合记录已访问的节点，避免重复访问和死循环。</li>
  <li><strong>最短路径：</strong>在<strong>无权图</strong>中，BFS 首次到达目标节点的路径即为最短路径，因为它是按层数递增的顺序进行搜索的。</li>
</ol>
<p><strong>核心思想：</strong>从起点开始，像水波一样一层一层地向外扩散，直到找到目标节点或遍历完所有可达节点。</p>`,
          pseudoCode: `function BFS(start, target):
  queue = new Queue()
  visited = new Set()

  queue.enqueue(start)
  visited.add(start)

  while queue is not empty:
    current = queue.dequeue()

    if current == target:
      return true

    for neighbor in current.neighbors:
      if neighbor not in visited:
        visited.add(neighbor)
        queue.enqueue(neighbor)

  return false`
        }
      }
    }
  },

  computed: {
    // 根据当前Tab和难度筛选过滤题目
    filteredProblems() {
      let filtered = this.problemList || []

      // 过滤掉标题为"默认题目"的记录
      filtered = filtered.filter(problem =>
        problem.title !== '默认题目'
      )

      // 根据难度筛选
      if (this.selectedDifficulty !== 'all') {
        filtered = filtered.filter(problem =>
          problem.difficulty === this.selectedDifficulty
        )
      }

      return filtered
    }
  },

  watch: {
    activeTab(newVal) {
      if (newVal) {
        this.listQuery.algorithmId = parseInt(newVal)
        this.listQuery.page = 1
        this.getProblemList()
      }
    }
  },

  created() {
    this.loadAlgorithms()
  },

  methods: {
    // 加载算法数据 - 使用模拟数据
    async loadAlgorithms() {
      try {
        this.loading = true
        // 模拟算法数据（与数据库algorithm表ID对应：1=A*, 2=Dijkstra, 4=DFS, 5=BFS）
        this.backendAlgorithms = [
          { algorithmId: 1, name: 'A*', description: '启发式搜索算法', complexity: 'O(b^d)' },
          { algorithmId: 2, name: 'Dijkstra', description: '迪杰斯特拉最短路径算法', complexity: 'O((V+E)logV)' },
          { algorithmId: 4, name: 'DFS', description: '深度优先搜索算法', complexity: 'O(V+E)' },
          { algorithmId: 5, name: 'BFS', description: '广度优先搜索算法', complexity: 'O(V+E)' }
        ]

        // 设置默认激活的tab
        if (this.backendAlgorithms.length > 0) {
          this.activeTab = this.backendAlgorithms[0].algorithmId.toString()
          this.listQuery.algorithmId = this.backendAlgorithms[0].algorithmId
        }
      } catch (error) {
        console.error('加载算法数据失败:', error)
        this.$message.error('加载算法数据失败')
      } finally {
        this.loading = false
      }
    },

    // 获取问题列表
    async getProblemList() {
      if (!this.listQuery.algorithmId) return

      try {
        this.loading = true

        // 根据算法ID获取问题列表
        const params = {
          page: this.listQuery.page,
          size: this.listQuery.size,
          algorithmId: this.listQuery.algorithmId
        }

        // 如果选择了难度，添加到参数中
        if (this.selectedDifficulty !== 'all') {
          params.difficulty = this.selectedDifficulty
        }

        console.log('算法学习页面请求参数:', params)
        try {
          const response = await getProblemList(params)
          console.log('算法学习页面API响应:', response)

          // 根据接口规范，response已经是data字段的内容
          // 但需要检查实际的数据结构
          if (response) {
            // 尝试不同的数据结构
            if (response.records !== undefined) {
              // 标准分页结构
              this.problemList = response.records || []
              this.total = response.total || 0
              console.log('算法学习页面：使用标准分页结构，total:', this.total)
            } else if (Array.isArray(response)) {
              // 如果是数组（不分页接口）
              this.problemList = response
              this.total = response.length
              console.log('算法学习页面：使用数组结构，length:', this.total)
            } else if (response.data && response.data.records) {
              // 如果response包含data字段（未拆壳的情况）
              this.problemList = response.data.records || []
              this.total = response.data.total || 0
              console.log('算法学习页面：使用data.records结构，total:', this.total)
            } else if (response.code !== undefined) {
              // 如果响应包含code字段，说明拦截器可能没有正确拆壳
              console.warn('算法学习页面：响应包含code字段，可能拦截器未正确拆壳:', response)
              if (response.data && response.data.records) {
                this.problemList = response.data.records || []
                this.total = response.data.total || 0
                console.log('算法学习页面：从response.data中提取数据，total:', this.total)
              } else {
                this.problemList = []
                this.total = 0
                console.warn('算法学习页面：无法从响应中提取数据:', response)
              }
            } else {
              // 其他情况
              this.problemList = []
              this.total = 0
              console.warn('算法学习页面：未知的响应结构:', response)
            }
          } else {
            this.problemList = []
            this.total = 0
            console.warn('算法学习页面：响应为空')
          }
        } catch (error) {
          // 捕获API调用错误
          console.error('算法学习页面：API调用异常:', error)
          // 检查error.response是否存在，获取原始响应数据
          if (error.response && error.response.data) {
            console.error('算法学习页面：原始响应数据:', error.response.data)
            // 尝试从错误响应中提取数据
            const errorData = error.response.data
            if (errorData.data && errorData.data.records) {
              this.problemList = errorData.data.records || []
              this.total = errorData.data.total || 0
              console.log('算法学习页面：从错误响应中提取数据成功，total:', this.total)
            }
          }
          throw error // 重新抛出错误，让外层的catch处理
        }

        // 前端过滤：过滤掉标题为"默认题目"的记录（双重保障）
        // 注意：不要覆盖total，total应该来自后端的分页结果
        this.problemList = (this.problemList || []).filter(item => item.title !== '默认题目')

        console.log('算法学习页面处理后数据:', {
          problemList: this.problemList,
          total: this.total
        })
      } catch (error) {
        console.error('加载问题列表失败:', error)
        this.$message.error('加载问题列表失败')
        this.problemList = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    // Tab切换处理
    handleTabClick(tab) {
      console.log('切换到算法:', tab.label)
    },

    // 获取算法特点
    getAlgorithmFeatures(algorithmId) {
      const content = this.staticAlgorithmContent[algorithmId]
      return content ? content.features.join('；') : '请参考算法描述'
    },

    // 获取算法基本原理
    getAlgorithmPrinciple(algorithmId) {
      const content = this.staticAlgorithmContent[algorithmId]
      return content ? content.principle : '<p>暂无基本原理描述</p>'
    },

    // 获取算法伪代码
    getAlgorithmPseudoCode(algorithmId) {
      const content = this.staticAlgorithmContent[algorithmId]
      return content ? content.pseudoCode : '// 暂无伪代码'
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

    // 筛选处理
    handleFilter() {
      this.listQuery.page = 1
      this.getProblemList()
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.listQuery.size = val
      this.listQuery.page = 1
      this.getProblemList()
    },

    // 当前页改变
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getProblemList()
    },

    // 处理练习点击
    handleProblemClick(problem) {
      // 保存当前题目信息到本地存储，以便详情页使用
      localStorage.setItem('currentProblem', JSON.stringify(problem))
      // 设置来源页面为learning
      localStorage.setItem('problemDetailSource', 'learning')

      // 跳转到题目详情页
      this.$router.push({
        name: 'ProblemDetail',
        params: {
          id: problem.problemId,
          problem: problem
        }
      })
    },

    // 开始练习
    handleStartPractice(problem) {
      // 保存当前题目信息到本地存储
      localStorage.setItem('currentProblem', JSON.stringify(problem))
      // 设置来源页面为learning
      localStorage.setItem('problemDetailSource', 'learning')

      // 跳转到题目详情页
      this.$router.push({
        name: 'ProblemDetail',
        params: {
          id: problem.problemId,
          problem: problem
        }
      })

      this.$message.success(`开始记录题目：${problem.title}`)
    }
  }
}
</script>

<style scoped>
.algorithm-learning-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
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

/* Tabs样式 */
.el-tabs {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 算法内容区域 */
.algorithm-content {
  padding: 20px;
}

.algorithm-explanation {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.algorithm-explanation h2 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.section {
  margin-bottom: 25px;
}

.section h3 {
  font-size: 16px;
  color: #606266;
  margin-bottom: 12px;
  font-weight: 600;
}

.section p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  text-align: justify;
}

/* 核心思想列表 */
.core-ideas {
  padding-left: 20px;
  margin: 0;
}

.core-ideas li {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 8px;
}

/* 基本原理内容样式 */
.principle-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  text-align: justify;
  background-color: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px 20px;
}

.principle-content p {
  margin-bottom: 12px;
}

.principle-content ol {
  padding-left: 20px;
  margin: 10px 0;
}

.principle-content ol li {
  margin-bottom: 10px;
  line-height: 1.8;
}

.principle-content ul {
  padding-left: 20px;
  margin: 6px 0;
}

.principle-content ul li {
  margin-bottom: 4px;
  line-height: 1.7;
  list-style-type: circle;
}

.principle-content strong {
  color: #409eff;
  font-weight: 600;
}

/* 伪代码样式 */
.pseudo-code {
  background-color: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #24292e;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 相关题目区域 */
.related-problems h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.filter-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

/* 题目链接样式 */
.problem-title {
  color: #409eff;
  text-decoration: underline;
  cursor: pointer;
  transition: color 0.3s;
}

.problem-title:hover {
  color: #66b1ff;
  text-decoration: none;
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  text-align: center;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .algorithm-learning-container {
    padding: 10px;
  }

  .algorithm-content {
    padding: 15px;
  }

  .filter-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-label {
    margin-bottom: 8px;
  }
}
</style>
