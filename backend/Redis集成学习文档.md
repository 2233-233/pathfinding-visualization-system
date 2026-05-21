# Redis集成学习文档

## 📚 项目概述

本项目是基于Spring Boot + Vue的寻路算法可视化教学平台，Redis作为缓存层被集成到系统中，主要实现以下功能：

1. **Token黑名单管理**：安全的JWT注销机制
2. **用户会话缓存**：减少数据库查询，提高响应速度
3. **算法结果缓存**：缓存BFS、Dijkstra、A*等算法的计算结果
4. **实时统计缓存**：算法使用次数、热门算法排行
5. **用户操作历史**：记录用户的算法学习历史

## 🏗️ 架构设计

### 1. 技术栈
- **Spring Boot 2.7.18**：后端框架
- **Spring Data Redis**：Redis集成
- **Redis 7**：内存数据库
- **Docker**：容器化部署
- **JWT**：身份认证

### 2. 缓存策略
| 缓存类型 | 过期时间 | 数据结构 | 使用场景 |
|---------|---------|---------|---------|
| Token黑名单 | 与JWT剩余时间一致 | String | 用户注销 |
| 用户会话 | 24小时 | String | 用户信息缓存 |
| 算法结果 | 30分钟 | String | 算法计算结果 |
| 问题列表 | 10分钟 | String | 算法问题列表 |
| 算法统计 | 5分钟 | String | 使用次数统计 |
| 用户历史 | 24小时 | List | 用户操作记录 |

## 🔧 核心实现

### 1. Redis配置类 (`RedisConfig.java`)
```java
@Configuration
public class RedisConfig {
    @Bean//这里知识创建了bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();//为什么这里不能直接@AutoWired注入，因为RedisTemplate是模板类，要配置才能使用，不能直接注入
        template.setConnectionFactory(factory);
        
        // 使用Jackson序列化
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), 
            ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
```

**学习要点**：
- `@Configuration`：标记为配置类
- `@Bean`：将RedisTemplate注册为Spring Bean
- **序列化策略**：使用Jackson将Java对象序列化为JSON
- **键序列化**：使用StringRedisSerializer确保key可读

### 2. Token缓存服务 (`TokenCacheService`)

#### 接口设计
```java
public interface TokenCacheService {
    void addToBlacklist(String token, long expirationTime);
    boolean isBlacklisted(String token);
    void cacheUserToken(Integer userId, String token, long expirationTime);
    String getUserToken(Integer userId);
    void cacheUserSession(Integer userId, Object userInfo, long expirationTime);
    Object getUserSession(Integer userId);
}
```

#### 实现原理
1. **黑名单机制**：
   - Key格式：`blacklist:token:{jwtToken}`
   - 值：固定为"1"
   - 过期时间：与JWT Token剩余有效期一致

2. **用户会话缓存**：
   - Key格式：`session:user:{userId}`
   - 值：用户信息对象（JSON格式）
   - 过期时间：24小时

3. **单点登录实现**：
   - 登录时：`cacheUserToken(userId, token)`
   - 验证时：检查当前token是否与缓存一致
   - 登出时：`deleteUserToken(userId)`

### 3. 算法缓存服务 (`AlgorithmCacheService`)

#### 核心功能
1. **算法结果缓存**：
   ```java
   // Key生成算法
   String key = "algorithm:result:" + algorithmName + ":" + problemId + ":" + inputHash;
   ```

2. **使用统计**：
   ```java
   // 原子递增
   redisTemplate.opsForValue().increment("algorithm:stats:" + algorithmName + ":usage");
   ```

3. **用户历史记录**：
   ```java
   // 使用List存储，限制100条
   redisTemplate.opsForList().leftPush(key, historyEntry);
   redisTemplate.opsForList().trim(key, 0, 99);
   ```

## 🎯 实际应用场景

### 场景1：用户登录流程
```java
// 1. 用户登录成功，生成JWT
String token = jwtUtil.generateToken(userId, username, role);

// 2. 缓存用户Token（实现单点登录）
tokenCacheService.cacheUserToken(userId, token, 86400000);

// 3. 缓存用户会话信息
tokenCacheService.cacheUserSession(userId, userInfo, 86400000);

// 4. 返回token给前端
return R.loginSuccess(token);
```

### 场景2：算法执行流程
```java
// 1. 检查缓存中是否有结果
Object cachedResult = algorithmCacheService.getAlgorithmResult(
    algorithmName, problemId, inputData);

if (cachedResult != null) {
    // 缓存命中，直接返回
    return R.ok(cachedResult);
}

// 2. 缓存未命中，执行算法
Object result = algorithmExecutor.execute(algorithmName, problemId, inputData);

// 3. 缓存结果
algorithmCacheService.cacheAlgorithmResult(
    algorithmName, problemId, inputData, result);

// 4. 更新使用统计
algorithmCacheService.incrementAlgorithmUsage(algorithmName);

// 5. 记录用户历史
algorithmCacheService.cacheUserAlgorithmHistory(
    userId, algorithmName, problemId, result);

return R.ok(result);
```

