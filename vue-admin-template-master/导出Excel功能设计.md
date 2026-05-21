# 实验记录导出Excel功能设计

## 一、需求分析

目标：实现 http://localhost:9528/#/admin/experiments/index 页面的导出Excel表格按钮功能

### 1.1 当前状态
- 页面已有"导出Excel"按钮，但`handleExport()`方法仅显示提示消息
- 项目中没有Excel导出相关的库依赖
- 需要支持筛选条件下的数据导出

### 1.2 功能要求
1. 支持按当前筛选条件导出数据
2. 导出所有数据（不分页）
3. 导出文件格式为Excel（.xlsx）
4. 包含表格中的所有列数据
5. 支持中文列名
6. 文件自动下载

## 二、技术方案选择

### 方案A：前端导出（不推荐）
- 需要安装Excel导出库（如xlsx、file-saver）
- 浏览器处理大量数据可能性能问题
- 需要前端处理所有数据获取

### 方案B：后端导出（推荐）
- 后端生成Excel文件
- 前端仅负责触发下载
- 支持大数据量导出
- 符合前后端分离架构

**选择方案B：后端导出**

## 三、后端API设计

### 3.1 接口规范
基于现有项目API规范：
- 基础URL：http://localhost:8082
- 响应格式：
```json
{
  "code": 200,
  "data": {...},
  "message": "成功"
}
```

### 3.2 导出接口设计

#### 接口1：实验记录导出接口
- **方法**：GET
- **URL**：`/api/experiments/export`
- **请求头**：`Authorization: Bearer {token}`
- **查询参数**：
  - `userId` (可选)：用户ID筛选
  - `algorithmId` (可选)：算法ID筛选
  - `startTimeFrom` (可选)：开始时间筛选（>=）
  - `endTimeTo` (可选)：结束时间筛选（<=）
  - `status` (可选)：实验状态筛选
  - `exportType` (可选)：导出类型，默认'all'（全部数据）
- **响应**：
  - 直接返回Excel文件流
  - Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
  - Content-Disposition: `attachment; filename="experiment_records_YYYYMMDD_HHmmss.xlsx"`

#### 接口2：获取导出任务状态接口（可选，支持异步导出）
- **方法**：GET
- **URL**：`/api/experiments/export/task/{taskId}`
- **响应**：
```json
{
  "code": 200,
  "data": {
    "taskId": "task_123456",
    "status": "processing|completed|failed",
    "progress": 75,
    "downloadUrl": "/api/experiments/export/download/{taskId}",
    "fileName": "experiment_records_20240204_140330.xlsx",
    "createdAt": "2024-02-04 14:03:30",
    "completedAt": "2024-02-04 14:03:35"
  },
  "message": "导出任务状态"
}
```

## 四、后端实现提示词（给后端AI）

### 4.1 核心提示词

```
请实现实验记录导出Excel功能的后端接口，具体要求如下：

1. 技术栈：Spring Boot + MyBatis Plus + EasyExcel（推荐）或 Apache POI
2. 数据库表：experiment_records（实验记录表）
3. 导出字段：
   - 记录ID (record_id)
   - 用户ID (user_id)
   - 用户名 (user_name)
   - 算法ID (algorithm_id)
   - 算法名称 (algorithm_name)
   - 网格大小 (grid_size)
   - 访问节点数 (visited_count)
   - 路径长度 (path_length)
   - 开始时间 (start_time)
   - 结束时间 (end_time)
   - 实验状态 (status)：completed/failed/running
   - 创建时间 (created_at)

4. 功能要求：
   - 支持按筛选条件导出：用户ID、算法ID、时间范围、状态
   - 导出全部数据，不分页
   - 使用EasyExcel生成.xlsx格式文件
   - 文件自动下载，文件名包含时间戳：experiment_records_YYYYMMDD_HHmmss.xlsx
   - 支持中文列标题
   - 处理大数据量导出（使用分页查询+流式写入）

5. 接口路径：GET /api/experiments/export
6. 权限控制：需要管理员权限（@PreAuthorize("hasRole('ADMIN')")）
7. 响应：直接返回Excel文件流

8. 数据库查询示例SQL：
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
     er.status,
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

9. 使用EasyExcel的示例代码结构：
   @GetMapping("/export")
   @PreAuthorize("hasRole('ADMIN')")
   public void exportExperimentRecords(
       @RequestParam(required = false) Long userId,
       @RequestParam(required = false) Long algorithmId,
       @RequestParam(required = false) String startTimeFrom,
       @RequestParam(required = false) String endTimeTo,
       @RequestParam(required = false) String status,
       HttpServletResponse response) {
       
       // 设置响应头
       String fileName = "experiment_records_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
       response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
       response.setCharacterEncoding("UTF-8");
       response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
       
       // 查询数据
       List<ExperimentRecordExportVO> records = experimentService.getExportRecords(userId, algorithmId, startTimeFrom, endTimeTo, status);
       
       // 使用EasyExcel写入
       EasyExcel.write(response.getOutputStream(), ExperimentRecordExportVO.class)
           .sheet("实验记录")
           .doWrite(records);
   }

10. 实体类示例：
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

### 4.2 异步导出提示词（可选）

```
如果需要支持大数据量异步导出，请实现以下功能：

