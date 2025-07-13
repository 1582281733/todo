package com.todoapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统设置实体类
 */
@Data
@TableName("setting")
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer defaultRestTime;

    private String theme;

    private Boolean notificationEnabled;

    private Boolean soundEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 