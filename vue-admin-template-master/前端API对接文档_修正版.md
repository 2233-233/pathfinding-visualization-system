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

### 9. 算法管理接口

#### 9.1 获取所有算法列表
- **方法**：GET
- **URL**：`http://localhost:8082/api/algorithm/listAll`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "algorithmId": 1,
        "name": "A*算法",
        "description": "启发式搜索算法",
        "complexity": "O(b^d)"
      }
    ],
    "message": "成功"
  }
  ```

#### 9.2 获取算法列表（分页+搜索）
- **方法**：GET
- **URL**：`http://localhost:8082/api/algorithm/algorithms?page=1&size=10&name=算法名称&complexity=复杂度`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "records": [
        {
          "algorithmId": 1,
          "name": "A*算法",
          "description": "启发式搜索算法",
          "complexity": "O(b^d)"
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

#### 9.3 获取算法详情
- **方法**：GET
- **URL**：`http://localhost:8082/api/algorithm/algorithms/{algorithmId}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "algorithmId": 1,
      "name": "A*算法",
      "description": "启发式搜索算法",
      "complexity": "O(b^d)"
    },
    "message": "成功"
  }
  ```

#### 9.4 创建算法
- **方法**：POST
- **URL**：`http://localhost:8082/api/algorithm/algorithms`
- **请求体**：
  ```json
  {
    "name": "算法名称",
    "description": "算法描述",
    "complexity": "时间复杂度"
  }
  ```

#### 9.5 更新算法
- **方法**：PUT
- **URL**：`http://localhost:8082/api/algorithm/algorithms/{algorithmId}`
- **请求体**：
  ```json
  {
    "name": "新算法名称",
    "description": "新算法描述",
    "complexity": "新时间复杂度"
  }
  ```

#### 9.6 删除算法
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/algorithm/algorithms/{algorithmId}`

#### 9.7 搜索算法
- **方法**：GET
- **URL**：`http://localhost:8082/api/algorithm/algorithms/search?name=算法名称`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "algorithmId": 1,
        "name": "A*算法",
        "description": "启发式搜索算法",
        "complexity": "O(b^d)"
      }
    ],
    "message": "成功"
  }
  ```

### 10. 题目管理接口

#### 10.1 根据算法ID获取问题列表
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem/listByAlgorithm?algorithmId=1`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "problemId": 1,
        "title": "A*的问题",
        "description": "A*算法实现问题",
        "difficulty": "easy",
        "algorithmId": 1,
        "leetcodeLink": "https://leetcode.com/problems/..."
      }
    ],
    "message": "成功"
  }
  ```

#### 10.2 获取问题列表（分页+搜索）
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem/problems?page=1&size=10&title=标题&description=描述&difficulty=难度&algorithmId=算法ID`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "records": [
        {
          "problemId": 1,
          "title": "A*的问题",
          "description": "A*算法实现问题",
          "difficulty": "easy",
          "algorithmId": 1,
          "leetcodeLink": "https://leetcode.com/problems/..."
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

#### 10.3 获取问题详情
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem/problems/{problemId}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "problemId": 1,
      "title": "A*的问题",
      "description": "A*算法实现问题",
      "difficulty": "easy",
      "algorithmId": 1,
      "leetcodeLink": "https://leetcode.com/problems/..."
    },
    "message": "成功"
  }
  ```

#### 10.4 创建问题
- **方法**：POST
- **URL**：`http://localhost:8082/api/problem/problems`
- **请求体**：
  ```json
  {
    "title": "问题标题",
    "description": "问题描述",
    "difficulty": "easy/medium/hard",
    "algorithmId": 1,
    "leetcodeLink": "https://leetcode.com/problems/..."
  }
  ```

#### 10.5 更新问题
- **方法**：PUT
- **URL**：`http://localhost:8082/api/problem/problems/{problemId}`
- **请求体**：
  ```json
  {
    "title": "新问题标题",
    "description": "新问题描述",
    "difficulty": "新难度",
    "algorithmId": 1,
    "leetcodeLink": "新LeetCode链接"
  }
  ```

