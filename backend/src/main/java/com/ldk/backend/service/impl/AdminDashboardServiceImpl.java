package com.ldk.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ExperimentRecord;
import com.ldk.backend.entity.Problem;
import com.ldk.backend.entity.ProblemCompletion;
import com.ldk.backend.entity.User;
import com.ldk.backend.mapper.ExperimentRecordMapper;
import com.ldk.backend.mapper.ProblemCompletionMapper;
import com.ldk.backend.mapper.ProblemMapper;
import com.ldk.backend.mapper.UserMapper;
import com.ldk.backend.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private ExperimentRecordMapper experimentRecordMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProblemCompletionMapper problemCompletionMapper;

    // 算法ID -> 名称映射（硬编码，algorithm表已废弃）
    private static final Map<Integer, String> ALGORITHM_MAP = new LinkedHashMap<>();
    static {
        ALGORITHM_MAP.put(1, "A*");
        ALGORITHM_MAP.put(2, "Dijkstra");
        ALGORITHM_MAP.put(4, "DFS");
        ALGORITHM_MAP.put(5, "BFS");
    }

    @Override
    public R<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> data = new LinkedHashMap<>();

            // 1. 近5天实验生成数
            data.put("experimentTrend", getExperimentTrend());

            // 2. 题库统计（总数 + 各难度 + 各算法）
            data.put("problemStats", getProblemStats());

            // 3. 用户总数
            Long totalUsers = userMapper.selectCount(null);
            data.put("totalUsers", totalUsers);

            // 4. 近5周新创建用户数
            data.put("userTrend", getUserTrend());

            // 5. 题目完成记录总数
            Long totalCompletions = problemCompletionMapper.selectCount(null);
            data.put("totalCompletions", totalCompletions);

            return R.ok(data);
        } catch (Exception e) {
            return R.error("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取近5天实验生成趋势
     */
    private List<Map<String, Object>> getExperimentTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 4; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            QueryWrapper<ExperimentRecord> qw = new QueryWrapper<>();
            qw.ge("start_time", dayStart)
               .le("start_time", dayEnd);

            Long count = experimentRecordMapper.selectCount(qw);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    /**
     * 获取题目统计（总数、各难度、各算法分别统计简单/中等/困难）
     */
    private Map<String, Object> getProblemStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 查询所有题目（排除"默认题目"）
        QueryWrapper<Problem> baseQuery = new QueryWrapper<>();
        baseQuery.ne("title", "默认题目");
        List<Problem> allProblems = problemMapper.selectList(baseQuery);

        // 总数
        stats.put("total", allProblems.size());

        // 按难度统计
        long easy = 0, medium = 0, hard = 0;
        for (Problem p : allProblems) {
            String diff = p.getDifficulty();
            if ("easy".equals(diff)) easy++;
            else if ("medium".equals(diff)) medium++;
            else if ("hard".equals(diff)) hard++;
        }
        stats.put("easy", easy);
        stats.put("medium", medium);
        stats.put("hard", hard);

        // 按算法统计（每个算法下分别统计 easy/medium/hard 数量）
        List<Map<String, Object>> byAlgorithm = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : ALGORITHM_MAP.entrySet()) {
            Integer algId = entry.getKey();
            String algName = entry.getValue();

            long algEasy = 0, algMedium = 0, algHard = 0;
            for (Problem p : allProblems) {
                if (algId.equals(p.getAlgorithmId())) {
                    String diff = p.getDifficulty();
                    if ("easy".equals(diff)) algEasy++;
                    else if ("medium".equals(diff)) algMedium++;
                    else if ("hard".equals(diff)) algHard++;
                }
            }

            Map<String, Object> algStats = new LinkedHashMap<>();
            algStats.put("name", algName);
            algStats.put("algorithmId", algId);
            algStats.put("easy", algEasy);
            algStats.put("medium", algMedium);
            algStats.put("hard", algHard);
            algStats.put("total", algEasy + algMedium + algHard);
            byAlgorithm.add(algStats);
        }
        stats.put("byAlgorithm", byAlgorithm);

        return stats;
    }

    /**
     * 获取近5周新创建用户数（按周分组）
     */
    private List<Map<String, Object>> getUserTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 4; i >= 0; i--) {
            // 计算当前周的起始日期（周一）
            LocalDate weekStart = today.minusWeeks(i)
                    .with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);

            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.ge("created_at", LocalDateTime.of(weekStart, LocalTime.MIN))
               .le("created_at", LocalDateTime.of(weekEnd, LocalTime.MAX));

            Long count = userMapper.selectCount(qw);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("weekLabel", weekStart.format(DateTimeFormatter.ofPattern("MM-dd")) + "~" + weekEnd.format(DateTimeFormatter.ofPattern("MM-dd")));
            item.put("startDate", weekStart.toString());
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }
}
