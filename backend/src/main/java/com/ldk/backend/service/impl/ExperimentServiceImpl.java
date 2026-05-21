package com.ldk.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ExperimentRecord;
import com.ldk.backend.entity.ExperimentStep;
import com.ldk.backend.entity.Obstacle;
import com.ldk.backend.entity.Problem;
import com.ldk.backend.mapper.ExperimentRecordMapper;
import com.ldk.backend.mapper.ExperimentStepMapper;
import com.ldk.backend.mapper.ObstacleMapper;
import com.ldk.backend.mapper.ProblemMapper;
import com.ldk.backend.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ldk.backend.DTO.ExperimentRecordExportVO;

@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private ExperimentRecordMapper experimentRecordMapper;

    @Autowired
    private ExperimentStepMapper experimentStepMapper;

    @Autowired
    private ObstacleMapper obstacleMapper;

    @Autowired
    private ProblemMapper problemMapper;
    
    @Autowired
    private com.ldk.backend.mapper.UserMapper userMapper;

    // ========== 实验记录CRUD接口 ==========

    @Override
    public R<Page<ExperimentRecord>> getExperimentList(Integer page, Integer size, Integer userId, 
                                                      String algorithmName, String status,
                                                      java.time.LocalDateTime startTime,
                                                      java.time.LocalDateTime endTime,
                                                      java.time.LocalDateTime startTimeFrom,
                                                      java.time.LocalDateTime startTimeTo,
                                                      java.time.LocalDateTime endTimeFrom,
                                                      java.time.LocalDateTime endTimeTo) {
        try {
            if (page == null || page < 1) {
                page = 1;
            }
            if (size == null || size < 1) {
                size = 10;
            }

            Page<ExperimentRecord> pageInfo = new Page<>(page, size);
            QueryWrapper<ExperimentRecord> queryWrapper = new QueryWrapper<>();

            if (userId != null) {
                queryWrapper.eq("user_id", userId);
            }
            if (algorithmName != null && !algorithmName.trim().isEmpty()) {
                // 根据算法名称获取算法ID
                Integer algorithmId = getAlgorithmIdByName(algorithmName);
                if (algorithmId != null) {
                    queryWrapper.eq("algorithm_id", algorithmId);
                } else {
                    // 如果算法名称不存在，返回空结果
                    queryWrapper.eq("algorithm_id", -1);
                }
            }
            if (status != null && !status.trim().isEmpty()) {
                queryWrapper.eq("status", status);
            }

            // 处理开始时间（精确匹配，向后兼容）
            if (startTime != null) {
                queryWrapper.eq("start_time", startTime);
            }
            
            // 处理开始时间段
            if (startTimeFrom != null) {
                queryWrapper.ge("start_time", startTimeFrom);
            }
            if (startTimeTo != null) {
                queryWrapper.le("start_time", startTimeTo);
            }
            
            // 处理结束时间（精确匹配，向后兼容）
            if (endTime != null) {
                queryWrapper.eq("end_time", endTime);
            }
            
            // 处理结束时间段
            if (endTimeFrom != null) {
                queryWrapper.ge("end_time", endTimeFrom);
            }
            if (endTimeTo != null) {
                queryWrapper.le("end_time", endTimeTo);
            }

            queryWrapper.orderByDesc("start_time");
            Page<ExperimentRecord> result = experimentRecordMapper.selectPage(pageInfo, queryWrapper);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询实验记录列表失败: " + e.getMessage());
        }
    }

    @Override
    public R<List<ExperimentRecord>> getExperimentListAll(Integer userId, String algorithmName, String status,
                                                         java.time.LocalDateTime startTime,
                                                         java.time.LocalDateTime endTime,
                                                         java.time.LocalDateTime startTimeFrom,
                                                         java.time.LocalDateTime startTimeTo,
                                                         java.time.LocalDateTime endTimeFrom,
                                                         java.time.LocalDateTime endTimeTo) {
        try {
            QueryWrapper<ExperimentRecord> queryWrapper = new QueryWrapper<>();

            if (userId != null) {
                queryWrapper.eq("user_id", userId);
            }
            if (algorithmName != null && !algorithmName.trim().isEmpty()) {
                // 根据算法名称获取算法ID
                Integer algorithmId = getAlgorithmIdByName(algorithmName);
                if (algorithmId != null) {
                    queryWrapper.eq("algorithm_id", algorithmId);
                } else {
                    // 如果算法名称不存在，返回空结果
                    queryWrapper.eq("algorithm_id", -1);
                }
            }
            if (status != null && !status.trim().isEmpty()) {
                queryWrapper.eq("status", status);
            }

            // 处理开始时间（精确匹配，向后兼容）
            if (startTime != null) {
                queryWrapper.eq("start_time", startTime);
            }
            
            // 处理开始时间段
            if (startTimeFrom != null) {
                queryWrapper.ge("start_time", startTimeFrom);
            }
            if (startTimeTo != null) {
                queryWrapper.le("start_time", startTimeTo);
            }
            
            // 处理结束时间（精确匹配，向后兼容）
            if (endTime != null) {
                queryWrapper.eq("end_time", endTime);
            }
            
            // 处理结束时间段
            if (endTimeFrom != null) {
                queryWrapper.ge("end_time", endTimeFrom);
            }
            if (endTimeTo != null) {
                queryWrapper.le("end_time", endTimeTo);
            }

            queryWrapper.orderByDesc("start_time");
            List<ExperimentRecord> result = experimentRecordMapper.selectList(queryWrapper);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询实验记录列表失败: " + e.getMessage());
        }
    }

    @Override
    public R<ExperimentRecord> getExperimentById(Integer recordId) {
        try {
            if (recordId == null || recordId < 1) {
                return R.error("实验记录ID不能为空");
            }

            ExperimentRecord record = experimentRecordMapper.selectById(recordId);
            if (record == null) {
                return R.notFound("实验记录不存在");
            }

            return R.ok(record);
        } catch (Exception e) {
            return R.error("查询实验记录失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> createExperiment(ExperimentRecord record) {
        try {
            if (record == null) {
                return R.error("实验记录不能为空");
            }
            if (record.getUserId() == null) {
                return R.error("用户ID不能为空");
            }
            if (record.getAlgorithmId() == null) {
                return R.error("算法ID不能为空");
            }

            // 处理problemId：实验记录与problem实际上没有业务关联，只是为了满足外键约束
            // 先检查前端传入的problemId是否存在，如果不存在则查找一个可用的默认problem
            if (record.getProblemId() != null) {
                Problem existingProblem = problemMapper.selectById(record.getProblemId());
                if (existingProblem != null) {
                    // 前端传入的problemId有效，直接使用
                } else {
                    // 前端传入的problemId无效，查找一个可用的默认problem
                    record.setProblemId(getDefaultProblemId());
                }
            } else {
                // 前端没有传入problemId，查找一个可用的默认problem
                record.setProblemId(getDefaultProblemId());
            }

            // 设置开始时间
            if (record.getStartTime() == null) {
                record.setStartTime(LocalDateTime.now());
            }

            // 设置默认状态
            if (record.getStatus() == null || record.getStatus().trim().isEmpty()) {
                record.setStatus("completed");
            }

            int result = experimentRecordMapper.insert(record);
            if (result == 1) {
                return R.ok("创建实验记录成功");
            } else {
                return R.error("创建实验记录失败");
            }
        } catch (Exception e) {
            return R.error("创建实验记录失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> updateExperiment(Integer recordId, ExperimentRecord record) {
        try {
            if (recordId == null) {
                return R.error("实验记录ID不能为空");
            }
            
            ExperimentRecord existingRecord = experimentRecordMapper.selectById(recordId);
            if (existingRecord == null) {
                return R.notFound("实验记录不存在");
            }

            // 更新字段
            if (record.getEndTime() != null) {
                existingRecord.setEndTime(record.getEndTime());
            }
            if (record.getPathLength() != null) {
                existingRecord.setPathLength(record.getPathLength());
            }
            if (record.getVisitedCount() != null) {
                existingRecord.setVisitedCount(record.getVisitedCount());
            }
            if (record.getStatus() != null && !record.getStatus().trim().isEmpty()) {
                existingRecord.setStatus(record.getStatus());
            }

            int result = experimentRecordMapper.updateById(existingRecord);
            if (result > 0) {
                return R.ok("更新实验记录成功");
            } else {
                return R.error("更新实验记录失败");
            }
        } catch (Exception e) {
            return R.error("更新实验记录失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> deleteExperiment(Integer recordId) {
        try {
            if (recordId == null) {
                return R.error("实验记录ID不能为空");
            }

            // 检查实验记录是否存在
            ExperimentRecord existingRecord = experimentRecordMapper.selectById(recordId);
            if (existingRecord == null) {
                return R.notFound("实验记录不存在");
            }

            // 删除相关的实验步骤
            QueryWrapper<ExperimentStep> stepQueryWrapper = new QueryWrapper<>();
            stepQueryWrapper.eq("record_id", recordId);
            experimentStepMapper.delete(stepQueryWrapper);

            // 删除实验记录
            int result = experimentRecordMapper.deleteById(recordId);
            if (result > 0) {
                return R.ok("删除实验记录成功");
            } else {
                return R.error("删除实验记录失败");
            }
        } catch (Exception e) {
            return R.error("删除实验记录失败: " + e.getMessage());
        }
    }

    // ========== 实验步骤管理接口 ==========

    @Override
    public R<List<ExperimentStep>> getExperimentSteps(Integer recordId) {
        try {
            if (recordId == null) {
                return R.error("实验记录ID不能为空");
            }

            QueryWrapper<ExperimentStep> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("record_id", recordId);
            queryWrapper.orderByAsc("step_index");

            List<ExperimentStep> steps = experimentStepMapper.selectList(queryWrapper);
            return R.ok(steps);
        } catch (Exception e) {
            return R.error("获取实验步骤列表失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> addExperimentStep(ExperimentStep step) {
        try {
            if (step == null) {
                return R.error("实验步骤不能为空");
            }
            if (step.getRecordId() == null) {
                return R.error("实验记录ID不能为空");
            }
            if (step.getStepIndex() == null) {
                return R.error("步骤索引不能为空");
            }

            // 检查实验记录是否存在
            ExperimentRecord record = experimentRecordMapper.selectById(step.getRecordId());
            if (record == null) {
                return R.error("实验记录不存在");
            }

            int result = experimentStepMapper.insert(step);
            if (result == 1) {
                return R.ok("添加实验步骤成功");
            } else {
                return R.error("添加实验步骤失败");
            }
        } catch (Exception e) {
            return R.error("添加实验步骤失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> updateExperimentStep(Integer stepId, ExperimentStep step) {
        try {
            if (stepId == null) {
                return R.error("实验步骤ID不能为空");
            }

            ExperimentStep existingStep = experimentStepMapper.selectById(stepId);
            if (existingStep == null) {
                return R.notFound("实验步骤不存在");
            }

            // 更新字段
            if (step.getStepIndex() != null) {
                existingStep.setStepIndex(step.getStepIndex());
            }
            if (step.getNodeX() != null) {
                existingStep.setNodeX(step.getNodeX());
            }
            if (step.getNodeY() != null) {
                existingStep.setNodeY(step.getNodeY());
            }
            if (step.getNodeState() != null && !step.getNodeState().trim().isEmpty()) {
                existingStep.setNodeState(step.getNodeState());
            }
            if (step.getGridState() != null) {
                existingStep.setGridState(step.getGridState());
            }

            int result = experimentStepMapper.updateById(existingStep);
            if (result > 0) {
                return R.ok("更新实验步骤成功");
            } else {
                return R.error("更新实验步骤失败");
            }
        } catch (Exception e) {
            return R.error("更新实验步骤失败: " + e.getMessage());
        }
    }

    // ========== 实验统计接口 ==========

    @Override
    public R<Map<String, Object>> getUserExperimentStats(Integer userId) {
        try {
            if (userId == null) {
                return R.error("用户ID不能为空");
            }

            QueryWrapper<ExperimentRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);

            List<ExperimentRecord> records = experimentRecordMapper.selectList(queryWrapper);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalExperiments", records.size());
            
            long completedCount = records.stream()
                .filter(r -> "completed".equals(r.getStatus()))
                .count();
            stats.put("completedExperiments", completedCount);
            
            long failedCount = records.stream()
                .filter(r -> "failed".equals(r.getStatus()))
                .count();
            stats.put("failedExperiments", failedCount);

            // 计算平均路径长度和访问节点数
            double avgPathLength = records.stream()
                .filter(r -> r.getPathLength() != null)
                .mapToInt(ExperimentRecord::getPathLength)
                .average()
                .orElse(0.0);
            stats.put("averagePathLength", avgPathLength);

            double avgVisitedCount = records.stream()
                .filter(r -> r.getVisitedCount() != null)
                .mapToInt(ExperimentRecord::getVisitedCount)
                .average()
                .orElse(0.0);
            stats.put("averageVisitedCount", avgVisitedCount);

            return R.ok(stats);
        } catch (Exception e) {
            return R.error("获取用户实验统计失败: " + e.getMessage());
        }
    }

    @Override
    public R<Map<String, Object>> getAlgorithmExperimentStats(String algorithmName) {
        try {
            if (algorithmName == null || algorithmName.trim().isEmpty()) {
                return R.error("算法名称不能为空");
            }

            // 根据算法名称获取算法ID
            Integer algorithmId = getAlgorithmIdByName(algorithmName);
            if (algorithmId == null) {
                // 如果没有找到对应的算法ID，返回空统计
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalExperiments", 0);
                stats.put("completedExperiments", 0);
                stats.put("failedExperiments", 0);
                stats.put("userDistribution", new HashMap<>());
                stats.put("successRate", 0.0);
                return R.ok(stats);
            }

            QueryWrapper<ExperimentRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("algorithm_id", algorithmId);

            List<ExperimentRecord> records = experimentRecordMapper.selectList(queryWrapper);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalExperiments", records.size());
            
            long completedCount = records.stream()
                .filter(r -> "completed".equals(r.getStatus()))
                .count();
            stats.put("completedExperiments", completedCount);
            
            long failedCount = records.stream()
                .filter(r -> "failed".equals(r.getStatus()))
                .count();
            stats.put("failedExperiments", failedCount);

            // 按用户分组统计
            Map<Integer, Long> userExperimentCount = new HashMap<>();
            for (ExperimentRecord record : records) {
                userExperimentCount.merge(record.getUserId(), 1L, Long::sum);
            }
            stats.put("userDistribution", userExperimentCount);

            // 计算成功率
            double successRate = records.isEmpty() ? 0.0 : 
                (double) completedCount / records.size() * 100;
            stats.put("successRate", successRate);

            return R.ok(stats);
        } catch (Exception e) {
            return R.error("获取算法实验统计失败: " + e.getMessage());
        }
    }

    // ========== 原有接口（保持兼容） ==========

    @Override
    public R<Void> saveRecord(ExperimentRecord record) {
        try {
            // 同样处理problemId，确保外键约束满足
            if (record.getProblemId() != null) {
                Problem existingProblem = problemMapper.selectById(record.getProblemId());
                if (existingProblem == null) {
                    record.setProblemId(getDefaultProblemId());
                }
            } else {
                record.setProblemId(getDefaultProblemId());
            }
            experimentRecordMapper.insert(record);
            return R.ok();
        } catch (Exception e) {
            return R.error("保存实验记录失败: " + e.getMessage());
        }
    }

    @Override
    public R<List<ExperimentRecord>> listByUser(Integer userId) {
        try {
            QueryWrapper<ExperimentRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).orderByDesc("start_time");
            return R.ok(experimentRecordMapper.selectList(queryWrapper));
        } catch (Exception e) {
            return R.error("获取用户实验记录列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<com.ldk.backend.DTO.ExperimentRecordExportVO> getExportRecords(
            Integer userId,
            String algorithmName,
            String status,
            String startTimeFrom,
            String endTimeTo) {
        
        try {
            // 构建查询条件
            QueryWrapper<ExperimentRecord> queryWrapper = new QueryWrapper<>();
            
            if (userId != null) {
                queryWrapper.eq("user_id", userId);
            }
            if (algorithmName != null && !algorithmName.trim().isEmpty()) {
                // 根据算法名称获取算法ID
                Integer algorithmId = getAlgorithmIdByName(algorithmName);
                if (algorithmId != null) {
                    queryWrapper.eq("algorithm_id", algorithmId);
                } else {
                    // 如果算法名称不存在，返回空结果
                    queryWrapper.eq("algorithm_id", -1);
                }
            }
            if (status != null && !status.trim().isEmpty()) {
                queryWrapper.eq("status", status);
            }
            
            // 处理时间参数
            if (startTimeFrom != null && !startTimeFrom.trim().isEmpty()) {
                // 如果传入的是LocalDateTime的toString()格式，直接使用
                if (startTimeFrom.contains("T")) {
                    // 格式如：2026-03-03T14:16:25.931
                    queryWrapper.ge("start_time", startTimeFrom.replace("T", " "));
                } else {
                    // 格式如：2026-03-03 14:16:25
                    queryWrapper.ge("start_time", startTimeFrom);
                }
            }
            
            if (endTimeTo != null && !endTimeTo.trim().isEmpty()) {
                // 如果传入的是LocalDateTime的toString()格式，直接使用
                if (endTimeTo.contains("T")) {
                    // 格式如：2026-03-03T14:16:25.931
                    queryWrapper.le("end_time", endTimeTo.replace("T", " "));
                } else {
                    // 格式如：2026-03-03 14:16:25
                    queryWrapper.le("end_time", endTimeTo);
                }
            }
            
            queryWrapper.orderByDesc("start_time");
            
            // 查询数据
            List<ExperimentRecord> records = experimentRecordMapper.selectList(queryWrapper);
            
            // 转换为导出VO
            return convertToExportVO(records);
            
        } catch (Exception e) {
            throw new RuntimeException("获取导出记录失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将ExperimentRecord实体列表转换为导出VO列表
     * 从user表查询用户的name字段作为用户名
     */
    private List<com.ldk.backend.DTO.ExperimentRecordExportVO> convertToExportVO(List<ExperimentRecord> records) {
        return records.stream().map(record -> {
            com.ldk.backend.DTO.ExperimentRecordExportVO vo = new com.ldk.backend.DTO.ExperimentRecordExportVO();
            
            vo.setRecordId(record.getRecordId());
            
            // 从user表查询用户的name字段
            String userName = "用户" + record.getUserId();
            try {
                com.ldk.backend.entity.User user = userMapper.selectById(record.getUserId());
                if (user != null && user.getName() != null && !user.getName().trim().isEmpty()) {
                    userName = user.getName();
                }
            } catch (Exception e) {
                // 查询失败时使用默认值
                userName = "用户" + record.getUserId();
            }
            vo.setUsername(userName);
            
            // 根据algorithmId获取算法名称
            String algorithmName = getAlgorithmNameById(record.getAlgorithmId());
            vo.setAlgorithmName(algorithmName);
            
            vo.setStartTime(record.getStartTime());
            vo.setEndTime(record.getEndTime());
            vo.setPathLength(record.getPathLength());
            vo.setVisitedCount(record.getVisitedCount());
            vo.setStatus(record.getStatus());
            
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 根据算法ID获取算法名称
     */
    private String getAlgorithmNameById(Integer algorithmId) {
        if (algorithmId == null) {
            return "未知算法";
        }
        
        // 根据数据库中的实际ID映射：
        // A*算法: algorithm_id = 1
        // Dijkstra算法: algorithm_id = 2
        // DFS: algorithm_id = 4
        // BFS: algorithm_id = 5
        Map<Integer, String> algorithmMap = new HashMap<>();
        algorithmMap.put(1, "A*");
        algorithmMap.put(2, "Dijkstra");
        algorithmMap.put(4, "DFS");
        algorithmMap.put(5, "BFS");
        
        return algorithmMap.getOrDefault(algorithmId, "算法" + algorithmId);
    }
    
    /**
     * 获取一个可用的默认problemId
     * 查询problem表中最小的problem_id作为默认值
     */
    private Integer getDefaultProblemId() {
        try {
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByAsc("problem_id").last("LIMIT 1");
            Problem problem = problemMapper.selectOne(queryWrapper);
            if (problem != null) {
                return problem.getProblemId();
            }
        } catch (Exception e) {
            // 查询失败时忽略
        }
        // 如果没有任何problem记录，返回一个兜底值
        return 1;
    }

    /**
     * 根据算法名称获取算法ID
     */
    private Integer getAlgorithmIdByName(String algorithmName) {
        if (algorithmName == null || algorithmName.trim().isEmpty()) {
            return null;
        }
        
        // 根据算法名称映射到ID
        Map<String, Integer> algorithmMap = new HashMap<>();
        algorithmMap.put("A*", 1);
        algorithmMap.put("Dijkstra", 2);
        algorithmMap.put("DFS", 4);
        algorithmMap.put("BFS", 5);
        
        // 也支持小写和带空格的变体
        algorithmMap.put("a*", 1);
        algorithmMap.put("dijkstra", 2);
        algorithmMap.put("dfs", 4);
        algorithmMap.put("bfs", 5);
        algorithmMap.put("A*算法", 1);
        algorithmMap.put("Dijkstra算法", 2);
        algorithmMap.put("深度优先搜索", 4);
        algorithmMap.put("广度优先搜索", 5);
        
        return algorithmMap.get(algorithmName);
    }
}
