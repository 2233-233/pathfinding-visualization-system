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
@TableName("experimentStep")
public class ExperimentStep {
    @TableId(type = IdType.AUTO)
    private Integer stepId;

    private Integer recordId;

    private Integer stepIndex;

    private Integer nodeX;

    private Integer nodeY;

    private String nodeState;

    private String gridState;
}
