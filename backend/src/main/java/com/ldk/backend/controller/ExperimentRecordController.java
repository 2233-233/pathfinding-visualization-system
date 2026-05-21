package com.ldk.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.JwtUtil;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ExperimentRecord;
import com.ldk.backend.entity.ExperimentStep;
import com.ldk.backend.entity.Obstacle;
import com.ldk.backend.service.ExperimentService;
import com.ldk.backend.service.ObstacleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.ldk.backend.DTO.ExperimentRecordExportVO;

/**
 * 实验记录管理接口控制器
 * 学习要点：
 * 1. @RestController：声明这是一个RESTful控制器，会自动将返回对象转换为JSON
 * 2. @RequestMapping：定义控制器的基础路径
 * 3. @Tag：Swagger文档标签，用于API分组
 * 4. @CrossOrigin：允许跨域请求，方便前端开发
 */
@RestController
@RequestMapping("/api/experiments")
@Tag(name = "实验记录管理接口", description = "实验记录CRUD、实验步骤管理、实验统计等接口")
@CrossOrigin
public class ExperimentRecordController {

    @Autowired
    private ExperimentService experimentService;
    
    @Autowired
    private ObstacleService obstacleService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // ========== 实验记录CRUD接口 ==========

    /**
     * 获取实验记录列表（分页）
     * 路径：GET /api/experiments
     * 
     * 学习要点：
     * 1. @GetMapping：声明这是一个GET请求处理方法
     * 2. @RequestParam：从请求参数中获取值，可以设置默认值
     * 3. @RequestHeader：从请求头中获取Authorization字段的值
     * 4. 支持按用户ID、算法ID、状态筛选
     * 5. 支持时间段搜索：开始时间、结束时间、开始时间范围、结束时间范围
     */
    @GetMapping
    @Operation(summary = "获取实验记录列表（分页）", description = "获取实验记录列表，支持分页和按用户ID、算法名称、状态筛选，支持时间段搜索")
    public R<Page<ExperimentRecord>> getExperimentList(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String algorithmName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTimeTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTimeTo) {
        
        // 权限检查：管理员可以查看所有，普通用户只能查看自己的
        R<String> authResult = checkExperimentPermission(authHeader, userId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.getExperimentList(page, size, userId, algorithmName, status, 
                                                  startTime, endTime, startTimeFrom, startTimeTo, 
                                                  endTimeFrom, endTimeTo);
    }

    /**
     * 获取实验记录列表（不分页）
     * 路径：GET /api/experiments/all
     */
    @GetMapping("/all")
    @Operation(summary = "获取实验记录列表（不分页）", description = "获取所有实验记录列表，支持按用户ID、算法名称、状态筛选，支持时间段搜索")
    public R<List<ExperimentRecord>> getExperimentListAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String algorithmName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTimeTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTimeTo) {
        
        R<String> authResult = checkExperimentPermission(authHeader, userId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.getExperimentListAll(userId, algorithmName, status, 
                                                     startTime, endTime, startTimeFrom, startTimeTo, 
                                                     endTimeFrom, endTimeTo);
    }

    /**
     * 获取实验记录详情
     * 路径：GET /api/experiments/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取实验记录详情", description = "根据实验记录ID获取实验记录详情")
    public R<ExperimentRecord> getExperimentById(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Integer recordId) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return recordResult;
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return recordResult;
    }

    /**
     * 创建实验记录（开始实验）
     * 路径：POST /api/experiments
     */
    @PostMapping
    @Operation(summary = "创建实验记录", description = "创建新的实验记录（开始实验）")
    public R<String> createExperiment(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ExperimentRecord record) {
        
        // 检查token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        // 从token中获取用户ID，并设置到记录中
        Integer currentUserId = jwtUtil.getUserIdFromToken(token);
        record.setUserId(currentUserId);
        
        return experimentService.createExperiment(record);
    }

    /**
     * 更新实验记录（完成实验）
     * 路径：PUT /api/experiments/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新实验记录", description = "更新实验记录信息（完成实验）")
    public R<String> updateExperiment(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Integer recordId,
            @RequestBody ExperimentRecord record) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> existingRecordResult = experimentService.getExperimentById(recordId);
        if (!existingRecordResult.isSuccess()) {
            return R.error(existingRecordResult.getMsg());
        }
        
        Integer recordUserId = existingRecordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.updateExperiment(recordId, record);
    }

    /**
     * 删除实验记录
     * 路径：DELETE /api/experiments/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除实验记录", description = "删除指定的实验记录")
    public R<String> deleteExperiment(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Integer recordId) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> existingRecordResult = experimentService.getExperimentById(recordId);
        if (!existingRecordResult.isSuccess()) {
            return R.error(existingRecordResult.getMsg());
        }
        
        Integer recordUserId = existingRecordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.deleteExperiment(recordId);
    }

    // ========== 实验步骤管理接口 ==========

    /**
     * 获取实验步骤列表
     * 路径：GET /api/experiments/{id}/steps
     */
    @GetMapping("/{id}/steps")
    @Operation(summary = "获取实验步骤列表", description = "获取指定实验记录的所有实验步骤")
    public R<List<ExperimentStep>> getExperimentSteps(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Integer recordId) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.getExperimentSteps(recordId);
    }

