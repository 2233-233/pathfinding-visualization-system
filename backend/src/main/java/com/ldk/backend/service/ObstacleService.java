package com.ldk.backend.service;

import com.ldk.backend.commons.R;
import com.ldk.backend.entity.Obstacle;

import java.util.List;

public interface ObstacleService {
    
    /**
     * 根据实验记录ID获取障碍物列表
     * @param recordId 实验记录ID
     * @return 障碍物列表
     */
    R<List<Obstacle>> getObstaclesByRecordId(Integer recordId);
    
    /**
     * 批量添加障碍物
     * @param recordId 实验记录ID
     * @param obstacles 障碍物列表
     * @return 批量添加结果
     */
    R<BatchResult> addObstaclesBatch(Integer recordId, List<Obstacle> obstacles);
    
    /**
     * 删除实验记录的所有障碍物
     * @param recordId 实验记录ID
     * @return 删除结果
     */
    R<DeleteResult> deleteObstaclesByRecordId(Integer recordId);
    
    /**
     * 删除单个障碍物
     * @param recordId 实验记录ID
     * @param obstacleId 障碍物ID
     * @return 删除结果
     */
    R<String> deleteObstacle(Integer recordId, Integer obstacleId);
    
    /**
     * 获取实验记录的障碍物数量
     * @param recordId 实验记录ID
     * @return 障碍物数量
     */
    int getObstacleCountByRecordId(Integer recordId);
    
    // 批量操作结果类
    class BatchResult {
        private int successCount;
        private int failedCount;
        private int totalCount;
        
        public BatchResult() {}
        
        public BatchResult(int successCount, int failedCount, int totalCount) {
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.totalCount = totalCount;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }
        
        public int getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }
    
    // 删除结果类
    class DeleteResult {
        private int deletedCount;
        
        public DeleteResult() {}
        
        public DeleteResult(int deletedCount) {
            this.deletedCount = deletedCount;
        }
        
        public int getDeletedCount() {
            return deletedCount;
        }
        
        public void setDeletedCount(int deletedCount) {
            this.deletedCount = deletedCount;
        }
    }
}
