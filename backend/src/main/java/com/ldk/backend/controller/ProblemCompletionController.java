package com.ldk.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.JwtUtil;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ProblemCompletion;
import com.ldk.backend.service.ProblemCompletionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "题目完成情况接口", description = "用户题目学习记录管理接口")
@CrossOrigin
@RequestMapping("/api/problem-completions")
public class ProblemCompletionController {
    @Autowired
    private ProblemCompletionService problemCompletionService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getByUserAndProblem")
    @Operation(summary = "获取特定题目的学习记录", description = "根据当前用户和问题ID获取特定题目的学习记录，需要用户登录")
    public R<ProblemCompletion> getByUserAndProblem(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam Integer problemId) {
        
        // 验证token并获取当前用户ID
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录！");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        // 从token中获取当前用户ID
        Integer currentUserId = jwtUtil.getUserIdFromToken(token);
        
        return problemCompletionService.getByUserAndProblem(currentUserId, problemId);
    }

    @GetMapping("")
    @Operation(summary = "获取学习记录列表", description = "获取用户的学习记录列表，支持分页和筛选")
    public R<Page<ProblemCompletion>> getProblemCompletionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer problemId,
            @RequestParam(required = false) String completionStatus) {
        return problemCompletionService.getProblemCompletionList(page, size, userId, problemId, completionStatus);
    }
    
    @GetMapping("/{userId}/{problemId}")
    @Operation(summary = "获取特定题目的学习记录（路径参数）", description = "根据用户ID和问题ID获取特定题目的学习记录")
    public R<ProblemCompletion> getProblemCompletionByUserAndProblem(
            @PathVariable Integer userId,
            @PathVariable Integer problemId) {
        return problemCompletionService.getByUserAndProblem(userId, problemId);
    }
    
    @PostMapping("")
    @Operation(summary = "创建学习记录", description = "创建新的学习记录，需要用户登录")
    public R<String> createProblemCompletion(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ProblemCompletion problemCompletion) {
        
        // 验证token并获取当前用户ID
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized("请先登录");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized("token无效或已过期");
        }
        
        // 从token中获取当前用户ID
        Integer currentUserId = jwtUtil.getUserIdFromToken(token);
        
        // 设置当前用户ID到problemCompletion对象
        problemCompletion.setUserId(currentUserId);
        
        return problemCompletionService.createProblemCompletion(problemCompletion);
    }
    
    @PutMapping("/{completionId}")
    @Operation(summary = "更新学习记录", description = "更新学习记录信息")
    public R<String> updateProblemCompletion(
            @PathVariable Integer completionId,
            @RequestBody ProblemCompletion problemCompletion) {
        return problemCompletionService.updateProblemCompletion(completionId, problemCompletion);
    }
    
    @DeleteMapping("/{completionId}")
    @Operation(summary = "删除学习记录", description = "删除学习记录")
    public R<String> deleteProblemCompletion(@PathVariable Integer completionId) {
        return problemCompletionService.deleteProblemCompletion(completionId);
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学习记录", description = "批量删除多个学习记录")
    public R<String> batchDeleteProblemCompletions(@RequestBody List<Integer> completionIds) {
        return problemCompletionService.batchDeleteProblemCompletions(completionIds);
    }
    
    @PutMapping("/batch")
    @Operation(summary = "批量更新学习记录", description = "批量更新多个学习记录")
    public R<String> batchUpdateProblemCompletions(@RequestBody List<ProblemCompletion> problemCompletions) {
        return problemCompletionService.batchUpdateProblemCompletions(problemCompletions);
    }
    
    @GetMapping("/stats/{userId}")
    @Operation(summary = "获取用户学习统计", description = "获取用户的学习统计信息")
    public R<Map<String, Object>> getUserLearningStats(@PathVariable Integer userId) {
        return problemCompletionService.getUserLearningStats(userId);
    }
    
    @GetMapping("/progress/{userId}")
    @Operation(summary = "获取用户学习进度", description = "获取用户的学习进度信息")
    public R<Map<String, Object>> getUserLearningProgress(@PathVariable Integer userId) {
        return problemCompletionService.getUserLearningProgress(userId);
    }
    
    @GetMapping("/problem/{problemId}")
    @Operation(summary = "按照问题ID查找学习记录", description = "按照问题ID查找对应的学习记录")
    public R<List<ProblemCompletion>> getByProblemId(@PathVariable Integer problemId) {
        return problemCompletionService.getByProblemId(problemId);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "按照用户ID查找学习记录", description = "按照用户ID查找对应的学习记录")
    public R<List<ProblemCompletion>> getByUserId(@PathVariable Integer userId) {
        return problemCompletionService.getByUserId(userId);
    }
    
    @GetMapping("/user/{userId}/search")
    @Operation(summary = "搜索用户的学习记录", description = "搜索用户的学习记录，支持按算法类型、难度、标题、描述筛选，分页返回")
    public R<Page<Map<String, Object>>> searchUserCompletions(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer algorithmId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        return problemCompletionService.searchUserCompletions(userId, page, size, algorithmId, difficulty, title, description);
    }

    /**
     * 导出题目学习记录Excel
     * 路径：GET /api/problem-completions/export
     */
    @GetMapping("/export")
    @Operation(summary = "导出题目学习记录Excel", description = "导出当前用户的题目学习记录到Excel文件，支持筛选条件")
    public void exportProblemCompletions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer algorithmId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            javax.servlet.http.HttpServletResponse response) {
        
        try {
            // 验证token并获取当前用户ID
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            Integer currentUserId = jwtUtil.getUserIdFromToken(token);
            
            // 设置响应头
            String fileName = "题目学习记录_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            
            // 查询数据
            List<com.ldk.backend.DTO.ProblemCompletionExportVO> records = problemCompletionService.getExportRecords(currentUserId, algorithmId, difficulty, title, description);
            
            // 使用EasyExcel写入
            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.ldk.backend.DTO.ProblemCompletionExportVO.class)
                .sheet("题目学习记录")
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

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新学习记录", description = "保存或更新学习记录（兼容原有接口），需要用户登录")
    public R<Void> saveOrUpdate(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ProblemCompletion pc) {
        
        // 验证token并获取当前用户ID
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.unauthorized();
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return R.unauthorized();
        }
        
        // 从token中获取当前用户ID
        Integer currentUserId = jwtUtil.getUserIdFromToken(token);
        
        // 设置当前用户ID到pc对象
        pc.setUserId(currentUserId);
        
        return problemCompletionService.saveOrUpdate(pc);
    }
}
