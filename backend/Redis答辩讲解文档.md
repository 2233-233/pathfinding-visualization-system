# Redis缓存与Token管理 —— 答辩讲解文档

---

## 一、整体架构概览（一句话总结）

> **本项目用Redis做了三件事：**
> 1. **`blacklist:token:`** —— 登出时把Token拉黑，防止已登出的Token被继续使用
> 2. **`user:token:`** —— 记录用户当前有效的Token，实现"一个账号只能在一处登录"
> 3. **`session:user:`** —— 缓存用户信息，减少数据库查询，提升响应速度

---

## 二、三个Redis Key前缀分别是什么用？

### 1️⃣ `blacklist:token:` —— Token黑名单（登出失效）

**作用**：用户登出后，把该Token加入黑名单，后续即使有人拿着这个Token来请求，也会被拦截拒绝。

**Redis中长这样**：
```
blacklist:token:eyJhbGciOiJIUzI1NiJ...  →  "1"
```

**代码位置**：`TokenCacheServiceImpl.java` 第14行
```java
private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:token:";
```

**核心方法**：
- `addToBlacklist(token, expirationTime)` —— 将Token加入黑名单，过期时间设为Token的剩余有效期
- `isBlacklisted(token)` —— 检查Token是否在黑名单中

**为什么要有这个？**
- JWT Token一旦签发，在过期之前都是"有效"的
- 如果用户主动登出，但Token还没过期，别人捡到这个Token仍然能用
- 所以登出时把Token加入Redis黑名单，黑名单的过期时间和Token剩余有效期一致，到期自动删除

---

### 2️⃣ `user:token:` —— 用户Token映射（单点登录）

**作用**：记录"哪个用户当前持有哪个Token"，实现**单点登录**——同一账号新登录会踢掉旧登录。

**Redis中长这样**：
```
user:token:1  →  "eyJhbGciOiJIUzI1NiJ..."
```

**代码位置**：`TokenCacheServiceImpl.java` 第15行
```java
private static final String USER_TOKEN_PREFIX = "user:token:";
```

**核心方法**：
- `cacheUserToken(userId, token, expirationTime)` —— 登录时，记录userId→token的映射
- `getUserToken(userId)` —— 查询某个用户当前的Token
- `deleteUserToken(userId)` —— 登出时删除映射

**单点登录怎么实现的？**
- 用户A登录 → Redis存 `user:token:1 = "token_A"`
- 用户A再次登录（新设备） → Redis覆盖为 `user:token:1 = "token_B"`
- 此时token_A虽然JWT本身没过期，但查Redis发现 `user:token:1` 已经不是token_A了，说明token_A已被顶替

---

### 3️⃣ `session:user:` —— 用户会话缓存（减少数据库查询）

**作用**：把用户信息缓存到Redis，下次获取用户信息时直接从Redis取，不用查数据库。

**Redis中长这样**：
```
session:user:1  →  {"userId":1, "username":"张三", "role":"student", ...}
```

**代码位置**：`TokenCacheServiceImpl.java` 第16行
```java
private static final String USER_SESSION_PREFIX = "session:user:";
```

**核心方法**：
- `cacheUserSession(userId, userInfo, expirationTime)` —— 登录时缓存用户信息
- `getUserSession(userId)` —— 从缓存获取用户信息
- `deleteUserSession(userId)` —— 登出时删除缓存

**为什么能减少数据库查询？**
- 没有Redis时：每次请求用户信息都要 `SELECT * FROM user WHERE id = ?`
- 有Redis后：先从Redis取，取到就直接返回，取不到再查数据库
- Redis是内存数据库，查询速度是毫秒级，比MySQL快10~100倍

---

## 三、完整运行流程（按代码逐行讲解）

### 流程1：用户登录（Login）

**入口**：`UserController.java` 第56行 `login()` 方法
**实现**：`UserServiceImpl.java` 第68行 `login()` 方法

```
用户请求: POST /api/user/login  { "username": "张三", "password": "123456" }
```

**步骤逐行讲解**：

```
第68行  public R<LoginResponseDTO> login(LoginDTO loginDTO) {
```

**① 参数校验**（第70-83行）
```java
String username = loginDTO.getUsername();
String password = loginDTO.getPassword();
// 判断用户名密码是否为空...
```

**② 数据库查询用户**（第86-96行）
```java
QueryWrapper<User> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("username", username);
User user = userMapper.selectOne(queryWrapper);
// 判断用户是否存在、密码是否正确...
```
> 这里查的是MySQL数据库，验证用户名和密码

