package com.ldk.backend.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data  // 默认添加getter setter  toString()
@AllArgsConstructor //全参数构造器
@NoArgsConstructor  // 无参数构造器
@Accessors(chain = true)// 链式编程
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer userId;

    private String username;
    private String password;
    private String role;
    private String email;
    
    // 用户姓名（用于前端显示）
    private String name;
    
    // 学号（学生用户）
    private String studentId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;  // 使用Date类型

}
