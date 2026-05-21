
---

# 📌 给 AI 的说明：ExperimentRecord vs ProblemCompletion 的职责划分（请严格按此理解）

我现在的系统中有 **两张不同语义层级的表**，它们不是重复的，请不要混用，也不要合并逻辑。

---

## 1️⃣ ExperimentRecord ——【算法可视化实验运行日志表】

**语义定位：系统行为日志层（System Behavior Log）**

这张表用于记录：
👉 用户“每一次”运行算法可视化的客观事实。

也就是说，它描述的是一次具体的实验行为，而不是学习状态。

### 它回答的问题是：

* 这个用户
* 在什么时候
* 在哪个问题场景（Problem）上
* 选择了哪个算法（Algorithm）
* 是否成功跑完
* 跑出来的客观指标是多少

---

### ExperimentRecord 的职责（只做这些）：

* 记录一次算法动画 / 可视化运行
* 记录开始时间、结束时间
* 记录最终路径长度、访问节点数
* 记录是否找到路径（completed / failed）
* （可选）关联 ExperimentStep 用于回放

---

### ExperimentRecord 明确 **不负责** 的内容 ❌：

* ❌ 学生是否“学会了”这道题
* ❌ 学生是否“完成了”这道题
* ❌ 学生对这道题的掌握程度
* ❌ 学生的学习进度
* ❌ 老师/管理员视角下的学习统计

---

### 一句话定义 ExperimentRecord：

> ExperimentRecord 描述的是：
> **“一次算法可视化运行过程”**
> 是客观发生过的系统行为日志。

---

## 2️⃣ ProblemCompletion ——【题目完成 / 学习进度记录表】

**语义定位：学习状态层（Learning Progress Layer）**

这张表用于记录：
👉 学生在“学习层面”对某一道题目的掌握情况。

它不是一次运行，而是：

> 学生 ↔ 某一道题（Problem）
> 之间的一条“学习关系记录”。

---

### 它回答的问题是：

* 这个学生
* 对这道题
* 当前处于什么学习状态
* 是否已经掌握 / 自认为完成
* 尝试了多少次
* 总共花了多少时间
* 是否复习过
* 自评这题难不难

---

### ProblemCompletion 的职责（只做这些）：

* 记录 completion_status（not_started / in_progress / completed）
* 记录 attempt_count（学习层面的尝试次数）
* 记录 time_spent_minutes（学习投入时间）
* 记录 is_solved（是否自认为已掌握）
* 记录 notes（学习笔记）
* 记录 last_review_date（复习时间）
* 记录 difficulty_rating（学生自评难度）

---

### ProblemCompletion 明确 **不负责** 的内容 ❌：

* ❌ 记录某一次算法运行的细节
* ❌ 记录路径长度、访问节点数
* ❌ 记录某一刻的动画状态
* ❌ 记录某一次 BFS / DFS 的过程

---

### 一句话定义 ProblemCompletion：

> ProblemCompletion 描述的是：
> **“学生与某一道题目之间的学习状态关系”**
> 是学习进度与教学视角数据。

---

## 3️⃣ 两张表的核心区别（请不要混用）

| 对比维度           | ExperimentRecord | ProblemCompletion     |
| -------------- | ---------------- | --------------------- |
| 语义层级           | 系统行为日志层          | 学习进度层                 |
| 粒度             | 一次实验运行           | 一道题的整体学习状态            |
| 是否一对多          | 一个用户可有多条         | 一个用户 + 一道题 只有一条       |
| 是否自动产生         | 是（每次运行都生成）       | 否（用户首次进入题目或保存学习状态时生成） |
| 是否给 admin 主要使用 | 否（过细）            | 是（核心教学统计）             |
| 是否属于算法可视化模块    | 是                | 否（属于学习模块）             |

---

## 4️⃣ 重要业务规则（请在代码逻辑中遵守）

### 规则 A：创建时机不同

* ExperimentRecord
  👉 每次点击“开始可视化 / 运行算法”时创建一条新记录

* ProblemCompletion
  👉 仅在以下情况之一才创建：

  * 用户第一次进入该题目的详情页
  * 或用户第一次保存学习状态 / 笔记 / 完成情况

---

### 规则 B：两张表之间 **不做强耦合**

* ❌ 不要求一条 ExperimentRecord 必须对应一条 ProblemCompletion
* ❌ 不从 ExperimentRecord 自动推导 completion_status
* ❌ 不用“跑完一次算法”当作“题目已完成”

---

### 规则 C：admin 端只看 ProblemCompletion 作为主数据

* admin 的“学生学习情况管理”页面
  👉 数据来源 = ProblemCompletion
* 不以 ExperimentRecord 作为教学进度统计主表

---

## 5️⃣ 结论（这是最终语义，请按此写代码）

