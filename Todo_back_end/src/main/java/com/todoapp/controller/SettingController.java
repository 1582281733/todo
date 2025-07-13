package com.todoapp.controller;

import com.todoapp.common.Result;
import com.todoapp.dto.SettingDTO;
import com.todoapp.entity.Setting;
import com.todoapp.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    /**
     * 获取用户系统设置
     */
    @GetMapping
    public Result<Setting> getUserSetting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Setting setting = settingService.getUserSetting(username);
        return Result.success(setting);
    }

    /**
     * 更新用户系统设置
     */
    @PutMapping
    public Result<Setting> updateUserSetting(@RequestBody SettingDTO settingDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Setting setting = settingService.updateUserSetting(settingDTO, username);
        return Result.success("更新成功", setting);
    }
} 