package com.todoapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todoapp.dto.SettingDTO;
import com.todoapp.entity.Setting;
import com.todoapp.entity.User;
import com.todoapp.exception.BusinessException;
import com.todoapp.mapper.SettingMapper;
import com.todoapp.mapper.UserMapper;
import com.todoapp.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统设置服务实现类
 */
@Service
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {

    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Setting getUserSetting(String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 查询用户设置
        QueryWrapper<Setting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        Setting setting = settingMapper.selectOne(queryWrapper);
        
        // 如果没有设置，创建默认设置
        if (setting == null) {
            setting = createDefaultSetting(user.getId());
        }
        
        return setting;
    }

    @Override
    public Setting updateUserSetting(SettingDTO settingDTO, String username) {
        // 获取用户设置
        Setting setting = getUserSetting(username);
        
        // 更新设置
        if (settingDTO.getDefaultRestTime() != null) {
            setting.setDefaultRestTime(settingDTO.getDefaultRestTime());
        }
        if (settingDTO.getTheme() != null) {
            setting.setTheme(settingDTO.getTheme());
        }
        if (settingDTO.getNotificationEnabled() != null) {
            setting.setNotificationEnabled(settingDTO.getNotificationEnabled());
        }
        if (settingDTO.getSoundEnabled() != null) {
            setting.setSoundEnabled(settingDTO.getSoundEnabled());
        }
        
        setting.setUpdatedAt(LocalDateTime.now());
        settingMapper.updateById(setting);
        
        return setting;
    }
    
    /**
     * 创建默认设置
     */
    private Setting createDefaultSetting(Long userId) {
        Setting setting = new Setting();
        setting.setUserId(userId);
        setting.setDefaultRestTime(5);
        setting.setTheme("light");
        setting.setNotificationEnabled(true);
        setting.setSoundEnabled(true);
        setting.setCreatedAt(LocalDateTime.now());
        
        settingMapper.insert(setting);
        return setting;
    }
    
    /**
     * 根据用户名获取用户
     */
    private User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }
} 