学平台设计与架构
1. 角色权限划分
   学生（Student）：拥有注册、登录、修改个人信息等基本账号操作权限。可访问寻路可视化模块（选择起点、终点和障碍、运行算法并查看动画结果）、查看个人实验记录与成绩、参与题库中的练习题或测验，并查看统计数据（如自己的完成情况）。学生用户只能查看和完成与自己相关的内容（“只能查看内容并提交作业”）[1][2]。
   管理员（Admin）：拥有系统管理权限，包括管理用户账户（增删改查学生帐号）、管理题库和算法模块、查看所有学生的实验记录和统计数据等。管理员可以创建或修改课程内容（此处为题目/实验任务）、管理题库题目和算法设置，以及生成和查看系统级的数据报表[3][1]。简言之，管理员可对整个平台的内容、用户和统计权限进行管理（“administrators have broader access”）[1][3]。
2. 系统功能模块划分
   用户管理模块（Authentication & User Management）：实现用户注册、登录、密码修改、个人资料维护等功能，并根据角色控制访问权限。如在前端使用 Vue 实现登录界面，在后端通过 Spring Security 结合 MySQL 验证用户身份。参考案例中，学生可注册并登录系统，管理个人档案[2]。
   寻路算法可视化模块（Pathfinding Visualization）：核心模块，提供交互式的网格地图界面，允许用户设置起点、终点和障碍，选择不同的路径算法并实时演示过程。该模块需支持常见算法（如 DFS、BFS、Dijkstra、A 等），并能逐步动画展示搜索过程与最终路径[4][5]。用户可以调整动画速度、清空/重置画布、即时显示最短路径或分步显示算法运行过程。例如 Tauseef-Hilal 的可视化项目就实现了逐步动画演示搜索过程、放置障碍和选择起止点等功能[4]。该模块应实时绘制节点状态（未访问、已访问、路径等），并提供性能统计（节点数量、耗时等）[5]。

图：寻路算法可视化界面示例。学生可在网格上放置障碍物，选择算法并观看其搜索过程的动态演示。*
题库管理模块（Problem/Question Bank）：由管理员使用，提供算法相关练习题目的增删改查功能。题目可以包含算法类型、难度等级、题面描述等信息。通过 Spring Boot 后端和 MySQL 存储题库数据，前端 Vue 提供页面管理题目。此模块类似于 LMS 中的“课程/题目管理”，支持创建新题、导入题目、分类检索等功能（参考 LMS 系统的课程创建与管理模块[6][3]）。
实验记录模块（Experiment Management）：用于记录和管理学生的实验/作业记录。每当学生运行一次算法，可生成一条实验记录，包括选择的题目、算法、运行结果（路径长度、用时等）、完成时间等，并保存于数据库。学生可以查看自己的历史实验记录，管理员可以浏览或批量导出所有学生的记录。该模块对应 LMS 中的“进度跟踪”功能[7]。
数据统计模块（Analytics & Reporting）：负责对实验数据进行统计分析，生成报表和图表。例如统计各算法使用次数、平均路径长度、完成率、学生成绩分布等。管理员可在后台查看全局统计（用户数、实验次数、活跃度等），学生可以查看自己的练习进度和成绩趋势。此模块参考 LMS 的“成绩跟踪与统计”功能[7][5]。
系统管理/后台模块：提供平台配置与监控功能，包括定时任务设置、系统日志查看、备份恢复、权限分配等。管理员还可查看平台运行状态、并适时维护数据库。该模块保证平台运行稳定、配置灵活。
3. UML 图
   用例图（Use Case Diagram）：用例图围绕两个主要角色：学生和管理员。学生用例包括：注册/登录、选择题目、选择算法、开始实验（观看算法可视化）、查看实验结果、查看个人统计等。管理员用例包括：登录、管理题库（增删改查题目）、管理用户（增删学生）、查看所有实验记录、生成统计报表等。如在线教育平台用例中，学生可注册、选课、提交作业，管理员可管理课程和用户[2][3]。本系统中，学生从平台首页登录后，进入“算法实验”进行操作，管理员则登录后台管理界面执行维护任务。
   类图（Class Diagram）：核心实体类包括：User（用户类，属性：userId, username, password, role, …）、Algorithm（算法类，属性：algorithmId, name, description, …）、Problem（题目类，属性：problemId, title, description, difficulty, algorithmId(外键)）和ExperimentRecord（实验记录类，属性：recordId, userId(FK), problemId(FK), algorithmId(FK), startTime, endTime, pathLength, visitedCount, etc）。此外可有 ExperimentStep（实验步骤类，可选，用于记录算法运行过程状态）。User 与 ExperimentRecord 一对多，Problem 与 ExperimentRecord 一对多，Algorithm 与 Problem 以及 ExperimentRecord 分别是一对多关系。每个类包含相应方法（如运行算法、计算统计等）。该类图体现了学生、题目、算法和实验记录等核心模块之间的关系。
   顺序图（Sequence Diagram）：以“学生运行算法实验”为例，描述用户操作过程：学生（Actor）在前端界面触发“开始实验”用例→前端（Vue组件）构造请求（包含选定的题目、算法、网格信息）并调用后端API→后端（Spring 服务层）接收请求，执行相应算法生成路径及步骤序列→后端将算法每一步状态逐帧返回给前端（或将整个状态序列打包返回）→前端在画布上逐步渲染状态（展示节点搜索过程和最终路径）→后端将实验结果（如路径长度、用时等）存入数据库→前端显示最终结果。若设计“实验步骤”表，则后端亦可边生成边保存每一步状态。以上过程可参考标准顺序图设计方法[8][3]。由于未找到专门针对路径算法可视化的顺序图示例，此处根据业务流程设计该顺序图。
