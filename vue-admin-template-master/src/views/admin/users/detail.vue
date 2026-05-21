<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>用户详情</span>
        <el-button
          style="float: right;"
          type="primary"
          icon="el-icon-edit"
          @click="handleEdit"
        >
          编辑
        </el-button>
        <el-button
          style="float: right; margin-right: 10px;"
          @click="handleBack"
        >
          返回
        </el-button>
      </div>

      <div v-loading="loading" class="detail-container">
        <!-- 使用简单的div布局 -->
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">用户ID：</span>
            <span class="info-content">{{ userInfo.userId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">学号：</span>
            <span class="info-content">{{ userInfo.studentId || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">用户名：</span>
            <span class="info-content">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">姓名：</span>
            <span class="info-content">{{ userInfo.name }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">邮箱：</span>
            <span class="info-content">{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">角色：</span>
            <span class="info-content">
              <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'success'">
                {{ userInfo.role === 'admin' ? '管理员' : '学生' }}
              </el-tag>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">注册时间：</span>
            <span class="info-content">
              <i class="el-icon-time" />
              {{ userInfo.createdAt || '未知' }}
            </span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getUserByStudentId } from '@/api/user'

export default {
  name: 'UserDetail',
  data() {
    return {
      loading: false,
      userInfo: {}
    }
  },
  watch: {
    '$route': {
      handler: function(to, from) {
        // 当路由变化时重新获取数据
        // 注意：from可能为undefined（首次加载时）
        if (from && to.query.studentId !== from.query.studentId) {
          this.fetchUserDetail()
        } else if (!from) {
          // 首次加载时，如果已经有studentId，则获取数据
          if (to.query.studentId) {
            this.fetchUserDetail()
          }
        }
      },
      immediate: true
    }
  },
  created() {
    this.fetchUserDetail()
  },
  methods: {
    // 获取用户详情
    fetchUserDetail() {
      this.loading = true
      const studentId = this.$route.query.studentId || this.$route.params.studentId

      if (!studentId) {
        this.$message({
          type: 'error',
          message: '学号不存在'
        })
        this.$router.push('/admin/users')
        return
      }

      console.log('调用getUserByStudentId，学号:', studentId)

      getUserByStudentId(studentId)
        .then(response => {
          console.log('getUserByStudentId响应:', response)
          // 根据后端返回的数据结构，response就是用户对象
          // 后端返回格式：R.ok(user)，其中user是用户对象
          const userData = response
          this.userInfo = {
            userId: userData.userId || userData.user_id || '',
            studentId: userData.studentId || studentId,
            username: userData.username || '',
            name: userData.name || userData.username || '',
            email: userData.email || '',
            role: userData.role || 'student',
            createdAt: userData.createdAt || userData.created_at || ''
          }
        })
        .catch(error => {
          this.$message({
            type: 'error',
            message: error.message || '获取用户详情失败'
          })
          this.$router.push('/admin/users')
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 编辑用户
    handleEdit() {
      this.$router.push({
        path: '/admin/users/edit',
        query: { id: this.userInfo.userId }
      })
    },

    // 返回列表
    handleBack() {
      this.$router.push('/admin/users')
    }
  }
}
</script>

<style scoped>
.box-card {
  margin: 20px;
}

.detail-container {
  padding: 20px;
}

.info-list {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fff;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: bold;
  color: #606266;
  min-width: 120px;
  text-align: right;
  margin-right: 20px;
}

.info-content {
  color: #303133;
  flex: 1;
}

.el-tag {
  margin-right: 5px;
}

.el-icon-time {
  margin-right: 5px;
  color: #909399;
}
</style>