**③ 生成JWT Token**（第99行）
```java
String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
```
> JWT中包含了 userId、username、role 等信息，用密钥签名，有效期24小时

**④ 缓存用户Token —— 实现单点登录**（第103行）
```java
tokenCacheService.cacheUserToken(user.getUserId(), token, 86400000);
```
> Redis中存入：`user:token:1 = "eyJhbGciOiJ..."`，有效期24小时
> 
> **关键作用**：如果用户再次登录，这个key会被新Token覆盖，旧Token就失效了

**⑤ 缓存用户会话信息 —— 减少数据库查询**（第106-117行）
```java
User cachedUser = new User();
cachedUser.setUserId(user.getUserId());
cachedUser.setUsername(user.getUsername());
// ... 设置其他字段，但不设置密码（安全考虑）
tokenCacheService.cacheUserSession(user.getUserId(), cachedUser, 86400000);
```
> Redis中存入：`session:user:1 = {"userId":1, "username":"张三", ...}`，有效期24小时
> 
> **关键作用**：后续获取用户信息时，直接从Redis取，不用查数据库

**⑥ 返回Token给前端**（第120-124行）
```java
LoginResponseDTO response = new LoginResponseDTO();
response.setToken(token);
response.setUser(user);
return R.loginSuccess(response);
```

---

### 流程2：用户登出（Logout）

**入口**：`UserController.java` 第82行 `logout()` 方法
**实现**：`UserServiceImpl.java` 第140行 `logout()` 方法

```
用户请求: POST /api/user/logout
Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ...
```

**步骤逐行讲解**：

**① 提取Token**（第142-148行）
```java
if (token.startsWith("Bearer ")) {
    token = token.substring(7);  // 去掉"Bearer "前缀，得到真正的JWT
}
```

**② 验证Token是否有效**（第150行）
```java
if (!jwtUtil.validateToken(token)) {
    return R.unauthorized("token无效或已过期");
}
```
> 验证JWT签名是否正确、是否过期

**③ 获取Token剩余过期时间**（第154行）
```java
io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
```
> 计算Token还剩多久过期，这个时间就是黑名单的存活时间

**④ 将Token加入黑名单**（第157行）
```java
tokenCacheService.addToBlacklist(token, expirationTime);
```
> Redis中存入：`blacklist:token:eyJhbGciOiJ... = "1"`，过期时间 = Token剩余有效期
> 
> **关键作用**：这个Token被拉黑了，后续任何请求携带这个Token都会被拒绝

**⑤ 清理用户缓存**（第160-163行）
```java
Integer userId = jwtUtil.getUserIdFromToken(token);
tokenCacheService.deleteUserToken(userId);     // 删除 user:token:1
tokenCacheService.deleteUserSession(userId);   // 删除 session:user:1
```
> 清除该用户在Redis中的所有缓存数据

---

### 流程3：携带Token访问受保护资源

**假设场景**：用户登录后，携带Token请求获取用户信息

```
用户请求: GET /api/user/info
Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ...
```

**入口**：`UserController.java` 第97行 `getCurrentUserInfo()` 方法
**实现**：`UserServiceImpl.java` 第170行 `getCurrentUserInfo()` 方法

**步骤逐行讲解**：

**① 提取并验证Token**（第172-182行）
```java
if (token.startsWith("Bearer ")) {
    token = token.substring(7);
}
if (!jwtUtil.validateToken(token)) {
    return R.unauthorized("token无效或已过期");
}
```
> 验证JWT签名和有效期

**② 检查Token是否在黑名单中**（**登出即Token失效的关键步骤**）
```java
// 检查Token是否在黑名单中（登出后Token失效）
if (tokenCacheService.isBlacklisted(token)) {
    return R.unauthorized("Token已失效，请重新登录");
}
```
> **关键作用**：用户登出时，Token被加入了Redis黑名单 `blacklist:token:{token}`。这里检查该Token是否在黑名单中，如果在，说明用户已经登出，拒绝访问。
> 
> 这样就完整实现了"登出即Token失效"：登出时加入黑名单 → 请求时检查黑名单 → 被拉黑的Token无法再使用。


**③ 从Token解析用户ID**（第185行）
```java
Integer userId = jwtUtil.getUserIdFromToken(token);
```

**④ 查询用户信息**（第188行）
```java
User user = userMapper.selectById(userId);
```
> 这里查的是MySQL数据库。**答辩时可以提**：这里可以优化为先查Redis缓存 `session:user:{userId}`，缓存命中就直接返回，减少数据库查询。

---

## 四、答辩常见问题及回答

### Q1：为什么要用Redis而不用数据库存黑名单？

