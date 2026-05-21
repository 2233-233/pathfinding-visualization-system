<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 左侧：图表区域 -->
      <el-col :span="14">
        <!-- 折线图：近5天实验生成数 -->
        <div class="chart-card">
          <div class="chart-header">
            <i class="el-icon-data-line"></i>
            <span>近5天实验生成数</span>
          </div>
          <div id="experimentChart" class="chart-container chart-tall"></div>
        </div>

        <!-- 柱状图：近5周新用户注册数 -->
        <div class="chart-card" style="margin-top: 1px;">
          <div class="chart-header">
            <i class="el-icon-user"></i>
            <span>近5周新用户注册数</span>
          </div>
          <div id="userChart" class="chart-container chart-tall"></div>
        </div>
      </el-col>

      <!-- 右侧：统计卡片 -->
      <el-col :span="10">
        <!-- 用户总数 -->
        <div class="stat-card">
          <div class="stat-icon stat-icon-users">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-body">
            <div class="stat-label">用户总数</div>
            <div class="stat-value">{{ dashboardData.totalUsers }}</div>
          </div>
        </div>

        <!-- 题库总数 -->
        <div class="stat-card">
          <div class="stat-icon stat-icon-problems">
            <i class="el-icon-edit-outline"></i>
          </div>
          <div class="stat-body">
            <div class="stat-label">题库总数</div>
            <div class="stat-value">{{ dashboardData.problemStats.total }}</div>
          </div>
        </div>

        <!-- 完成记录总数 -->
        <div class="stat-card">
          <div class="stat-icon stat-icon-completions">
            <i class="el-icon-document-checked"></i>
          </div>
          <div class="stat-body">
            <div class="stat-label">完成记录总数</div>
            <div class="stat-value">{{ dashboardData.totalCompletions }}</div>
          </div>
        </div>

        <!-- 难度分布 -->
        <div class="stat-card">
          <div class="stat-body" style="width: 100%;">
            <div class="stat-label" style="margin-bottom: 12px;">难度分布</div>
            <div class="difficulty-bar">
              <div class="diff-item">
                <span class="diff-dot diff-easy"></span>
                <span class="diff-name">简单</span>
                <span class="diff-num">{{ dashboardData.problemStats.easy }}</span>
              </div>
              <div class="diff-item">
                <span class="diff-dot diff-medium"></span>
                <span class="diff-name">中等</span>
                <span class="diff-num">{{ dashboardData.problemStats.medium }}</span>
              </div>
              <div class="diff-item">
                <span class="diff-dot diff-hard"></span>
                <span class="diff-name">困难</span>
                <span class="diff-num">{{ dashboardData.problemStats.hard }}</span>
              </div>
            </div>
            <div class="diff-progress">
              <div class="diff-progress-bar diff-easy" :style="{ width: easyPercent + '%' }"></div>
              <div class="diff-progress-bar diff-medium" :style="{ width: mediumPercent + '%' }"></div>
              <div class="diff-progress-bar diff-hard" :style="{ width: hardPercent + '%' }"></div>
            </div>
          </div>
        </div>

        <!-- 各算法题目占比饼图 -->
        <div class="chart-card" style="margin-top: 0;">
          <div class="chart-header">
            <i class="el-icon-pie-chart"></i>
            <span>各算法题目占比</span>
          </div>
          <div id="algorithmPieChart" class="chart-container" style="height: 270px;"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 各算法题库分布 -->
    <div class="section-header">
      <i class="el-icon-collection-tag"></i>
      <span>各算法题库分布</span>
    </div>
    <el-row :gutter="20" class="algorithm-row">
      <el-col :span="6" v-for="alg in algorithmList" :key="alg.algorithmId">
        <div class="algorithm-card" :class="'alg-' + alg.algorithmId">
          <div class="alg-name">{{ alg.name }}</div>
          <div class="alg-total">{{ alg.total }} 题</div>
          <div class="alg-divider"></div>
          <div class="alg-difficulties">
            <div class="alg-diff-item">
              <span class="alg-diff-dot diff-easy"></span>
              <span>简单 {{ alg.easy }}</span>
            </div>
            <div class="alg-diff-item">
              <span class="alg-diff-dot diff-medium"></span>
              <span>中等 {{ alg.medium }}</span>
            </div>
            <div class="alg-diff-item">
              <span class="alg-diff-dot diff-hard"></span>
              <span>困难 {{ alg.hard }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboardData } from '@/api/dashboard'
