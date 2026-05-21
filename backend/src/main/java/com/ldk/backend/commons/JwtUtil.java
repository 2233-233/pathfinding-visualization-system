package com.ldk.backend.commons;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT token
 * 
 * 学习要点：
 * 1. @Component注解：将此类声明为Spring组件，可以被Spring容器管理
 * 2. @Value注解：从application.yml中读取配置值
 * 3. JWT组成：header.payload.signature
 * 4. 密钥使用：使用HMAC-SHA算法进行签名
 */
@Component
public class JwtUtil {

    // 从配置文件中读取JWT密钥和过期时间
    @Value("${jwt.secret:ldk-secret-key-2024-springboot-backend-jwt-token}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认24小时
    private Long expiration;

    // 生成安全的密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT token字符串
     * 
     * 学习要点：
     * 1. Jwts.builder()：创建JWT构建器
     * 2. setClaims()：设置payload中的声明（claims）
     * 3. setSubject()：设置主题（通常是用户ID）
     * 4. setIssuedAt()：设置签发时间
     * 5. setExpiration()：设置过期时间
     * 6. signWith()：使用密钥和算法进行签名
     * 7. compact()：生成最终的token字符串
     */
    public String generateToken(Integer userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中解析声明（claims）
     * @param token JWT token
     * @return Claims对象，包含token中的所有声明
     * 
     * 学习要点：
     * 1. Jwts.parserBuilder()：创建JWT解析器构建器
     * 2. setSigningKey()：设置验证密钥
     * 3. build().parseClaimsJws()：解析并验证token
     * 4. getBody()：获取payload部分
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证token是否有效
     * @param token JWT token
     * @return 是否有效
     * 
     * 学习要点：
     * 1. 尝试解析token，如果解析成功且未过期则有效
     * 2. 使用try-catch处理解析异常
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查token是否过期
     * @param claims token声明
     * @return 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Integer.class);
    }

    /**
     * 从token中获取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从token中获取用户角色
     * @param token JWT token
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 刷新token（生成新的token，使用相同的声明但新的过期时间）
     * @param token 旧token
     * @return 新token
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + expiration));
        
        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
