<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">寻路算法可视化教学平台</h3>
        <p class="subtitle">毕业设计演示系统</p>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="用户名 / 学号"
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
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">登录</el-button>

      <!-- 注册按钮 -->
      <div style="text-align:center;margin-top:20px;">
        <el-button type="text" @click="goToRegister">还没有账号？立即注册</el-button>
      </div>

    </el-form>
  </div>
</template>

<script>
import { login } from '@/api/user'
import { setToken } from '@/utils/auth'
import NProgress from 'nprogress' // 添加导入
import 'nprogress/nprogress.css' // 添加导入

export default {
  name: 'Login',
  data() {
    // 表单验证规则
    const validateUsername = (rule, value, callback) => {
      if (!value || value.trim() === '') {
        callback(new Error('请输入用户名或学号'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (!value || value.trim() === '') {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }

    return {
      // 登录表单数据
      loginForm: {
        username: '',
        password: ''
      },
      // 表单验证规则
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
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

    // 跳转到注册页面
    goToRegister() {
      this.$router.push('/register')
    },

    // 处理登录
    async handleLogin() {
      // 表单验证
      const valid = await this.$refs.loginForm.validate()
      if (!valid) {
        return false
      }

      this.loading = true
      NProgress.start() // 添加进度条开始

      try {
        // 调用真实后端API - 错误处理已经在请求拦截器中完成
        const response = await login(this.loginForm)
        console.log('登录成功响应:', response)

        // 处理不同的响应格式
        let token = null
        let user = {}

        // 情况1: response是data字段的内容（旧格式）
        if (response.token) {
          token = response.token
          user = response.user || {}
        } else if (response.data && response.data.token) {
          // 情况2: response包含code和data字段（新格式）
          token = response.data.token
          user = response.data.user || {}
        } else if (response.user) {
          // 情况3: response直接包含user和token（其他格式）
          user = response.user
          token = response.user.token || response.token
        }

        console.log('登录成功 - 用户数据:', user)

        if (token) {
          setToken(token)
        }

        // 提取用户信息
        const username = user.username || this.loginForm.username
        const role = user.role || (username === 'admin' ? 'admin' : 'student')
        const name = user.name || username
        const studentId = user.studentId || ''
        const userId = user.userId || user.id

        // 保存用户信息到localStorage（兼容现有代码）
        localStorage.setItem('user-token', token || '')
        localStorage.setItem('user-role', role || '')
        localStorage.setItem('user-info', JSON.stringify({
          name: name,
          username: username,
          role: role,
          studentId: studentId,
          userId: userId,
          email: user.email
        }))

        // 调试：检查保存的值
        console.log('保存到localStorage - token:', token)
        console.log('保存到localStorage - role:', role)
        console.log('保存到localStorage - name:', name)
        console.log('保存到localStorage - userId:', userId)

        // 显示成功提示
        this.$message({
          message: `${name}，欢迎登录寻路算法可视化教学平台！`,
          type: 'success',
          duration: 2000
        })

        // 根据角色跳转到不同页面
        setTimeout(() => {
          console.log('跳转前 - 用户角色:', role)
          if (role === 'admin') {
            // 管理员跳转到管理仪表板
            console.log('跳转到管理员仪表板')
            this.$router.push({ path: '/admin/dashboard' })
          } else {
            // 学生跳转到算法可视化页面
            console.log('跳转到学生可视化页面')
            this.$router.push({ path: '/visualization' })
          }
          NProgress.done() // 添加进度条结束
        }, 1000)
      } catch (error) {
        // 错误已经在请求拦截器中处理，这里只需要处理loading状态
        console.log('登录过程捕获到错误（已在前端拦截器中处理）:', error.message)
        NProgress.done() // 确保进度条结束
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
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
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

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;

  .login-form {
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
    .login-form {
      width: 90%;
      padding: 40px 25px;
    }

    .title-container .title {
      font-size: 24px;
    }
  }
}
</style>
