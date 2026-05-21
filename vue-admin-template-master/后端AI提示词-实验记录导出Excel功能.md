# 后端AI提示词：实验记录导出Excel功能

## 项目背景
前端Vue项目需要实现实验记录管理页面的导出Excel功能。页面地址：http://localhost:9528/#/admin/experiments/index

## 技术栈要求
- Spring Boot 2.x
- MyBatis Plus
- EasyExcel（推荐）或 Apache POI
- MySQL数据库

## 数据库表结构
```sql
-- 实验记录表（假设结构）
CREATE TABLE experiment_records (
  record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  algorithm_id INT NOT NULL,
  grid_size VARCHAR(20),
  visited_count INT,
  path_length INT,
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'running', -- completed/failed/running
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE users (
  user_id BIGINT PRIMARY KEY,
  username VARCHAR(50),
  name VARCHAR(100)
);

-- 算法表
CREATE TABLE algorithms (
  algorithm_id INT PRIMARY KEY,
  name VARCHAR(100)
);
```

## 核心需求

### 1. 导出接口
- **URL**: `GET /api/experiments/export`
- **权限**: 需要管理员权限（ADMIN角色）
- **参数**:
  - `userId` (可选): 用户ID筛选
  - `algorithmId` (可选): 算法ID筛选  
  - `startTimeFrom` (可选): 开始时间筛选（>=）
  - `endTimeTo` (可选): 结束时间筛选（<=）
  - `status` (可选): 实验状态筛选
- **响应**: 直接返回Excel文件流
- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **文件名**: `experiment_records_YYYYMMDD_HHmmss.xlsx`

### 2. 导出字段
需要导出以下字段，使用中文列名：
1. 记录ID
2. 用户ID
3. 用户名
4. 算法ID
5. 算法名称
6. 网格大小
7. 访问节点数
8. 路径长度
9. 开始时间
10. 结束时间
11. 实验状态（中文：完成/失败/运行中）
12. 创建时间

### 3. 数据查询SQL
```sql
SELECT 
  er.record_id,
  er.user_id,
  u.username as user_name,
  er.algorithm_id,
  a.name as algorithm_name,
  er.grid_size,
  er.visited_count,
  er.path_length,
  er.start_time,
  er.end_time,
  CASE er.status
    WHEN 'completed' THEN '完成'
    WHEN 'failed' THEN '失败'
    WHEN 'running' THEN '运行中'
    ELSE er.status
  END as status,
  er.created_at
FROM experiment_records er
LEFT JOIN users u ON er.user_id = u.user_id
LEFT JOIN algorithms a ON er.algorithm_id = a.algorithm_id
WHERE 1=1
AND (:userId IS NULL OR er.user_id = :userId)
AND (:algorithmId IS NULL OR er.algorithm_id = :algorithmId)
AND (:startTimeFrom IS NULL OR er.start_time >= :startTimeFrom)
AND (:endTimeTo IS NULL OR er.end_time <= :endTimeTo)
AND (:status IS NULL OR er.status = :status)
ORDER BY er.created_at DESC
```

## 代码实现示例

### 1. 添加依赖（pom.xml）
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.2</version>
</dependency>
```

### 2. 导出VO类
```java
@Data
public class ExperimentRecordExportVO {
    @ExcelProperty("记录ID")
    private Long recordId;
    
    @ExcelProperty("用户ID")
    private Long userId;
    
    @ExcelProperty("用户名")
    private String userName;
    
    @ExcelProperty("算法ID")
    private Long algorithmId;
    
    @ExcelProperty("算法名称")
    private String algorithmName;
    
    @ExcelProperty("网格大小")
    private String gridSize;
    
    @ExcelProperty("访问节点数")
    private Integer visitedCount;
    
    @ExcelProperty("路径长度")
    private Integer pathLength;
    
    @ExcelProperty("开始时间")
    private String startTime;
    
    @ExcelProperty("结束时间")
    private String endTime;
    
    @ExcelProperty("实验状态")
    private String status;
    
    @ExcelProperty("创建时间")
    private String createdAt;
}
```

### 3. Controller层
```java
@RestController
@RequestMapping("/api/experiments")
public class ExperimentExportController {
    
    @Autowired
    private ExperimentService experimentService;
    
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportExperimentRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long algorithmId,
            @RequestParam(required = false) String startTimeFrom,
            @RequestParam(required = false) String endTimeTo,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {
        
        // 设置响应头
        String fileName = "experiment_records_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", 
            "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 查询数据
        List<ExperimentRecordExportVO> records = experimentService.getExportRecords(
            userId, algorithmId, startTimeFrom, endTimeTo, status);
        
        // 使用EasyExcel写入
        EasyExcel.write(response.getOutputStream(), ExperimentRecordExportVO.class)
            .sheet("实验记录")
            .doWrite(records);
    }
}
```

### 4. Service层
```java
@Service
public class ExperimentServiceImpl implements ExperimentService {
    
    @Autowired
    private ExperimentMapper experimentMapper;
    
