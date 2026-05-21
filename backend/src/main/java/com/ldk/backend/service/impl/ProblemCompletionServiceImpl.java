package com.ldk.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.Algorithm;
import com.ldk.backend.entity.Problem;
import com.ldk.backend.entity.ProblemCompletion;
import com.ldk.backend.mapper.AlgorithmMapper;
import com.ldk.backend.mapper.ProblemCompletionMapper;
import com.ldk.backend.mapper.ProblemMapper;
import com.ldk.backend.service.ProblemCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProblemCompletionServiceImpl implements ProblemCompletionService {
    @Autowired
    private ProblemCompletionMapper mapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private AlgorithmMapper algorithmMapper;

    @Override
    public R<ProblemCompletion> getByUserAndProblem(Integer userId, Integer problemId) {
        try {
            if (userId == null || problemId == null) {
                return R.error("用户ID和问题ID不能为空");
            }
            
            QueryWrapper<ProblemCompletion> qw = new QueryWrapper<>();
            qw.eq("user_id", userId).eq("problem_id", problemId);
            
            ProblemCompletion result = mapper.selectOne(qw);
            if (result == null) {
                // 如果记录不存在，自动创建一条新的记录
                ProblemCompletion newRecord = new ProblemCompletion();
                newRecord.setUserId(userId);
                newRecord.setProblemId(problemId);
                newRecord.setCompletionStatus("not_started");
                newRecord.setAttemptCount(0);
                newRecord.setIsSolved(false);
                newRecord.setTimeSpentMinutes(0);
                newRecord.setCreatedAt(LocalDateTime.now());
                newRecord.setUpdatedAt(LocalDateTime.now());
                
                int insertResult = mapper.insert(newRecord);
                if (insertResult == 1) {
                    // 重新查询新创建的记录
                    result = mapper.selectOne(qw);
                    return R.ok(result);
                } else {
                    return R.error("自动创建学习记录失败");
                }
            }
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<Page<ProblemCompletion>> getProblemCompletionList(Integer page, Integer size, 
                                                              Integer userId, Integer problemId,
                                                              String completionStatus) {
        try {
            if (page == null || page < 1) {
                page = 1;
            }
            if (size == null || size < 1) {
                size = 10;
            }
            
            Page<ProblemCompletion> pageInfo = new Page<>(page, size);
            QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
            
            if (userId != null) {
                queryWrapper.eq("user_id", userId);
            }
            if (problemId != null) {
                queryWrapper.eq("problem_id", problemId);
            }
            if (completionStatus != null && !completionStatus.trim().isEmpty()) {
                queryWrapper.eq("completion_status", completionStatus);
            }
            
            queryWrapper.orderByDesc("updated_at");
            Page<ProblemCompletion> result = mapper.selectPage(pageInfo, queryWrapper);
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询学习记录列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> createProblemCompletion(ProblemCompletion problemCompletion) {
        try {
            if (problemCompletion == null) {
                return R.error("学习记录不能为空");
            }
            if (problemCompletion.getUserId() == null) {
                return R.error("用户ID不能为空");
            }
            if (problemCompletion.getProblemId() == null) {
                return R.error("问题ID不能为空");
            }
            
            // 检查是否已存在相同的用户-问题记录
            QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", problemCompletion.getUserId())
                       .eq("problem_id", problemCompletion.getProblemId());
            
            if (mapper.selectOne(queryWrapper) != null) {
                return R.error("该用户对该题目的学习记录已存在");
            }
            
            // 设置默认值
            if (problemCompletion.getCompletionStatus() == null) {
                problemCompletion.setCompletionStatus("not_started");
            }
            if (problemCompletion.getAttemptCount() == null) {
                problemCompletion.setAttemptCount(0);
            }
            if (problemCompletion.getIsSolved() == null) {
                problemCompletion.setIsSolved(false);
            }
            if (problemCompletion.getTimeSpentMinutes() == null) {
                problemCompletion.setTimeSpentMinutes(0);
            }
            
            // 设置创建时间和更新时间
            problemCompletion.setCreatedAt(LocalDateTime.now());
            problemCompletion.setUpdatedAt(LocalDateTime.now());
            
            int result = mapper.insert(problemCompletion);
            if (result == 1) {
                return R.ok("创建学习记录成功");
            } else {
                return R.error("创建学习记录失败");
            }
        } catch (Exception e) {
            return R.error("创建学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> updateProblemCompletion(Integer completionId, ProblemCompletion problemCompletion) {
        try {
            if (completionId == null) {
                return R.error("学习记录ID不能为空");
            }
            if (problemCompletion == null) {
                return R.error("学习记录不能为空");
            }
            
            // 检查学习记录是否存在
            ProblemCompletion existingRecord = mapper.selectById(completionId);
            if (existingRecord == null) {
                return R.notFound("学习记录不存在");
            }
            
            // 更新字段
            if (problemCompletion.getCompletionStatus() != null) {
                existingRecord.setCompletionStatus(problemCompletion.getCompletionStatus());
            }
            if (problemCompletion.getAttemptCount() != null) {
                existingRecord.setAttemptCount(problemCompletion.getAttemptCount());
            }
            if (problemCompletion.getIsSolved() != null) {
                existingRecord.setIsSolved(problemCompletion.getIsSolved());
            }
            if (problemCompletion.getNotes() != null) {
                existingRecord.setNotes(problemCompletion.getNotes());
            }
            if (problemCompletion.getLastReviewDate() != null) {
                existingRecord.setLastReviewDate(problemCompletion.getLastReviewDate());
            }
            if (problemCompletion.getDifficultyRating() != null) {
                existingRecord.setDifficultyRating(problemCompletion.getDifficultyRating());
            }
            if (problemCompletion.getTimeSpentMinutes() != null) {
                existingRecord.setTimeSpentMinutes(problemCompletion.getTimeSpentMinutes());
            }
            
            // 更新更新时间
            existingRecord.setUpdatedAt(LocalDateTime.now());
            
            int result = mapper.updateById(existingRecord);
            if (result > 0) {
                return R.ok("更新学习记录成功");
            } else {
                return R.error("更新学习记录失败");
            }
        } catch (Exception e) {
            return R.error("更新学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> deleteProblemCompletion(Integer completionId) {
        try {
            if (completionId == null) {
                return R.error("学习记录ID不能为空");
            }
            
            // 检查学习记录是否存在
            ProblemCompletion existingRecord = mapper.selectById(completionId);
            if (existingRecord == null) {
                return R.notFound("学习记录不存在");
            }
            
            int result = mapper.deleteById(completionId);
            if (result > 0) {
                return R.ok("删除学习记录成功");
            } else {
                return R.error("删除学习记录失败");
            }
        } catch (Exception e) {
            return R.error("删除学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> batchDeleteProblemCompletions(List<Integer> completionIds) {
        try {
            if (completionIds == null || completionIds.isEmpty()) {
                return R.error("学习记录ID列表不能为空");
            }
            
            int deleteCount = 0;
            for (Integer id : completionIds) {
                ProblemCompletion existingRecord = mapper.selectById(id);
                if (existingRecord != null) {
                    int result = mapper.deleteById(id);
                    if (result > 0) {
                        deleteCount++;
                    }
                }
            }
            
            return R.ok("批量删除完成，成功删除 " + deleteCount + " 条记录");
        } catch (Exception e) {
            return R.error("批量删除学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> batchUpdateProblemCompletions(List<ProblemCompletion> problemCompletions) {
        try {
            if (problemCompletions == null || problemCompletions.isEmpty()) {
                return R.error("学习记录列表不能为空");
            }
            
            int successCount = 0;
            for (ProblemCompletion pc : problemCompletions) {
                if (pc.getCompletionId() == null) {
                    continue; // 跳过没有ID的记录
                }
                
                ProblemCompletion existingRecord = mapper.selectById(pc.getCompletionId());
                if (existingRecord == null) {
                    continue; // 跳过不存在的记录
                }
                
                // 更新字段
                if (pc.getCompletionStatus() != null) {
                    existingRecord.setCompletionStatus(pc.getCompletionStatus());
                }
                if (pc.getAttemptCount() != null) {
                    existingRecord.setAttemptCount(pc.getAttemptCount());
                }
                if (pc.getIsSolved() != null) {
                    existingRecord.setIsSolved(pc.getIsSolved());
                }
                if (pc.getNotes() != null) {
                    existingRecord.setNotes(pc.getNotes());
                }
                if (pc.getLastReviewDate() != null) {
                    existingRecord.setLastReviewDate(pc.getLastReviewDate());
                }
                if (pc.getDifficultyRating() != null) {
                    existingRecord.setDifficultyRating(pc.getDifficultyRating());
                }
                if (pc.getTimeSpentMinutes() != null) {
                    existingRecord.setTimeSpentMinutes(pc.getTimeSpentMinutes());
                }
                
                existingRecord.setUpdatedAt(LocalDateTime.now());
                
                int result = mapper.updateById(existingRecord);
                if (result > 0) {
                    successCount++;
                }
            }
            
            return R.ok("批量更新完成，成功更新 " + successCount + " 条记录");
        } catch (Exception e) {
            return R.error("批量更新学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<Map<String, Object>> getUserLearningStats(Integer userId) {
        try {
            if (userId == null) {
                return R.error("用户ID不能为空");
            }
            
            QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            List<ProblemCompletion> records = mapper.selectList(queryWrapper);
            
            Map<String, Object> stats = new HashMap<>();
            int totalProblems = records.size();
            int completedCount = 0;
            int inProgressCount = 0;
            int notStartedCount = 0;
            int solvedCount = 0;
            int totalAttempts = 0;
            int totalTimeSpent = 0;
            
            for (ProblemCompletion record : records) {
                if ("completed".equals(record.getCompletionStatus())) {
                    completedCount++;
                } else if ("in_progress".equals(record.getCompletionStatus())) {
                    inProgressCount++;
                } else {
                    notStartedCount++;
                }
                
                if (Boolean.TRUE.equals(record.getIsSolved())) {
                    solvedCount++;
                }
                
                if (record.getAttemptCount() != null) {
                    totalAttempts += record.getAttemptCount();
                }
                
                if (record.getTimeSpentMinutes() != null) {
                    totalTimeSpent += record.getTimeSpentMinutes();
                }
            }
            
            stats.put("totalProblems", totalProblems);
            stats.put("completedCount", completedCount);
            stats.put("inProgressCount", inProgressCount);
            stats.put("notStartedCount", notStartedCount);
            stats.put("solvedCount", solvedCount);
            stats.put("totalAttempts", totalAttempts);
            stats.put("totalTimeSpent", totalTimeSpent);
            stats.put("completionRate", totalProblems > 0 ? (double) completedCount / totalProblems * 100 : 0);
            stats.put("successRate", totalAttempts > 0 ? (double) solvedCount / totalAttempts * 100 : 0);
            
            return R.ok(stats);
        } catch (Exception e) {
            return R.error("获取用户学习统计失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<Map<String, Object>> getUserLearningProgress(Integer userId) {
        try {
            if (userId == null) {
                return R.error("用户ID不能为空");
            }
            
            QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("updated_at");
            queryWrapper.last("LIMIT 10"); // 获取最近10条记录
            
            List<ProblemCompletion> recentRecords = mapper.selectList(queryWrapper);
            
            Map<String, Object> progress = new HashMap<>();
            progress.put("recentActivities", recentRecords);
            progress.put("totalCount", recentRecords.size());
            
            // 计算最近7天的学习情况
            LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
            QueryWrapper<ProblemCompletion> weeklyQuery = new QueryWrapper<>();
            weeklyQuery.eq("user_id", userId);
            weeklyQuery.ge("updated_at", sevenDaysAgo.atStartOfDay());
            
            List<ProblemCompletion> weeklyRecords = mapper.selectList(weeklyQuery);
            int weeklyCompleted = 0;
            int weeklyTimeSpent = 0;
            
            for (ProblemCompletion record : weeklyRecords) {
                if ("completed".equals(record.getCompletionStatus())) {
                    weeklyCompleted++;
                }
                if (record.getTimeSpentMinutes() != null) {
                    weeklyTimeSpent += record.getTimeSpentMinutes();
                }
            }
            
            progress.put("weeklyCompleted", weeklyCompleted);
            progress.put("weeklyTimeSpent", weeklyTimeSpent);
            progress.put("weeklyActivityCount", weeklyRecords.size());
            
            return R.ok(progress);
        } catch (Exception e) {
            return R.error("获取用户学习进度失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<List<ProblemCompletion>> getByProblemId(Integer problemId) {
        try {
            if (problemId == null) {
                return R.error("问题ID不能为空");
            }
            
            QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("problem_id", problemId);
            queryWrapper.orderByDesc("updated_at");
            
            List<ProblemCompletion> result = mapper.selectList(queryWrapper);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("按照问题ID查询学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<List<ProblemCompletion>> getByUserId(Integer userId) {
        try {
            if (userId == null) {
                return R.error("用户ID不能为空");
            }
            
            QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("updated_at");
            
            List<ProblemCompletion> result = mapper.selectList(queryWrapper);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("按照用户ID查询学习记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<Page<Map<String, Object>>> searchUserCompletions(Integer userId, Integer page, Integer size,
                                                               Integer algorithmId, String difficulty,
                                                               String title, String description) {
        try {
            if (userId == null) {
                return R.error("用户ID不能为空");
            }
            if (page == null || page < 1) {
                page = 1;
            }
            if (size == null || size < 1) {
                size = 10;
            }

            // 1. 先查询该用户的所有ProblemCompletion记录（分页）
            Page<ProblemCompletion> pageInfo = new Page<>(page, size);
            QueryWrapper<ProblemCompletion> qw = new QueryWrapper<>();
            qw.eq("user_id", userId);
            qw.orderByDesc("updated_at");

            Page<ProblemCompletion> completionPage = mapper.selectPage(pageInfo, qw);
            List<ProblemCompletion> completions = completionPage.getRecords();

            if (completions.isEmpty()) {
                Page<Map<String, Object>> emptyPage = new Page<>(page, size, 0);
                emptyPage.setRecords(new ArrayList<>());
                return R.ok(emptyPage);
            }

            // 2. 收集所有problemId，批量查询Problem信息
            List<Integer> problemIds = completions.stream()
                    .map(ProblemCompletion::getProblemId)
                    .collect(Collectors.toList());

            QueryWrapper<Problem> problemQw = new QueryWrapper<>();
            problemQw.in("problem_id", problemIds);
            List<Problem> problems = problemMapper.selectList(problemQw);

            // 构建 problemId -> Problem 的映射
            Map<Integer, Problem> problemMap = problems.stream()
                    .collect(Collectors.toMap(Problem::getProblemId, p -> p));

            // 3. 组装结果，并应用筛选条件
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (ProblemCompletion pc : completions) {
                Problem problem = problemMap.get(pc.getProblemId());

                // 如果题目不存在，跳过
                if (problem == null) {
                    continue;
                }

                // 应用筛选条件
                if (algorithmId != null && !algorithmId.equals(problem.getAlgorithmId())) {
                    continue;
                }
                if (difficulty != null && !difficulty.trim().isEmpty()
                        && !difficulty.equals(problem.getDifficulty())) {
                    continue;
                }
                if (title != null && !title.trim().isEmpty()
                        && (problem.getTitle() == null || !problem.getTitle().toLowerCase().contains(title.toLowerCase()))) {
                    continue;
                }
                if (description != null && !description.trim().isEmpty()
                        && (problem.getDescription() == null || !problem.getDescription().toLowerCase().contains(description.toLowerCase()))) {
                    continue;
                }

                Map<String, Object> item = new HashMap<>();
                // ProblemCompletion字段
                item.put("completionId", pc.getCompletionId());
                item.put("userId", pc.getUserId());
                item.put("problemId", pc.getProblemId());
                item.put("completionStatus", pc.getCompletionStatus());
                item.put("attemptCount", pc.getAttemptCount());
                item.put("isSolved", pc.getIsSolved());
                item.put("notes", pc.getNotes());
                item.put("lastReviewDate", pc.getLastReviewDate());
                item.put("difficultyRating", pc.getDifficultyRating());
                item.put("timeSpentMinutes", pc.getTimeSpentMinutes());
                item.put("createdAt", pc.getCreatedAt());
                item.put("updatedAt", pc.getUpdatedAt());
                // Problem字段
                item.put("problemTitle", problem.getTitle());
                item.put("difficulty", problem.getDifficulty());
                item.put("description", problem.getDescription());
                item.put("algorithmId", problem.getAlgorithmId());
                item.put("leetcodeLink", problem.getLeetcodeLink());

                resultList.add(item);
            }

            // 4. 构建分页结果
            Page<Map<String, Object>> resultPage = new Page<>(page, size, completionPage.getTotal());
            resultPage.setRecords(resultList);

            return R.ok(resultPage);
        } catch (Exception e) {
            return R.error("搜索用户学习记录失败: " + e.getMessage());
        }
    }

    @Override
    public List<com.ldk.backend.DTO.ProblemCompletionExportVO> getExportRecords(Integer userId, Integer algorithmId, String difficulty, String title, String description) {
        try {
            // 1. 查询该用户的所有ProblemCompletion记录
            QueryWrapper<ProblemCompletion> qw = new QueryWrapper<>();
            qw.eq("user_id", userId);
            qw.orderByDesc("updated_at");

            List<ProblemCompletion> completions = mapper.selectList(qw);

            if (completions.isEmpty()) {
                return new ArrayList<>();
            }

            // 2. 收集所有problemId，批量查询Problem信息
            List<Integer> problemIds = completions.stream()
                    .map(ProblemCompletion::getProblemId)
                    .collect(Collectors.toList());

            QueryWrapper<Problem> problemQw = new QueryWrapper<>();
            problemQw.in("problem_id", problemIds);
            List<Problem> problems = problemMapper.selectList(problemQw);

            // 构建 problemId -> Problem 的映射
            Map<Integer, Problem> problemMap = problems.stream()
                    .collect(Collectors.toMap(Problem::getProblemId, p -> p));

            // 3. 组装结果，并应用筛选条件
            List<com.ldk.backend.DTO.ProblemCompletionExportVO> resultList = new ArrayList<>();
            for (ProblemCompletion pc : completions) {
                Problem problem = problemMap.get(pc.getProblemId());

                // 如果题目不存在，跳过
                if (problem == null) {
                    continue;
                }

                // 应用筛选条件
                if (algorithmId != null && !algorithmId.equals(problem.getAlgorithmId())) {
                    continue;
                }
                if (difficulty != null && !difficulty.trim().isEmpty()
                        && !difficulty.equals(problem.getDifficulty())) {
                    continue;
                }
                if (title != null && !title.trim().isEmpty()
                        && (problem.getTitle() == null || !problem.getTitle().toLowerCase().contains(title.toLowerCase()))) {
                    continue;
                }
                if (description != null && !description.trim().isEmpty()
                        && (problem.getDescription() == null || !problem.getDescription().toLowerCase().contains(description.toLowerCase()))) {
                    continue;
                }

                com.ldk.backend.DTO.ProblemCompletionExportVO vo = new com.ldk.backend.DTO.ProblemCompletionExportVO();
                vo.setCompletionId(pc.getCompletionId());
                vo.setProblemTitle(problem.getTitle());

                // 获取算法名称
                String algorithmName = "未知";
                if (problem.getAlgorithmId() != null) {
                    Algorithm algorithm = algorithmMapper.selectById(problem.getAlgorithmId());
                    if (algorithm != null && algorithm.getName() != null) {
                        algorithmName = algorithm.getName();
                    }
                }
                vo.setAlgorithmName(algorithmName);

                // 格式化难度
                String diff = problem.getDifficulty();
                if ("easy".equals(diff)) {
                    vo.setDifficulty("简单");
                } else if ("medium".equals(diff)) {
                    vo.setDifficulty("中等");
                } else if ("hard".equals(diff)) {
                    vo.setDifficulty("困难");
                } else {
                    vo.setDifficulty(diff);
                }

                // 格式化完成状态
                String status = pc.getCompletionStatus();
                if ("not_started".equals(status)) {
                    vo.setCompletionStatus("未开始");
                } else if ("in_progress".equals(status)) {
                    vo.setCompletionStatus("进行中");
                } else if ("completed".equals(status)) {
                    vo.setCompletionStatus("已完成");
                } else {
                    vo.setCompletionStatus(status);
                }

                // 解题状态
                vo.setSolvedStatus(Boolean.TRUE.equals(pc.getIsSolved()) ? "已解决" : "未解决");

                vo.setAttemptCount(pc.getAttemptCount());
                vo.setDifficultyRating(pc.getDifficultyRating());

                // 格式化最近复习日期
                if (pc.getLastReviewDate() != null) {
                    vo.setLastReviewDate(pc.getLastReviewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } else {
                    vo.setLastReviewDate("从未复习");
                }

                // 学习笔记
                vo.setNotes(pc.getNotes() != null ? pc.getNotes() : "");

                resultList.add(vo);
            }

            return resultList;

        } catch (Exception e) {
            throw new RuntimeException("获取导出记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public R<Void> saveOrUpdate(ProblemCompletion pc) {
        try {
            if (pc.getCompletionId() == null) {
                // 检查是否已存在相同的用户-问题记录
                QueryWrapper<ProblemCompletion> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", pc.getUserId())
                           .eq("problem_id", pc.getProblemId());
                
                ProblemCompletion existing = mapper.selectOne(queryWrapper);
                if (existing != null) {
                    // 更新现有记录
                    pc.setCompletionId(existing.getCompletionId());
                }
            }
            
            if (pc.getCompletionId() == null) {
                // 设置创建时间和更新时间
                if (pc.getCreatedAt() == null) {
                    pc.setCreatedAt(LocalDateTime.now());
                }
                pc.setUpdatedAt(LocalDateTime.now());
                mapper.insert(pc);
            } else {
                pc.setUpdatedAt(LocalDateTime.now());
                mapper.updateById(pc);
            }
            
            return R.ok();
        } catch (Exception e) {
            return R.error("保存或更新学习记录失败: " + e.getMessage());
        }
    }
}
