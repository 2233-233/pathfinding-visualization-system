package com.ldk.backend.DTO;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实验记录导出VO类
 * 用于Excel导出时的数据格式
 */
@Data
public class ExperimentRecordExportVO {
    
    @ExcelProperty("记录ID")
    private Integer recordId;
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("算法名称")
    private String algorithmName;
    
    @ExcelProperty("开始时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @ExcelProperty("结束时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    @ExcelProperty("路径长度")
    private Integer pathLength;
    
    @ExcelProperty("访问节点数")
    private Integer visitedCount;
    
    @ExcelProperty("实验状态")
    private String status;
}
