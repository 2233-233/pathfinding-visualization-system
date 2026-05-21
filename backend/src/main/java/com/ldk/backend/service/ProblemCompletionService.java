package com.ldk.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ProblemCompletion;

import java.util.List;
import java.util.Map;

public interface ProblemCompletionService {
    // 获取特定题目的学习记录
    R<ProblemCompletion> getByUserAndProblem(Integer userId, Integer problemId);
    
    // 获取用户的学习记录列表
    R<Page<ProblemCompletion>> getProblemCompletionList(Integer page, Integer size, 
                                                       Integer userId, Integer problemId,
                                                       String completionStatus);
    
    // 创建学习记录
    R<String> createProblemCompletion(ProblemCompletion problemCompletion);
    
    // 更新学习记录
    R<String> updateProblemCompletion(Integer completionId, ProblemCompletion problemCompletion);
    
    // 删除学习记录
    R<String> deleteProblemCompletion(Integer completionId);
    
    // 批量删除学习记录
    R<String> batchDeleteProblemCompletions(List<Integer> completionIds);
    
    // 批量更新学习记录
    R<String> batchUpdateProblemCompletions(List<ProblemCompletion> problemCompletions);
    
    // 获取用户学习统计
    R<Map<String, Object>> getUserLearningStats(Integer userId);
    
    // 获取用户学习进度
    R<Map<String, Object>> getUserLearningProgress(Integer userId);
    
    // 按照问题ID查找对应的学习记录
    R<List<ProblemCompletion>> getByProblemId(Integer problemId);
    
    // 按照用户ID查找对应的学习记录
    R<List<ProblemCompletion>> getByUserId(Integer userId);
    
    // 保存或更新（原有方法）
    R<Void> saveOrUpdate(ProblemCompletion pc);

    // 搜索用户的学习记录（分页+筛选，关联Problem表）
    R<Page<Map<String, Object>>> searchUserCompletions(Integer userId, Integer page, Integer size,
                                                       Integer algorithmId, String difficulty,
                                                       String title, String description);

    /**
     * 获取导出数据（题目学习记录）
     * @param userId 用户ID
     * @param algorithmId 算法ID筛选
     * @param difficulty 难度筛选
     * @param title 标题筛选
     * @param description 描述筛选
     * @return 导出VO列表
     */
    List<com.ldk.backend.DTO.ProblemCompletionExportVO> getExportRecords(Integer userId, Integer algorithmId, String difficulty, String title, String description);
}