    /**
     * 添加实验步骤
     * 路径：POST /api/experiments/{id}/steps
     */
    @PostMapping("/{id}/steps")
    @Operation(summary = "添加实验步骤", description = "为指定实验记录添加新的实验步骤")
    public R<String> addExperimentStep(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Integer recordId,
            @RequestBody ExperimentStep step) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        // 设置实验记录ID
        step.setRecordId(recordId);
        
        return experimentService.addExperimentStep(step);
    }

    /**
     * 更新实验步骤
     * 路径：PUT /api/experiments/{id}/steps/{stepId}
     */
    @PutMapping("/{id}/steps/{stepId}")
    @Operation(summary = "更新实验步骤", description = "更新指定实验记录的实验步骤")
    public R<String> updateExperimentStep(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Integer recordId,
            @PathVariable("stepId") Integer stepId,
            @RequestBody ExperimentStep step) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.updateExperimentStep(stepId, step);
    }

    // ========== 实验统计接口 ==========

    /**
     * 获取用户实验统计
     * 路径：GET /api/experiments/stats/user/{userId}
     */
    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "获取用户实验统计", description = "获取指定用户的实验统计信息")
    public R<Map<String, Object>> getUserExperimentStats(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("userId") Integer userId) {
        
        R<String> authResult = checkExperimentPermission(authHeader, userId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return experimentService.getUserExperimentStats(userId);
    }

    /**
     * 获取算法实验统计
     * 路径：GET /api/experiments/stats/algorithm/{algorithmName}
     */
    @GetMapping("/stats/algorithm/{algorithmName}")
    @Operation(summary = "获取算法实验统计", description = "获取指定算法的实验统计信息")
    public R<Map<String, Object>> getAlgorithmExperimentStats(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("algorithmName") String algorithmName) {
        
        // 检查token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        return experimentService.getAlgorithmExperimentStats(algorithmName);
    }

    // ========== 原有接口（保持兼容） ==========

    /**
     * 保存实验记录（兼容原有接口）
     * 路径：POST /api/experimentRecord/saveRecord
     * 注意：这个接口使用旧的路径，保持向前兼容
     */
    @PostMapping("/saveRecord")
    @Operation(summary = "保存实验记录（兼容接口）", description = "保存实验记录，兼容原有接口")
    public R<Void> saveRecord(@RequestBody ExperimentRecord record) {
        return experimentService.saveRecord(record);
    }

    /**
     * 根据用户ID获取实验记录列表（兼容原有接口）
     * 路径：GET /api/experimentRecord/listByUser
     * 注意：这个接口使用旧的路径，保持向前兼容
     */
    @GetMapping("/listByUser")
    @Operation(summary = "获取用户实验记录列表（兼容接口）", description = "根据用户ID获取实验记录列表，兼容原有接口")
    public R<List<ExperimentRecord>> listByUser(@RequestParam Integer userId) {
        return experimentService.listByUser(userId);
    }

    // ========== 障碍物管理接口 ==========

    /**
     * 获取实验记录的障碍物列表
     * 路径：GET /api/experiments/{recordId}/obstacles
     */
    @GetMapping("/{recordId}/obstacles")
    @Operation(summary = "获取实验记录的障碍物列表", description = "获取指定实验记录的所有障碍物")
    public R<List<Obstacle>> getObstaclesByRecordId(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("recordId") Integer recordId) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return obstacleService.getObstaclesByRecordId(recordId);
    }

    /**
     * 批量添加障碍物
     * 路径：POST /api/experiments/{recordId}/obstacles/batch
     */
    @PostMapping("/{recordId}/obstacles/batch")
    @Operation(summary = "批量添加障碍物", description = "为指定实验记录批量添加障碍物")
    public R<ObstacleService.BatchResult> addObstaclesBatch(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("recordId") Integer recordId,
            @RequestBody List<Obstacle> obstacles) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return obstacleService.addObstaclesBatch(recordId, obstacles);
    }

    /**
     * 删除实验记录的所有障碍物
     * 路径：DELETE /api/experiments/{recordId}/obstacles
     */
    @DeleteMapping("/{recordId}/obstacles")
    @Operation(summary = "删除实验记录的所有障碍物", description = "删除指定实验记录的所有障碍物")
    public R<ObstacleService.DeleteResult> deleteObstaclesByRecordId(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("recordId") Integer recordId) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return obstacleService.deleteObstaclesByRecordId(recordId);
    }

    /**
     * 删除单个障碍物
     * 路径：DELETE /api/experiments/{recordId}/obstacles/{obstacleId}
     */
    @DeleteMapping("/{recordId}/obstacles/{obstacleId}")
    @Operation(summary = "删除单个障碍物", description = "删除指定实验记录的单个障碍物")
    public R<String> deleteObstacle(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("recordId") Integer recordId,
            @PathVariable("obstacleId") Integer obstacleId) {
        
        // 先获取实验记录以获取用户ID
        R<ExperimentRecord> recordResult = experimentService.getExperimentById(recordId);
        if (!recordResult.isSuccess()) {
            return R.error(recordResult.getMsg());
        }
        
        Integer recordUserId = recordResult.getData().getUserId();
        R<String> authResult = checkExperimentPermission(authHeader, recordUserId);
        if (!authResult.isSuccess()) {
            return R.unauthorized(authResult.getMsg());
        }
        
        return obstacleService.deleteObstacle(recordId, obstacleId);
    }

    // ========== 权限检查私有方法 ==========

    /**
     * 检查实验记录权限的私有方法
     * 管理员可以查看所有实验记录，普通用户只能查看自己的实验记录
     */
    private R<String> checkExperimentPermission(String authHeader, Integer targetUserId) {
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
        
        // 4. 如果targetUserId为null，表示查看所有记录，需要管理员权限
        if (targetUserId == null) {
            String role = jwtUtil.getRoleFromToken(token);
            if (!"admin".equals(role)) {
                return R.forbidden("需要管理员权限才能查看所有实验记录");
            }
            return R.ok("权限验证通过");
        }
        
        // 5. 获取当前用户信息
        Integer currentUserId = jwtUtil.getUserIdFromToken(token);
        String currentUserRole = jwtUtil.getRoleFromToken(token);
        
        // 6. 管理员可以查看任何用户的记录，普通用户只能查看自己的记录
        if (!"admin".equals(currentUserRole) && !currentUserId.equals(targetUserId)) {
            return R.forbidden("没有权限查看其他用户的实验记录");
        }
        
        return R.ok("权限验证通过");
    }
    
    // ========== 导出功能接口 ==========
    
    /**
     * 导出实验记录Excel
     * 路径：GET /api/experiments/export
     */
    @GetMapping("/export")
    @Operation(summary = "导出实验记录Excel", description = "导出实验记录到Excel文件，支持筛选条件")
    public void exportExperimentRecords(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String algorithmName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTimeTo,
            javax.servlet.http.HttpServletResponse response) {
        
        try {
            // 权限检查
            R<String> authResult = checkExperimentPermission(authHeader, userId);
            if (!authResult.isSuccess()) {
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            // 设置响应头
            String fileName = "实验记录_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            
            // 转换时间参数为字符串格式
            String startTimeFromStr = startTimeFrom != null ? startTimeFrom.toString() : null;
            String endTimeToStr = endTimeTo != null ? endTimeTo.toString() : null;
            
            // 查询数据
            List<com.ldk.backend.DTO.ExperimentRecordExportVO> records = experimentService.getExportRecords(userId, algorithmName, status, startTimeFromStr, endTimeToStr);
            
            // 使用EasyExcel写入
            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.ldk.backend.DTO.ExperimentRecordExportVO.class)
                .sheet("实验记录")
                .doWrite(records);
                
        } catch (Exception e) {
            try {
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出失败: " + e.getMessage());
            } catch (Exception ex) {
                // 忽略
            }
        }
    }
}