4. 数据库设计（MySQL）
   各表结构示例（字段类型和主外键关系）：
   用户表（User）：存储所有用户信息。主键 user_id（INT AUTO_INCREMENT），字段：username（VARCHAR）、password（VARCHAR）、role（ENUM('student','admin')）、email、created_at（DATETIME）等。
   算法表（Algorithm）：存储支持的路径算法。主键 algorithm_id，字段：name（VARCHAR）、description（TEXT）、complexity（VARCHAR，如复杂度描述）等。
   题目表（Problem）：存储题库中的练习题。主键 problem_id，字段：title（VARCHAR）、description（TEXT）、difficulty（ENUM或INT）、algorithm_id（INT，外键 REFERENCES Algorithm(algorithm_id)）等，用于关联算法类型。
   实验记录表（ExperimentRecord）：学生每次运行算法的记录。主键 record_id，字段：user_id（INT，FK REFERENCES User(user_id)）、problem_id（INT，FK REFERENCES Problem(problem_id)）、algorithm_id（INT，FK REFERENCES Algorithm(algorithm_id)）、start_time/end_time（DATETIME）、path_length（INT，最终路径长度）、visited_count（INT，访问节点数）、status（ENUM存储结果状态如“完成”/“未找到路径”）等。
   实验步骤表（ExperimentStep，可选）：可存储算法每一步的状态（用于回放）。主键 step_id，字段：record_id（INT，FK REFERENCES ExperimentRecord(record_id)）、step_index（INT，步序号）、node_x、node_y（当前访问节点坐标）、node_state（ENUM，例如“visited”、“path”等）或整体网格状态（可用TEXT/JSON存储全局状态），每条记录表示算法走一步的结果。
   其他表：如用于统计缓存的表、管理员日志表等可酌情增加。
   各表设计遵循 MySQL 语法风格，建立主键和外键约束以保证关联性和完整性。例如：

```sql
CREATE TABLE `User` (
  `user_id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `role` ENUM('student','admin') NOT NULL,
  `email` VARCHAR(100),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 2️⃣ 算法表 `Algorithm`

```sql
CREATE TABLE `Algorithm` (
  `algorithm_id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `description` TEXT,
  `complexity` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 3️⃣ 题目表 `Problem`

