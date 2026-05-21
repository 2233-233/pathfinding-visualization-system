# 前端API对接文档（修正版）

## 一、问题诊断结果
经过检查发现，前端无法访问后端API的原因是**API路径不匹配**：
- 后端实际路径：`/api/user/users` 或 `/api/auth/users`
- 前端原路径：`/api/users`（缺少 `/user` 或 `/auth` 路径段）

## 二、后端API信息
- **基础URL**：http://localhost:8082
- **认证方式**：Bearer Token (JWT)
- **响应格式**：统一使用以下格式：
  ```json
  {
    "code": 200,      // 200表示成功，其他表示错误
    "data": {...},    // 实际数据
    "message": "成功" // 提示信息
  }
  ```

## 三、需要对接的接口（已修正路径）

### 1. 用户登录接口
- **方法**：POST
- **URL**：`http://localhost:8082/api/user/login` 或 `http://localhost:8082/api/auth/login`
- **请求体**：
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "userId": 1,
        "username": "admin",
        "role": "admin",
        "email": "admin@example.com"
      },
      "expiresAt": 1737878400000,
      "tokenType": "Bearer"
    },
    "message": "登录成功"
  }
  ```

### 2. 用户列表接口（已修正路径）
- **方法**：GET
- **URL**：`http://localhost:8082/api/user/users?page=1&size=10&username=搜索词&role=角色`
  - **注意**：也可以使用 `/api/auth/users`，两者功能相同
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "records": [
        {
          "userId": 1,
          "username": "admin",
          "role": "admin",
          "email": "admin@example.com",
          "name": "系统管理员",
          "studentId": "",
          "createdAt": "2026-01-22 16:08:00"
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    },
    "message": "成功"
  }
  ```

### 3. 用户详情接口（已修正路径）
- **方法**：GET
- **URL**：`http://localhost:8082/api/user/users/{userId}`
  - **注意**：也可以使用 `/api/auth/users/{userId}`
- **请求头**：`Authorization: Bearer {token}`

### 4. 创建用户接口（已修正路径）
- **方法**：POST
- **URL**：`http://localhost:8082/api/user/users`
  - **注意**：也可以使用 `/api/auth/users`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "username": "新用户名",
    "password": "密码",
    "name": "姓名",
    "email": "邮箱@example.com",
    "studentId": "学号",
    "role": "student"
  }
  ```

### 5. 更新用户接口（已修正路径）
- **方法**：PUT
- **URL**：`http://localhost:8082/api/user/users/{userId}`
  - **注意**：也可以使用 `/api/auth/users/{userId}`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "name": "新姓名",
    "email": "新邮箱@example.com",
    "studentId": "新学号",
    "role": "student"
  }
  ```

### 6. 删除用户接口（已修正路径）
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/user/users/{userId}`
  - **注意**：也可以使用 `/api/auth/users/{userId}`
- **请求头**：`Authorization: Bearer {token}`

### 7. 获取当前用户信息
- **方法**：GET
- **URL**：`http://localhost:8082/api/user/info` 或 `http://localhost:8082/api/auth/info`
- **请求头**：`Authorization: Bearer {token}`

### 8. 用户登出接口
- **方法**：POST
- **URL**：`http://localhost:8082/api/user/logout` 或 `http://localhost:8082/api/auth/logout`
- **请求头**：`Authorization: Bearer {token}`

## 四、前端API函数示例（已修正）

### 1. 基础请求函数（通常位于 utils/request.js）
```javascript
import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8082',
  timeout: 5000
})

// 请求拦截器 - 添加token
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应格式
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 处理错误
      console.error('API错误:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default service
```

### 2. 用户API模块（api/user.js - 已修正路径）
```javascript
import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/user/login',  // 或 '/api/auth/login'
    method: 'post',
    data
  })
}

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/api/user/users',  // 已修正：添加了 /user 路径段
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserById(userId) {
  return request({
    url: `/api/user/users/${userId}`,  // 已修正：添加了 /user 路径段
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/user/users',  // 已修正：添加了 /user 路径段
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(userId, data) {
  return request({
    url: `/api/user/users/${userId}`,  // 已修正：添加了 /user 路径段
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(userId) {
  return request({
    url: `/api/user/users/${userId}`,  // 已修正：添加了 /user 路径段
    method: 'delete'
  })
}

// 获取当前用户信息
export function getCurrentUserInfo() {
  return request({
    url: '/api/user/info',  // 或 '/api/auth/info'
    method: 'get'
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/api/user/logout',  // 或 '/api/auth/logout'
    method: 'post'
  })
}
```

