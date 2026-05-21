package com.ldk.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.Obstacle;
import com.ldk.backend.mapper.ObstacleMapper;
import com.ldk.backend.service.ObstacleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ObstacleServiceImpl implements ObstacleService {

    @Autowired
    private ObstacleMapper obstacleMapper;

    @Override
    public R<List<Obstacle>> getObstaclesByRecordId(Integer recordId) {
        try {
            if (recordId == null || recordId < 1) {
                return R.error("实验记录ID不能为空");
            }

            List<Obstacle> obstacles = obstacleMapper.selectByRecordId(recordId);
            return R.ok(obstacles);
        } catch (Exception e) {
            return R.error("获取障碍物列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R<BatchResult> addObstaclesBatch(Integer recordId, List<Obstacle> obstacles) {
        try {
            if (recordId == null || recordId < 1) {
                return R.error("实验记录ID不能为空");
            }
            if (obstacles == null || obstacles.isEmpty()) {
                return R.error("障碍物列表不能为空");
            }

            int successCount = 0;
            int failedCount = 0;

            // 设置每个障碍物的recordId和创建时间
            for (Obstacle obstacle : obstacles) {
                try {
                    obstacle.setRecordId(recordId);
                    if (obstacle.getObstacleType() == null || obstacle.getObstacleType().trim().isEmpty()) {
                        obstacle.setObstacleType("wall");
                    }
                    if (obstacle.getCreatedAt() == null) {
                        obstacle.setCreatedAt(LocalDateTime.now());
                    }

                    int result = obstacleMapper.insert(obstacle);
                    if (result == 1) {
                        successCount++;
                    } else {
                        failedCount++;
                    }
                } catch (Exception e) {
                    failedCount++;
                }
            }

            BatchResult result = new BatchResult(successCount, failedCount, obstacles.size());
            return R.ok(result);
        } catch (Exception e) {
            return R.error("批量添加障碍物失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R<DeleteResult> deleteObstaclesByRecordId(Integer recordId) {
        try {
            if (recordId == null || recordId < 1) {
                return R.error("实验记录ID不能为空");
            }

            int deletedCount = obstacleMapper.deleteByRecordId(recordId);
            DeleteResult result = new DeleteResult(deletedCount);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("删除障碍物失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R<String> deleteObstacle(Integer recordId, Integer obstacleId) {
        try {
            if (recordId == null || recordId < 1) {
                return R.error("实验记录ID不能为空");
            }
            if (obstacleId == null || obstacleId < 1) {
                return R.error("障碍物ID不能为空");
            }

            // 先检查障碍物是否存在且属于该实验记录
            Obstacle obstacle = obstacleMapper.selectById(obstacleId);
            if (obstacle == null) {
                return R.notFound("障碍物不存在");
            }
            if (!recordId.equals(obstacle.getRecordId())) {
                return R.error("该障碍物不属于指定的实验记录");
            }

            int result = obstacleMapper.deleteById(obstacleId);
            if (result > 0) {
                return R.ok("删除障碍物成功");
            } else {
                return R.error("删除障碍物失败");
            }
        } catch (Exception e) {
            return R.error("删除障碍物失败: " + e.getMessage());
        }
    }

    @Override
    public int getObstacleCountByRecordId(Integer recordId) {
        try {
            if (recordId == null || recordId < 1) {
                return 0;
            }
            return obstacleMapper.countByRecordId(recordId);
        } catch (Exception e) {
            return 0;
        }
    }
}