#### 10.6 删除单个问题
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/problem/problems/{problemId}`

#### 10.7 批量删除问题
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/problem/problems/batch`
- **请求体**：
  ```json
  [1, 2, 3]  // 问题ID数组
  ```

#### 10.8 搜索问题
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem/problems/search?keyword=搜索关键词`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "problemId": 1,
        "title": "A*的问题",
        "description": "A*算法实现问题",
        "difficulty": "easy",
        "algorithmId": 1,
        "leetcodeLink": "https://leetcode.com/problems/..."
      }
    ],
    "message": "成功"
  }
  ```

#### 10.9 获取所有问题（不分页）
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem/problems/all`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "problemId": 1,
        "title": "A*的问题",
        "description": "A*算法实现问题",
        "difficulty": "easy",
        "algorithmId": 1,
        "leetcodeLink": "https://leetcode.com/problems/..."
      },
      {
        "problemId": 2,
        "title": "Dijkstra算法问题",
        "description": "最短路径算法实现",
        "difficulty": "medium",
        "algorithmId": 2,
        "leetcodeLink": "https://leetcode.com/problems/..."
      }
    ],
    "message": "成功"
  }
  ```

### 11. 学习记录管理接口

#### 11.1 获取学习记录列表
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem-completions?page=1&size=10&userId=用户ID&problemId=问题ID&completionStatus=状态`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "records": [
        {
          "completionId": 1,
          "userId": 1,
          "problemId": 1,
          "completionStatus": "in_progress",
          "attemptCount": 3,
          "isSolved": true,
          "notes": "这道题需要多练习",
          "lastReviewDate": "2026-01-27",
          "difficultyRating": 4,
          "timeSpentMinutes": 120,
          "createdAt": "2026-01-25T10:30:00",
          "updatedAt": "2026-01-27T15:45:00"
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

#### 11.2 获取特定题目的学习记录
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem-completions/{userId}/{problemId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "completionId": 1,
      "userId": 1,
      "problemId": 1,
      "completionStatus": "in_progress",
      "attemptCount": 3,
      "isSolved": true,
      "notes": "这道题需要多练习",
      "lastReviewDate": "2026-01-27",
      "difficultyRating": 4,
      "timeSpentMinutes": 120,
      "createdAt": "2026-01-25T10:30:00",
      "updatedAt": "2026-01-27T15:45:00"
    },
    "message": "成功"
  }
  ```

#### 11.3 创建学习记录
- **方法**：POST
- **URL**：`http://localhost:8082/api/problem-completions`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "userId": 1,
    "problemId": 1,
    "completionStatus": "not_started",
    "attemptCount": 0,
    "isSolved": false,
    "notes": "开始学习这道题",
    "difficultyRating": 3,
    "timeSpentMinutes": 0
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "data": "创建学习记录成功",
    "message": "成功"
  }
  ```

#### 11.4 更新学习记录
- **方法**：PUT
- **URL**：`http://localhost:8082/api/problem-completions/{completionId}`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "completionStatus": "completed",
    "attemptCount": 5,
    "isSolved": true,
    "notes": "终于解决了这道题",
    "lastReviewDate": "2026-01-28",
    "difficultyRating": 4,
    "timeSpentMinutes": 180
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "data": "更新学习记录成功",
    "message": "成功"
  }
  ```

#### 11.5 删除学习记录
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/problem-completions/{completionId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": "删除学习记录成功",
    "message": "成功"
  }
  ```