## 五、使用示例

### 1. 在Vue组件中使用
```vue
<script setup>
import { getUserList, login } from '@/api/user'
import { ref } from 'vue'

const userList = ref([])

// 登录示例
async function handleLogin() {
  try {
    const response = await login({
      username: 'admin',
      password: '123456'
    })
    localStorage.setItem('token', response.data.token)
    console.log('登录成功', response.data.user)
  } catch (error) {
    console.error('登录失败', error)
  }
}

// 获取用户列表示例
async function fetchUserList() {
  try {
    const response = await getUserList({
      page: 1,
      size: 10,
      role: 'student'
    })
    userList.value = response.data.records
    console.log('获取用户列表成功', response.data)
  } catch (error) {
    console.error('获取用户列表失败', error)
  }
}
</script>
```

### 2. 在React组件中使用
```jsx
import React, { useState, useEffect } from 'react'
import { getUserList, login } from '@/api/user'

function UserManagement() {
  const [users, setUsers] = useState([])

  useEffect(() => {
    fetchUsers()
  }, [])

  const fetchUsers = async () => {
    try {
      const response = await getUserList({ page: 1, size: 10 })
      setUsers(response.data.records)
    } catch (error) {
      console.error('获取用户列表失败', error)
    }
  }

  return (
    <div>
      <h2>用户列表</h2>
      <ul>
        {users.map(user => (
          <li key={user.userId}>{user.username} - {user.role}</li>
        ))}
      </ul>
    </div>
  )
}
```

## 六、常见问题排查

### 1. 404错误（找不到API）
- **症状**：前端报404错误
- **原因**：API路径不正确
- **解决**：确保使用正确的路径 `/api/user/users` 而不是 `/api/users`

### 2. 401/403错误（权限问题）
- **症状**：返回"未授权"或"禁止访问"
- **原因**：缺少token或token无效
- **解决**：
  1. 检查是否已登录并获取token
  2. 检查token是否已过期
  3. 检查用户角色是否有权限访问该API

### 3. 跨域问题（CORS）
- **症状**：浏览器控制台显示CORS错误
- **原因**：前端和后端域名/端口不同
- **解决**：后端已配置`@CrossOrigin`，确保前端使用正确的端口（8082）

### 4. 参数格式错误
- **症状**：API返回400错误
- **原因**：请求参数格式不正确
- **解决**：
  1. 检查GET请求参数是否正确编码
  2. 检查POST请求体是否为JSON格式
  3. 检查必填参数是否缺失

## 七、测试验证步骤

### 步骤1：验证后端服务
```bash
# 检查端口是否监听
curl http://localhost:8082/api-docs

# 检查Swagger UI
# 浏览器访问：http://localhost:8082/swagger-ui/index.html#
```

