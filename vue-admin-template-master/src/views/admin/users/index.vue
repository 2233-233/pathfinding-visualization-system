<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>用户管理</h1>
      <p class="page-description">管理系统中的所有用户，支持模糊搜索和角色筛选</p>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-container">
        <!-- 用户名搜索 -->
        <div class="filter-item">
          <span class="filter-label">用户名：</span>
          <el-input
            v-model="listQuery.username"
            placeholder="模糊搜索用户名"
            style="width: 200px;"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </div>

        <!-- 姓名搜索 -->
        <div class="filter-item">
          <span class="filter-label">姓名：</span>
          <el-input
            v-model="listQuery.name"
            placeholder="模糊搜索姓名"
            style="width: 200px;"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </div>

        <!-- 学号搜索 -->
        <div class="filter-item">
          <span class="filter-label">学号：</span>
          <el-input
            v-model="listQuery.studentId"
            placeholder="模糊搜索学号"
            style="width: 200px;"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </div>

        <!-- 角色筛选 -->
        <div class="filter-item">
          <span class="filter-label">角色：</span>
          <el-select
            v-model="listQuery.role"
            placeholder="选择角色"
            clearable
            style="width: 120px;"
            @change="handleFilter"
          >
            <el-option label="全部" value="" />
            <el-option label="学生" value="student" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </div>

        <!-- 搜索和重置按钮 -->
        <div class="filter-item">
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleFilter"
          >
            搜索
          </el-button>
          <el-button
            icon="el-icon-refresh"
            @click="resetFilter"
          >
            重置
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 统计信息卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #409eff;">
                <i class="el-icon-user" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.total }}</div>
                <div class="stats-label">用户总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #67c23a;">
                <i class="el-icon-user-solid" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.studentCount }}</div>
                <div class="stats-label">学生总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-icon" style="background-color: #f56c6c;">
                <i class="el-icon-s-custom" />
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ stats.adminCount }}</div>
                <div class="stats-label">管理员总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operation-container">
      <el-button
        type="success"
        icon="el-icon-plus"
        @click="handleAdd"
      >
        添加用户
      </el-button>
    </div>

    <!-- 用户列表表格 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="加载中"
      border
      fit
      highlight-current-row
      style="cursor: pointer;"
      @row-click="handleRowClick"
    >
      <el-table-column
        label="ID"
        prop="userId"
        width="80"
        align="center"
      >
        <template slot-scope="scope">
          {{ scope.row.userId || scope.row.user_id }}
        </template>
      </el-table-column>

      <el-table-column
        label="学号"
        prop="studentId"
        width="120"
        align="center"
      >
        <template slot-scope="scope">
          <span
            v-if="scope.row.studentId"
            class="student-id-link"
            style="color: #409EFF; cursor: pointer; text-decoration: underline;"
            @click.stop="handleStudentIdClick(scope.row)"
          >
            {{ scope.row.studentId }}
          </span>
          <span v-else>
            未设置
          </span>
        </template>
      </el-table-column>

      <el-table-column
        label="用户名"
        prop="username"
        width="120"
        align="center"
      >
        <template slot-scope="scope">
          {{ scope.row.username }}
        </template>
      </el-table-column>

      <el-table-column
        label="姓名"
        prop="name"
        width="120"
        align="center"
      >
        <template slot-scope="scope">
          {{ scope.row.name || scope.row.username }}
        </template>
      </el-table-column>

      <el-table-column
        label="邮箱"
        prop="email"
        align="center"
      >
        <template slot-scope="scope">
          {{ scope.row.email }}
        </template>
      </el-table-column>

      <el-table-column
        label="角色"
        prop="role"
        width="100"
        align="center"
      >
        <template slot-scope="scope">
          <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'success'">
            {{ scope.row.role === 'admin' ? '管理员' : '学生' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        label="注册时间"
        prop="createdAt"
        width="180"
        align="center"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" />
          <span>{{ scope.row.createdAt || scope.row.created_at }}</span>
        </template>
      </el-table-column>

      <el-table-column
        label="操作"
        align="center"
        width="200"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-edit"
            @click.stop="handleEdit(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click.stop="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 使用Element UI自带的分页组件 -->
    <div v-show="total>0" class="pagination-container">
      <el-pagination
        :background="true"
        :current-page.sync="listQuery.page"
        :page-size.sync="listQuery.size"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getUserList, deleteUser } from '@/api/user'

export default {
  name: 'UserManagement',
  filters: {
    roleFilter(role) {
      const roleMap = {
        student: '学生',
        admin: '管理员'
      }
      return roleMap[role] || role
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        size: 10,
        username: '',
        name: '',
        role: '',
        studentId: ''
      },
      // 用户统计数据
      stats: {
        total: 0,
        studentCount: 0,
        adminCount: 0
      }
    }
  },
  created() {
    this.fetchData()
    this.loadUserStats()
  },
  methods: {
    // 获取数据（分页）
    fetchData() {
      this.listLoading = true

      // 准备API参数
      const params = {
        page: this.listQuery.page,
        size: this.listQuery.size
      }

      // 添加搜索条件
      if (this.listQuery.username && this.listQuery.username.trim()) {
        params.username = this.listQuery.username.trim()
      }
      if (this.listQuery.name && this.listQuery.name.trim()) {
        params.name = this.listQuery.name.trim()
      }
      if (this.listQuery.role) {
        params.role = this.listQuery.role
      }
      if (this.listQuery.studentId && this.listQuery.studentId.trim()) {
        params.studentId = this.listQuery.studentId.trim()
      }

      console.log('调用getUserList，参数:', params)

      getUserList(params)
        .then(response => {
          console.log('getUserList响应:', response)
          // 后端返回 R<Page<User>>，拦截器已拆壳，response 为 Page 对象
          // Page 对象包含: records, total, current, size, pages 等字段
          if (response && response.records !== undefined) {
            this.list = response.records || []
            this.total = response.total || 0
            // 同步当前页码和每页大小（后端可能做了调整）
            this.listQuery.page = response.current || 1
            this.listQuery.size = response.size || 10
          } else if (Array.isArray(response)) {
            // 如果是数组（不分页接口兜底）
            this.list = response
            this.total = response.length
          } else {
            console.warn('未知的响应结构:', response)
            this.list = []
            this.total = 0
          }
        })
        .catch(error => {
          console.error('getUserList错误:', error)
          this.$message({
            type: 'error',
            message: error.message || '获取用户列表失败'
          })
        })
        .finally(() => {
          this.listLoading = false
        })
    },

    // 加载用户统计数据
    async loadUserStats() {
      try {
        // 获取所有用户（不分页）来统计
        const response = await getUserList({
          page: 1,
          size: 9999
        })
        const allUsers = response.records || response.data || []
        this.stats.total = response.total || allUsers.length || 0
        this.stats.studentCount = allUsers.filter(u => u.role === 'student').length
        this.stats.adminCount = allUsers.filter(u => u.role === 'admin').length
      } catch (error) {
        console.error('获取用户统计信息失败:', error)
      }
    },

    // 搜索
    handleFilter() {
      console.log('搜索参数:', this.listQuery)
      this.listQuery.page = 1
      this.fetchData()
    },

    // 重置筛选
    resetFilter() {
      this.listQuery = {
        page: 1,
        size: 10,
        username: '',
        name: '',
        role: '',
        studentId: ''
      }
      this.fetchData()
    },

    // 添加用户
    handleAdd() {
      this.$router.push('/admin/users/add')
    },

    // 编辑用户
    handleEdit(row) {
      this.$router.push({
        path: '/admin/users/edit',
        query: { id: row.userId || row.user_id }
      })
    },

    // 删除用户
    handleDelete(row) {
      this.$confirm(`确定要删除用户 "${row.name || row.username}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const userId = row.userId || row.user_id
        deleteUser(userId)
          .then(() => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.fetchData()
            this.loadUserStats()
          })
          .catch(error => {
            this.$message({
              type: 'error',
              message: error.message || '删除用户失败'
            })
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },

    // 点击行跳转到详情页（通过学号）
    handleRowClick(row) {
      if (row.studentId) {
        this.$router.push({
          path: '/admin/users/detail',
          query: { studentId: row.studentId }
        })
      } else {
        this.$message({
          type: 'warning',
          message: '该用户没有设置学号，无法查看详情'
        })
      }
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.listQuery.size = val
      this.listQuery.page = 1
      this.fetchData()
    },

    // 当前页改变
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.fetchData()
    },

    // 点击学号跳转到详情页（通过学号）
    handleStudentIdClick(row) {
      if (row.studentId) {
        this.$router.push({
          path: '/admin/users/detail',
          query: { studentId: row.studentId }
        })
      }
    }
  }
}
</script>

<style scoped>
.app-container {
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

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 15px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.stats-section {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.operation-container {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 0;
}

.el-table .cell {
  white-space: nowrap;
}

.el-table .el-button {
  margin: 2px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-item {
    width: 100%;
    margin-bottom: 10px;
  }

  .filter-label {
    min-width: 60px;
  }

  .pagination-container {
    padding: 15px 10px;
  }

  .pagination-container ::v-deep .el-pagination {
    font-size: 12px;
  }

  .pagination-container ::v-deep .el-pagination .btn-prev,
  .pagination-container ::v-deep .el-pagination .btn-next,
  .pagination-container ::v-deep .el-pagination .el-pager li {
    min-width: 28px;
    height: 28px;
    line-height: 28px;
  }
}
</style>
