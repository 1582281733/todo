package com.todoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 结束计时DTO
 */
@Data
public class TimerEndDTO {

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
} 