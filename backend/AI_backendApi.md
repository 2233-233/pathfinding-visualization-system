基于对后端项目的详细分析，以下是项目中实际存在的接口列表：

## 一、UserController（用户管理）
### 1. 认证相关接口
- `POST /api/user/register` 或 `POST /api/auth/register` - 用户注册（返回token和用户信息）
- `POST /api/user/login` 或 `POST /api/auth/login` - 用户登录（返回token和用户信息）
- `POST /api/auth/frontend-login` - 前端兼容登录（返回格式匹配前端Vue项目的Mock数据）
- `POST /api/user/logout` 或 `POST /api/auth/logout` - 用户登出（需要携带有效的JWT token）
- `GET /api/user/info` 或 `GET /api/auth/info` - 获取当前用户信息（需要携带有效的JWT token）

### 2. 用户管理接口（管理员权限）
- `GET /api/user/users` - 获取用户列表（支持分页、按用户名/角色搜索，需要管理员权限）
- `GET /api/user/users/{userId}` - 获取用户详情（需要管理员权限或查看自己）
- `POST /api/user/users` - 创建新用户（需要管理员权限）
- `PUT /api/user/users/{userId}` - 更新用户信息（需要管理员权限或更新自己）
- `DELETE /api/user/users/{userId}` - 删除用户（需要管理员权限）
- `GET /api/user/users/student/{studentId}` - 根据学号获取用户（需要管理员权限）

## 二、AlgorithmController（算法管理）
### 1. 算法CRUD接口
- `GET /api/algorithm/listAll` - 获取所有算法列表
- `GET /api/algorithm/algorithms` - 获取算法列表（支持按算法名称模糊搜索和复杂度筛选）
- `GET /api/algorithm/algorithms/{algorithmId}` - 获取算法详情
- `POST /api/algorithm/algorithms` - 创建新算法
- `PUT /api/algorithm/algorithms/{algorithmId}` - 更新算法信息
- `DELETE /api/algorithm/algorithms/{algorithmId}` - 删除算法
- `GET /api/algorithm/algorithms/search` - 根据算法名称模糊搜索算法

## 三、ProblemController（题目管理）
### 1. 题目CRUD接口
- `GET /api/problem/listByAlgorithm` - 根据算法ID获取问题列表
- `GET /api/problem/problems` - 获取问题列表（支持分页、按标题/描述/难度/算法ID筛选）
- `GET /api/problem/problems/{problemId}` - 获取问题详情
- `POST /api/problem/problems` - 创建新题目
- `PUT /api/problem/problems/{problemId}` - 更新题目信息
- `DELETE /api/problem/problems/{problemId}` - 删除单个题目
- `DELETE /api/problem/problems/batch` - 批量删除题目
- `GET /api/problem/problems/search` - 根据标题或描述模糊搜索问题
- `GET /api/problem/problems/all` - 获取所有问题列表（不分页）

## 四、ExperimentRecordController（实验记录管理）
### 1. 实验记录CRUD接口
- `GET /api/experiments` - 获取实验记录列表（支持分页、按用户ID/算法ID/状态筛选，支持时间段搜索）
- `GET /api/experiments/all` - 获取实验记录列表（不分页，支持按用户ID/算法ID/状态筛选，支持时间段搜索）
- `GET /api/experiments/{id}` - 获取实验记录详情
- `POST /api/experiments` - 创建实验记录（开始实验）
- `PUT /api/experiments/{id}` - 更新实验记录（完成实验）
- `DELETE /api/experiments/{id}` - 删除实验记录

### 2. 实验步骤管理
- `GET /api/experiments/{id}/steps` - 获取实验步骤列表
- `POST /api/experiments/{id}/steps` - 添加实验步骤
- `PUT /api/experiments/{id}/steps/{stepId}` - 更新实验步骤

### 3. 障碍物管理接口
- `GET /api/experiments/{recordId}/obstacles` - 获取实验记录的障碍物列表
- `POST /api/experiments/{recordId}/obstacles/batch` - 批量添加障碍物
- `DELETE /api/experiments/{recordId}/obstacles` - 删除实验记录的所有障碍物
- `DELETE /api/experiments/{recordId}/obstacles/{obstacleId}` - 删除单个障碍物

### 4. 实验统计接口
- `GET /api/experiments/stats/user/{userId}` - 获取用户实验统计
- `GET /api/experiments/stats/algorithm/{algorithmId}` - 获取算法实验统计

### 5. 兼容接口（保持向前兼容）
- `POST /api/experiments/saveRecord` - 保存实验记录（兼容原有接口）
- `GET /api/experiments/listByUser` - 根据用户ID获取实验记录列表（兼容原有接口）

## 五、ProblemCompletionController（题目学习记录管理）
### 1. 学习记录CRUD接口
- `GET /api/problem-completions/getByUserAndProblem` - 获取特定题目的学习记录（查询参数）
- `GET /api/problem-completions` - 获取用户的学习记录列表（支持分页和筛选）
- `GET /api/problem-completions/{userId}/{problemId}` - 获取特定题目的学习记录（路径参数）
- `POST /api/problem-completions` - 创建学习记录
- `PUT /api/problem-completions/{completionId}` - 更新学习记录
- `DELETE /api/problem-completions/{completionId}` - 删除学习记录
- `PUT /api/problem-completions/batch` - 批量更新学习记录

### 2. 学习统计接口
- `GET /api/problem-completions/stats/{userId}` - 获取用户学习统计
- `GET /api/problem-completions/progress/{userId}` - 获取用户学习进度

### 3. 查询接口
- `GET /api/problem-completions/problem/{problemId}` - 按照问题ID查找学习记录
- `GET /api/problem-completions/user/{userId}` - 按照用户ID查找学习记录

### 4. 兼容接口
- `POST /api/problem-completions/saveOrUpdate` - 保存或更新学习记录（兼容原有接口）

## 六、DashboardController（仪表板数据）
注：项目中未发现DashboardController，此部分为计划功能

## 七、数据库表结构对应关系
1. **User表** → UserController
2. **Algorithm表** → AlgorithmController
3. **Problem表** → ProblemController
4. **ExperimentRecord表** → ExperimentRecordController
5. **ExperimentStep表** → ExperimentRecordController（子资源）
6. **Obstacle表** → ExperimentRecordController（子资源，新增）
7. **ProblemCompletion表** → ProblemCompletionController

## 八、技术实现要点
1. **Spring Boot分层架构**：Controller → Service → Mapper → Entity
2. **RESTful API设计**：遵循HTTP方法语义
3. **JWT认证**：基于token的用户认证和授权
4. **角色权限控制**：@PreAuthorize注解实现方法级权限
5. **分页查询**：PageHelper或MyBatis Plus分页插件
6. **参数验证**：Spring Validation注解
7. **统一响应格式**：Result<T>包装类
8. **异常处理**：全局异常处理器

这个文档反映了项目中实际存在的接口，每个接口都对应后端Controller的具体实现。
