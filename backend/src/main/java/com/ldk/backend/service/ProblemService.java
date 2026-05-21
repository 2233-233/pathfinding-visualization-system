package com.ldk.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.Problem;

import java.util.List;

public interface ProblemService {
    /**
     * 根据算法名称获取问题列表
     * @param algorithmName 算法名称
     * @return 问题列表
     */
    R<List<Problem>> listByAlgorithmName(String algorithmName);
    
    // ========== 问题管理相关方法 ==========
    
    /**
     * 获取问题列表（分页+搜索）
     * @param page 页码
     * @param size 每页大小
     * @param title 问题标题（可选，用于模糊搜索）
     * @param description 问题描述（可选，用于模糊搜索）
     * @param difficulty 难度（可选，用于筛选）
     * @param algorithmName 算法名称（可选，用于筛选）
     * @param algorithmId 算法ID（可选，用于筛选）
     * @return 分页问题列表
     */
    R<Page<Problem>> getProblemList(Integer page, Integer size, String title, String description, 
                                   String difficulty, String algorithmName, Integer algorithmId);
    
    /**
     * 根据ID获取问题信息
     * @param problemId 问题ID
     * @return 问题信息
     */
    R<Problem> getProblemById(Integer problemId);
    
    /**
     * 创建新问题
     * @param problem 问题信息
     * @return 创建结果
     */
    R<String> createProblem(Problem problem);
    
    /**
     * 更新问题信息
     * @param problemId 问题ID
     * @param problem 更新的问题信息
     * @return 更新结果
     */
    R<String> updateProblem(Integer problemId, Problem problem);
    
    /**
     * 删除问题
     * @param problemId 问题ID
     * @return 删除结果
     */
    R<String> deleteProblem(Integer problemId);
    
    /**
     * 批量删除问题
     * @param problemIds 问题ID列表
     * @return 删除结果
     */
    R<String> batchDeleteProblems(List<Integer> problemIds);
    
    /**
     * 根据标题或描述模糊搜索问题
     * @param keyword 搜索关键词（匹配标题或描述）
     * @return 问题列表
     */
    R<List<Problem>> searchProblems(String keyword);
    
    /**
     * 获取所有问题列表（不分页）
     * @return 所有问题列表
     */
    R<List<Problem>> getAllProblems();

    /**
     * 获取题目统计信息
     * @return 题目统计信息（总数、各难度数量、各算法数量）
     */
    R<com.ldk.backend.controller.ProblemController.ProblemStats> getProblemStats();

    /**
     * 获取导出数据
     * @param title 标题筛选
     * @param description 描述筛选
     * @param difficulty 难度筛选
     * @param algorithmId 算法ID筛选
     * @return 导出VO列表
     */
    List<com.ldk.backend.DTO.ProblemExportVO> getExportRecords(String title, String description, String difficulty, Integer algorithmId);
}
