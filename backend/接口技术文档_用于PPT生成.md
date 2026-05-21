# 后端接口技术文档（用于PPT生成）

## 项目概述
这是一个基于Spring Boot的路径规划算法学习平台后端系统，提供用户认证、算法管理、题目管理、学习记录和实验记录等功能。

## 技术栈概览

### 后端框架
- **Spring Boot 2.7.18** - 主框架
- **Java 11** - 开发语言

### 数据持久层
- **MyBatis-Plus 3.5.3.1** - ORM框架
- **MySQL 8.0** - 数据库
- **连接池** - Spring Boot默认HikariCP

### 安全认证
- **JWT (JSON Web Token)** - 用户认证和授权
- **jjwt 0.11.5** - JWT实现库

### API文档
- **SpringDoc OpenAPI 1.7.0** - API文档生成
- **Swagger UI** - 可视化API文档

### 其他技术
- **Lombok** - 代码简化
- **Spring Validation** - 参数验证
- **Spring Boot DevTools** - 开发工具

## 接口设计原则

### 1. RESTful设计
- 使用HTTP方法对应CRUD操作：GET(查询)、POST(创建)、PUT(更新)、DELETE(删除)
- 资源路径使用复数名词：`/api/users`、`/api/algorithms`
- 嵌套资源表示关系：`/api/users/{userId}/experiments`

### 2. 统一响应格式
所有接口返回统一的`R<T>`响应对象：
```json
{
  "code": 200,        // 状态码：200成功，401未授权，403禁止，500错误
  "data": {...},      // 响应数据
  "message": "成功"    // 提示信息
}
```

### 3. 权限控制设计
- **基于角色的访问控制(RBAC)**：admin和user两种角色
- **JWT令牌验证**：所有需要认证的接口都需要在Authorization头中携带Bearer token
- **细粒度权限**：管理员可以操作所有资源，普通用户只能操作自己的资源

### 4. 分页和筛选
- 使用MyBatis-Plus的分页插件
- 支持多条件筛选和模糊搜索
- 统一的分页参数：`page`(页码)、`size`(每页大小)

## 接口分类列表

### 1. 用户认证接口 (`UserController`)
**基础路径**：`/api/user` 或 `/api/auth`

| 接口 | 方法 | 路径 | 描述 | 是否需要认证 |
|------|------|------|------|--------------|
| 用户登录 | POST | `/login` | 用户登录，返回JWT token | 否 |
| 前端兼容登录 | POST | `/frontend-login` | 为Vue前端设计的登录接口 | 否 |
| 用户登出 | POST | `/logout` | 用户登出 | 是 |
| 获取当前用户信息 | GET | `/info` | 获取当前登录用户信息 | 是 |
| 获取用户列表 | GET | `/users` | 获取所有用户列表（分页） | 是（管理员） |
| 获取用户详情 | GET | `/users/{userId}` | 根据ID获取用户详情 | 是（管理员或自己） |
| 创建用户 | POST | `/users` | 创建新用户 | 是（管理员） |
| 更新用户 | PUT | `/users/{userId}` | 更新用户信息 | 是（管理员或自己） |
| 删除用户 | DELETE | `/users/{userId}` | 删除用户 | 是（管理员） |
| 按学号查询 | GET | `/users/student/{studentId}` | 根据学号获取用户 | 是（管理员） |

### 2. 算法管理接口 (`AlgorithmController`)
**基础路径**：`/api/algorithm`

| 接口 | 方法 | 路径 | 描述 | 是否需要认证 |
|------|------|------|------|--------------|
| 获取所有算法 | GET | `/listAll` | 获取所有算法列表 | 否 |
| 获取算法列表 | GET | `/algorithms` | 获取算法列表（支持筛选） | 否 |
| 获取算法详情 | GET | `/algorithms/{algorithmId}` | 根据ID获取算法详情 | 否 |
| 创建算法 | POST | `/algorithms` | 创建新算法 | 是 |
| 更新算法 | PUT | `/algorithms/{algorithmId}` | 更新算法信息 | 是 |
| 删除算法 | DELETE | `/algorithms/{algorithmId}` | 删除算法 | 是 |
| 搜索算法 | GET | `/algorithms/search` | 根据名称模糊搜索 | 否 |

