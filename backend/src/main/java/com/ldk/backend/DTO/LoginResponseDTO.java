package com.ldk.backend.DTO;

import com.ldk.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 登录响应DTO
 * 包含token和用户信息
 * 
 * 学习要点：
 * 1. DTO（Data Transfer Object）：数据传输对象，用于在不同层之间传输数据
 * 2. 为什么需要LoginResponseDTO：登录成功后需要返回token和用户信息，但User实体包含敏感信息（如密码）
 * 3. 使用@JsonIgnore可以在User实体中忽略密码字段，但创建专门的DTO更清晰
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginResponseDTO {
    /**
     * JWT token
     * 前端需要将此token存储在localStorage或cookie中
     * 后续请求需要在Authorization header中携带：Bearer {token}
     */
    private String token;

    /**
     * 用户信息（不包含密码）
     * 使用User实体，但实际返回时会忽略密码字段
     */
    private User user;

    /**
     * token过期时间（毫秒时间戳）
     * 前端可以根据此时间判断token是否即将过期
     */
    private Long expiresAt;

    /**
     * token类型，固定为"Bearer"
     * 这是OAuth 2.0标准，告诉前端如何携带token
     */
    private String tokenType = "Bearer";
}
