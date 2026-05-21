package com.ldk.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.Accessors;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("problemcompletion")
public class ProblemCompletion {

    @TableId(type = IdType.AUTO)
    private Integer completionId;

    @TableField("user_id")
    private Integer userId;

    @TableField("problem_id")
    private Integer problemId;

    @TableField("completion_status")
    private String completionStatus = "not_started";

    @TableField("attempt_count")
    private Integer attemptCount = 0;

    @TableField("is_solved")
    private Boolean isSolved = false;

    private String notes;

    @TableField("last_review_date")
    private LocalDate lastReviewDate;

    @TableField("difficulty_rating")
    private Byte difficultyRating;

    @TableField("time_spent_minutes")
    private Integer timeSpentMinutes = 0;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}