### 3. 题目管理接口 (`ProblemController`)
**基础路径**：`/api/problem`

| 接口 | 方法 | 路径 | 描述 | 是否需要认证 |
|------|------|------|------|--------------|
| 按算法获取题目 | GET | `/listByAlgorithm` | 根据算法ID获取问题列表 | 否 |
| 获取题目列表 | GET | `/problems` | 获取问题列表（分页+筛选） | 否 |
| 获取题目详情 | GET | `/problems/{problemId}` | 根据ID获取问题详情 | 否 |
| 创建题目 | POST | `/problems` | 创建新问题 | 是 |
| 更新题目 | PUT | `/problems/{problemId}` | 更新问题信息 | 是 |
| 删除题目 | DELETE | `/problems/{problemId}` | 删除单个问题 | 是 |
| 批量删除题目 | DELETE | `/problems/batch` | 批量删除多个问题 | 是 |
| 搜索题目 | GET | `/problems/search` | 根据关键词模糊搜索 | 否 |
| 获取所有题目 | GET | `/problems/all` | 获取所有问题列表 | 否 |

### 4. 题目完成情况接口 (`ProblemCompletionController`)
**基础路径**：`/api/problem-completions`

| 接口 | 方法 | 路径 | 描述 | 是否需要认证 |
|------|------|------|------|--------------|
| 获取学习记录 | GET | `/getByUserAndProblem` | 根据用户ID和问题ID获取学习记录 | 是 |
| 获取学习记录列表 | GET | ` ` | 获取学习记录列表（分页） | 是 |
| 获取特定学习记录 | GET | `/{userId}/{problemId}` | 根据用户ID和问题ID获取学习记录 | 是 |
| 创建学习记录 | POST | ` ` | 创建新的学习记录 | 是 |
| 更新学习记录 | PUT | `/{completionId}` | 更新学习记录信息 | 是 |
| 删除学习记录 | DELETE | `/{completionId}` | 删除学习记录 | 是 |
| 批量更新记录 | PUT | `/batch` | 批量更新多个学习记录 | 是 |
| 用户学习统计 | GET | `/stats/{userId}` | 获取用户的学习统计信息 | 是 |
| 用户学习进度 | GET | `/progress/{userId}` | 获取用户的学习进度信息 | 是 |
| 按问题查找记录 | GET | `/problem/{problemId}` | 按照问题ID查找学习记录 | 是 |
| 按用户查找记录 | GET | `/user/{userId}` | 按照用户ID查找学习记录 | 是 |
| 保存或更新记录 | POST | `/saveOrUpdate` | 保存或更新学习记录（兼容接口） | 是 |

### 5. 实验记录管理接口 (`ExperimentRecordController`)
**基础路径**：`/api/experiments`
*注：根据要求，忽略ExperimentStep相关接口*

| 接口 | 方法 | 路径 | 描述 | 是否需要认证 |
|------|------|------|------|--------------|
| 获取实验记录列表 | GET | ` ` | 获取实验记录列表（分页+筛选） | 是 |
| 获取所有实验记录 | GET | `/all` | 获取所有实验记录列表 | 是 |
| 获取实验记录详情 | GET | `/{id}` | 根据ID获取实验记录详情 | 是 |
| 创建实验记录 | POST | ` ` | 创建新的实验记录 | 是 |
| 更新实验记录 | PUT | `/{id}` | 更新实验记录信息 | 是 |
| 删除实验记录 | DELETE | `/{id}` | 删除指定的实验记录 | 是 |
| 用户实验统计 | GET | `/stats/user/{userId}` | 获取指定用户的实验统计信息 | 是 |
| 算法实验统计 | GET | `/stats/algorithm/{algorithmId}` | 获取指定算法的实验统计信息 | 是 |
| 保存实验记录 | POST | `/saveRecord` | 保存实验记录（兼容接口） | 是 |
| 获取用户实验记录 | GET | `/listByUser` | 根据用户ID获取实验记录列表（兼容接口） | 是 |

