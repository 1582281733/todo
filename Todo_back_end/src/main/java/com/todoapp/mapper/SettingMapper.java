package com.todoapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todoapp.entity.Setting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统设置Mapper接口
 */
@Mapper
public interface SettingMapper extends BaseMapper<Setting> {
} 