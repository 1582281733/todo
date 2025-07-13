package com.todoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 待办事项DTO
 */
@Data
public class TodoDTO {

    @NotBlank(message = "待办名称不能为空")
    private String name;

    @NotBlank(message = "待办类型不能为空")
    private String type;

    @NotBlank(message = "计时方式不能为空")
    private String timerMode;

    @NotNull(message = "时长不能为空")
    private Integer duration;

    private String backgroundColor;

    private String description;
} 