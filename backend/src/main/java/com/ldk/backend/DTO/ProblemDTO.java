package com.ldk.backend.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 题目数据传输对象
 * 包含算法名称（而不是算法ID）
 */
@Data
@Accessors(chain = true)
public class ProblemDTO {
    private Integer problemId;
    private String title;
    private String description;
    private String difficulty;
    private String algorithmName;  // 算法名称（前端需要）
    private String leetcodeLink;
}