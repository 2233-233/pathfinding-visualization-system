package com.ldk.backend.service.impl;

import com.ldk.backend.service.AlgorithmCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 算法缓存服务实现类
 * 用于缓存算法计算结果、问题列表、热门算法等
 */
@Service
public class AlgorithmCacheServiceImpl implements AlgorithmCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis key前缀常量
    private static final String ALGORITHM_RESULT_PREFIX = "algorithm:result:";
    private static final String PROBLEM_LIST_PREFIX = "problem:list:";
    private static final String ALGORITHM_STATS_PREFIX = "algorithm:stats:";
    private static final String HOT_ALGORITHMS_KEY = "algorithm:hot";
    private static final String USER_ALGORITHM_HISTORY_PREFIX = "user:algorithm:history:";

    // 缓存过期时间常量（毫秒）
    private static final long RESULT_CACHE_TTL = 30 * 60 * 1000;      // 30分钟
    private static final long LIST_CACHE_TTL = 10 * 60 * 1000;        // 10分钟
    private static final long STATS_CACHE_TTL = 5 * 60 * 1000;        // 5分钟
    private static final long HOT_CACHE_TTL = 60 * 60 * 1000;         // 1小时
    private static final long HISTORY_CACHE_TTL = 24 * 60 * 60 * 1000; // 24小时

    // ========== 算法结果缓存 ==========

    /**
     * 缓存算法计算结果
     * @param algorithmName 算法名称（如BFS、Dijkstra、A*）
     * @param problemId 问题ID
     * @param inputData 输入数据（JSON格式）
     * @param result 计算结果
     */
    @Override
    public void cacheAlgorithmResult(String algorithmName, Integer problemId, String inputData, Object result) {
        String cacheKey = generateResultKey(algorithmName, problemId, inputData);
        redisTemplate.opsForValue().set(cacheKey, result, RESULT_CACHE_TTL, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取缓存的算法结果
     * @param algorithmName 算法名称
     * @param problemId 问题ID
     * @param inputData 输入数据
     * @return 缓存的结果，如果不存在返回null
     */
    @Override
    public Object getAlgorithmResult(String algorithmName, Integer problemId, String inputData) {
        String cacheKey = generateResultKey(algorithmName, problemId, inputData);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 删除算法结果缓存
     * @param algorithmName 算法名称
     * @param problemId 问题ID
     * @param inputData 输入数据
     */
    @Override
    public void deleteAlgorithmResult(String algorithmName, Integer problemId, String inputData) {
        String cacheKey = generateResultKey(algorithmName, problemId, inputData);
        redisTemplate.delete(cacheKey);
    }

    /**
     * 生成算法结果缓存key
     */
    private String generateResultKey(String algorithmName, Integer problemId, String inputData) {
        // 使用简单hash处理inputData，避免key过长
        String inputHash = Integer.toHexString(inputData.hashCode());
        return ALGORITHM_RESULT_PREFIX + algorithmName + ":" + problemId + ":" + inputHash;
    }

    // ========== 问题列表缓存 ==========

    /**
     * 缓存问题列表
     * @param algorithmName 算法名称
     * @param problems 问题列表
     */
    @Override
    public void cacheProblemList(String algorithmName, List<?> problems) {
        String cacheKey = PROBLEM_LIST_PREFIX + algorithmName;
        redisTemplate.opsForValue().set(cacheKey, problems, LIST_CACHE_TTL, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取缓存的问题列表
     * @param algorithmName 算法名称
     * @return 问题列表，如果不存在返回null
     */
    @Override
    public Object getCachedProblemList(String algorithmName) {
        String cacheKey = PROBLEM_LIST_PREFIX + algorithmName;
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 删除问题列表缓存
     * @param algorithmName 算法名称
     */
    @Override
    public void deleteProblemListCache(String algorithmName) {
        String cacheKey = PROBLEM_LIST_PREFIX + algorithmName;
        redisTemplate.delete(cacheKey);
    }

    // ========== 算法统计缓存 ==========

    /**
     * 缓存算法使用统计
     * @param algorithmName 算法名称
     * @param stats 统计信息（如使用次数、平均耗时等）
     */
    @Override
    public void cacheAlgorithmStats(String algorithmName, Object stats) {
        String cacheKey = ALGORITHM_STATS_PREFIX + algorithmName;
        redisTemplate.opsForValue().set(cacheKey, stats, STATS_CACHE_TTL, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取算法统计缓存
     * @param algorithmName 算法名称
     * @return 统计信息
     */
    @Override
    public Object getAlgorithmStats(String algorithmName) {
        String cacheKey = ALGORITHM_STATS_PREFIX + algorithmName;
        return redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 更新算法使用次数（原子操作）
     * @param algorithmName 算法名称
     * @return 更新后的使用次数
     */
    @Override
    public Long incrementAlgorithmUsage(String algorithmName) {
        String counterKey = ALGORITHM_STATS_PREFIX + algorithmName + ":usage";
        return redisTemplate.opsForValue().increment(counterKey);
    }

    /**
     * 获取算法使用次数
     * @param algorithmName 算法名称
     * @return 使用次数
     */
    @Override
    public Long getAlgorithmUsageCount(String algorithmName) {
        String counterKey = ALGORITHM_STATS_PREFIX + algorithmName + ":usage";
        Object value = redisTemplate.opsForValue().get(counterKey);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    // ========== 热门算法缓存 ==========

    /**
     * 缓存热门算法列表
     * @param hotAlgorithms 热门算法列表（按使用次数排序）
     */
    @Override
    public void cacheHotAlgorithms(List<?> hotAlgorithms) {
        redisTemplate.opsForValue().set(HOT_ALGORITHMS_KEY, hotAlgorithms, HOT_CACHE_TTL, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取热门算法列表
     * @return 热门算法列表
     */
    @Override
    public Object getHotAlgorithms() {
        return redisTemplate.opsForValue().get(HOT_ALGORITHMS_KEY);
    }

    // ========== 用户算法历史缓存 ==========

    /**
     * 缓存用户算法操作历史
     * @param userId 用户ID
     * @param algorithmName 算法名称
     * @param problemId 问题ID
     * @param result 操作结果
     */
    @Override
    public void cacheUserAlgorithmHistory(Integer userId, String algorithmName, Integer problemId, Object result) {
        String historyKey = USER_ALGORITHM_HISTORY_PREFIX + userId;
        
        // 使用List存储历史记录（最近操作在列表开头）
        String historyEntry = String.format("%s|%d|%s|%d", 
            algorithmName, problemId, result.toString(), System.currentTimeMillis());
        
        // 添加到列表开头，并限制列表长度（最近100条）
        redisTemplate.opsForList().leftPush(historyKey, historyEntry);
        redisTemplate.opsForList().trim(historyKey, 0, 99);
        
        // 设置过期时间
        redisTemplate.expire(historyKey, HISTORY_CACHE_TTL, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取用户算法历史
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 历史记录列表
     */
    @Override
    public List<Object> getUserAlgorithmHistory(Integer userId, int limit) {
        String historyKey = USER_ALGORITHM_HISTORY_PREFIX + userId;
        return redisTemplate.opsForList().range(historyKey, 0, limit - 1);
    }

    // ========== 批量操作和清理 ==========

    /**
     * 批量删除算法相关缓存
     * @param algorithmName 算法名称
     */
    @Override
    public void clearAlgorithmCache(String algorithmName) {
        // 删除结果缓存（使用模式匹配）
        String resultPattern = ALGORITHM_RESULT_PREFIX + algorithmName + ":*";
        redisTemplate.delete(redisTemplate.keys(resultPattern));
        
        // 删除问题列表缓存
        deleteProblemListCache(algorithmName);
        
        // 删除统计缓存
        String statsKey = ALGORITHM_STATS_PREFIX + algorithmName;
        redisTemplate.delete(statsKey);
        
        String usageKey = ALGORITHM_STATS_PREFIX + algorithmName + ":usage";
        redisTemplate.delete(usageKey);
    }

    /**
     * 清理所有算法缓存（管理员功能）
     */
    @Override
    public void clearAllAlgorithmCache() {
        // 清理结果缓存
        String resultPattern = ALGORITHM_RESULT_PREFIX + "*";
        redisTemplate.delete(redisTemplate.keys(resultPattern));
        
        // 清理问题列表缓存
        String listPattern = PROBLEM_LIST_PREFIX + "*";
        redisTemplate.delete(redisTemplate.keys(listPattern));
        
        // 清理统计缓存
        String statsPattern = ALGORITHM_STATS_PREFIX + "*";
        redisTemplate.delete(redisTemplate.keys(statsPattern));
        
        // 清理热门算法缓存
        redisTemplate.delete(HOT_ALGORITHMS_KEY);
        
        // 注意：不清理用户历史缓存，因为这是用户个人数据
    }

    /**
     * 获取缓存统计信息
     * @return 各类缓存的数量统计
     */
    @Override
    public String getCacheStats() {
        long resultCount = redisTemplate.keys(ALGORITHM_RESULT_PREFIX + "*") != null ? redisTemplate.keys(ALGORITHM_RESULT_PREFIX + "*").size() : 0;
        long listCount = redisTemplate.keys(PROBLEM_LIST_PREFIX + "*") != null ? redisTemplate.keys(PROBLEM_LIST_PREFIX + "*").size() : 0;
        long statsCount = redisTemplate.keys(ALGORITHM_STATS_PREFIX + "*") != null ? redisTemplate.keys(ALGORITHM_STATS_PREFIX + "*").size() : 0;
        long historyCount = redisTemplate.keys(USER_ALGORITHM_HISTORY_PREFIX + "*") != null ? redisTemplate.keys(USER_ALGORITHM_HISTORY_PREFIX + "*").size() : 0;
        
        return String.format("算法缓存统计：结果缓存=%d, 列表缓存=%d, 统计缓存=%d, 用户历史=%d", 
            resultCount, listCount, statsCount, historyCount);
    }
}
