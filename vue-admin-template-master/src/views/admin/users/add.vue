<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>添加用户</span>
        <el-button
          style="float: right; margin-left: 10px;"
          @click="handleBack"
        >
          返回
        </el-button>
      </div>

      <div class="form-container">
        <el-form
          ref="userForm"
          :model="form"
          :rules="rules"
          label-width="100px"
          label-position="right"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              style="width: 300px;"
            />
          </el-form-item>

          <el-form-item label="姓名" prop="name">
            <el-input
              v-model="form.name"
              placeholder="请输入姓名"
              style="width: 300px;"
            />
          </el-form-item>

          <el-form-item label="学号" prop="studentId">
            <el-input
              v-model="form.studentId"
              placeholder="请输入学号（可选）"
              style="width: 300px;"
            />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱"
              style="width: 300px;"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              style="width: 300px;"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              style="width: 300px;"
              show-password
            />
          </el-form-item>

          <el-form-item label="角色" prop="role">
            <el-select
              v-model="form.role"
              placeholder="请选择角色"
              style="width: 300px;"
            >
              <el-option label="学生" value="student" />
              <el-option label="管理员" value="admin" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="handleSubmit"
            >
              提交
            </el-button>
            <el-button @click="handleReset">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import { createUser } from '@/api/user'

export default {
  name: 'AddUser',
  data() {
    // 密码验证规则
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.form.confirmPassword !== '') {
          this.$refs.userForm.validateField('confirmPassword')
        }
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      form: {
        username: '',
        password: '',
        name: '',
        email: '',
        studentId: '',
        role: 'student'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        studentId: [
          { required: false, message: '请输入学号', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      },
      loading: false
    }
  },
  methods: {
    // 提交表单
    handleSubmit() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.loading = true

          // 准备提交数据，移除confirmPassword字段
          const submitData = {
            username: this.form.username,
            password: this.form.password,
            name: this.form.name,
            email: this.form.email,
            studentId: this.form.studentId,
            role: this.form.role
          }

          createUser(submitData)
            .then(response => {
              this.$message({
                type: 'success',
                message: '用户添加成功!'
              })

              // 延迟跳转
              setTimeout(() => {
                this.$router.push('/admin/users')
              }, 1000)
            })
            .catch(error => {
              this.$message({
                type: 'error',
                message: error.message || '添加用户失败'
              })
            })
            .finally(() => {
              this.loading = false
            })
        } else {
          this.$message({
            type: 'error',
            message: '请检查表单填写是否正确'
          })
          return false
        }
      })
    },

    // 重置表单
    handleReset() {
      this.$refs.userForm.resetFields()
      this.form.role = 'student'
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

.form-container {
  padding: 20px 50px;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>
