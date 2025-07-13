package com.todoapp.dto;

import lombok.Data;

/**
 * 系统设置DTO
 */
@Data
public class SettingDTO {

    private Integer defaultRestTime;

    private String theme;

    private Boolean notificationEnabled;

    private Boolean soundEnabled;
} 