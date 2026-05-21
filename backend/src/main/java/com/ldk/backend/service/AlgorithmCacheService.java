package com.ldk.backend.service;

import java.util.List;

/**
 * 算法缓存服务接口
 * 用于缓存算法计算结果、问题列表、热门算法等
 */
public interface AlgorithmCacheService {
    
    // ========== 算法结果缓存 ==========
    
    /**
     * 缓存算法计算结果
     * @param algorithmName 算法名称（如BFS、Dijkstra、A*）
     * @param problemId 问题ID
     * @param inputData 输入数据（JSON格式）
     * @param result 计算结果
     */
    void cacheAlgorithmResult(String algorithmName, Integer problemId, String inputData, Object result);
    
    /**
     * 获取缓存的算法结果
     * @param algorithmName 算法名称
     * @param problemId 问题ID
     * @param inputData 输入数据
     * @return 缓存的结果，如果不存在返回null
     */
    Object getAlgorithmResult(String algorithmName, Integer problemId, String inputData);
    
    /**
     * 删除算法结果缓存
     * @param algorithmName 算法名称
     * @param problemId 问题ID
     * @param inputData 输入数据
     */
    void deleteAlgorithmResult(String algorithmName, Integer problemId, String inputData);
    
    // ========== 问题列表缓存 ==========
    
    /**
     * 缓存问题列表
     * @param algorithmName 算法名称
     * @param problems 问题列表
     */
    void cacheProblemList(String algorithmName, List<?> problems);
    
    /**
     * 获取缓存的问题列表
     * @param algorithmName 算法名称
     * @return 问题列表，如果不存在返回null
     */
    Object getCachedProblemList(String algorithmName);
    
    /**
     * 删除问题列表缓存
     * @param algorithmName 算法名称
     */
    void deleteProblemListCache(String algorithmName);
    
    // ========== 算法统计缓存 ==========
    
    /**
     * 缓存算法使用统计
     * @param algorithmName 算法名称
     * @param stats 统计信息（如使用次数、平均耗时等）
     */
    void cacheAlgorithmStats(String algorithmName, Object stats);
    
    /**
     * 获取算法统计缓存
     * @param algorithmName 算法名称
     * @return 统计信息
     */
    Object getAlgorithmStats(String algorithmName);
    
    /**
     * 更新算法使用次数（原子操作）
     * @param algorithmName 算法名称
     * @return 更新后的使用次数
     */
    Long incrementAlgorithmUsage(String algorithmName);
    
    /**
     * 获取算法使用次数
     * @param algorithmName 算法名称
     * @return 使用次数
     */
    Long getAlgorithmUsageCount(String algorithmName);
    
    // ========== 热门算法缓存 ==========
    
    /**
     * 缓存热门算法列表
     * @param hotAlgorithms 热门算法列表（按使用次数排序）
     */
    void cacheHotAlgorithms(List<?> hotAlgorithms);
    
    /**
     * 获取热门算法列表
     * @return 热门算法列表
     */
    Object getHotAlgorithms();
    
    // ========== 用户算法历史缓存 ==========
    
    /**
     * 缓存用户算法操作历史
     * @param userId 用户ID
     * @param algorithmName 算法名称
     * @param problemId 问题ID
     * @param result 操作结果
     */
    void cacheUserAlgorithmHistory(Integer userId, String algorithmName, Integer problemId, Object result);
    
    /**
     * 获取用户算法历史
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 历史记录列表
     */
    List<Object> getUserAlgorithmHistory(Integer userId, int limit);
    
    // ========== 批量操作和清理 ==========
    
    /**
     * 批量删除算法相关缓存
     * @param algorithmName 算法名称
     */
    void clearAlgorithmCache(String algorithmName);
    
    /**
     * 清理所有算法缓存（管理员功能）
     */
    void clearAllAlgorithmCache();
    
    /**
     * 获取缓存统计信息
     * @return 各类缓存的数量统计
     */
    String getCacheStats();
}