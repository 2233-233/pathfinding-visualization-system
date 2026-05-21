package com.ldk.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.DTO.LoginDTO;
import com.ldk.backend.DTO.LoginResponseDTO;
import com.ldk.backend.DTO.RegisterDTO;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.User;

import java.util.Map;

public interface UserService {
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册响应，包含token和用户信息
     */
    R<LoginResponseDTO> register(RegisterDTO registerDTO);
    
    /**
     * 用户登录（原始格式）
     * @param loginDTO 登录信息（用户名和密码）
     * @return 登录响应，包含token和用户信息
     */
    R<LoginResponseDTO> login(LoginDTO loginDTO);
    
    /**
     * 用户登录（前端兼容格式）
     * @param loginDTO 登录信息（用户名和密码）
     * @return 登录响应，格式匹配前端Vue项目的Mock数据
     */
    R<Map<String, Object>> frontendLogin(LoginDTO loginDTO);
    
    /**
     * 用户登出
     * @param token JWT token
     * @return 登出结果
     */
    R<String> logout(String token);
    
    /**
     * 获取当前用户信息
     * @param token JWT token
     * @return 用户信息
     */
    R<User> getCurrentUserInfo(String token);
    
    // ========== 用户管理相关方法 ==========
    
    /**
     * 获取用户列表（分页+搜索）
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选，用于精确搜索）
     * @param name 姓名（可选，用于精确搜索）
     * @param studentId 学号（可选，用于精确搜索）
     * @param role 角色（可选，用于精确筛选）
     * @return 分页用户列表
     */
    R<Page<User>> getUserList(Integer page, Integer size, String username, String name, String studentId, String role);
    
    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    R<User> getUserById(Integer userId);
    
    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建结果
     */
    R<String> createUser(User user);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param user 更新的用户信息
     * @return 更新结果
     */
    R<String> updateUser(Integer userId, User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    R<String> deleteUser(Integer userId);

    //按学号查找
    R<User> getUserByStudentId(String studentId);
}