#### 11.6 批量更新学习记录
- **方法**：PUT
- **URL**：`http://localhost:8082/api/problem-completions/batch`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  [
    {
      "completionId": 1,
      "completionStatus": "completed",
      "attemptCount": 5
    },
    {
      "completionId": 2,
      "completionStatus": "in_progress",
      "attemptCount": 3
    }
  ]
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "data": "批量更新完成，成功更新 2 条记录",
    "message": "成功"
  }
  ```

#### 11.7 获取用户学习统计
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem-completions/stats/{userId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "totalProblems": 10,
      "completedCount": 3,
      "inProgressCount": 5,
      "notStartedCount": 2,
      "solvedCount": 3,
      "totalAttempts": 25,
      "totalTimeSpent": 600,
      "completionRate": 30.0,
      "successRate": 12.0
    },
    "message": "成功"
  }
  ```

#### 11.8 获取用户学习进度
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem-completions/progress/{userId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": {
      "recentActivities": [
        {
          "completionId": 1,
          "userId": 1,
          "problemId": 1,
          "completionStatus": "completed",
          "attemptCount": 5,
          "isSolved": true,
          "updatedAt": "2026-01-28T10:30:00"
        }
      ],
      "totalCount": 1,
      "weeklyCompleted": 2,
      "weeklyTimeSpent": 240,
      "weeklyActivityCount": 5
    },
    "message": "成功"
  }
  ```

#### 11.9 按照问题ID查找学习记录
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem-completions/problem/{problemId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "completionId": 1,
        "userId": 1,
        "problemId": 1,
        "completionStatus": "completed",
        "attemptCount": 5,
        "isSolved": true,
        "updatedAt": "2026-01-28T10:30:00"
      },
      {
        "completionId": 2,
        "userId": 2,
        "problemId": 1,
        "completionStatus": "in_progress",
        "attemptCount": 3,
        "isSolved": false,
        "updatedAt": "2026-01-27T15:45:00"
      }
    ],
    "message": "成功"
  }
  ```

#### 11.10 按照用户ID查找学习记录
- **方法**：GET
- **URL**：`http://localhost:8082/api/problem-completions/user/{userId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
  ```json
  {
    "code": 200,
    "data": [
      {
        "completionId": 1,
        "userId": 1,
        "problemId": 1,
        "completionStatus": "completed",
        "attemptCount": 5,
        "isSolved": true,
        "updatedAt": "2026-01-28T10:30:00"
      },
      {
        "completionId": 3,
        "userId": 1,
        "problemId": 2,
        "completionStatus": "in_progress",
        "attemptCount": 3,
        "isSolved": false,
        "updatedAt": "2026-01-27T15:45:00"
      }
    ],
    "message": "成功"
  }
  ```

#### 11.11 保存或更新学习记录（兼容接口）
- **方法**：POST
- **URL**：`http://localhost:8082/api/problem-completions/saveOrUpdate`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
  ```json
  {
    "userId": 1,
    "problemId": 1,
    "completionStatus": "in_progress",
    "attemptCount": 2,
    "isSolved": false
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "data": null,
    "message": "成功"
  }
  ```

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

### 3. 算法API模块（api/algorithm.js）
```javascript
import request from '@/utils/request'

// 获取所有算法列表
export function listAllAlgorithms() {
  return request({
    url: '/api/algorithm/listAll',
    method: 'get'
  })
}

// 获取算法列表（分页+搜索）
export function getAlgorithmList(params) {
  return request({
    url: '/api/algorithm/algorithms',
    method: 'get',
    params
  })
}

// 获取算法详情
export function getAlgorithmById(algorithmId) {
  return request({
    url: `/api/algorithm/algorithms/${algorithmId}`,
    method: 'get'
  })
}

// 创建算法
export function createAlgorithm(data) {
  return request({
    url: '/api/algorithm/algorithms',
    method: 'post',
    data
  })
}

// 更新算法
export function updateAlgorithm(algorithmId, data) {
  return request({
    url: `/api/algorithm/algorithms/${algorithmId}`,
    method: 'put',
    data
  })
}

// 删除算法
export function deleteAlgorithm(algorithmId) {
  return request({
    url: `/api/algorithm/algorithms/${algorithmId}`,
    method: 'delete'
  })
}

// 搜索算法
export function searchAlgorithmByName(name) {
  return request({
    url: '/api/algorithm/algorithms/search',
    method: 'get',
    params: { name }
  })
}
```

