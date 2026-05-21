# 前端API对接文档（修正版3.0）

## 更新说明
本版本在2.0版本基础上新增了障碍物管理接口，并修改了实验记录详情接口以包含障碍物信息。

## 新增：障碍物管理接口

### 1. 获取实验记录的障碍物列表
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/{recordId}/obstacles`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
```json
{
  "code": 200,
  "data": [
    {
      "obstacleId": 1,
      "recordId": 1,
      "x": 5,
      "y": 5,
      "obstacleType": "wall",
      "createdAt": "2026-01-29 10:00:00"
    },
    {
      "obstacleId": 2,
      "recordId": 1,
      "x": 5,
      "y": 6,
      "obstacleType": "wall",
      "createdAt": "2026-01-29 10:00:00"
    }
  ],
  "message": "成功"
}
```

### 2. 批量添加障碍物
- **方法**：POST
- **URL**：`http://localhost:8082/api/experiments/{recordId}/obstacles/batch`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：
```json
[
  {
    "x": 5,
    "y": 5,
    "obstacleType": "wall"
  },
  {
    "x": 5,
    "y": 6,
    "obstacleType": "wall"
  }
]
```
- **响应**：
```json
{
  "code": 200,
  "data": {
    "successCount": 2,
    "failedCount": 0,
    "totalCount": 2
  },
  "message": "成功添加2个障碍物"
}
```

### 3. 删除实验记录的所有障碍物
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/experiments/{recordId}/obstacles`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
```json
{
  "code": 200,
  "data": {
    "deletedCount": 5
  },
  "message": "成功删除5个障碍物"
}
```

### 4. 删除单个障碍物
- **方法**：DELETE
- **URL**：`http://localhost:8082/api/experiments/{recordId}/obstacles/{obstacleId}`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：
```json
{
  "code": 200,
  "data": null,
  "message": "成功删除障碍物"
}
```

## 修改：实验记录详情接口（新增障碍物信息）

### 原接口（2.0版本）：
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/{id}`
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

### 新接口（3.0版本，新增障碍物信息）：
- **方法**：GET
- **URL**：`http://localhost:8082/api/experiments/{id}`
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
    "status": "completed",
    "obstacles": [
      {
        "obstacleId": 1,
        "recordId": 1,
        "x": 5,
        "y": 5,
        "obstacleType": "wall",
        "createdAt": "2026-01-29 10:00:00"
      },
      {
        "obstacleId": 2,
        "recordId": 1,
        "x": 5,
        "y": 6,
        "obstacleType": "wall",
        "createdAt": "2026-01-29 10:00:00"
      }
    ],
    "obstacleCount": 2
  },
  "message": "成功"
}
```

## 前端API函数示例（新增障碍物管理）

### 1. 障碍物API模块（api/obstacle.js）
```javascript
import request from '@/utils/request'

// 障碍物管理API
export const obstacleApi = {
  // 获取实验记录的障碍物列表
  getObstaclesByRecordId(recordId) {
    return request({
      url: `/api/experiments/${recordId}/obstacles`,
      method: 'get'
    })
  },

  // 批量添加障碍物
  addObstaclesBatch(recordId, obstacles) {
    return request({
      url: `/api/experiments/${recordId}/obstacles/batch`,
      method: 'post',
      data: obstacles
    })
  },

  // 删除实验记录的所有障碍物
  deleteObstaclesByRecordId(recordId) {
    return request({
      url: `/api/experiments/${recordId}/obstacles`,
      method: 'delete'
    })
  },

  // 删除单个障碍物
  deleteObstacle(recordId, obstacleId) {
    return request({
      url: `/api/experiments/${recordId}/obstacles/${obstacleId}`,
      method: 'delete'
    })
  }
}

export default obstacleApi
```

### 2. 修改实验记录API模块（api/experiment.js）
```javascript
import request from '@/utils/request'

// 实验记录管理API（更新版）
export const experimentApi = {
  // 获取实验记录详情（现在包含障碍物信息）
  getExperimentById(id) {
    return request({
      url: `/api/experiments/${id}`,
      method: 'get'
    })
  },
  
  // 其他原有接口保持不变...
  // getExperimentList, createExperiment, updateExperiment, deleteExperiment等
}

export default experimentApi
```

### 3. 使用示例
```javascript
// 在Vue组件中使用障碍物API
import { obstacleApi } from '@/api/obstacle'
import { experimentApi } from '@/api/experiment'

// 获取实验记录详情（包含障碍物）
async function loadExperimentDetail(recordId) {
  try {
    const response = await experimentApi.getExperimentById(recordId)
    console.log('实验记录详情:', response.data)
    console.log('障碍物数量:', response.data.obstacleCount)
    console.log('障碍物列表:', response.data.obstacles)
  } catch (error) {
    console.error('加载实验记录失败:', error)
  }
}

// 批量添加障碍物
async function saveObstacles(recordId, obstacles) {
  try {
    const response = await obstacleApi.addObstaclesBatch(recordId, obstacles)
    console.log(`成功添加${response.data.successCount}个障碍物`)
  } catch (error) {
    console.error('保存障碍物失败:', error)
  }
}

// 获取障碍物列表
async function loadObstacles(recordId) {
  try {
    const response = await obstacleApi.getObstaclesByRecordId(recordId)
    console.log('障碍物列表:', response.data)
  } catch (error) {
    console.error('加载障碍物失败:', error)
  }
}
```

## 批量操作结果类和删除结果类说明

### 1. BatchResult（批量操作结果类）
用于批量添加障碍物的返回结果，包含以下字段：
- `successCount`：成功添加的数量
- `failedCount`：失败的数量
- `totalCount`：总数量

**设计原因**：
- 批量操作可能部分成功、部分失败
- 前端需要知道具体成功/失败的数量，以便给用户明确的反馈
- 例如：用户批量添加100个障碍物，成功95个，失败5个

### 2. DeleteResult（删除结果类）
用于批量删除障碍物的返回结果，包含以下字段：
- `deletedCount`：实际删除的数量

**设计原因**：
- 批量删除需要确认实际删除了多少个
- 有助于数据一致性和用户确认
- 例如：删除操作返回"成功删除10个障碍物"

## 注意事项

1. **数据一致性**：删除实验记录时会自动删除关联的障碍物
2. **权限控制**：障碍物管理接口遵循与实验记录相同的权限规则
3. **错误处理**：批量操作可能部分失败，需要检查返回的`failedCount`
4. **性能考虑**：大量障碍物时建议使用批量接口

## 版本信息
- **文档版本**：3.0
- **更新日期**：2026-02-03
- **主要更新**：
  1. 新增障碍物管理接口（获取、批量添加、删除）
  2. 修改实验记录详情接口，包含障碍物信息
  3. 新增前端API函数示例
  4. 说明批量操作结果类的设计原因

## 向后兼容性
- 实验记录详情接口返回格式扩展，新增`obstacles`和`obstacleCount`字段
- 原有接口保持不变，不影响现有功能
- 新增接口不影响原有接口的使用
