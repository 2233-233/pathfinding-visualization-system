import router from './router'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

router.beforeEach(async(to, from, next) => {
  NProgress.start()

  // 检查localStorage中的token
  const token = localStorage.getItem('user-token')
  const userRole = localStorage.getItem('user-role')
  const userInfo = localStorage.getItem('user-info')

  // 调试信息
  console.log('路由守卫 - 当前路径:', to.path)
  console.log('路由守卫 - token存在:', !!token)
  console.log('路由守卫 - 用户角色:', userRole)
  console.log('路由守卫 - 用户信息:', userInfo)

  if (token) {
    // 有token，用户已登录
    if (to.path === '/login') {
      // 已登录用户访问登录页，重定向到首页
      console.log('路由守卫 - 已登录用户访问登录页，角色:', userRole)

      Message.info('您已登录，正在跳转到首页...')

      // 修复：直接使用从localStorage读取的角色，不要设置默认值
      if (userRole === 'admin') {
        console.log('路由守卫 - 重定向到管理员仪表板')
        next({ path: '/admin/dashboard' })
      } else {
        console.log('路由守卫 - 重定向到学生可视化页面')
        next({ path: '/visualization' })
      }
      NProgress.done()
    } else {
      // 已登录用户访问其他页面，直接放行
      console.log('路由守卫 - 已登录用户访问其他页面:', to.path)
      next()
    }
  } else {
    // 没有token，用户未登录
    console.log('路由守卫 - 用户未登录，检查白名单')

    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接放行（登录页、注册页）
      console.log('路由守卫 - 访问白名单页面:', to.path)
      next()
    } else {
      // 不在白名单中，重定向到登录页
      console.log('路由守卫 - 重定向到登录页')
      next('/login')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
