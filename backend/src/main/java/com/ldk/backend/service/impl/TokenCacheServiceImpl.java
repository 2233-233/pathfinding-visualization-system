package com.ldk.backend.service.impl;

import com.ldk.backend.service.TokenCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Token缓存服务实现类
 * 用于管理JWT Token的黑名单和用户会话
 */
@Service
public class TokenCacheServiceImpl implements TokenCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis key前缀常量
    private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:token:";
    private static final String USER_TOKEN_PREFIX = "user:token:";
    private static final String USER_SESSION_PREFIX = "session:user:";

    /**
     * 将Token加入黑名单
     * @param token JWT Token
     * @param expirationTime 过期时间（毫秒）
     */
    @Override
    public void addToBlacklist(String token, long expirationTime) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        // 计算剩余过期时间（秒）
        long ttlSeconds = expirationTime / 1000;
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, "1", ttlSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 检查Token是否在黑名单中
     * @param token JWT Token
     * @return true表示在黑名单中，false表示不在
     */
    @Override
    public boolean isBlacklisted(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 缓存用户Token关联
     * @param userId 用户ID
     * @param token JWT Token
     * @param expirationTime 过期时间（毫秒）
     */
    @Override
    public void cacheUserToken(Integer userId, String token, long expirationTime) {
        String key = USER_TOKEN_PREFIX + userId;
        long ttlSeconds = expirationTime / 1000;
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取用户的当前Token
     * @param userId 用户ID
     * @return 用户的Token，如果不存在返回null
     */
    @Override
    public String getUserToken(Integer userId) {
        String key = USER_TOKEN_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 删除用户的Token缓存
     * @param userId 用户ID
     */
    @Override
    public void deleteUserToken(Integer userId) {
        String key = USER_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 缓存用户会话信息
     * @param userId 用户ID
     * @param userInfo 用户信息（JSON格式）
     * @param expirationTime 过期时间（毫秒）
     */
    @Override
    public void cacheUserSession(Integer userId, Object userInfo, long expirationTime) {
        String key = USER_SESSION_PREFIX + userId;
        long ttlSeconds = expirationTime / 1000;
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, userInfo, ttlSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取用户会话信息
     * @param userId 用户ID
     * @return 用户会话信息
     */
    @Override
    public Object getUserSession(Integer userId) {
        String key = USER_SESSION_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除用户会话信息
     * @param userId 用户ID
     */
    @Override
    public void deleteUserSession(Integer userId) {
        String key = USER_SESSION_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 延长Token有效期（续期）
     * @param token Token
     * @param additionalTime 延长时间（毫秒）
     */
    @Override
    public void extendTokenExpiration(String token, long additionalTime) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        Long currentTtl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (currentTtl != null && currentTtl > 0) {
            long newTtl = currentTtl + (additionalTime / 1000);
            redisTemplate.expire(key, newTtl, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取在线用户数量（统计功能）
     * @return 在线用户数量
     */
    @Override
    public long getOnlineUserCount() {
        // 使用Redis的keys命令获取所有会话key（生产环境建议使用SCAN）
        String pattern = USER_SESSION_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? keys.size() : 0;
    }

    /**
     * 清理过期的Token（定时任务调用）
     */
    @Override
    public void cleanupExpiredTokens() {
        // Redis会自动清理过期的key，这个方法主要用于统计或日志记录
        // 在实际项目中，可以在这里添加清理逻辑或发送清理报告
        System.out.println("Token清理任务执行时间: " + System.currentTimeMillis());
    }
}