package com.todoapp.service;

import com.todoapp.dto.SettingDTO;
import com.todoapp.entity.Setting;

/**
 * 系统设置服务接口
 */
public interface SettingService {

    /**
     * 获取用户系统设置
     * @param username 用户名
     * @return 系统设置
     */
    Setting getUserSetting(String username);

    /**
     * 更新用户系统设置
     * @param settingDTO 系统设置信息
     * @param username 用户名
     * @return 更新后的系统设置
     */
    Setting updateUserSetting(SettingDTO settingDTO, String username);
} 