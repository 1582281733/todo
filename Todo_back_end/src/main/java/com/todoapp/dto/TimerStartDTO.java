package com.todoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 开始计时DTO
 */
@Data
public class TimerStartDTO {

    @NotNull(message = "待办事项ID不能为空")
    private Long todoId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
} 