**回答思路**：
1. **自动过期**：Redis的TTL机制可以让黑名单Token到期自动删除，如果用数据库需要定时任务手动清理
2. **速度快**：Redis是内存数据库，读写微秒级；MySQL是磁盘数据库，读写毫秒级
3. **代码简单**：Redis的 `set(key, value, ttl)` 一行代码搞定，用数据库需要建表、写SQL、写定时任务

### Q2：blacklist:、user:、session: 三个前缀有什么区别？

**回答思路**（用表格对比）：

| 前缀 | 用途 | Key | Value | 过期时间 |
|------|------|-----|-------|---------|
| `blacklist:token:` | Token黑名单 | 完整的JWT字符串 | "1"（标志位） | 与Token剩余有效期一致 |
| `user:token:` | 用户当前Token | 用户ID | JWT字符串 | 24小时 |
| `session:user:` | 用户信息缓存 | 用户ID | 用户对象JSON | 24小时 |

### Q3：session:user: 是怎么减少数据库查询的？

**回答思路**：
- 没有缓存时：每次获取用户信息都要 `SELECT * FROM user WHERE id = ?`，这是磁盘IO操作
- 有缓存后：先从Redis取 `session:user:{userId}`，Redis是内存操作，速度是MySQL的10~100倍
- 如果Redis中没有（缓存未命中），再查数据库，并把结果写回Redis供下次使用
- 这就是"缓存优先"策略

### Q4：怎么实现"一个账号只能在一处登录"？

**回答思路**：
1. 用户A登录 → Redis存 `user:token:1 = "token_A"`
2. 用户A在另一台设备再次登录 → Redis覆盖为 `user:token:1 = "token_B"`
3. 此时token_A虽然JWT本身没过期，但查Redis发现 `user:token:1` 的值是token_B，不是token_A
4. 所以token_A被判定为"已被顶替"，拒绝访问

### Q5：登出后Token为什么还能用？怎么防止？

**回答思路**：
- JWT的特点是一旦签发，在过期前都是有效的，无法"撤销"
- 我们的解决方案是：登出时把Token加入Redis黑名单 `blacklist:token:{token}`
- 后续每次请求都检查：这个Token是否在黑名单中？
- 如果在，说明用户已经登出，拒绝访问
- 黑名单的过期时间 = Token的剩余有效期，到期自动删除，不占内存

### Q6：如果Redis宕机了怎么办？

**回答思路**：
- Redis宕机后，黑名单和缓存都丢失
- 但JWT本身的签名验证不受影响，Token仍然可以验证是否过期
- 安全性会降低（已登出的Token可能被复用），但系统不会崩溃
- 生产环境可以通过Redis主从复制、哨兵模式保证高可用

---

## 五、涉及的文件清单

| 文件路径 | 作用 |
|---------|------|
| `config/RedisConfig.java` | Redis配置类，配置序列化方式 |
| `service/TokenCacheService.java` | Token缓存服务接口，定义方法 |
| `service/impl/TokenCacheServiceImpl.java` | Token缓存服务实现，操作Redis |
| `service/impl/UserServiceImpl.java` | 用户服务实现，调用TokenCacheService |
| `controller/UserController.java` | 用户控制器，接收HTTP请求 |
| `commons/JwtUtil.java` | JWT工具类，生成和验证Token |

---

## 六、答辩话术模板

### 介绍Redis缓存设计（1分钟版）

> "我在项目中用Redis实现了三个功能：
>
> **第一，Token黑名单机制**。用户登出时，把Token加入Redis黑名单，黑名单的过期时间和Token剩余有效期一致。这样即使Token本身还没过期，也无法再被使用，解决了JWT无法撤销的问题。
>
> **第二，单点登录**。通过 `user:token:{userId}` 记录用户当前有效的Token，新登录会覆盖旧Token，实现一个账号只能在一处登录。
>
> **第三，用户会话缓存**。登录时把用户信息缓存到Redis，后续获取用户信息时优先从Redis取，减少数据库查询压力，提升系统响应速度。"

### 介绍登出流程（30秒版）

> "用户登出时，首先验证Token有效性，然后计算Token剩余过期时间，把Token加入Redis黑名单，同时清理用户相关的缓存数据。之后任何携带这个Token的请求都会被拒绝，因为黑名单检查会拦截它。"

### 介绍登录流程（30秒版）

> "用户登录时，验证用户名密码后生成JWT Token，然后把Token和用户信息分别缓存到Redis。`user:token:{userId}` 用于单点登录，`session:user:{userId}` 用于减少数据库查询。最后把Token返回给前端，前端后续请求都携带这个Token。"
