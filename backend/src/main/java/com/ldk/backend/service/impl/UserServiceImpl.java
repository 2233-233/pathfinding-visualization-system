package com.ldk.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.DTO.LoginDTO;
import com.ldk.backend.DTO.LoginResponseDTO;
import com.ldk.backend.DTO.RegisterDTO;
import com.ldk.backend.commons.JwtUtil;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.User;
import com.ldk.backend.mapper.UserMapper;
import com.ldk.backend.service.TokenCacheService;
import com.ldk.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private TokenCacheService tokenCacheService;

    @Override
    public R<LoginResponseDTO> register(RegisterDTO registerDTO) {
        // 1. 验证必填字段
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().length() < 3) {
            return R.registerError("用户名不能少于3位");
        }
        
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().length() < 6) {
            return R.registerError("密码不能少于6位");
        }
        
        if (registerDTO.getStudentId() == null || registerDTO.getStudentId().trim().isEmpty()) {
            return R.registerError("学号不能为空");
        }
        
        // 2. 检查用户名是否已存在
        QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
        usernameQueryWrapper.eq("username", registerDTO.getUsername());
        if (userMapper.selectOne(usernameQueryWrapper) != null) {
            return R.registerError("用户名已存在");
        }
        
        // 3. 检查学号是否已存在
        QueryWrapper<User> studentIdQueryWrapper = new QueryWrapper<>();
        studentIdQueryWrapper.eq("student_id", registerDTO.getStudentId());
        if (userMapper.selectOne(studentIdQueryWrapper) != null) {
            return R.registerError("学号已存在");
        }
        
        // 4. 创建用户实体
        User user = new User();
        user.setUsername(registerDTO.getUsername().trim());
        user.setPassword(registerDTO.getPassword().trim());
        user.setStudentId(registerDTO.getStudentId().trim());
        user.setRole("student"); // 默认角色为student
        
        // 设置可选字段
        if (registerDTO.getName() != null && !registerDTO.getName().trim().isEmpty()) {
            user.setName(registerDTO.getName().trim());
        }
        
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty()) {
            user.setEmail(registerDTO.getEmail().trim());
        }
        
        // 设置创建时间
        user.setCreatedAt(new Date());
        
        // 5. 插入数据库
        int result = userMapper.insert(user);
        if (result != 1) {
            return R.registerError("注册失败，请稍后重试");
        }
        
        // 6. 生成JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        
        // 7. 创建登录响应DTO
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUser(user);
        response.setExpiresAt(new Date(System.currentTimeMillis() + 86400000).getTime());
        response.setTokenType("Bearer");
        
        return R.registerSuccess(response);
    }
    
    @Override
    public R<LoginResponseDTO> login(LoginDTO loginDTO) {
        // 1. 获取用户名和密码
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        
        // 2. 判断用户名密码是否为空
        if (username == null || username.trim().length() == 0) {
            return R.loginError("用户名不能为空");
        }

        if (password == null || password.trim().length() == 0) {
            return R.loginError("密码不能为空");
        }
        
        // 3. 创建查询条件包装器，用于构建SQL查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 设置查询条件为 username 字段等于传入的用户名参数
        queryWrapper.eq("username", username);

        // 4. 根据查询条件从数据库获取用户信息
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return R.loginError("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            return R.loginError("密码错误");
        }
        
        // 5. 生成JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        
        // 6. 缓存用户会话信息
        try {
            // 缓存用户token关联（实现单点登录）
            tokenCacheService.cacheUserToken(user.getUserId(), token, 86400000); // 24小时
            
            // 缓存用户会话信息（减少数据库查询）
            // 注意：这里缓存的是完整的用户对象，包含密码（实际项目中应该移除敏感信息）
            User cachedUser = new User();
            cachedUser.setUserId(user.getUserId());
            cachedUser.setUsername(user.getUsername());
            cachedUser.setName(user.getName());
            cachedUser.setStudentId(user.getStudentId());
            cachedUser.setEmail(user.getEmail());
            cachedUser.setRole(user.getRole());
            cachedUser.setCreatedAt(user.getCreatedAt());
            // 不缓存密码，安全考虑
            
            tokenCacheService.cacheUserSession(user.getUserId(), cachedUser, 86400000); // 24小时
        } catch (Exception e) {
            // 缓存失败不影响登录流程，记录日志即可
            // 实际项目中应该使用日志框架记录
            System.err.println("用户会话缓存失败: " + e.getMessage());
        }
        
        // 7. 创建登录响应DTO
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUser(user);
        response.setExpiresAt(new Date(System.currentTimeMillis() + 86400000).getTime());
        response.setTokenType("Bearer");
        
        return R.loginSuccess(response);
    }
    
    @Override
    public R<Map<String, Object>> frontendLogin(LoginDTO loginDTO) {
        R<LoginResponseDTO> loginResponse = login(loginDTO);
        
        if (!loginResponse.isSuccess()) {
            return R.loginError(loginResponse.getMsg());
        }
        
        LoginResponseDTO data = loginResponse.getData();
        User user = data.getUser();
        
        // 转换为前端期望的格式
        Map<String, Object> frontendData = new HashMap<>();
        frontendData.put("token", data.getToken());
        frontendData.put("role", user.getRole() != null ? user.getRole() : "student");
        frontendData.put("name", user.getName() != null ? user.getName() : user.getUsername());
        frontendData.put("username", user.getUsername());
        frontendData.put("studentId", user.getStudentId() != null ? user.getStudentId() : "");
        
        return R.loginSuccess(frontendData);
    }
    
    @Override
    public R<String> logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return R.error("token不能为空");
        }
        
        // 验证token格式（Bearer token）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        try {
            // 获取token的剩余过期时间
            io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
            long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
            
            if (expirationTime > 0) {
                // 将token加入黑名单，剩余时间即为黑名单有效期
                tokenCacheService.addToBlacklist(token, expirationTime);
                
                // 从token中获取用户ID，删除用户token缓存
                Integer userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    tokenCacheService.deleteUserToken(userId);
                    tokenCacheService.deleteUserSession(userId);
                }
            }
            
            return R.ok("登出成功，token已加入黑名单");
        } catch (Exception e) {
            // 如果获取token信息失败，仍然返回登出成功（前端已删除token）
            return R.ok("登出成功");
        }
    }
    
    @Override
    public R<User> getCurrentUserInfo(String token) {
        if (token == null || token.trim().isEmpty()) {
            return R.unauthorized("未提供token");
        }
        
        // 验证token格式（Bearer token）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        // 检查Token是否在黑名单中（登出后Token失效）
        if (tokenCacheService.isBlacklisted(token)) {
            return R.unauthorized("Token已失效，请重新登录");
        }
        
        try {
            // 从token中获取用户ID
            Integer userId = jwtUtil.getUserIdFromToken(token);
            
            // 先从Redis缓存获取用户信息（减少数据库查询）
            try {
                Object cachedUserObj = tokenCacheService.getUserSession(userId);
                if (cachedUserObj != null) {
                    // 缓存命中，直接返回（缓存中不包含密码，安全）
                    return R.ok((User) cachedUserObj);
                }
            } catch (Exception cacheException) {
                // 缓存查询失败不影响主流程，继续查数据库
                System.err.println("用户缓存查询失败: " + cacheException.getMessage());
            }
            
            // 缓存未命中，查询数据库
            User user = userMapper.selectById(userId);
            if (user == null) {
                return R.notFound("用户不存在");
            }
            
            // 清除密码信息（安全考虑）
            user.setPassword(null);
            
            return R.ok(user);
        } catch (Exception e) {
            return R.error("获取用户信息失败: " + e.getMessage());
        }

    }

    @Override
    public R<Page<User>> getUserList(Integer page, Integer size, String username, String name, String studentId, String role){
        try {
            if(page == null || page < 1){
                page = 1;
            }
            if(size == null || size < 1){
                size = 10;
            }

            Page<User> pageInfo = new Page<>(page, size);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();

            // 模糊匹配用户名（如果提供）
            if (username != null && !username.trim().isEmpty()) {
                queryWrapper.like("username", username.trim());
            }
            
            // 模糊匹配姓名（如果提供）
            if (name != null && !name.trim().isEmpty()) {
                queryWrapper.like("name", name.trim());
            }
            
            // 模糊匹配学号（如果提供）
            if (studentId != null && !studentId.trim().isEmpty()) {
                queryWrapper.like("student_id", studentId.trim());
            }
            
            // 精确匹配角色（如果提供）
            if (role != null && !role.trim().isEmpty()) {
                queryWrapper.eq("role", role.trim());
            }

            queryWrapper.orderByDesc("created_at");
            Page<User> result = userMapper.selectPage(pageInfo, queryWrapper);
            //清除密码信息
            result.getRecords().forEach(user -> user.setPassword(null));
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取用户列表失败: " + e.getMessage());
        }
    }
    @Override
    public R<User> getUserById(Integer userId) {
        try {
            if(userId == null || userId < 1){
                return R.error("用户ID不能为空");
            }
            
            User user = userMapper.selectById(userId);
            if(user == null){
                return R.notFound("用户不存在");
            }
            
            //清除密码信息
            user.setPassword(null);
            return R.ok(user);
        }
        catch (Exception e) {
            return R.error("查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> createUser(User user){
        try {
            if(user==null||user.getUsername().trim().isEmpty()){
                return R.error("用户名不能为空");
            }
            if(user.getPassword()==null||user.getPassword().trim().isEmpty()){
                return R.error("密码不能为空");
            }
            if(user.getRole()==null||user.getRole().trim().isEmpty()){
                user.setRole("student");
            }
            
            // 检查用户名是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            
            if(userMapper.selectOne(queryWrapper) != null){
                return R.error("用户名已存在");
            }
            
            // 检查学号是否已存在（如果学号不为空）
            if(user.getStudentId() != null && !user.getStudentId().trim().isEmpty()){
                QueryWrapper<User> studentIdQueryWrapper = new QueryWrapper<>();
                studentIdQueryWrapper.eq("student_id", user.getStudentId());
                
                if(userMapper.selectOne(studentIdQueryWrapper) != null){
                    return R.error("学号已存在");
                }
            }
            
            // 设置创建时间
            user.setCreatedAt(new Date());
            
            // 插入数据库
            int result = userMapper.insert(user);
            
            if(result == 1){
                return R.ok("创建用户成功");
            }else{
                return R.error("创建用户失败");
            }
        }
        catch (Exception e) {
            return R.error("创建用户失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> updateUser(Integer userId ,User user) {
        try {
            if(userId==null){
                return R.error("用户ID不能为空");
            }
            if(userMapper.selectById(userId)==null){
                return R.error("用户不存在");
            }
            User existingUser = userMapper.selectById(userId);
            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getStudentId() != null) {
                existingUser.setStudentId(user.getStudentId());
            }
            if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
                existingUser.setRole(user.getRole());
            }
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            int result = userMapper.updateById(existingUser);
            if (result > 0) {
                return R.ok("更新用户成功");
            } else {
                return R.error("更新用户失败");
            }
        }
        catch (Exception e) {
            return R.error("更新用户失败: " + e.getMessage());
        }
    }
    @Override
    public R<String> deleteUser(Integer userId) {
        try {
            if (userId == null) {
                return R.error("用户ID不能为空");
            }

            // 检查用户是否存在
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return R.notFound("用户不存在");
            }

            // 执行删除
            int result = userMapper.deleteById(userId);
            if (result > 0) {
                return R.ok("删除用户成功");
            } else {
                return R.error("删除用户失败");
            }
        } catch (Exception e) {
            return R.error("删除用户失败: " + e.getMessage());
        }
    }

    @Override
    public R<User> getUserByStudentId(String studentId) {
        try {
            if (studentId == null || studentId.trim().isEmpty()) {
                return R.error("学号不能为空");
            }

            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id", studentId);

            User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                return R.notFound("用户不存在");
            }
            // 清除密码信息
            user.setPassword(null);
            return R.ok(user);
        }
        catch (Exception e) {
            return R.error("查询用户失败: " + e.getMessage());
        }
    }

}