    @Override
    public List<ExperimentRecordExportVO> getExportRecords(Long userId, Long algorithmId, 
            String startTimeFrom, String endTimeTo, String status) {
        
        // 构建查询条件
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("algorithmId", algorithmId);
        params.put("startTimeFrom", startTimeFrom);
        params.put("endTimeTo", endTimeTo);
        params.put("status", status);
        
        // 执行查询
        return experimentMapper.selectExportRecords(params);
    }
}
```

### 5. Mapper层
```java
@Mapper
public interface ExperimentMapper {
    
    List<ExperimentRecordExportVO> selectExportRecords(@Param("params") Map<String, Object> params);
}
```

```xml
<!-- mapper.xml -->
<select id="selectExportRecords" resultType="com.example.vo.ExperimentRecordExportVO">
    SELECT 
      er.record_id as recordId,
      er.user_id as userId,
      u.username as userName,
      er.algorithm_id as algorithmId,
      a.name as algorithmName,
      er.grid_size as gridSize,
      er.visited_count as visitedCount,
      er.path_length as pathLength,
      DATE_FORMAT(er.start_time, '%Y-%m-%d %H:%i:%s') as startTime,
      DATE_FORMAT(er.end_time, '%Y-%m-%d %H:%i:%s') as endTime,
      CASE er.status
        WHEN 'completed' THEN '完成'
        WHEN 'failed' THEN '失败'
        WHEN 'running' THEN '运行中'
        ELSE er.status
      END as status,
      DATE_FORMAT(er.created_at, '%Y-%m-%d %H:%i:%s') as createdAt
    FROM experiment_records er
    LEFT JOIN users u ON er.user_id = u.user_id
    LEFT JOIN algorithms a ON er.algorithm_id = a.algorithm_id
    WHERE 1=1
    <if test="params.userId != null">
      AND er.user_id = #{params.userId}
    </if>
    <if test="params.algorithmId != null">
      AND er.algorithm_id = #{params.algorithmId}
    </if>
    <if test="params.startTimeFrom != null">
      AND er.start_time >= #{params.startTimeFrom}
    </if>
    <if test="params.endTimeTo != null">
      AND er.end_time <= #{params.endTimeTo}
    </if>
    <if test="params.status != null">
      AND er.status = #{params.status}
    </if>
    ORDER BY er.created_at DESC
</select>
```

## 性能优化建议

### 1. 大数据量处理
```java
// 使用分页查询+流式写入，避免内存溢出
EasyExcel.write(response.getOutputStream(), ExperimentRecordExportVO.class)
    .sheet("实验记录")
    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
    .doWrite(() -> {
        // 使用数据流，分批查询
        int page = 1;
        int size = 1000;
        List<ExperimentRecordExportVO> data;
        do {
            data = experimentService.getExportRecordsByPage(params, page, size);
            page++;
            return data.iterator();
        } while (!data.isEmpty());
    });
```

### 2. 异步导出（可选）
如果需要支持大数据量异步导出：
1. 创建任务接口：`POST /api/experiments/export/task`
2. 查询任务状态：`GET /api/experiments/export/task/{taskId}`
3. 下载文件：`GET /api/experiments/export/download/{taskId}`

## 错误处理

### 1. 权限错误
- 返回401：未授权
- 返回403：权限不足

### 2. 参数错误
- 返回400：参数格式错误

### 3. 服务器错误
- 返回500：服务器内部错误
- 记录详细日志

## 测试要点

### 1. 功能测试
- 正常导出：测试各种筛选条件组合
- 空数据导出：测试无数据时的导出
- 权限测试：测试非管理员用户访问

### 2. 性能测试
- 小数据量导出（<1000条）
- 大数据量导出（>10000条）
- 并发导出测试

### 3. 兼容性测试
- 不同浏览器下载测试
- Excel文件打开测试（Office、WPS、Numbers等）

## 前端对接说明

### 1. 前端已实现
- 导出按钮点击事件
- 筛选参数传递
- 文件下载处理
- 错误提示

### 2. 前端代码位置
- API接口：`src/api/experiment.js`
- 页面组件：`src/views/admin/experiments/index.vue`

### 3. 请求示例
```javascript
// 前端请求示例
GET /api/experiments/export?userId=1&algorithmId=2&startTimeFrom=2024-01-01&endTimeTo=2024-12-31
```

## 部署注意事项

### 1. 依赖安装
确保添加EasyExcel依赖到pom.xml

### 2. 服务器配置
- 调整JVM内存参数，支持大数据量导出
- 设置请求超时时间

### 3. 安全配置
- 确保导出接口有权限控制
- 防止SQL注入

## 扩展功能建议

### 1. 短期扩展
- 导出进度提示
- 导出格式选择（CSV、PDF）
- 导出模板自定义

### 2. 长期扩展
- 定时导出任务
- 导出数据校验
- 多表关联导出

## 联系信息
如有疑问，请参考前端代码实现或联系前端开发人员。

---
**提示词结束** - 请根据实际项目结构调整代码实现
