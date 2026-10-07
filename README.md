# 🧭 基于Spring Boot+Vue的寻路算法可视化系统

> 面向算法教学的路径规划可视化实验平台

[![Vue 2](https://img.shields.io/badge/Vue-2.6.10-brightgreen)](https://vuejs.org/)
[![Element UI](https://img.shields.io/badge/Element%20UI-2.13.2-409eff)](https://element.eleme.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-11-orange)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-alpine-red)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ed)](https://www.docker.com/)

---

## 📖 项目简介

本系统通过**网格地图可视化**的方式，直观展示 A\*、Dijkstra、BFS、DFS 等经典路径寻路算法的搜索过程，帮助学生理解算法的工作原理和效率差异。

为满足功能数量需要系统包含**学生**和**管理员**双角色：学生可以进行算法可视化演示、学习算法知识、完成习题并记录实验数据；管理员可以管理用户、题库、实验记录，并查看统计看板。
**本项目为个人学习作业。**


---

## 🎯 功能特性

### 已实现功能

| 模块 | 功能 | 说明 |
|------|------|------|
| 🔐 **用户系统** | 注册 / 登录 / JWT 认证 | 学生与管理员双角色权限控制 |
| 🗺️ **算法可视化** | 网格地图 + 逐步搜索演示 | 支持 BFS、DFS、Dijkstra、A\* 四种算法 |
| 🎓 **算法学习** | 算法原理说明 / 复杂度分析 | 配套学习页面 |
| 📝 **题库管理** | 按算法类型和难度分类题目 | 关联 LeetCode 题目链接 |
| 📊 **实验记录** | 记录路径长度、访问节点数、耗时 | 记录管理 |
| 📈 **管理仪表板** | 用户统计 / 实验统计 / 数据看板 | 管理端专属页面 |
| 📋 **数据导出** | 实验记录 Excel 导出 | 基于 EasyExcel |
| 📚 **题目学习记录** | 跟踪题目完成状态和进度 | 支持笔记和难度评分 |



## 🛠️ 技术栈

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| [Vue.js](https://vuejs.org/) | 2.6.10 | 前端框架 |
| [Element UI](https://element.eleme.io/) | 2.13.2 | UI 组件库 |
| [Vue Router](https://router.vuejs.org/) | 3.0.6 | 路由管理 |
| [Vuex](https://vuex.vuejs.org/) | 3.1.0 | 状态管理 |
| [Axios](https://axios-http.com/) | 0.18.1 | HTTP 请求库 |
| [ECharts](https://echarts.apache.org/) | ^6.0.0 | 数据图表可视化 |

> 前端基于 [PanJiaChen/vue-admin-template](https://github.com/PanJiaChen/vue-admin-template) 构建，这是一个极简的 Vue 后台管理模板，在此之上进行了二次开发。

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 2.7.18 | 后端框架 |
| [MyBatis-Plus](https://baomidou.com/) | 3.5.3.1 | ORM 框架 |
| [MySQL](https://www.mysql.com/) | 8.0 | 关系型数据库 |
| [Redis](https://redis.io/) | 7 (Alpine) | 缓存（JWT Token + 算法数据） |
| [JWT](https://jwt.io/) (jjwt) | 0.11.5 | 用户认证 |
| [EasyExcel](https://easyexcel.opensource.alibaba.com/) | 3.3.2 | Excel 导入导出 |
| [SpringDoc OpenAPI](https://springdoc.org/) | 1.7.0 | API 文档生成 |

---

## 📁 项目结构

```
bishe-workplace-github/
│
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/ldk/backend/
│   │   ├── BackendApplication.java   # 启动类
│   │   ├── controller/               # REST API 控制器
│   │   │   ├── UserController.java           # 用户登录/注册
│   │   │   ├── ProblemController.java        # 题目管理
│   │   │   ├── ProblemCompletionController.java  # 题目完成记录
│   │   │   ├── ExperimentRecordController.java   # 实验记录 + 障碍物
│   │   │   └── AdminDashboardController.java     # 管理后台
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── impl/
│   │   │   ├── AlgorithmCacheService.java
│   │   │   └── TokenCacheService.java
│   │   ├── mapper/                   # MyBatis-Plus 数据访问层
│   │   ├── entity/                   # 数据库实体
│   │   │   ├── User.java
│   │   │   ├── Problem.java
│   │   │   ├── Algorithm.java
│   │   │   ├── ExperimentRecord.java
│   │   │   ├── ExperimentStep.java
│   │   │   ├── ProblemCompletion.java
│   │   │   └── Obstacle.java         # ⚠️ 未使用（保留供扩展）
│   │   ├── DTO/                      # 数据传输对象
│   │   ├── config/                   # 配置类（Redis、数据源）
│   │   └── commons/                  # 通用工具
│   │       ├── R.java                # 统一响应封装
│   │       └── JwtUtil.java          # JWT 工具类
│   └── pom.xml                       # Maven 依赖
│
├── vue-admin-template-master/        # Vue 2 前端
│   ├── src/
│   │   ├── views/
│   │   │   ├── visualization/        # 算法可视化（核心功能）
│   │   │   ├── algorithm/            # 算法学习
│   │   │   ├── experiment/           # 实验记录
│   │   │   ├── admin/                # 管理员端页面
│   │   │   ├── user/                 # 个人中心
│   │   │   ├── ai-chat/              # 🤖 AI 学习助手（前端页面）
│   │   │   ├── login/ & register/    # 登录注册
│   │   │   └── ...
│   │   ├── layout/                   # 布局组件
│   │   ├── store/                    # Vuex 状态管理
│   │   ├── router/                   # 路由配置
│   │   └── api/                      # 后端 API 调用
│   └── package.json
│
├── docker-compose.yml                # Docker 编排（前端 + 后端 + MySQL + Redis）
├── init.sql                          # 数据库初始化脚本
└── start-frontend.bat                # 前端启动脚本（Windows）
```

---

## 🗄️ 数据库设计


| 表名 | 说明 |
|------|------|
| `user` | 用户表，支持 `student` / `admin` 双角色 |
| `algorithm` | 算法表，存储算法名称、描述和复杂度 |
| `problem` | 题目表，关联算法和 LeetCode 链接 |
| `problemcompletion` | 题目完成记录，追踪学习进度 |
| `experimentrecord` | 实验记录，存储每次可视化的结果数据 |
| `experimentstep` | 实验步骤，记录算法每一步的搜索状态 |
| `obstacle` | 障碍物表，已创建但**当前未在前端使用** |

---

## 🚀 快速开始

### 克隆项目

```bash
git clone https://github.com/2233-233/pathfinding-visualization-system.git
cd bishe-workplace-github
```

### 本地开发环境（推荐）

**前提条件**：
- Node.js ≥ 8.9、npm ≥ 3.0.0
- Java 11、Maven 3.x
- MySQL 8.0、Redis 7

#### 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

后端默认运行在 `http://localhost:8081`。

#### 启动前端

```bash
cd vue-admin-template-master
npm install              # 安装依赖
npm run dev              # 启动开发服务器
```

前端默认运行在 `http://localhost:9528`。

> 首次运行前请确保 MySQL 中已导入 `init.sql` 初始化脚本，并修改后端配置中的数据库连接信息。

### Docker 部署

项目根目录下提供了 `docker-compose.yml` 以及前后端的 Dockerfile，可用于容器化部署。不过目前配置还不够完善（如数据库连接地址写死了 IP、前端跑的是开发模式），需要做些调整才能正常使用，仅供参考。

---

## 📚 支持的算法

| 算法 | 时间复杂度 | 空间复杂度 | 描述 |
|------|-----------|-----------|------|
| **A\*** | O(b^d) | O(V) | 启发式搜索，使用估价函数 f(n)=g(n)+h(n)，效率最高 |
| **Dijkstra** | O((V+E)logV) | O(V) | 单源最短路径，使用贪心策略 + 优先队列 |
| **BFS** | O(V+E) | O(V) | 广度优先搜索，逐层扩展，适合无权图最短路径 |
| **DFS** | O(V+E) | O(V) | 深度优先搜索，沿一条路径搜索到底再回溯 |

---

## 🔌 API 文档

启动后端后，访问 [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) 查看完整的 REST API 文档。

主要接口模块：

| 模块 | 路径前缀 | 说明 |
|------|---------|------|
| 用户认证 | `/api/users` | 登录、注册、用户信息管理 |
| 题目管理 | `/api/problems` | 题目的增删改查 |
| 题目记录 | `/api/problem-completions` | 学习进度跟踪 |
| 实验记录 | `/api/experiments` | 实验 CRUD、步骤管理、障碍物管理 |
| 管理后台 | `/api/admin` | 仪表板统计、用户管理 |
| 算法管理 | `/api/algorithms` | 算法信息管理 |

---


