package com.ldk.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("experimentrecord")
public class ExperimentRecord {
    @TableId(type = IdType.AUTO)
    private Integer recordId;

    private Integer userId;

    private Integer problemId;

    // 数据库中是algorithm_id，不是algorithm_name
    private Integer algorithmId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime endTime;

    private Integer pathLength;

    private Integer visitedCount;

    private String status;
}
