package com.ldk.backend.DTO;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 题目学习记录导出VO类
 * 用于Excel导出时的数据格式
 * 字段与前端problem-records/index页面表格一致
 */
@Data
public class ProblemCompletionExportVO {

    @ExcelProperty("记录ID")
    private Integer completionId;

    @ExcelProperty("题目标题")
    private String problemTitle;

    @ExcelProperty("算法类型")
    private String algorithmName;

    @ExcelProperty("难度")
    private String difficulty;

    @ExcelProperty("完成状态")
    private String completionStatus;

    @ExcelProperty("解题状态")
    private String solvedStatus;

    @ExcelProperty("尝试次数")
    private Integer attemptCount;

    @ExcelProperty("难度自评")
    private Byte difficultyRating;

    @ExcelProperty("最近复习")
    private String lastReviewDate;

    @ExcelProperty("学习笔记")
    private String notes;
}
