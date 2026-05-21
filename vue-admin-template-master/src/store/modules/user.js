import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: ''
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password }).then(response => {
        // 注意：response已经是响应拦截器提取的data字段
        // 根据后端API格式，response应该包含token字段
        const token = response.token || response.accessToken
        if (!token) {
          return reject(new Error('登录响应中没有找到token'))
        }
        commit('SET_TOKEN', token)
        setToken(token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        // 注意：response已经是响应拦截器提取的data字段
        if (!response) {
          return reject('Verification failed, please Login again.')
        }

        const { name, username, role, email, studentId } = response
        const displayName = name || username || '用户'

        commit('SET_NAME', displayName)
        // 如果没有avatar，使用默认值
        commit('SET_AVATAR', 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif')

        // 保存额外用户信息到localStorage（兼容现有代码）
        localStorage.setItem('user-role', role || '')
        localStorage.setItem('user-info', JSON.stringify({
          name: displayName,
          username: username || '',
          role: role || '',
          email: email || '',
          studentId: studentId || ''
        }))

        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      // 先执行本地清理，然后尝试调用后端logout（如果失败也不影响）
      removeToken() // must remove token first
      resetRouter()
      commit('RESET_STATE')

      // 尝试调用后端logout接口，但忽略错误（因为可能没有实现）
      logout(state.token).then(() => {
        resolve()
      }).catch(() => {
        // 即使后端logout失败，也认为退出成功（已经完成本地清理）
        resolve()
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