### 步骤2：测试API连通性
```bash
# 测试登录接口（无需token）
curl -X POST "http://localhost:8082/api/user/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 测试用户列表接口（需要token）
curl -X GET "http://localhost:8082/api/user/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 步骤3：前端集成测试
1. 先调用登录接口获取token
2. 将token保存到localStorage
3. 使用token调用其他需要认证的接口
4. 验证数据是否正确返回

## 八、注意事项

1. **路径选择**：可以使用 `/api/user/` 或 `/api/auth/` 前缀，两者功能相同
2. **Token管理**：登录成功后务必保存token，并在后续请求中携带
3. **错误处理**：所有API调用都需要添加错误处理
4. **分页参数**：用户列表接口支持分页，默认page=1, size=10
5. **权限控制**：用户管理接口需要管理员权限，普通用户只能查看自己的信息

## 九、实验记录管理接口（新增）

### 1. 实验记录CRUD接口

#### 1.1 获取实验记录列表（分页）
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments?page=1&size=10&userId=用户ID&algorithmId=算法ID&status=状态`
- **请求头**：`Authorization: Bearer {token}`
- **参数说明**：
  - `page`：页码，默认1
  - `size`：每页大小，默认10
  - `userId`：用户ID（可选，用于筛选）
  - `algorithmId`：算法ID（可选，用于筛选）
  - `status`：状态（可选，completed/failed）
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "records": [
        {
          "recordId": 1,
          "userId": 1,
          "problemId": 1,
          "algorithmId": 1,
          "startTime": "2026-01-29 10:00:00",
          "endTime": "2026-01-29 10:05:00",
          "pathLength": 15,
          "visitedCount": 30,
          "status": "completed"
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    },
    "message": "成功"
  }
  ```

#### 1.2 获取实验记录列表（不分页）
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/all?userId=用户ID&algorithmId=算法ID&status=状态`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "recordId": 1,
        "userId": 1,
        "problemId": 1,
        "algorithmId": 1,
        "startTime": "2026-01-29 10:00:00",
        "endTime": "2026-01-29 10:05:00",
        "pathLength": 15,
        "visitedCount": 30,
        "status": "completed"
      }
    ],
    "message": "成功"
  }
  ```

#### 1.3 获取实验记录详情
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/{id}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "recordId": 1,
      "userId": 1,
      "problemId": 1,
      "algorithmId": 1,
      "startTime": "2026-01-29 10:00:00",
      "endTime": "2026-01-29 10:05:00",
      "pathLength": 15,
      "visitedCount": 30,
      "status": "completed"
    },
    "message": "成功"
  }
  ```

#### 1.4 创建实验记录（开始实验）
- **方法**：POST
- **URL**：`http://localhost:8082/api/experiments`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "problemId": 1,
    "algorithmId": 1,
    "startTime": "2026-01-29 10:00:00",
    "status": "completed"
  }
  ```
- **注意**：用户ID会自动从token中获取，无需前端传递

#### 1.5 更新实验记录（完成实验）
- **方法**：PUT
- **URL**：`http://localhost:8082/api/experiments/{id}`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "endTime": "2026-01-29 10:05:00",
    "pathLength": 15,
    "visitedCount": 30,
    "status": "completed"
  }
  ```

#### 1.6 删除实验记录
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/experiments/{id}`
- **请求头**：`Authorization: Bearer {token}`

### 2. 实验步骤管理接口

#### 2.1 获取实验步骤列表
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/{id}/steps`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "stepId": 1,
        "recordId": 1,
        "stepIndex": 1,
        "nodeX": 0,
        "nodeY": 0,
        "nodeState": "visited",
        "gridState": "{\"grid\": [[0,1,0], [1,0,1]]}"
      }
    ],
    "message": "成功"
  }
  ```

#### 2.2 添加实验步骤
- **方法**：POST
- **URL**：`http://localhost:8082/api/experiments/{id}/steps`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "stepIndex": 1,
    "nodeX": 0,
    "nodeY": 0,
    "nodeState": "visited",
    "gridState": "{\"grid\": [[0,1,0], [1,0,1]]}"
  }
  ```

#### 2.3 更新实验步骤
- **方法**：PUT
- **URL**：`http://localhost:8082/api/experiments/{id}/steps/{stepId}`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "stepIndex": 2,
    "nodeX": 1,
    "nodeY": 1,
    "nodeState": "path",
    "gridState": "{\"grid\": [[0,1,0], [1,1,1]]}"
  }
  ```

### 3. 实验统计接口

#### 3.1 获取用户实验统计
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/stats/user/{userId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "totalExperiments": 10,
      "completedExperiments": 8,
      "failedExperiments": 2,
      "averagePathLength": 15.5,
      "averageVisitedCount": 32.1
    },
    "message": "成功"
  }
  ```

#### 3.2 获取算法实验统计
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/stats/algorithm/{algorithmId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "totalExperiments": 25,
      "completedExperiments": 20,
      "failedExperiments": 5,
      "userDistribution": {
        "1": 10,
        "2": 8,
        "3": 7
      },
      "successRate": 80.0
    },
    "message": "成功"
  }
  ```

### 4. 兼容接口（原有接口）

#### 4.1 保存实验记录（兼容接口）
- **方法**：POST
- **URL**：`http://localhost:8082/api/experimentRecord/saveRecord`
- **请求体**：
  ```json
  {
    "userId": 1,
    "problemId": 1,
    "algorithmId": 1,
    "startTime": "2026-01-29 10:00:00",
    "status": "completed"
  }
  ```

