package com.ldk.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.Algorithm;
import com.ldk.backend.entity.Problem;
import com.ldk.backend.mapper.AlgorithmMapper;
import com.ldk.backend.mapper.ProblemMapper;
import com.ldk.backend.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl implements ProblemService {
    @Autowired
    private ProblemMapper problemMapper;
    
    @Autowired
    private AlgorithmMapper algorithmMapper;

    @Override
    public R<List<Problem>> listByAlgorithmName(String algorithmName) {
        try {
            // 1. 根据算法名称查询算法ID
            QueryWrapper<Algorithm> algorithmQuery = new QueryWrapper<>();
            algorithmQuery.eq("name", algorithmName);
            Algorithm algorithm = algorithmMapper.selectOne(algorithmQuery);
            
            if (algorithm == null) {
                return R.notFound("算法不存在: " + algorithmName);
            }
            
            // 2. 根据算法ID查询问题，过滤掉标题为"默认题目"的记录
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("algorithm_id", algorithm.getAlgorithmId())
                         .ne("title", "默认题目");
            List<Problem> problems = problemMapper.selectList(queryWrapper);
            
            return R.ok(problems);
        } catch (Exception e) {
            return R.error("根据算法名称查询问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<Page<Problem>> getProblemList(Integer page, Integer size, String title, String description, 
                                          String difficulty, String algorithmName, Integer algorithmId) {
        try {
            if (page == null || page < 1) {
                page = 1;
            }
            if (size == null || size < 1) {
                size = 10;
            }

            Page<Problem> pageInfo = new Page<>(page, size);
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();

            // 模糊搜索标题
            if (title != null && !title.trim().isEmpty()) {
                queryWrapper.like("title", title);
            }
            
            // 模糊搜索描述
            if (description != null && !description.trim().isEmpty()) {
                queryWrapper.like("description", description);
            }
            
            // 精确匹配难度
            if (difficulty != null && !difficulty.trim().isEmpty()) {
                queryWrapper.eq("difficulty", difficulty);
            }
            
            // 精确匹配算法ID（优先使用algorithmId）
            if (algorithmId != null) {
                queryWrapper.eq("algorithm_id", algorithmId);
            } else if (algorithmName != null && !algorithmName.trim().isEmpty()) {
                // 如果没有algorithmId，则根据算法名称查询算法ID
                QueryWrapper<Algorithm> algorithmQuery = new QueryWrapper<>();
                algorithmQuery.eq("name", algorithmName);
                Algorithm algorithm = algorithmMapper.selectOne(algorithmQuery);
                
                if (algorithm != null) {
                    queryWrapper.eq("algorithm_id", algorithm.getAlgorithmId());
                } else {
                    // 如果算法不存在，返回空结果
                    queryWrapper.eq("algorithm_id", -1);
                }
            }

            queryWrapper.orderByDesc("problem_id");
            Page<Problem> result = problemMapper.selectPage(pageInfo, queryWrapper);
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询问题列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<Problem> getProblemById(Integer problemId) {
        try {
            if (problemId == null || problemId < 1) {
                return R.error("问题ID不能为空");
            }
            
            Problem problem = problemMapper.selectById(problemId);
            if (problem == null) {
                return R.notFound("问题不存在");
            }
            
            return R.ok(problem);
        } catch (Exception e) {
            return R.error("查询问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> createProblem(Problem problem) {
        try {
            if (problem == null || problem.getTitle() == null || problem.getTitle().trim().isEmpty()) {
                return R.error("问题标题不能为空");
            }
            
            if (problem.getAlgorithmId() == null) {
                return R.error("算法ID不能为空");
            }
            
            // 检查算法是否存在
            Algorithm algorithm = algorithmMapper.selectById(problem.getAlgorithmId());
            if (algorithm == null) {
                return R.error("算法不存在，算法ID: " + problem.getAlgorithmId());
            }
            
            // 检查问题标题是否已存在（同一算法下）
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("title", problem.getTitle());
            queryWrapper.eq("algorithm_id", problem.getAlgorithmId());
            
            if (problemMapper.selectOne(queryWrapper) != null) {
                return R.error("该算法下已存在相同标题的问题");
            }
            
            // 插入数据库
            int result = problemMapper.insert(problem);
            
            if (result == 1) {
                return R.ok("创建问题成功");
            } else {
                return R.error("创建问题失败");
            }
        } catch (Exception e) {
            return R.error("创建问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> updateProblem(Integer problemId, Problem problem) {
        try {
            if (problemId == null) {
                return R.error("问题ID不能为空");
            }
            
            // 检查问题是否存在
            Problem existingProblem = problemMapper.selectById(problemId);
            if (existingProblem == null) {
                return R.error("问题不存在");
            }
            
            // 如果提供了新标题，检查标题是否与其他问题冲突（同一算法下）
            if (problem.getTitle() != null && !problem.getTitle().trim().isEmpty()) {
                QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("title", problem.getTitle());
                queryWrapper.eq("algorithm_id", existingProblem.getAlgorithmId());
                queryWrapper.ne("problem_id", problemId); // 排除当前问题
                
                if (problemMapper.selectOne(queryWrapper) != null) {
                    return R.error("该算法下已存在相同标题的问题");
                }
                existingProblem.setTitle(problem.getTitle());
            }
            
            // 更新其他字段
            if (problem.getDescription() != null) {
                existingProblem.setDescription(problem.getDescription());
            }
            if (problem.getDifficulty() != null) {
                existingProblem.setDifficulty(problem.getDifficulty());
            }
            if (problem.getAlgorithmId() != null) {
                // 检查算法是否存在
                Algorithm algorithm = algorithmMapper.selectById(problem.getAlgorithmId());
                if (algorithm == null) {
                    return R.error("算法不存在，算法ID: " + problem.getAlgorithmId());
                }
                existingProblem.setAlgorithmId(problem.getAlgorithmId());
            }
            if (problem.getLeetcodeLink() != null) {
                existingProblem.setLeetcodeLink(problem.getLeetcodeLink());
            }
            
            int result = problemMapper.updateById(existingProblem);
            if (result > 0) {
                return R.ok("更新问题成功");
            } else {
                return R.error("更新问题失败");
            }
        } catch (Exception e) {
            return R.error("更新问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> deleteProblem(Integer problemId) {
        try {
            if (problemId == null) {
                return R.error("问题ID不能为空");
            }

            // 检查问题是否存在
            Problem existingProblem = problemMapper.selectById(problemId);
            if (existingProblem == null) {
                return R.notFound("问题不存在");
            }

            // 执行删除
            int result = problemMapper.deleteById(problemId);
            if (result > 0) {
                return R.ok("删除问题成功");
            } else {
                return R.error("删除问题失败");
            }
        } catch (Exception e) {
            return R.error("删除问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<String> batchDeleteProblems(List<Integer> problemIds) {
        try {
            if (problemIds == null || problemIds.isEmpty()) {
                return R.error("请选择要删除的问题");
            }

            // 检查所有问题是否存在
            for (Integer problemId : problemIds) {
                Problem existingProblem = problemMapper.selectById(problemId);
                if (existingProblem == null) {
                    return R.error("问题ID " + problemId + " 不存在");
                }
            }

            // 执行批量删除
            int result = problemMapper.deleteBatchIds(problemIds);
            if (result > 0) {
                return R.ok("成功删除 " + result + " 个问题");
            } else {
                return R.error("批量删除问题失败");
            }
        } catch (Exception e) {
            return R.error("批量删除问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<List<Problem>> searchProblems(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return R.error("搜索关键词不能为空");
            }

            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(wrapper -> wrapper.like("title", keyword).or().like("description", keyword))
                         .ne("title", "默认题目");
            
            List<Problem> problems = problemMapper.selectList(queryWrapper);
            
            if (problems.isEmpty()) {
                return R.notFound("未找到匹配的问题");
            }
            
            return R.ok(problems);
        } catch (Exception e) {
            return R.error("搜索问题失败: " + e.getMessage());
        }
    }
    
    @Override
    public R<List<Problem>> getAllProblems() {
        try {
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("title", "默认题目")
                         .orderByDesc("problem_id");
            
            List<Problem> problems = problemMapper.selectList(queryWrapper);
            
            if (problems.isEmpty()) {
                return R.notFound("暂无问题数据");
            }
            
            return R.ok(problems);
        } catch (Exception e) {
            return R.error("获取问题列表失败: " + e.getMessage());
        }
    }

    @Override
    public List<com.ldk.backend.DTO.ProblemExportVO> getExportRecords(String title, String description, String difficulty, Integer algorithmId) {
        try {
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("title", "默认题目");

            if (title != null && !title.trim().isEmpty()) {
                queryWrapper.like("title", title);
            }
            if (description != null && !description.trim().isEmpty()) {
                queryWrapper.like("description", description);
            }
            if (difficulty != null && !difficulty.trim().isEmpty()) {
                queryWrapper.eq("difficulty", difficulty);
            }
            if (algorithmId != null) {
                queryWrapper.eq("algorithm_id", algorithmId);
            }

            queryWrapper.orderByDesc("problem_id");
            List<Problem> problems = problemMapper.selectList(queryWrapper);

            return problems.stream().map(problem -> {
                com.ldk.backend.DTO.ProblemExportVO vo = new com.ldk.backend.DTO.ProblemExportVO();
                vo.setProblemId(problem.getProblemId());
                vo.setTitle(problem.getTitle());

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
                String difficultyStr = problem.getDifficulty();
                if ("easy".equals(difficultyStr)) {
                    vo.setDifficulty("简单");
                } else if ("medium".equals(difficultyStr)) {
                    vo.setDifficulty("中等");
                } else if ("hard".equals(difficultyStr)) {
                    vo.setDifficulty("困难");
                } else {
                    vo.setDifficulty(difficultyStr);
                }

                vo.setDescription(problem.getDescription());

                return vo;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("获取导出记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public R<com.ldk.backend.controller.ProblemController.ProblemStats> getProblemStats() {
        try {
            // 查询所有题目（排除"默认题目"）
            QueryWrapper<Problem> baseQuery = new QueryWrapper<>();
            baseQuery.ne("title", "默认题目");
            List<Problem> allProblems = problemMapper.selectList(baseQuery);

            com.ldk.backend.controller.ProblemController.ProblemStats stats =
                new com.ldk.backend.controller.ProblemController.ProblemStats();

            // 总数
            stats.setTotal(allProblems.size());

            // 按难度统计
            long easy = 0, medium = 0, hard = 0;
            for (Problem p : allProblems) {
                String diff = p.getDifficulty();
                if ("easy".equals(diff)) easy++;
                else if ("medium".equals(diff)) medium++;
                else if ("hard".equals(diff)) hard++;
            }
            stats.setEasy(easy);
            stats.setMedium(medium);
            stats.setHard(hard);

            // 按算法统计
            Map<Integer, Long> byAlgorithm = new HashMap<>();
            for (Problem p : allProblems) {
                Integer algId = p.getAlgorithmId();
                byAlgorithm.put(algId, byAlgorithm.getOrDefault(algId, 0L) + 1);
            }
            stats.setByAlgorithm(byAlgorithm);

            return R.ok(stats);
        } catch (Exception e) {
            return R.error("获取题目统计信息失败: " + e.getMessage());
        }
    }
}