1. 创建导出任务接口：POST /api/experiments/export/task
   - 接收筛选参数
   - 创建后台导出任务
   - 返回任务ID

2. 查询任务状态接口：GET /api/experiments/export/task/{taskId}
   - 返回任务状态、进度、下载URL

3. 下载文件接口：GET /api/experiments/export/download/{taskId}
   - 返回生成的Excel文件

4. 使用Redis或数据库存储任务状态
5. 使用线程池或消息队列处理导出任务
```

## 五、前端实现方案

### 5.1 修改API接口文件

在 `src/api/experiment.js` 中添加导出接口：

```javascript
// 导出实验记录Excel
export function exportExperimentRecords(params) {
  return request({
    url: '/api/experiments/export',
    method: 'get',
    params,
    responseType: 'blob' // 重要：指定响应类型为blob
  })
}

// 添加到experimentApi对象中
export const experimentApi = {
  // ... 现有方法
  exportExperimentRecords(params) {
    return exportExperimentRecords(params)
  },
  // ...
}
```

### 5.2 修改前端页面组件

修改 `src/views/admin/experiments/index.vue` 中的 `handleExport` 方法：

```javascript
/**
 * 处理导出Excel
 */
async handleExport() {
  try {
    // 显示加载提示
    const loading = this.$loading({
      lock: true,
      text: '正在生成Excel文件，请稍候...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 准备导出参数（使用当前筛选条件）
    const exportParams = {}
    
    // 复制筛选条件
    if (this.filterForm.userId) {
      exportParams.userId = this.filterForm.userId
    }
    if (this.filterForm.algorithmId) {
      exportParams.algorithmId = this.filterForm.algorithmId
    }
    if (this.filterForm.status) {
      exportParams.status = this.filterForm.status
    }
    if (this.filterForm.startTime) {
      exportParams.startTimeFrom = this.filterForm.startTime
    }
    if (this.filterForm.endTime) {
      exportParams.endTimeTo = this.filterForm.endTime
    }
    
    // 调用导出API
    const response = await exportExperimentRecords(exportParams)
    
    // 创建Blob对象并下载
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    
    // 创建下载链接
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    
    // 从响应头获取文件名，或使用默认文件名
    const contentDisposition = response.headers['content-disposition']
    let fileName = 'experiment_records.xlsx'
    
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename\*?=(?:UTF-8'')?([^;]+)/i)
      if (fileNameMatch && fileNameMatch[1]) {
        fileName = decodeURIComponent(fileNameMatch[1])
      }
    }
    
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    // 释放URL对象
    window.URL.revokeObjectURL(downloadUrl)
    
    // 关闭加载提示
    loading.close()
    
    // 显示成功消息
    this.$message({
      message: 'Excel文件导出成功！',
      type: 'success',
      duration: 3000
    })
    
  } catch (error) {
    console.error('导出失败:', error)
    
    // 关闭加载提示
    if (loading) loading.close()
    
    // 显示错误消息
    this.$message({
      message: error.message || '导出失败，请重试',
      type: 'error',
      duration: 3000
    })
  }
}
```

### 5.3 添加导入语句

在 `src/views/admin/experiments/index.vue` 的script部分添加导入：

```javascript
// 导入API接口
import { getExperimentList, getExperimentSteps, exportExperimentRecords } from '@/api/experiment'
import { getUserList } from '@/api/user'
```

## 六、测试方案

### 6.1 后端测试
1. 单元测试：测试数据查询和Excel生成逻辑
2. 集成测试：测试完整导出流程
3. 性能测试：测试大数据量导出性能

### 6.2 前端测试
1. 功能测试：点击导出按钮，检查文件下载
2. 筛选测试：使用不同筛选条件导出
3. 错误处理：测试网络错误、权限错误等场景

## 七、部署注意事项

### 7.1 后端部署
1. 添加EasyExcel依赖：
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.2</version>
</dependency>
```

2. 配置服务器内存：大数据量导出需要足够内存
3. 设置超时时间：调整导出接口的超时时间

### 7.2 前端部署
1. 确保请求携带正确的token
2. 处理跨域问题（如果前后端分离部署）
3. 测试不同浏览器的兼容性

## 八、扩展功能建议

### 8.1 短期扩展
1. 导出进度提示
2. 导出历史记录
3. 多格式支持（CSV、PDF）

### 8.2 长期扩展
1. 定时导出任务
2. 导出模板自定义
3. 批量导出多个报表
4. 导出数据校验和清洗

## 九、风险与应对

### 9.1 技术风险
1. **大数据量内存溢出**：使用分页查询+流式写入
2. **网络超时**：设置合理的超时时间，支持断点续传
3. **文件损坏**：添加文件校验机制

### 9.2 业务风险
1. **数据安全**：确保导出权限控制
2. **数据一致性**：导出时加锁或使用快照
3. **性能影响**：避开业务高峰期执行导出

## 十、总结

本方案采用后端导出方式，具有以下优点：
1. 性能好，支持大数据量
2. 安全性高，权限控制在后端
3. 扩展性强，易于添加新功能
4. 兼容性好，不依赖前端Excel库

实现步骤：
1. 后端实现导出接口
2. 前端调用接口并处理文件下载
3. 测试验证功能完整性
4. 部署上线