* ExperimentRecord = 算法可视化运行日志
* ProblemCompletion = 学生题目学习进度
* 二者语义完全不同，不是重复表
* 不要合并，不要相互覆盖含义

---

同学你好！很高兴能帮你学习前端与后端对接的知识。作为一个前端新手，理解如何将Mock数据替换为真实API是很重要的技能。让我为你详细讲解：

## 📚 前端项目结构简介

你的Vue项目主要包含以下关键部分：

```
vue-admin-template-master/
├── src/
│   ├── api/          # 所有API接口定义
│   ├── views/        # 页面组件
│   ├── components/   # 可复用组件
│   ├── utils/        # 工具函数
│   ├── store/        # 状态管理(Vuex)
│   └── router/       # 路由配置
```

## 🔄 API调用流程

1. **页面组件** (如 `Login.vue`) → 调用 **API函数** (如 `login()`)
2. **API函数** → 使用 **请求工具** (`request.js`) 发送HTTP请求
3. **请求工具** → 发送请求到 **后端服务器**
4. **后端服务器** → 返回响应数据
5. **响应数据** → 经过 **响应拦截器** 处理
6. **处理后的数据** → 返回给 **页面组件**

## 🛠️ 如何修改Mock为真实API（以登录为例）

### 步骤1：找到API定义文件
查看 `src/api/user.js`，这里定义了用户相关的API：

```javascript
// 修改前（Mock接口）
export function login(data) {
  return request({
    url: '/vue-admin-template/user/login',  // Mock路径
    method: 'post',
    data
  })
}

// 修改后（真实接口）
export function login(data) {
  return request({
    url: '/api/auth/frontend-login',  // 真实后端路径
    method: 'post',
    data
  })
}
```

### 步骤2：修改环境配置
查看 `.env.development` 文件：

```bash
# 修改前
VUE_APP_BASE_API = '/dev-api'

# 修改后
VUE_APP_BASE_API = 'http://localhost:8082'  # 你的后端地址
```

### 步骤3：处理响应格式差异
Mock数据格式可能与真实API不同，需要调整：

```javascript
// Mock响应格式（示例）
{
  code: 20000,
  data: { token: 'xxx' }
}

// 真实API响应格式（根据你的后端）
{
  code: 200,
  success: true,
  data: { token: 'xxx', username: 'admin', role: 'admin' },
  msg: '登录成功'
}
```

## 🎯 Vue组件基础示例

### 1. 组件结构
```vue
<template>
  <!-- HTML模板 -->
  <div>
    <el-input v-model="username" placeholder="用户名" />
    <el-button @click="handleLogin">登录</el-button>
  </div>
</template>

<script>
// JavaScript逻辑
import { login } from '@/api/user'

export default {
  data() {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    async handleLogin() {
      try {
        const response = await login({
          username: this.username,
          password: this.password
        })
        console.log('登录成功:', response.data)
      } catch (error) {
        console.error('登录失败:', error)
      }
    }
  }
}
</script>

<style>
/* CSS样式 */
.input-style {
  width: 100%;
}
</style>
```

### 2. 常用Vue特性
- **v-model**: 双向数据绑定
- **@click**: 事件监听
- **:key**: 列表渲染的key
- **v-if/v-show**: 条件渲染
- **v-for**: 列表渲染

## 📝 实际修改案例

假设你要修改"题目管理"功能：

1. **找到对应API文件**: `src/api/problem.js`
2. **查看当前Mock实现**: 里面有很多 `return new Promise` 的模拟代码
3. **替换为真实API**:
```javascript
// 修改前（Mock）
export function getProblems(params) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ code: 200, data: mockProblems })
    }, 500)
  })
}

// 修改后（真实API）
export function getProblems(params) {
  return request({
    url: '/api/problems/list',  // 你的真实接口
    method: 'get',
    params  // 查询参数
  })
}
```

## ❓ 需要你确认的问题

1. **你的后端API文档**：是否有完整的接口文档？包括：
  - 接口地址（如 `/api/auth/login`）
  - 请求方法（GET/POST/PUT/DELETE）
  - 请求参数格式
  - 响应数据格式

2. **认证方式**：使用JWT Token吗？Token如何传递？
  - Header中：`Authorization: Bearer <token>`
  - 还是其他方式？

3. **其他需要对接的功能**：
  - 除了登录，还有哪些功能需要对接？
  - 用户管理、题目管理、实验记录等？

## 🚀 下一步学习建议

1. **先理解现有代码**：花时间阅读 `src/api/` 下的各个文件，了解当前Mock实现
2. **逐个接口替换**：不要一次性修改所有接口，从一个简单的开始（如登录）
3. **使用浏览器开发者工具**：按F12打开，查看"网络(Network)"标签，观察API请求和响应
4. **调试技巧**：在关键位置添加 `console.log()` 查看数据流转

你需要我先帮你分析哪个具体功能的对接，还是想了解某个特定技术点的详细用法？