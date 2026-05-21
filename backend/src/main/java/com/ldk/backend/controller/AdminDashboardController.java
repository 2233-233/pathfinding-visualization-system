package com.ldk.backend.controller;

import com.ldk.backend.commons.JwtUtil;
import com.ldk.backend.commons.R;
import com.ldk.backend.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员仪表板接口", description = "管理员仪表板数据统计接口")
@CrossOrigin
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表板数据", description = "获取管理员仪表板的所有统计数据，需要管理员权限")
    public R<Map<String, Object>> getDashboardData(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // 验证管理员权限
        R<String> authResult = checkAdminPermission(authHeader);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }

        return adminDashboardService.getDashboardData();
    }

    /**
     * 检查管理员权限
     */
    private R<String> checkAdminPermission(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }

        String role = jwtUtil.getRoleFromToken(token);
        if (!"admin".equals(role)) {
            return R.forbidden("需要管理员权限");
        }

        return R.ok("权限验证通过");
    }
}
