package com.ldk.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ExperimentRecord;
import com.ldk.backend.entity.ExperimentStep;

import java.util.List;
import java.util.Map;

public interface ExperimentService {
    // ========== 实验记录CRUD接口 ==========
    
    /**
     * 获取实验记录列表（分页）
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID（可选，用于筛选）
     * @param algorithmName 算法名称（可选，用于筛选）
     * @param status 状态（可选，用于筛选）
     * @param startTime 开始时间（精确匹配，可选，向后兼容）
     * @param endTime 结束时间（精确匹配，可选，向后兼容）
     * @param startTimeFrom 开始时间起始（可选，时间段搜索）
     * @param startTimeTo 开始时间结束（可选，时间段搜索）
     * @param endTimeFrom 结束时间起始（可选，时间段搜索）
     * @param endTimeTo 结束时间结束（可选，时间段搜索）
     * @return 分页实验记录列表
     */
    R<Page<ExperimentRecord>> getExperimentList(Integer page, Integer size, Integer userId, 
                                                String algorithmName, String status,
                                                java.time.LocalDateTime startTime,
                                                java.time.LocalDateTime endTime,
                                                java.time.LocalDateTime startTimeFrom,
                                                java.time.LocalDateTime startTimeTo,
                                                java.time.LocalDateTime endTimeFrom,
                                                java.time.LocalDateTime endTimeTo);
    
    /**
     * 获取实验记录列表（不分页）
     * @param userId 用户ID（可选，用于筛选）
     * @param algorithmName 算法名称（可选，用于筛选）
     * @param status 状态（可选，用于筛选）
     * @param startTime 开始时间（精确匹配，可选，向后兼容）
     * @param endTime 结束时间（精确匹配，可选，向后兼容）
     * @param startTimeFrom 开始时间起始（可选，时间段搜索）
     * @param startTimeTo 开始时间结束（可选，时间段搜索）
     * @param endTimeFrom 结束时间起始（可选，时间段搜索）
     * @param endTimeTo 结束时间结束（可选，时间段搜索）
     * @return 实验记录列表
     */
    R<List<ExperimentRecord>> getExperimentListAll(Integer userId, String algorithmName, String status,
                                                   java.time.LocalDateTime startTime,
                                                   java.time.LocalDateTime endTime,
                                                   java.time.LocalDateTime startTimeFrom,
                                                   java.time.LocalDateTime startTimeTo,
                                                   java.time.LocalDateTime endTimeFrom,
                                                   java.time.LocalDateTime endTimeTo);
    
    /**
     * 根据ID获取实验记录详情
     * @param recordId 实验记录ID
     * @return 实验记录详情
     */
    R<ExperimentRecord> getExperimentById(Integer recordId);
    
    /**
     * 创建实验记录（开始实验）
     * @param record 实验记录信息
     * @return 创建结果
     */
    R<String> createExperiment(ExperimentRecord record);
    
    /**
     * 更新实验记录（完成实验）
     * @param recordId 实验记录ID
     * @param record 更新的实验记录信息
     * @return 更新结果
     */
    R<String> updateExperiment(Integer recordId, ExperimentRecord record);
    
    /**
     * 删除实验记录
     * @param recordId 实验记录ID
     * @return 删除结果
     */
    R<String> deleteExperiment(Integer recordId);
    
    // ========== 实验步骤管理接口 ==========
    
    /**
     * 获取实验步骤列表
     * @param recordId 实验记录ID
     * @return 实验步骤列表
     */
    R<List<ExperimentStep>> getExperimentSteps(Integer recordId);
    
    /**
     * 添加实验步骤
     * @param step 实验步骤信息
     * @return 添加结果
     */
    R<String> addExperimentStep(ExperimentStep step);
    
    /**
     * 更新实验步骤
     * @param stepId 实验步骤ID
     * @param step 更新的实验步骤信息
     * @return 更新结果
     */
    R<String> updateExperimentStep(Integer stepId, ExperimentStep step);
    
    // ========== 实验统计接口 ==========
    
    /**
     * 获取用户实验统计
     * @param userId 用户ID
     * @return 用户实验统计信息
     */
    R<Map<String, Object>> getUserExperimentStats(Integer userId);
    
    /**
     * 获取算法实验统计
     * @param algorithmName 算法名称
     * @return 算法实验统计信息
     */
    R<Map<String, Object>> getAlgorithmExperimentStats(String algorithmName);
    
    // ========== 原有接口（保持兼容） ==========
    
    /**
     * 保存实验记录（兼容原有接口）
     * @param record 实验记录
     * @return 保存结果
     */
    R<Void> saveRecord(ExperimentRecord record);
    
    /**
     * 根据用户ID获取实验记录列表（兼容原有接口）
     * @param userId 用户ID
     * @return 实验记录列表
     */
    R<List<ExperimentRecord>> listByUser(Integer userId);
    
    // ========== 导出功能接口 ==========
    
    /**
     * 获取实验记录导出数据
     * @param userId 用户ID（可选）
     * @param algorithmName 算法名称（可选）
     * @param status 状态（可选）
     * @param startTimeFrom 开始时间起始（可选）
     * @param endTimeTo 结束时间结束（可选）
     * @return 导出VO列表
     */
    List<com.ldk.backend.DTO.ExperimentRecordExportVO> getExportRecords(
            Integer userId,
            String algorithmName,
            String status,
            String startTimeFrom,
            String endTimeTo);
}
