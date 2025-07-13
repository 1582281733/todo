package com.todoapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 计时记录实体类
 */
@Data
@TableName("timer")
public class Timer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long todoId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer expectedDuration;

    private Integer actualDuration;

    private Boolean completed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 