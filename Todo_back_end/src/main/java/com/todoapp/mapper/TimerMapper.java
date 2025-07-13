package com.todoapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todoapp.entity.Timer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计时记录Mapper接口
 */
@Mapper
public interface TimerMapper extends BaseMapper<Timer> {
} 