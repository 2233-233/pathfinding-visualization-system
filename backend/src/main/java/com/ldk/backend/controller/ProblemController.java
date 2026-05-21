package com.ldk.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.Problem;
import com.ldk.backend.service.ProblemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "题目接口", description = "题目管理接口")
@CrossOrigin
@RequestMapping("/api/problem")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @GetMapping("/listByAlgorithm")
    @Operation(summary = "根据算法名称获取问题列表", description = "根据算法名称获取对应的问题列表")
    public R<List<Problem>> listByAlgorithm(@RequestParam String algorithmName) {
        return problemService.listByAlgorithmName(algorithmName);
    }
    
    @GetMapping("/problems")
    @Operation(summary = "获取问题列表", description = "获取问题列表，可分页、筛选，支持按标题或描述模糊搜索")
    public R<Page<Problem>> getProblemList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) String description,
                                           @RequestParam(required = false) String difficulty,
                                           @RequestParam(required = false) String algorithmName,
                                           @RequestParam(required = false) Integer algorithmId) {
        return problemService.getProblemList(page, size, title, description, difficulty, algorithmName, algorithmId);
    }
    
    @GetMapping("/problems/stats")
    @Operation(summary = "获取题目统计信息", description = "获取题目总数、各难度数量、各算法数量")
    public R<ProblemStats> getProblemStats() {
        return problemService.getProblemStats();
    }

    @GetMapping("/problems/{problemId}")
    @Operation(summary = "获取问题详情", description = "根据问题ID获取问题详情")
    public R<Problem> getProblemById(@PathVariable Integer problemId) {
        return problemService.getProblemById(problemId);
    }
    
    @PostMapping("/problems")
    @Operation(summary = "创建问题", description = "创建新问题")
    public R<String> createProblem(@RequestBody Problem problem) {
        return problemService.createProblem(problem);
    }
    
    @PutMapping("/problems/{problemId}")
    @Operation(summary = "更新问题", description = "更新问题信息")
    public R<String> updateProblem(@PathVariable Integer problemId,
                                   @RequestBody Problem problem) {
        return problemService.updateProblem(problemId, problem);
    }
    
    @DeleteMapping("/problems/{problemId}")
    @Operation(summary = "删除问题", description = "删除单个问题")
    public R<String> deleteProblem(@PathVariable Integer problemId) {
        return problemService.deleteProblem(problemId);
    }
    
    @DeleteMapping("/problems/batch")
    @Operation(summary = "批量删除问题", description = "批量删除多个问题")
    public R<String> batchDeleteProblems(@RequestBody List<Integer> problemIds) {
        return problemService.batchDeleteProblems(problemIds);
    }
    
    @GetMapping("/problems/search")
    @Operation(summary = "搜索问题", description = "根据标题或描述模糊搜索问题")
    public R<List<Problem>> searchProblems(@RequestParam String keyword) {
        return problemService.searchProblems(keyword);
    }
    
    @GetMapping("/problems/all")
    @Operation(summary = "获取所有问题", description = "获取所有问题列表（不分页）")
    public R<List<Problem>> getAllProblems() {
        return problemService.getAllProblems();
    }

    /**
     * 导出题目Excel
     * 路径：GET /api/problem/export
     */
    @GetMapping("/export")
    @Operation(summary = "导出题目Excel", description = "导出题目到Excel文件，支持筛选条件")
    public void exportProblems(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer algorithmId,
            javax.servlet.http.HttpServletResponse response) {
        
        try {
            // 设置响应头
            String fileName = "题目列表_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            
            // 查询数据
            List<com.ldk.backend.DTO.ProblemExportVO> records = problemService.getExportRecords(title, description, difficulty, algorithmId);
            
            // 使用EasyExcel写入
            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.ldk.backend.DTO.ProblemExportVO.class)
                .sheet("题目列表")
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

    /**
     * 题目统计信息内部类
     */
    public static class ProblemStats {
        private long total;
        private long easy;
        private long medium;
        private long hard;
        private java.util.Map<Integer, Long> byAlgorithm;

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public long getEasy() { return easy; }
        public void setEasy(long easy) { this.easy = easy; }
        public long getMedium() { return medium; }
        public void setMedium(long medium) { this.medium = medium; }
        public long getHard() { return hard; }
        public void setHard(long hard) { this.hard = hard; }
        public java.util.Map<Integer, Long> getByAlgorithm() { return byAlgorithm; }
        public void setByAlgorithm(java.util.Map<Integer, Long> byAlgorithm) { this.byAlgorithm = byAlgorithm; }
    }
}