### 4. 题目API模块（api/problem.js）
```javascript
import request from '@/utils/request'

// 根据算法ID获取问题列表
export function listProblemsByAlgorithmId(algorithmId) {
  return request({
    url: '/api/problem/listByAlgorithm',
    method: 'get',
    params: { algorithmId }
  })
}

// 获取问题列表（分页+搜索）
export function getProblemList(params) {
  return request({
    url: '/api/problem/problems',
    method: 'get',
    params
  })
}

// 获取问题详情
export function getProblemById(problemId) {
  return request({
    url: `/api/problem/problems/${problemId}`,
    method: 'get'
  })
}

// 创建问题
export function createProblem(data) {
  return request({
    url: '/api/problem/problems',
    method: 'post',
    data
  })
}

// 更新问题
export function updateProblem(problemId, data) {
  return request({
    url: `/api/problem/problems/${problemId}`,
    method: 'put',
    data
  })
}

// 删除单个问题
export function deleteProblem(problemId) {
  return request({
    url: `/api/problem/problems/${problemId}`,
    method: 'delete'
  })
}

// 批量删除问题
export function batchDeleteProblems(problemIds) {
  return request({
    url: '/api/problem/problems/batch',
    method: 'delete',
    data: problemIds
  })
}

// 搜索问题
export function searchProblems(keyword) {
  return request({
    url: '/api/problem/problems/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取所有问题（不分页）
export function getAllProblems() {
  return request({
    url: '/api/problem/problems/all',
    method: 'get'
  })
}
```

### 5. 学习记录API模块（api/problemCompletion.js）
```javascript
import request from '@/utils/request'

// 获取学习记录列表
export function getProblemCompletionList(params) {
  return request({
    url: '/api/problem-completions',
    method: 'get',
    params
  })
}

// 获取特定题目的学习记录
export function getProblemCompletionByUserAndProblem(userId, problemId) {
  return request({
    url: `/api/problem-completions/${userId}/${problemId}`,
    method: 'get'
  })
}

// 创建学习记录
export function createProblemCompletion(data) {
  return request({
    url: '/api/problem-completions',
    method: 'post',
    data
  })
}

// 更新学习记录
export function updateProblemCompletion(completionId, data) {
  return request({
    url: `/api/problem-completions/${completionId}`,
    method: 'put',
    data
  })
}

// 删除学习记录
export function deleteProblemCompletion(completionId) {
  return request({
    url: `/api/problem-completions/${completionId}`,
    method: 'delete'
  })
}

// 批量更新学习记录
export function batchUpdateProblemCompletions(data) {
  return request({
    url: '/api/problem-completions/batch',
    method: 'put',
    data
  })
}

// 获取用户学习统计
export function getUserLearningStats(userId) {
  return request({
    url: `/api/problem-completions/stats/${userId}`,
    method: 'get'
  })
}

// 获取用户学习进度
export function getUserLearningProgress(userId) {
  return request({
    url: `/api/problem-completions/progress/${userId}`,
    method: 'get'
  })
}

// 按照问题ID查找学习记录
export function getProblemCompletionsByProblemId(problemId) {
  return request({
    url: `/api/problem-completions/problem/${problemId}`,
    method: 'get'
  })
}

// 按照用户ID查找学习记录
export function getProblemCompletionsByUserId(userId) {
  return request({
    url: `/api/problem-completions/user/${userId}`,
    method: 'get'
  })
}

// 保存或更新学习记录（兼容接口）
export function saveOrUpdateProblemCompletion(data) {
  return request({
    url: '/api/problem-completions/saveOrUpdate',
    method: 'post',
    data
  })
}
```
