import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    const token = getToken()
    if (token) {
      // 使用Bearer Token认证方式
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    const url = response.config.url || ''

    // 检查是否是登录请求 - 登录请求的失败不应该触发重新登录弹窗
    const isLoginRequest = url.includes('/auth/frontend-login') || url.includes('/user/login')
    // 检查是否是登出请求 - 登出请求的失败也不应该显示错误
    const isLogoutRequest = url.includes('/user/logout')

    // 对于登录请求，特殊处理响应格式
    if (isLoginRequest) {
      // 登录请求可能返回不同的响应格式
      // 如果res有code字段且不是200，则认为是错误
      if (res.code !== undefined && res.code !== 200 && res.code !== 20000 && res.code !== 0 && res.code !== 201) {
        const errorMsg = res.msg || res.message || '登录失败'
        Message({
          message: errorMsg,
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(new Error(errorMsg))
      }

      // 登录成功，返回整个响应，让登录页面处理
      return res
    }

    // 非登录请求的处理
    // 根据后端R类的设计，success字段可能表示成功状态
    // 如果res.success为false，或者code不是200/20000/201，则判断为错误
    const isError = (res.success === false) ||
                   (res.code !== undefined && res.code !== 200 && res.code !== 20000 && res.code !== 0 && res.code !== 201)

    if (isError) {
      // 登出请求的失败不显示错误消息
      if (!isLogoutRequest) {
        // 显示错误消息，但不显示重新登录弹窗（如果是登录请求）
        const errorMsg = res.msg || res.message || '请求失败'
        Message({
          message: errorMsg,
          type: 'error',
          duration: 5 * 1000
        })
      }

      // 只有非登录请求且是认证错误(401/403)时才处理登录过期
      if (!isLoginRequest && (res.code === 401 || res.code === 403)) {
        // 清除本地存储的登录信息
        localStorage.removeItem('user-token')
        localStorage.removeItem('user-role')
        localStorage.removeItem('user-info')
        // 同时清除Cookie中的token
        store.dispatch('user/resetToken')

        // 显示登录过期提示
        Message({
          message: '登录已过期，请重新登录',
          type: 'warning',
          duration: 3 * 1000
        })

        // 直接跳转到登录页，而不是弹窗确认
        // 使用setTimeout确保消息显示后再跳转
        setTimeout(() => {
          window.location.href = '/#/login'
        }, 500)
      }
      return Promise.reject(new Error(isLogoutRequest ? '登出请求失败（忽略）' : (res.msg || res.message || '请求失败')))
    } else {
      // 返回data字段，如果data不存在则返回整个响应
      // 注意：后端可能返回data字段为null，这时返回整个res
      if (res.data !== undefined && res.data !== null) {
        return res.data
      } else {
        return res
      }
    }
  },
  error => {
    console.log('err' + error) // for debug
    // 处理HTTP错误（网络错误、服务器错误等）
    const url = error.config?.url || ''
    const isLogoutRequest = url.includes('/user/logout')

    // 登出请求的网络错误不显示错误消息
    if (!isLogoutRequest) {
      let errorMessage = '网络错误，请检查网络连接'
      if (error.response) {
        // 服务器返回了错误状态码
        const status = error.response.status
        const data = error.response.data || {}

        if (status === 401 || status === 403) {
          errorMessage = data.msg || data.message || '认证失败，请重新登录'
          // HTTP层面的401/403也清除登录状态并跳转
          localStorage.removeItem('user-token')
          localStorage.removeItem('user-role')
          localStorage.removeItem('user-info')
          store.dispatch('user/resetToken')
          Message({
            message: '登录已过期，请重新登录',
            type: 'warning',
            duration: 3 * 1000
          })
          setTimeout(() => {
            window.location.href = '/#/login'
          }, 500)
        } else if (status === 404) {
          errorMessage = '请求的资源不存在'
        } else if (status === 500) {
          errorMessage = '服务器内部错误'
        } else {
          errorMessage = data.msg || data.message || `请求失败 (${status})`
        }
      } else if (error.request) {
        // 请求已发出但没有收到响应
        errorMessage = '网络连接失败，请检查网络设置'
      }

      Message({
        message: errorMessage,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

export default service
