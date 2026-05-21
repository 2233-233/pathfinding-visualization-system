package com.ldk.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户注册DTO
 * 用于接收用户注册请求的数据
 * 
 * 学习要点：
 * 1. DTO（Data Transfer Object）：数据传输对象，用于接收前端传入的数据
 * 2. 为什么需要RegisterDTO：注册时需要验证字段，但User实体可能包含其他字段（如userId、createdAt等）
 * 3. 使用Lombok注解简化代码：@Data自动生成getter/setter，@AllArgsConstructor生成全参构造器
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RegisterDTO {
    /**
     * 用户名，长度不少于3位
     */
    private String username;

    /**
     * 密码，长度不少于6位
     */
    private String password;

    /**
     * 学号，必填
     */
    private String studentId;

    /**
     * 姓名，可选
     */
    private String name;

    /**
     * 邮箱，可选
     */
    private String email;
}
