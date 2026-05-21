package com.ldk.backend.service;

/**
 * Token缓存服务接口
 * 用于管理JWT Token的黑名单和用户会话
 */
public interface TokenCacheService {
    
    /**
     * 将Token加入黑名单
     * @param token JWT Token
     * @param expirationTime 过期时间（毫秒）
     */
    void addToBlacklist(String token, long expirationTime);
    
    /**
     * 检查Token是否在黑名单中
     * @param token JWT Token
     * @return true表示在黑名单中，false表示不在
     */
    boolean isBlacklisted(String token);
    
    /**
     * 缓存用户Token关联
     * @param userId 用户ID
     * @param token JWT Token
     * @param expirationTime 过期时间（毫秒）
     */
    void cacheUserToken(Integer userId, String token, long expirationTime);
    
    /**
     * 获取用户的当前Token
     * @param userId 用户ID
     * @return 用户的Token，如果不存在返回null
     */
    String getUserToken(Integer userId);
    
    /**
     * 删除用户的Token缓存
     * @param userId 用户ID
     */
    void deleteUserToken(Integer userId);
    
    /**
     * 缓存用户会话信息
     * @param userId 用户ID
     * @param userInfo 用户信息（JSON格式）
     * @param expirationTime 过期时间（毫秒）
     */
    void cacheUserSession(Integer userId, Object userInfo, long expirationTime);
    
    /**
     * 获取用户会话信息
     * @param userId 用户ID
     * @return 用户会话信息
     */
    Object getUserSession(Integer userId);
    
    /**
     * 删除用户会话信息
     * @param userId 用户ID
     */
    void deleteUserSession(Integer userId);
    
    /**
     * 延长Token有效期（续期）
     * @param token Token
     * @param additionalTime 延长时间（毫秒）
     */
    void extendTokenExpiration(String token, long additionalTime);
    
    /**
     * 获取在线用户数量（统计功能）
     * @return 在线用户数量
     */
    long getOnlineUserCount();
    
    /**
     * 清理过期的Token（定时任务调用）
     */
    void cleanupExpiredTokens();
}