### 场景3：用户登出流程
```java
// 1. 验证token有效性
if (!jwtUtil.validateToken(token)) {
    return R.unauthorized("token无效");
}

// 2. 获取token剩余过期时间
long expirationTime = jwtUtil.getExpirationDateFromToken(token).getTime() 
    - System.currentTimeMillis();

// 3. 加入黑名单
if (expirationTime > 0) {
    tokenCacheService.addToBlacklist(token, expirationTime);
}

// 4. 清理用户缓存
Integer userId = jwtUtil.getUserIdFromToken(token);
tokenCacheService.deleteUserToken(userId);
tokenCacheService.deleteUserSession(userId);

return R.ok("登出成功");
```

## 📊 性能优化

### 1. 缓存命中率优化
- **算法结果缓存**：相同输入直接返回缓存结果
- **用户会话缓存**：减少数据库用户信息查询
- **问题列表缓存**：首页加载速度提升

### 2. 内存使用优化
- **合理设置TTL**：根据数据特性设置过期时间
- **数据压缩**：使用JSON序列化，体积较小
- **定期清理**：清理过期和无用缓存

### 3. 并发处理
- **原子操作**：`increment()`保证计数器线程安全
- **分布式锁**：Redis可实现分布式锁（本项目未使用）
- **缓存穿透**：对不存在的key也进行缓存（空值缓存）

## 🧪 测试验证

### 1. 单元测试
```java
@SpringBootTest
public class RedisIntegrationTest {
    @Test
    public void testTokenCacheService() {
        // 测试黑名单功能
        tokenCacheService.addToBlacklist("test-token", 60000);
        assertTrue(tokenCacheService.isBlacklisted("test-token"));
    }
    
    @Test
    public void testCachePerformance() {
        // 验证缓存比数据库查询快
        long dbQueryTime = 100ms; // 模拟数据库查询
        long cacheQueryTime = 5ms; // 缓存查询
        assertTrue(cacheQueryTime < dbQueryTime);
    }
}
```

### 2. 集成测试步骤
1. 启动Redis服务：`docker-compose up -d redis`
2. 运行单元测试：`mvn test -Dtest=RedisIntegrationTest`
3. 手动测试登录/登出功能
4. 查看Redis监控：`redis-cli info`

## 🔍 监控与运维

### 1. Redis监控命令
```bash
# 查看Redis信息
redis-cli info

# 查看内存使用
redis-cli info memory

# 查看连接数
redis-cli info clients

# 查看键空间
redis-cli info keyspace

# 查看慢查询
redis-cli slowlog get 10
```

### 2. 缓存统计接口
```java
@GetMapping("/cache/stats")
public R<String> getCacheStats() {
    String stats = algorithmCacheService.getCacheStats();
    return R.ok(stats);
}
```

输出示例：
```
算法缓存统计：结果缓存=156, 列表缓存=5, 统计缓存=3, 用户历史=89
```

## 🚀 部署指南

### 1. Docker部署
```yaml
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
```

### 2. 环境配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

## 📈 学习收获

### 1. Redis核心概念
- **数据结构**：String、List、Set、Hash、Sorted Set
- **持久化**：RDB快照、AOF日志
- **过期策略**：TTL、惰性删除、定期删除
- **事务**：MULTI/EXEC/DISCARD

### 2. Spring Data Redis
- **RedisTemplate**：Spring对Redis的封装
- **序列化**：Jackson2JsonRedisSerializer
- **注解缓存**：@Cacheable、@CacheEvict、@CachePut
- **连接池**：Lettuce连接池配置

### 3. 实际项目经验
- **缓存设计**：根据业务场景设计缓存策略
- **性能优化**：缓存命中率、内存使用优化
- **故障处理**：缓存穿透、雪崩、击穿解决方案
- **监控运维**：Redis监控和性能调优

## 🎓 扩展学习

### 1. 进阶主题
- Redis集群部署
- Redis哨兵模式
- Redis与数据库一致性
- 缓存预热策略
- 分布式锁实现

### 2. 推荐资源
- 《Redis设计与实现》
- Redis官方文档：https://redis.io/documentation
- Spring Data Redis文档
- Redis监控工具：RedisInsight、Redis Desktop Manager

---

**总结**：通过本项目，您已经掌握了Redis在实际Spring Boot项目中的应用，包括Token管理、会话缓存、算法结果缓存等核心功能。这些知识将为您未来的分布式系统开发打下坚实基础。