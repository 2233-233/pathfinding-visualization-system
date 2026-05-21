
const tokens = {
  admin: {
    token: 'admin-token'
  },
  editor: {
    token: 'editor-token'
  }
}

const users = {
  'admin-token': {
    roles: ['admin'],
    introduction: 'I am a super administrator',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: 'Super Admin'
  },
  'editor-token': {
    roles: ['editor'],
    introduction: 'I am an editor',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: 'Normal Editor'
  }
}

module.exports = [
  // user login
  {
    url: '/api/user/login',
    type: 'post',
    response: config => {
      const { username, password } = config.body

      // 模拟用户数据
      const mockUsers = [
        { username: 'user', password: '123456', role: 'student', name: '测试学生', studentId: '20210001', email: 'user@example.com' },
        { username: 'admin', password: '123456', role: 'admin', name: '管理员', studentId: '', email: 'admin@example.com' }
      ]

      // 查找用户
      const user = mockUsers.find(u => u.username === username && u.password === password)

      if (!user) {
        return {
          code: 60204,
          message: '用户名或密码错误'
        }
      }

      // 生成token
      const token = `${username}-token-${Date.now()}`

      return {
        code: 20000,
        data: {
          token,
          user: {
            userId: Date.now(),
            username: user.username,
            role: user.role,
            name: user.name,
            studentId: user.studentId,
            email: user.email
          }
        }
      }
    }
  },

  // user register
  {
    url: '/api/user/register',
    type: 'post',
    response: config => {
      const { username, password, email, name, studentId } = config.body

      // 验证必填字段 - 模拟后端验证
      if (!username || username.trim().length < 3) {
        return {
          code: 400,
          msg: '用户名不能少于3位',
          data: null
        }
      }

      if (!password || password.trim().length < 6) {
        return {
          code: 400,
          msg: '密码不能少于6位',
          data: null
        }
      }

      if (!studentId || studentId.trim().isEmpty()) {
        return {
          code: 400,
          msg: '学号不能为空',
          data: null
        }
      }

      // 模拟用户名已存在检查
      const existingUsers = ['user', 'admin']
      if (existingUsers.includes(username)) {
        return {
          code: 400,
          msg: '用户名已存在',
          data: null
        }
      }

      // 模拟学号已存在检查
      const existingStudentIds = ['20210001']
      if (existingStudentIds.includes(studentId)) {
        return {
          code: 400,
          msg: '学号已存在',
          data: null
        }
      }

      // 生成用户ID和token
      const userId = Date.now()
      const token = `${username}-token-${Date.now()}`

      // 模拟成功响应 - 根据后端Java代码的格式
      return {
        code: 201,
        msg: '注册成功',
        data: {
          token,
          user: {
            userId,
            username,
            role: 'student', // 注册用户默认为学生
            name: name || '',
            studentId,
            email: email || '',
            createdAt: new Date().toISOString()
          },
          expiresAt: new Date(Date.now() + 86400000).getTime(),
          tokenType: 'Bearer'
        }
      }
    }
  },

  // get user info
  {
    url: '/vue-admin-template/user/info\.*',
    type: 'get',
    response: config => {
      const { token } = config.query
      const info = users[token]

      // mock error
      if (!info) {
        return {
          code: 50008,
          message: 'Login failed, unable to get user details.'
        }
      }

      return {
        code: 20000,
        data: info
      }
    }
  },

  // user logout
  {
    url: '/vue-admin-template/user/logout',
    type: 'post',
    response: _ => {
      return {
        code: 20000,
        data: 'success'
      }
    }
  }
]
