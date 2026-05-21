package com.ldk.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.DTO.LoginDTO;
import com.ldk.backend.DTO.LoginResponseDTO;
import com.ldk.backend.DTO.RegisterDTO;
import com.ldk.backend.commons.JwtUtil;
import com.ldk.backend.commons.R;
import com.ldk.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ldk.backend.entity.User;

import java.util.Map;

/**
 * 用户认证接口控制器
 * 学习要点：
 * 1. @RestController：声明这是一个RESTful控制器，会自动将返回对象转换为JSON
 * 2. @RequestMapping：定义控制器的基础路径，这里支持两种路径：/api/user 和 /api/auth
 * 3. @Tag：Swagger文档标签，用于API分组
 * 4. @CrossOrigin：允许跨域请求，方便前端开发
 */
@RestController
@RequestMapping({"/api/user", "/api/auth"})
@Tag(name = "用户认证接口", description = "用户登录、登出、获取信息等认证相关接口")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册接口
     * 路径：POST /api/user/register 或 POST /api/auth/register
     * 
     * 学习要点：
     * 1. @PostMapping：声明这是一个POST请求处理方法
     * 2. @RequestBody：将请求体中的JSON数据自动转换为RegisterDTO对象
     * 3. @Operation：Swagger操作描述，用于生成API文档
     * 4. 返回R<LoginResponseDTO>：统一响应格式，包含token和用户信息
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口，注册成功后返回JWT token和用户信息")
    public R<LoginResponseDTO> register(@RequestBody RegisterDTO registerDTO){
        return userService.register(registerDTO);
    }
    
    /**
     * 用户登录接口（原始格式）
     * 路径：POST /api/user/login 或 POST /api/auth/login
     * 
     * 学习要点：
     * 1. @PostMapping：声明这是一个POST请求处理方法
     * 2. @RequestBody：将请求体中的JSON数据自动转换为LoginDTO对象
     * 3. @Operation：Swagger操作描述，用于生成API文档
     * 4. 返回R<LoginResponseDTO>：统一响应格式，包含token和用户信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT token和用户信息")
    public R<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO){
        return userService.login(loginDTO);
    }

    /**
     * 用户登录接口（前端兼容格式）
     * 路径：POST /api/auth/frontend-login
     * 
     * 专门为Vue前端设计的登录接口，返回格式完全匹配前端Mock数据
     * 前端可以直接替换Mock数据调用此接口，无需修改前端代码逻辑
     */
    @PostMapping("/frontend-login")
    @Operation(summary = "前端兼容登录", description = "用户登录接口，返回格式匹配前端Vue项目的Mock数据")
    public R<Map<String, Object>> frontendLogin(@RequestBody LoginDTO loginDTO){
        return userService.frontendLogin(loginDTO);
    }

    /**
     * 用户登出接口
     * 路径：POST /api/user/logout 或 POST /api/auth/logout
     * 
     * 学习要点：
     * 1. @RequestHeader：从请求头中获取Authorization字段的值
     * 2. 前端需要在Authorization header中携带：Bearer {token}
     * 3. 登出逻辑：验证token有效性，然后返回成功（实际项目中可能需要将token加入黑名单）
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口，需要携带有效的JWT token")
    public R<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader){
        return userService.logout(authHeader);
    }

    /**
     * 获取当前用户信息接口
     * 路径：GET /api/user/info 或 GET /api/auth/info
     * 
     * 学习要点：
     * 1. @GetMapping：声明这是一个GET请求处理方法
     * 2. 需要身份验证的接口，必须携带有效的JWT token
     * 3. 从token中解析用户ID，然后查询数据库获取用户信息
     * 4. 返回的用户信息中不包含密码（安全考虑）
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息，需要携带有效的JWT token")
    public R<User> getCurrentUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader){
        return userService.getCurrentUserInfo(authHeader);
    }

    @GetMapping("/users")
    @Operation(summary = "获取用户列表", description = "获取所有用户的列表，可分页、按用户名/姓名/学号/角色筛选，需要管理员权限")
    public R<Page<User>> getUserList(@RequestHeader("Authorization") String authHeader,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String studentId,
                                     @RequestParam(required = false) String role){
        R<String> authResult = checkAdminPermission(authHeader);
        if(!authResult.isSuccess()){
            return R.unauthorized(authResult.getMsg());
        }
        
        return userService.getUserList(page, size, username, name, studentId, role);
    }
    
    @GetMapping("/users/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详情，需要管理员权限或查看自己")
    public R<User> getUserById(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Integer userId){
        R<String> authResult = checkAuthPermission(authHeader, userId);
        if(!authResult.isSuccess()){
            return R.unauthorized(authResult.getMsg());
        }
        
        return userService.getUserById(userId);
    }
    
    @PostMapping("/users")
    @Operation(summary = "创建用户", description = "创建新用户，需要管理员权限")
    public R<String> createUser(@RequestHeader("Authorization") String authHeader,
                                @RequestBody User user){
        R<String> authResult = checkAdminPermission(authHeader);
        if(!authResult.isSuccess()){
            return R.unauthorized(authResult.getMsg());
        }
        
        return userService.createUser(user);
    }
    
    @PutMapping("/users/{userId}")
    @Operation(summary = "更新用户", description = "更新用户信息，需要管理员权限或更新自己")
    public R<String> updateUser(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Integer userId,
                                @RequestBody User user){
        R<String> authResult = checkAuthPermission(authHeader, userId);
        if(!authResult.isSuccess()){
            return R.unauthorized(authResult.getMsg());
        }
        
        return userService.updateUser(userId, user);
    }
    
    @DeleteMapping("/users/{userId}")
    @Operation(summary = "删除用户", description = "删除用户，需要管理员权限")
    public R<String> deleteUser(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Integer userId){
        R<String> authResult = checkAdminPermission(authHeader);
        if(!authResult.isSuccess()){
            return R.unauthorized(authResult.getMsg());
        }
        
        return userService.deleteUser(userId);
    }

    @GetMapping("/users/student/{studentId}")
    @Operation(summary = "获取用户详情", description = "获取指定学号的用户，筛选，需要管理员权限")
    public R<User> getUserByStudentId(@RequestHeader("Authorization") String authHeader,
                                     @PathVariable String studentId){
        R<String> authResult = checkAdminPermission(authHeader);
        if(!authResult.isSuccess()){
            return R.unauthorized(authResult.getMsg());
        }

        return userService.getUserByStudentId(studentId);
    }
    
    /**
     * 检查管理员权限的私有方法
     * 只有管理员角色的用户才能通过验证
     */
    private R<String> checkAdminPermission(String authHeader) {
        // 1. 检查token是否存在
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录");
        }
        
        // 2. 提取token（去掉"Bearer "前缀）
        String token = authHeader.substring(7);
        
        // 3. 验证token是否有效
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        // 4. 从token中获取用户角色
        String role = jwtUtil.getRoleFromToken(token);
        
        // 5. 检查是否为管理员
        if (!"admin".equals(role)) {
            return R.forbidden("需要管理员权限");
        }
        
        // 6. 权限验证通过
        return R.ok("权限验证通过");
    }
    
    /**
     * 检查权限（管理员或操作自己）
     * 管理员可以操作任何用户，普通用户只能操作自己
     */
    private R<String> checkAuthPermission(String authHeader, Integer targetUserId) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        // 获取当前用户信息
        Integer currentUserId = jwtUtil.getUserIdFromToken(token);
        String currentUserRole = jwtUtil.getRoleFromToken(token);
        
        // 管理员可以操作任何用户，普通用户只能操作自己
        if (!"admin".equals(currentUserRole) && !currentUserId.equals(targetUserId)) {
            return R.forbidden("没有权限操作其他用户");
        }
        
        return R.ok("权限验证通过");
    }


}