import * as echarts from 'echarts'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      dashboardData: {
        totalUsers: 0,
        totalCompletions: 0,
        problemStats: {
          total: 0,
          easy: 0,
          medium: 0,
          hard: 0,
          byAlgorithm: []
        },
        experimentTrend: [],
        userTrend: []
      }
    }
  },
  computed: {
    algorithmList() {
      return this.dashboardData.problemStats.byAlgorithm || []
    },
    easyPercent() {
      const total = this.dashboardData.problemStats.total
      return total > 0 ? (this.dashboardData.problemStats.easy / total * 100).toFixed(1) : 0
    },
    mediumPercent() {
      const total = this.dashboardData.problemStats.total
      return total > 0 ? (this.dashboardData.problemStats.medium / total * 100).toFixed(1) : 0
    },
    hardPercent() {
      const total = this.dashboardData.problemStats.total
      return total > 0 ? (this.dashboardData.problemStats.hard / total * 100).toFixed(1) : 0
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        // 注意：响应拦截器已经提取了 res.data，所以这里直接拿到数据对象
        const data = await getDashboardData()
        if (data) {
          this.dashboardData = data
          this.$nextTick(() => {
            this.initExperimentChart()
            this.initUserChart()
            this.initAlgorithmPieChart()
          })
        }
      } catch (e) {
        console.error('获取仪表板数据失败:', e)
      }
    },
    initExperimentChart() {
      const chartDom = document.getElementById('experimentChart')
      if (!chartDom) return
      const myChart = echarts.init(chartDom)
      const trend = this.dashboardData.experimentTrend || []

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: trend.map(item => item.date),
          axisLabel: { color: '#909399' },
          axisLine: { lineStyle: { color: '#E4E7ED' } }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          axisLabel: { color: '#909399' },
          splitLine: { lineStyle: { color: '#F2F6FC' } }
        },
        series: [{
          name: '实验数',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { width: 3, color: '#409EFF' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64,158,255,0.3)' },
              { offset: 1, color: 'rgba(64,158,255,0.05)' }
            ])
          },
          itemStyle: { color: '#409EFF' },
          data: trend.map(item => item.count)
        }]
      }
      myChart.setOption(option)
      window.addEventListener('resize', () => myChart.resize())
    },
    initUserChart() {
      const chartDom = document.getElementById('userChart')
      if (!chartDom) return
      const myChart = echarts.init(chartDom)
      const trend = this.dashboardData.userTrend || []

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: trend.map(item => item.weekLabel),
          axisLabel: {
            color: '#909399',
            fontSize: 10,
            interval: 0,
            rotate: 15
          },
          axisLine: { lineStyle: { color: '#E4E7ED' } }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          axisLabel: { color: '#909399' },
          splitLine: { lineStyle: { color: '#F2F6FC' } }
        },
        series: [{
          name: '新用户数',
          type: 'bar',
          barWidth: '40%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#67C23A' },
              { offset: 1, color: 'rgba(103,194,58,0.3)' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          data: trend.map(item => item.count)
        }]
      }
      myChart.setOption(option)
      window.addEventListener('resize', () => myChart.resize())
    },
    initAlgorithmPieChart() {
      const chartDom = document.getElementById('algorithmPieChart')
      if (!chartDom) return
      const myChart = echarts.init(chartDom)
      const list = this.dashboardData.problemStats.byAlgorithm || []

      const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}题 ({d}%)'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            formatter: '{b}\n{d}%',
            fontSize: 11,
            color: '#606266'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: true
          },
          data: list.map((item, index) => ({
            name: item.name,
            value: item.total,
            itemStyle: { color: colors[index % colors.length] }
          }))
        }]
      }
      myChart.setOption(option)
      window.addEventListener('resize', () => myChart.resize())
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
}

// ===== 图表卡片 =====
.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .chart-header {
    margin-bottom: 16px;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 8px;

    i {
      font-size: 18px;
      color: #409EFF;
    }
  }

  .chart-container {
    width: 100%;
    height: 260px;
  }

  .chart-tall {
    height: 320px;
  }
}

// ===== 统计卡片 =====
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: default;
  margin-bottom: 16px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .stat-icon {
    width: 52px;
    height: 52px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    flex-shrink: 0;

    i {
      font-size: 26px;
      color: #fff;
    }
  }

  .stat-icon-users {
    background: linear-gradient(135deg, #409EFF, #337ecc);
  }

  .stat-icon-problems {
    background: linear-gradient(135deg, #67C23A, #529b2e);
  }

  .stat-icon-completions {
    background: linear-gradient(135deg, #E6A23C, #cf9236);
  }

  .stat-body {
    flex: 1;

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 4px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      color: #303133;
    }
  }
}

// ===== 难度分布 =====
.difficulty-bar {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;

  .diff-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #606266;

    .diff-dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
    }

    .diff-num {
      font-weight: 600;
      margin-left: 2px;
    }
  }
}

.diff-progress {
  display: flex;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
  background: #F2F6FC;

  .diff-progress-bar {
    height: 100%;
    transition: width 0.6s ease;
  }
}

.diff-easy {
  background: #67C23A;
}

.diff-medium {
  background: #E6A23C;
}

.diff-hard {
  background: #F56C6C;
}

// ===== 区块标题 =====
.section-header {
  margin: 24px 0 16px;
  padding-left: 12px;
  border-left: 4px solid #409EFF;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    font-size: 18px;
    color: #409EFF;
  }

  span {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

// ===== 算法卡片 =====
.algorithm-row {
  margin-bottom: 20px;
}

.algorithm-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: default;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  .alg-name {
    font-size: 22px;
    font-weight: 700;
    margin-bottom: 4px;
  }

  .alg-total {
    font-size: 13px;
    color: #909399;
    margin-bottom: 14px;
  }

  .alg-divider {
    width: 40px;
    height: 3px;
    border-radius: 2px;
    margin: 0 auto 14px;
    background: #E4E7ED;
  }

  .alg-difficulties {
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;

    .alg-diff-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #606266;

      .alg-diff-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        display: inline-block;
      }
    }
  }
}

.alg-1 {
  .alg-name { color: #409EFF; }
  .alg-divider { background: #409EFF; }
}

.alg-2 {
  .alg-name { color: #67C23A; }
  .alg-divider { background: #67C23A; }
}

.alg-4 {
  .alg-name { color: #E6A23C; }
  .alg-divider { background: #E6A23C; }
}

.alg-5 {
  .alg-name { color: #F56C6C; }
  .alg-divider { background: #F56C6C; }
}
</style>