## JWT技术实现详解

### JWT配置
```yaml
jwt:
  secret: ldk-secret-key-2024-springboot-backend-jwt-token
  expiration: 86400000  # 24小时
```

### JwtUtil核心功能
1. **生成Token**：包含用户ID、用户名、角色等信息
2. **解析Token**：验证签名并提取用户信息
3. **验证Token**：检查token是否有效且未过期
4. **刷新Token**：生成新的token（相同声明，新过期时间）

### Token结构
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "userId": 1,
    "username": "admin",
    "role": "admin",
    "sub": "1",
    "iat": 1672531200,
    "exp": 1672617600
  },
  "signature": "HMACSHA256(...)"
}
```

### 认证流程
1. 用户登录 → 服务端生成JWT token返回
2. 前端存储token（localStorage或cookie）
3. 后续请求在Authorization头中携带：`Bearer {token}`
4. 服务端验证token有效性并提取用户信息
5. 根据用户角色和ID进行权限验证

## 数据库设计要点

### 实体关系
1. **User** ↔ **ExperimentRecord**：一对多
2. **Algorithm** ↔ **Problem**：一对多  
3. **User** + **Problem** → **ProblemCompletion**：多对多关联表
4. **ExperimentRecord** → **Obstacle**：一对多（忽略细节）

### 逻辑删除
- 使用`is_deleted`字段实现软删除
- 删除值：1，未删除值：0
- MyBatis-Plus自动过滤已删除数据

## 接口设计亮点

### 1. 前后端分离友好
- 统一的JSON响应格式
- 完整的错误码体系
- CORS跨域支持
- Swagger API文档

### 2. 权限控制精细
- 基于角色的访问控制
- 资源级别的权限验证
- 管理员与普通用户权限分离

### 3. 查询功能强大
- 支持分页、排序、筛选
- 模糊搜索功能
- 多条件组合查询
- 时间段查询支持

### 4. 兼容性考虑
- 保留原有接口路径，确保向前兼容
- 新增接口不影响现有功能
- 提供多种查询方式满足不同需求

## 可放入PPT的内容建议

### 技术架构部分
1. **技术栈全景图**：Spring Boot + MyBatis-Plus + MySQL + JWT
2. **三层架构图**：Controller → Service → Mapper
3. **安全认证流程图**：JWT认证授权流程

### 接口设计部分
1. **RESTful设计原则**：HTTP方法对应CRUD操作
2. **统一响应格式示例**：R<T>对象结构
3. **权限控制模型**：RBAC角色权限示意图

### 核心功能部分
1. **用户认证流程**：登录→生成token→验证token
2. **数据关系图**：用户-算法-题目-学习记录关系
3. **分页查询示例**：多条件筛选界面示意

### 创新亮点部分
1. **JWT实现细节**：token生成、验证、刷新机制
2. **权限验证设计**：私有权限检查方法
3. **批量操作支持**：批量添加、删除、更新

### 数据统计部分
1. **接口数量统计**：5个控制器，约50个接口
2. **技术组件统计**：10+个关键技术组件
3. **设计模式应用**：统一响应、权限验证、异常处理

## 总结
本后端系统采用现代化的Spring Boot技术栈，实现了完整的RESTful API接口，具备良好的安全性、可扩展性和可维护性。通过JWT实现无状态认证，MyBatis-Plus简化数据操作，统一的响应格式和错误处理机制为前端开发提供了便利。系统设计考虑了实际教学需求，支持算法学习、题目练习、实验记录等核心功能。
