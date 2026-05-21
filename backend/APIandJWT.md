## 📋 用户管理API接口规范 & 开发计划

### 一、JWT简化解释（如果不想用可以不用）

#### **JWT是什么？**
```javascript
// 就像一张"门票"
{
  "header": {"alg": "HS256", "typ": "JWT"},
  "payload": {"userId": 1, "username": "admin", "exp": 1769426106},
  "signature": "签名防止伪造"
}
```

#### **为什么项目用了JWT？**
1. **无状态**：服务器不需要保存session
2. **自包含**：token里就有用户信息
3. **适合前后端分离**：前端保存token，每次请求带上

#### **如果不想用JWT的替代方案**：
1. **Session-based**：传统session，需要服务器存储
2. **简单的token**：只存userId，服务器查数据库验证
3. **当前方案**：已经实现了JWT，但可以简化使用

**建议**：既然已经实现了，就继续用，但可以简化：
- 前端：只需要保存token，请求时带上
- 后端：已经自动验证，你几乎不用管

### 二、用户管理API接口规范

#### **基础信息**
- **Base URL**: `/api/users`
- **认证方式**: Bearer Token (JWT)
- **响应格式**: 统一使用`R<T>`格式

#### **1. 用户列表接口**
```http
GET /api/users
GET /api/users?page=1&size=10&username=admin&role=student
```
**响应**：
```json
{
  "code": 200,
  "data": {
    "list": [
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
    "page": 1,
    "size": 10
  },
  "message": "成功"
}
```

#### **2. 用户详情接口**
```http
GET /api/users/{userId}
```
**响应**：
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "username": "admin",
    "role": "admin",
    "email": "admin@example.com",
    "name": "系统管理员",
    "studentId": "",
    "createdAt": "2026-01-22 16:08:00"
  },
  "message": "成功"
}
```

#### **3. 更新用户接口**
```http
PUT /api/users/{userId}
```
**请求体**：
```json
{
  "name": "新的姓名",
  "email": "new@example.com",
  "studentId": "20240001",
  "role": "student"
}
```
**注意**：不能修改username和password（单独接口）

#### **4. 创建用户接口（可选）**
```http
POST /api/users
```
**请求体**：
```json
{
  "username": "newuser",
  "password": "123456",
  "name": "新用户",
  "email": "new@example.com",
  "studentId": "20240002",
  "role": "student"
}
```

#### **5. 删除用户接口（可选）**
```http
DELETE /api/users/{userId}
```

### 三、详细开发计划

#### **阶段1：完善认证模块（1-2天）**
1. ✅ 数据库：User表已就绪
2. ✅ 后端：登录/登出接口已实现
3. ⏳ 前端：修改登录对接（按之前指南）
4. ⏳ 测试：完整测试登录流程

#### **阶段2：用户管理模块（3-5天）**
**第1步：后端实现（2天）**
1. UserService新增方法：
    - `getUserList()` - 用户列表（分页+搜索）
    - `getUserById()` - 用户详情
    - `updateUser()` - 更新用户
    - `createUser()` - 创建用户（可选）
    - `deleteUser()` - 删除用户（可选）

2. UserController新增接口

**第2步：接口文档（0.5天）**
1. 完善Swagger注解
2. 生成API文档
3. 提供Postman集合

**第3步：前端适配（2天）**
1. 用户列表页面对接
2. 用户编辑页面对接
3. 添加/删除功能（可选）

#### **阶段3：其他核心模块（按优先级）**
1. **算法管理模块**（如果有）
2. **实验记录模块**（如果有）
3. **问题管理模块**（如果有）

### 四、模块优先级建议

#### **高优先级（必须先做）**：
1. **用户管理模块**：因为：
    - 数据库已准备好
    - 是系统基础功能
    - 可以验证完整开发流程

2. **前端登录对接**：因为：
    - 让用户能实际使用系统
    - 验证JWT和认证流程

#### **中优先级**：
1. 算法/实验/问题管理（根据你的毕设主题）

#### **低优先级**：
1. 高级功能（权限管理、日志记录等）

### 五、技术决策点

#### **关于JWT**：
- **保持现状**：已经实现，运行正常
- **简化使用**：你只需要知道：
    1. 登录后保存token
    2. 请求时带上 `Authorization: Bearer <token>`
    3. 后端自动验证

#### **关于分页**：
- **简单实现**：先用基本分页
- **后期优化**：如果需要再添加搜索、排序

#### **关于字段**：
- **必填**：username, password, role
- **可选**：name, email, studentId（可空）

### 六、下一步行动

**建议你**：
1. **先完成前端登录对接**（按之前指南）
2. **然后开始用户管理后端**（我可以帮你实现）
3. **最后前端对接用户管理**

**需要我帮你**：
1. 实现用户管理后端代码？
2. 设计其他模块的API规范？
3. 解释具体的技术细节？

请告诉我你希望从哪里开始，我可以制定更具体的代码实现计划。