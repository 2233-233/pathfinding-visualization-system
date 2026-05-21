package com.ldk.backend.DTO;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 题目导出VO类
 * 用于Excel导出时的数据格式
 * 字段与前端admin/problems/index.vue页面表格一致
 */
@Data
public class ProblemExportVO {

    @ExcelProperty("ID")
    private Integer problemId;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("算法类型")
    private String algorithmName;

    @ExcelProperty("难度")
    private String difficulty;

    @ExcelProperty("描述")
    private String description;
}