#### 4.2 获取用户实验记录列表（兼容接口）
- **方法**：GET
- **URL**：`http://localhost:8082/api/experimentRecord/listByUser?userId=1`

### 5. 前端API函数示例（实验记录管理）

```javascript
import request from '@/utils/request'

// 实验记录管理API
export const experimentApi = {
  // 获取实验记录列表（分页）
  getExperimentList(params) {
    return request({
      url: '/api/experiments',
      method: 'get',
      params
    })
  },

  // 获取实验记录列表（不分页）
  getExperimentListAll(params) {
    return request({
      url: '/api/experiments/all',
      method: 'get',
      params
    })
  },

  // 获取实验记录详情
  getExperimentById(id) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'get'
    })
  },

  // 创建实验记录
  createExperiment(data) {
    return request({
      url: '/api/experiments',
      method: 'post',
      data
    })
  },

  // 更新实验记录
  updateExperiment(id, data) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'put',
      data
    })
  },

  // 删除实验记录
  deleteExperiment(id) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'delete'
    })
  },

  // 获取实验步骤列表
  getExperimentSteps(recordId) {
    return request({
      url: `/api/experiments/${recordId}/steps`,
      method: 'get'
    })
  },

  // 添加实验步骤
  addExperimentStep(recordId, data) {
    return request({
      url: `/api/experiments/${recordId}/steps`,
      method: 'post',
      data
    })
  },

  // 更新实验步骤
  updateExperimentStep(recordId, stepId, data) {
    return request({
      url: `/api/experiments/${recordId}/steps/${stepId}`,
      method: 'put',
      data
    })
  },

  // 获取用户实验统计
  getUserExperimentStats(userId) {
    return request({
      url: `/api/experiments/stats/user/${userId}`,
      method: 'get'
    })
  },

  // 获取算法实验统计
  getAlgorithmExperimentStats(algorithmId) {
    return request({
      url: `/api/experiments/stats/algorithm/${algorithmId}`,
      method: 'get'
    })
  },

  // 兼容接口：保存实验记录
  saveRecord(data) {
    return request({
      url: '/api/experimentRecord/saveRecord',
      method: 'post',
      data
    })
  },

  // 兼容接口：获取用户实验记录列表
  listByUser(userId) {
    return request({
      url: '/api/experimentRecord/listByUser',
      method: 'get',
      params: { userId }
    })
  }
}
```

### 6. 使用示例

```javascript
// 在Vue组件中使用
import { experimentApi } from '@/api/experiment'

// 获取实验记录列表
async function fetchExperiments() {
  try {
    const response = await experimentApi.getExperimentList({
      page: 1,
      size: 10,
      userId: 1,
      status: 'completed'
    })
    console.log('实验记录列表:', response.data.records)
  } catch (error) {
    console.error('获取实验记录失败:', error)
  }
}

// 创建实验记录
async function createNewExperiment() {
  try {
    const response = await experimentApi.createExperiment({
      problemId: 1,
      algorithmId: 1,
      startTime: new Date().toISOString(),
      status: 'completed'
    })
    console.log('创建实验记录成功:', response.message)
  } catch (error) {
    console.error('创建实验记录失败:', error)
  }
}

// 获取用户实验统计
async function getUserStats() {
  try {
    const response = await experimentApi.getUserExperimentStats(1)
    console.log('用户实验统计:', response.data)
  } catch (error) {
    console.error('获取用户统计失败:', error)
  }
}
```

## 十、联系支持

如果按照此文档修改后仍有问题，请检查：
1. 后端服务是否正常运行（端口8082）
2. 数据库连接是否正常
3. 用户表中是否有测试数据
4. 控制台是否有错误日志

---

**文档版本**：2.0  
**最后更新**：2026-01-29  
**适用后端版本**：Spring Boot + MyBatis-Plus  
**适用前端框架**：Vue 3 / React
