package com.ldk.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("problem")
public class Problem {
    @TableId(type = IdType.AUTO)
    private Integer problemId;

    private String title;

    private String description;

    private String difficulty;

    // 算法ID，关联Algorithm表
    private Integer algorithmId;

    private String leetcodeLink;
}
