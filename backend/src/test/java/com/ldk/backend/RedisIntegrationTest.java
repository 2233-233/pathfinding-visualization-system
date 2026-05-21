package com.ldk.backend;

import com.ldk.backend.service.AlgorithmCacheService;
import com.ldk.backend.service.TokenCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis集成测试类
 * 测试Redis配置和缓存服务是否正常工作
 */
@SpringBootTest
public class RedisIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TokenCacheService tokenCacheService;

    @Autowired
    private AlgorithmCacheService algorithmCacheService;

    /**
     * 测试Redis连接是否正常
     */
    @Test
    public void testRedisConnection() {
        // 测试基本的Redis操作
        String testKey = "test:connection";
        String testValue = "Redis is working!";
        
        // 设置值
        redisTemplate.opsForValue().set(testKey, testValue);
        
        // 获取值
        Object retrievedValue = redisTemplate.opsForValue().get(testKey);
        
        // 验证
        assertNotNull(retrievedValue);
        assertEquals(testValue, retrievedValue.toString());
        
        // 清理测试数据
        redisTemplate.delete(testKey);
        
        System.out.println("✅ Redis连接测试通过");
    }

    /**
     * 测试TokenCacheService功能
     */
    @Test
    public void testTokenCacheService() {
        String testToken = "test-jwt-token-123";
        Integer testUserId = 999;
        String testUserInfo = "{\"username\":\"testuser\",\"role\":\"student\"}";
        
        // 1. 测试黑名单功能
        tokenCacheService.addToBlacklist(testToken, 60000); // 1分钟
        assertTrue(tokenCacheService.isBlacklisted(testToken));
        
        // 2. 测试用户Token缓存
        tokenCacheService.cacheUserToken(testUserId, testToken, 60000);
        String cachedToken = tokenCacheService.getUserToken(testUserId);
        assertEquals(testToken, cachedToken);
        
        // 3. 测试用户会话缓存
        tokenCacheService.cacheUserSession(testUserId, testUserInfo, 60000);
        Object cachedSession = tokenCacheService.getUserSession(testUserId);
        assertNotNull(cachedSession);
        
        // 4. 测试删除功能
        tokenCacheService.deleteUserToken(testUserId);
        tokenCacheService.deleteUserSession(testUserId);
        assertNull(tokenCacheService.getUserToken(testUserId));
        
        System.out.println("✅ TokenCacheService测试通过");
    }

    /**
     * 测试AlgorithmCacheService功能
     */
    @Test
    public void testAlgorithmCacheService() {
        String algorithmName = "BFS";
        Integer problemId = 1;
        String inputData = "{\"grid\":[[0,1,0],[0,0,0],[1,0,0]],\"start\":[0,0],\"end\":[2,2]}";
        String result = "{\"path\":[[0,0],[0,1],[1,1],[2,1],[2,2]],\"steps\":5}";
        
        // 1. 测试算法结果缓存
        algorithmCacheService.cacheAlgorithmResult(algorithmName, problemId, inputData, result);
        Object cachedResult = algorithmCacheService.getAlgorithmResult(algorithmName, problemId, inputData);
        assertNotNull(cachedResult);
        assertEquals(result, cachedResult.toString());
        
        // 2. 测试问题列表缓存
        List<String> problems = Arrays.asList("Problem1", "Problem2", "Problem3");
        algorithmCacheService.cacheProblemList(algorithmName, problems);
        Object cachedProblems = algorithmCacheService.getCachedProblemList(algorithmName);
        assertNotNull(cachedProblems);
        
        // 3. 测试算法使用统计
        Long usageCount = algorithmCacheService.incrementAlgorithmUsage(algorithmName);
        assertNotNull(usageCount);
        assertTrue(usageCount > 0);
        
        Long retrievedCount = algorithmCacheService.getAlgorithmUsageCount(algorithmName);
        assertEquals(usageCount, retrievedCount);
        
        // 4. 测试用户算法历史
        algorithmCacheService.cacheUserAlgorithmHistory(999, algorithmName, problemId, "success");
        List<Object> history = algorithmCacheService.getUserAlgorithmHistory(999, 10);
        assertNotNull(history);
        assertFalse(history.isEmpty());
        
        // 5. 测试缓存统计
        String stats = algorithmCacheService.getCacheStats();
        assertNotNull(stats);
        System.out.println("缓存统计: " + stats);
        
        // 6. 测试清理功能
        algorithmCacheService.clearAlgorithmCache(algorithmName);
        Object clearedResult = algorithmCacheService.getAlgorithmResult(algorithmName, problemId, inputData);
        assertNull(clearedResult);
        
        System.out.println("✅ AlgorithmCacheService测试通过");
    }

    /**
     * 测试Redis过期功能
     */
    @Test
    public void testRedisExpiration() throws InterruptedException {
        String testKey = "test:expiration";
        String testValue = "This will expire";
        
        // 设置1秒后过期
        redisTemplate.opsForValue().set(testKey, testValue, 1, java.util.concurrent.TimeUnit.SECONDS);
        
        // 立即获取应该存在
        Object immediateValue = redisTemplate.opsForValue().get(testKey);
        assertNotNull(immediateValue);
        
        // 等待2秒
        Thread.sleep(2000);
        
        // 再次获取应该为null
        Object expiredValue = redisTemplate.opsForValue().get(testKey);
        assertNull(expiredValue);
        
        System.out.println("✅ Redis过期功能测试通过");
    }

    /**
     * 测试Redis数据结构
     */
    @Test
    public void testRedisDataStructures() {
        // 测试List数据结构
        String listKey = "test:list";
        redisTemplate.opsForList().rightPush(listKey, "item1");
        redisTemplate.opsForList().rightPush(listKey, "item2");
        redisTemplate.opsForList().rightPush(listKey, "item3");
        
        Long listSize = redisTemplate.opsForList().size(listKey);
        assertEquals(3, listSize);
        
        // 测试Set数据结构
        String setKey = "test:set";
        redisTemplate.opsForSet().add(setKey, "member1", "member2", "member3");
        
        Long setSize = redisTemplate.opsForSet().size(setKey);
        assertEquals(3, setSize);
        
        // 测试Hash数据结构
        String hashKey = "test:hash";
        redisTemplate.opsForHash().put(hashKey, "field1", "value1");
        redisTemplate.opsForHash().put(hashKey, "field2", "value2");
        
        Object hashValue = redisTemplate.opsForHash().get(hashKey, "field1");
        assertEquals("value1", hashValue);
        
        // 清理测试数据
        redisTemplate.delete(listKey);
        redisTemplate.delete(setKey);
        redisTemplate.delete(hashKey);
        
        System.out.println("✅ Redis数据结构测试通过");
    }

    /**
     * 测试性能：缓存命中率
     */
    @Test
    public void testCachePerformance() {
        String algorithmName = "Dijkstra";
        Integer problemId = 2;
        String inputData = "{\"graph\":\"test\"}";
        String result = "{\"distance\":10}";
        
        long startTime, endTime;
        
        // 第一次：数据库查询（模拟）
        startTime = System.currentTimeMillis();
        // 模拟数据库查询耗时
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        algorithmCacheService.cacheAlgorithmResult(algorithmName, problemId, inputData, result);
        endTime = System.currentTimeMillis();
        long dbQueryTime = endTime - startTime;
        
        // 第二次：缓存查询
        startTime = System.currentTimeMillis();
        Object cachedResult = algorithmCacheService.getAlgorithmResult(algorithmName, problemId, inputData);
        endTime = System.currentTimeMillis();
        long cacheQueryTime = endTime - startTime;
        
        assertNotNull(cachedResult);
        
        // 验证缓存查询比数据库查询快
        assertTrue(cacheQueryTime < dbQueryTime);
        
        System.out.println("数据库查询时间: " + dbQueryTime + "ms");
        System.out.println("缓存查询时间: " + cacheQueryTime + "ms");
        System.out.println("✅ 缓存性能测试通过，缓存比数据库快 " + (dbQueryTime - cacheQueryTime) + "ms");
    }
}