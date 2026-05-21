<template>
  <div class="register-container">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">用户注册</h3>
        <p class="subtitle">寻路算法可视化教学平台</p>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="username"
          v-model="registerForm.username"
          placeholder="用户名"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="registerForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="2"
          auto-complete="on"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="confirmPasswordType"
          ref="confirmPassword"
          v-model="registerForm.confirmPassword"
          :type="confirmPasswordType"
          placeholder="确认密码"
          name="confirmPassword"
          tabindex="3"
          auto-complete="on"
        />
        <span class="show-pwd" @click="showConfirmPwd">
          <svg-icon :icon-class="confirmPasswordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-form-item prop="studentId">
        <span class="svg-container">
          <svg-icon icon-class="form" />
        </span>
        <el-input
          ref="studentId"
          v-model="registerForm.studentId"
          placeholder="学号"
          name="studentId"
          type="text"
          tabindex="4"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="name">
        <span class="svg-container">
          <svg-icon icon-class="form" />
        </span>
        <el-input
          ref="name"
          v-model="registerForm.name"
          placeholder="姓名（可选）"
          name="name"
          type="text"
          tabindex="5"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="email">
        <span class="svg-container">
          <svg-icon icon-class="email" />
        </span>
        <el-input
          ref="email"
          v-model="registerForm.email"
          placeholder="邮箱（可选）"
          name="email"
          type="email"
          tabindex="6"
          auto-complete="on"
        />
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleRegister">注册</el-button>

      <div style="text-align:center;margin-top:20px;">
        <el-button type="text" style="color:#999;font-size:14px;" @click="goToLogin">
          已有账号？立即登录
        </el-button>
      </div>

    </el-form>
  </div>
</template>

<script>
import { register } from '@/api/user'
import { setToken } from '@/utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

export default {
  name: 'Register',
  data() {
    // 密码验证规则
    const validatePassword = (rule, value, callback) => {
      if (!value || value.trim() === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }

    // 确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value || value.trim() === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    // 用户名验证规则
    const validateUsername = (rule, value, callback) => {
      if (!value || value.trim() === '') {
        callback(new Error('请输入用户名'))
      } else if (value.length < 3) {
        callback(new Error('用户名长度不能少于3位'))
      } else {
        callback()
      }
    }

    // 学号验证规则
    const validateStudentId = (rule, value, callback) => {
      if (!value || value.trim() === '') {
        callback(new Error('请输入学号'))
      } else {
        callback()
      }
    }

    // 邮箱验证规则
    const validateEmail = (rule, value, callback) => {
      if (value && value.trim() !== '') {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(value)) {
          callback(new Error('请输入有效的邮箱地址'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }

    return {
      // 注册表单数据
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        studentId: '',
        name: '',
        email: ''
      },
      // 表单验证规则
      registerRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        confirmPassword: [{ required: true, trigger: 'blur', validator: validateConfirmPassword }],
        studentId: [{ required: true, trigger: 'blur', validator: validateStudentId }],
        name: [{ required: false, trigger: 'blur' }],
        email: [{ required: false, trigger: 'blur', validator: validateEmail }]
      },
      loading: false,
      passwordType: 'password',
      confirmPasswordType: 'password'
    }
  },
  methods: {
    // 显示/隐藏密码
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },

    // 显示/隐藏确认密码
    showConfirmPwd() {
      if (this.confirmPasswordType === 'password') {
        this.confirmPasswordType = ''
      } else {
        this.confirmPasswordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.confirmPassword.focus()
      })
    },

    // 跳转到登录页面
    goToLogin() {
      this.$router.push('/login')
    },

    // 处理注册
    async handleRegister() {
      // 表单验证 - Element UI的validate()方法在验证失败时会reject Promise
      try {
        await this.$refs.registerForm.validate()
      } catch (error) {
        // 表单验证失败，直接返回
        console.log('表单验证失败:', error)
        return
      }

      this.loading = true
      NProgress.start()

      try {
        // 准备注册数据
        const registerData = {
          username: this.registerForm.username,
          password: this.registerForm.password,
          studentId: this.registerForm.studentId,
          name: this.registerForm.name || '',
          email: this.registerForm.email || ''
        }

        // 调用注册API
        const response = await register(registerData)
        console.log('注册成功响应:', response)

        // 注意：response已经是响应拦截器提取的data字段
        // 对于注册响应，response包含token、user等字段
        const token = response.token
        const user = response.user || {}

        if (token) {
          setToken(token)
        }

        // 保存用户信息到localStorage
        localStorage.setItem('user-token', token || '')
        localStorage.setItem('user-role', user.role || 'student')
        localStorage.setItem('user-info', JSON.stringify({
          name: user.name || this.registerForm.name,
          username: user.username || this.registerForm.username,
          role: user.role || 'student',
          studentId: user.studentId || this.registerForm.studentId,
          userId: user.userId,
          email: user.email || this.registerForm.email
        }))

        // 显示成功提示
        this.$message({
          message: `注册成功！${user.name || this.registerForm.username}，欢迎加入寻路算法可视化教学平台！`,
          type: 'success',
          duration: 3000
        })

        // 立即跳转到学生页面（算法可视化页面）
        console.log('注册成功，跳转到学生可视化页面')
        this.$router.push({ path: '/visualization' })
        NProgress.done()
      } catch (error) {
        // 错误已经在请求拦截器中处理，这里只需要处理loading状态
        console.log('注册过程捕获到错误（已在前端拦截器中处理）:', error.message)
        NProgress.done()
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .register-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.register-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.register-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;

  .register-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 60px 35px 40px;
    margin: 0 auto;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 10px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;
    text-align: center;
    margin-bottom: 40px;

    .title {
      font-size: 28px;
      color: $light_gray;
      margin: 0px auto 15px auto;
      font-weight: bold;
      letter-spacing: 1px;
    }

    .subtitle {
      font-size: 16px;
      color: $dark_gray;
      margin: 0;
      font-weight: normal;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  // 响应式调整
  @media (max-width: 768px) {
    .register-form {
      width: 90%;
      padding: 40px 25px;
    }

    .title-container .title {
      font-size: 24px;
    }
  }
}
</style>