```sql
CREATE TABLE `Problem` (
`problem_id` INT PRIMARY KEY AUTO_INCREMENT,
                         `title` VARCHAR(100) NOT NULL,
                         `description` TEXT,
                         `leetcode_link` VARCHAR(255) NOT NULL COMMENT '力扣题目链接', -- 新字段在此
                         `difficulty` ENUM('easy','medium','hard') NOT NULL,
                         `algorithm_id` INT NOT NULL,
                         FOREIGN KEY (`algorithm_id`) REFERENCES `Algorithm`(`algorithm_id`)
                           ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 4️⃣ 实验记录表 `ExperimentRecord`

```sql
CREATE TABLE `ExperimentRecord` (
  `record_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `problem_id` INT NOT NULL,
  `algorithm_id` INT NOT NULL,
  `start_time` DATETIME,
  `end_time` DATETIME,
  `path_length` INT,
  `visited_count` INT,
  `status` ENUM('completed','failed') NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `User`(`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`problem_id`) REFERENCES `Problem`(`problem_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`algorithm_id`) REFERENCES `Algorithm`(`algorithm_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 5️⃣ 实验步骤表 `ExperimentStep`（可选）

```sql
CREATE TABLE `ExperimentStep` (
  `step_id` INT PRIMARY KEY AUTO_INCREMENT,
  `record_id` INT NOT NULL,
  `step_index` INT NOT NULL,
  `node_x` INT,
  `node_y` INT,
  `node_state` ENUM('visited','path','unvisited'),
  `grid_state` JSON,
  FOREIGN KEY (`record_id`) REFERENCES `ExperimentRecord`(`record_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
## 题目完成记录表`ProblemCompletion`

```sql
CREATE TABLE `ProblemCompletion` (
  `completion_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `problem_id` INT NOT NULL,
  `completion_status` ENUM('not_started', 'in_progress', 'completed') NOT NULL DEFAULT 'not_started',
  `attempt_count` INT NOT NULL DEFAULT 0,
  `is_solved` BOOLEAN DEFAULT FALSE,
  `notes` TEXT,
  `last_review_date` DATE,  -- 最近复习日期
  `difficulty_rating` TINYINT, -- 学生自评难度：1-5星
  `time_spent_minutes` INT DEFAULT 0, -- 花费时间（分钟）
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `user_problem_unique` (`user_id`, `problem_id`),
  FOREIGN KEY (`user_id`) REFERENCES `User`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`problem_id`) REFERENCES `Problem`(`problem_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

```
其它表结构可类似定义。主外键关系确保实验记录关联到正确的用户、题目和算法，支持后续统计查询。
5. 可视化细节设计
   状态序列生成与动画：路径寻路算法以步进（step-by-step）方式执行，将每次展开节点和路径更新按顺序记录。后端可在执行算法时，将每一步的节点坐标和当前网格状态（如访问标记、障碍位置）放入一个状态序列，实时通过接口发送给前端[9]。前端接收状态序列后，在画布上逐帧渲染，使学生直观地看到算法如何一步步搜索。该过程参考已有可视化项目对“逐步动画演示”的实现[9]。
   数据存储设计：如果需要保存算法可视化的历史记录，可使用“实验步骤表”或在ExperimentRecord中以JSON字段记录状态序列。当学生完成一次实验后，可将整条状态序列写入数据库（如TEXT/JSON格式），以便后续回放或分析。也可以只存储关键数据（如路径长度、访问节点数等）用于统计，避免过多数据占用。对于实时动画，可采用 WebSocket 或轮询方式，让前端在算法运行时同步获取并显示状态。
   性能优化：为了保证流畅动画，算法执行与可视化应控制在合理帧率和内存占用内（如文献中所述的 60FPS 可视化性能[5]）。算法模块应设计为可扩展和并行（如分片更新、跳步显示），并预留参数调整（如动画速度、网格大小）以适应不同需求。
   统计与反馈：可视化过程中收集的各类数据（路径长度、用时、扩展节点等）既用于前端实时显示，也存入后端用于生成统计报表。这满足教育平台对实验结果分析的需求，使学生和管理员能直观了解算法效率与学习进度[5][7]。
   综上所述，本设计围绕寻路算法可视化这一核心特色，构建了前后端分离的教学平台框架，既满足算法动态演示的需求，又兼顾题库管理、实验记录和统计分析等教学平台功能[4][10][6]。
   参考文献： 系统设计参考了算法可视化平台的相关研究和实践案例，例如 Bartaula 等的教育可视化平台实现（实时动画与性能指标）[5]、Tauseef-Hilal 的路径寻路可视化开源项目（多算法、多功能动画）[4]、以及在线教育系统的用户角色与功能描述[1][2][3][6]等。上述文献中的架构和功能点为本设计提供了理论依据。

# 页面设计：
## 用户端
### 登录页面与注册页面：
  登录页面应包含用户名、密码输入框，以及登录按钮。登录成功后，应跳转到主界面。还有一个注册按钮，点击后跳转到注册页面。在登录页面中，对于密码输入框，应设置密码可见性，以便用户在输入时查看密码。在注册页面中，应包含用户名、密码、确认密码输入框，邮箱，以及注册按钮，其中密码要求要有长度限制、需包含数字、字母，并验证邮箱格式。其中注册的user的role字段应为student字段。注册成功后，应跳转到登录页面。
### 侧边栏功能设计：
  主界面包含侧边栏，和顶部栏
  1. 侧边栏功能：仪表板、算法可视化、算法学习、实验记录、
包含用户名、头像、退出登录功能。
### 仪表板：
  登录进来是仪表板，显示用户名，角色，显示用户上次进行实验步骤的记录、按照算法类型形成的饼状图（完成了多少题目）
## 用户端（Student）

### 顶部栏（Header Bar）设计

顶部栏位于页面上方，贯穿整个用户端界面，主要用于展示当前登录用户信息与全局操作入口。

* 显示内容：

  * 系统名称（如“寻路算法可视化教学平台”）
  * 当前登录用户名
  * 用户角色标识（Student）
  * 用户头像（默认头像或用户自定义头像）
* 功能按钮：

  * 个人中心（跳转到个人信息页面）
  * 退出登录按钮（清除 Token，跳转至登录页）

顶部栏组件可复用 vue-admin-template 中的 `Navbar` 组件，通过 Vuex 获取当前用户信息。

---

### 算法可视化页面（核心页面）

**页面路径示例**：`/visualization/index`

该页面为学生端的核心功能页面，提供完整的路径寻路算法可视化交互体验。

#### 页面布局设计

页面整体采用左右分栏布局：

* 左侧：参数控制面板（Control Panel）
* 右侧：算法可视化画布区域（Canvas/Grid Area）

#### 左侧控制面板功能

采用 Element UI 的 `el-form` 与 `el-card` 组件实现，包含以下控制项：

1. **题目选择**

  * 下拉框（`el-select`）
  * 数据来源：题库模块（Problem）
  * 显示内容：题目标题 + 难度
  * 选择题目后加载默认地图配置（如固定障碍）

2. **算法选择**

  * 下拉框（BFS / DFS / Dijkstra / A*）
  * 根据题目允许的算法类型进行过滤

3. **算法参数设置**

  * 动画速度调节（`el-slider`）
  * 是否显示搜索过程（开关）
  * 是否显示最终最短路径（复选框）

4. **控制按钮**

  * 开始运行
  * 暂停 / 继续
  * 重置地图
  * 清空障碍

#### 右侧可视化画布区域

* 使用 Canvas 或 SVG 实现网格地图
* 网格单元状态：

  * 空白节点
  * 起点（绿色）
  * 终点（红色）
  * 障碍（黑色）
  * 已访问节点（蓝色）
  * 当前扩展节点（橙色）
  * 最终路径（黄色）
* 支持交互：

  * 鼠标点击设置起点 / 终点
  * 拖拽或点击设置障碍
* 动画方式：

  * 按步骤逐帧渲染算法执行过程
  * 可调节播放速度

该页面对应一个核心 Vue 组件（如 `PathfindingVisualizer.vue`），内部通过状态机管理算法执行状态（idle / running / paused / finished）。

---

### 算法学习页面（Algorithm Learning）

**页面路径示例**：`/algorithm/learning`

该页面用于辅助教学，帮助学生理解不同寻路算法的理论知识。

##  题目学习记录页面
页面基本信息
访问顺序：learning.vue(点击具体的题目)->problem（题目详情页，显示ProblemCompletion表中的内容，可修改）
页面路径：`/algorithm/problem/:problemId`

功能目的：学生记录和管理单个力扣题目的学习进度

访问权限：仅学生角色可访问

页面核心功能
1. 题目信息展示功能
   显示题目标题

显示题目难度等级（带视觉区分）

显示关联的算法类型

显示题目描述摘要

显示力扣原题链接（可点击查看）

2. 学习状态概览功能
   显示当前完成状态（未开始/进行中/已完成）

显示总尝试次数

显示累计学习时间

显示最近复习日期

3. 学习记录编辑功能
   完成状态管理：可切换"未开始"、"进行中"、"已完成"三种状态

尝试次数记录：可手动调整尝试次数，支持一键增加

解题状态标记：可标记"我已解决此题"

难度自评：提供1-5星难度评分

时间记录：可记录花费的学习时间（分钟）

笔记功能：支持记录解题思路、关键点、易错点等

复习标记：支持标记为"已复习"，自动更新复习日期

4. 数据操作功能
   保存功能：保存所有学习记录到数据库

自动加载：页面加载时自动获取已有的学习记录

状态提示：保存成功/失败的反馈提示

5. 外部链接功能
   跳转力扣：在新标签页中打开力扣原题页面

返回功能：返回算法学习列表页面

#### 页面内容设计

* 左侧：算法列表（BFS / DFS / Dijkstra / A*）
* 右侧：算法详情展示区

  * 算法简介
  * 适用场景
  * 时间复杂度与空间复杂度
  * 核心思想说明（文字 + 简要示意图）
* 可选扩展：

  * 伪代码展示（代码高亮）
  * 与其他算法对比说明

该页面以**静态内容 + 数据驱动展示为主**，不涉及动画执行。

---

### 实验记录页面（Experiment Records）

**页面路径示例**：`/experiment/records`

用于学生查看个人历史实验记录。

#### 页面功能

* 使用 `el-table` 展示实验记录列表
* 表格字段：

  * 实验编号
  * 题目名称
  * 算法类型
  * 路径长度
  * 访问节点数
  * 运行时间
  * 实验状态
  * 完成时间
* 支持功能：

  * 按算法类型筛选
  * 按时间排序
  * 查看实验详情

#### 实验详情页面

点击某条记录可进入详情页面：

* 显示实验参数
* 可回放算法执行过程（如果保存了 ExperimentStep）
* 显示统计信息（耗时、节点数等）

---

### 个人中心页面（Profile）

**页面路径示例**：`/user/profile`

用于学生管理个人信息。

* 可修改内容：

  * 用户名（可选）
  * 邮箱
  * 头像
* 不可修改内容：

  * 用户角色
* 支持修改密码功能（需原密码验证）
* 显示最近的题目学习记录，点击能进入题目学习记录页面
---

## 管理员端（Admin）

管理员端基于 **同一套 vue-admin-template 框架**，通过角色控制路由和菜单显示。

---

### 管理员仪表板（Admin Dashboard）

**页面路径示例**：`/admin/dashboard`

展示系统整体运行情况。

* 统计卡片：

  * 总用户数
  * 学生人数
  * 实验总次数
  * 今日活跃用户数
* 图表展示：

  * 各算法使用频率（柱状图 / 饼图）
  * 实验完成情况趋势（折线图）

---

### 用户管理页面（User Management）

**页面路径示例**：`/admin/users`

用于管理员管理学生账户。

* 功能：

  * 查看学生列表
  * 新增学生账号
  * 重置密码
  * 删除用户
* 表格字段：

  * 用户名
  * 邮箱
  * 角色
  * 注册时间
  * 操作列（编辑 / 删除）

---

### 题库管理页面（Problem Management）

**页面路径示例**：`/admin/problems`

用于管理算法题目。

* 功能：

  * 新增题目
  * 编辑题目
  * 删除题目
* 题目属性：

  * 标题
  * 描述
  * 难度
  * 对应算法
  * 默认地图配置（可选）

---

### 算法管理页面（Algorithm Management）

**页面路径示例**：`/admin/algorithms`

用于维护平台支持的算法列表。

* 功能：

  * 添加新算法
  * 修改算法说明
  * 设置算法是否对学生可见
* 不涉及算法代码逻辑修改，仅管理元数据

---

### 题目学习记录

**页面路径示例**：`/admin/experiments`

* 查看所有学生题目完成记录
* 支持导出 Excel
* 支持按：

  * 学生
  * 算法
  * 时间区间
    进行筛选

---

## 页面权限与路由控制设计

* 使用 vue-admin-template 内置的：

  * 动态路由
  * 权限指令
* 根据 `role` 字段：

  * Student：只能访问用户端页面
  * Admin：可访问后台管理页面
* 未授权路由自动重定向至 403 页面

---

views
├── dashboard
│   └── index.vue                # 公共仪表板（可学生/管理员共用）

├── visualization
│   ├── index.vue                # 算法可视化主页面
│   └── components
│       ├── GridBoard.vue        # 网格/地图
│       ├── ControlPanel.vue     # BFS / DFS / 按钮
│       └── StepInfo.vue         # 当前步信息

├── algorithm/                   # 算法学习模块（新增）
│   ├── learning.vue             # 算法学习页面（显示算法和题目列表）
│   ├── problem/                 # 题目学习记录（新增目录）
│   │   └── [id].vue             # 题目学习记录页面（动态路由）
│   

├── experiment
│   ├── records.vue              # 学生实验记录列表
│   └── detail.vue               # 单次实验详情（可选）

├── user
│   └── profile.vue              # 个人中心（学生）

├── admin
│   ├── dashboard.vue            # 管理员首页
│   ├── users
│   │   └── index.vue            # 用户管理
│   ├── problems
│   │   └── index.vue            # 题库管理
│   ├── algorithms
│   │   └── index.vue            # 算法管理
│   └── experiments
│       └── index.vue            # 题目完成记录

├── login
│   └── index.vue

├── register
│   └── index.vue

└── 404.vue

## 小结（这一段你在论文里可以直接用）

> 本系统页面设计基于 Vue 与 Element UI，并使用 vue-admin-template 作为前端基础框架，通过角色权限控制实现学生端与管理员端功能隔离。学生端以算法可视化与实验学习为核心，管理员端以系统管理与数据统计为重点，整体页面结构清晰、模块职责明确，便于后续功能扩展与维护。
管理员端最终侧边栏结构（一级）
- 管理仪表板
- 用户管理
- 题库管理
- 算法管理
- 实验统计
学生端最终侧边栏结构
- 算法可视化
- 算法学习
- 实验记录
- 个人中心

后端项目结构：
backend/
├── src/main/java/com/ldk/backend/
│   ├── commons/
│   │   └── R.java
│   ├── config/
│   ├── controller/
│   │   ├── AlgorithmController.java
│   │   ├── ExperimentRecordController.java
│   │   ├── ProblemCompletionController.java
│   │   ├── ProblemController.java
│   │   └── UserController.java
│   ├── DTO/
│   │   └── LoginDTO.java
│   ├── entity/
│   │   ├── Algorithm.java
│   │   ├── ExperimentRecord.java
│   │   ├── ExperimentStep.java
│   │   ├── Problem.java
│   │   ├── ProblemCompletion.java
│   │   └── User.java
│   ├── mapper/
│   │   ├── AlgorithmMapper.java
│   │   ├── ExperimentRecordMapper.java
│   │   ├── ProblemCompletionMapper.java
│   │   ├── ProblemMapper.java
│   │   └── UserMapper.java
│   └── service/
│       ├── impl/
│       │   ├── AlgorithmServiceImpl.java
│       │   ├── ExperimentServiceImpl.java
│       │   ├── ProblemCompletionServiceImpl.java
│       │   ├── ProblemServiceImpl.java
│       │   └── UserServiceImpl.java
│       ├── AlgorithmService.java
│       ├── ExperimentService.java
│       ├── ProblemCompletionService.java
│       ├── ProblemService.java
│       └── UserService.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── BackendApplication.java