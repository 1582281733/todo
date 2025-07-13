package com.todoapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todoapp.dto.TimerEndDTO;
import com.todoapp.dto.TimerStartDTO;
import com.todoapp.entity.Timer;
import com.todoapp.entity.Todo;
import com.todoapp.entity.User;
import com.todoapp.exception.BusinessException;
import com.todoapp.mapper.TimerMapper;
import com.todoapp.mapper.TodoMapper;
import com.todoapp.mapper.UserMapper;
import com.todoapp.service.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 计时器服务实现类
 */
@Service
public class TimerServiceImpl extends ServiceImpl<TimerMapper, Timer> implements TimerService {

    @Autowired
    private TimerMapper timerMapper;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Timer startTimer(TimerStartDTO timerStartDTO, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 获取待办事项
        Todo todo = todoMapper.selectById(timerStartDTO.getTodoId());
        if (todo == null) {
            throw new BusinessException(404, "待办事项不存在");
        }
        
        // 验证所有权
        if (!todo.getUserId().equals(user.getId())) {
            throw new BusinessException(403, "权限不足");
        }
        
        // 创建计时记录
        Timer timer = new Timer();
        timer.setUserId(user.getId());
        timer.setTodoId(timerStartDTO.getTodoId());
        timer.setStartTime(timerStartDTO.getStartTime());
        timer.setExpectedDuration(todo.getDuration());
        timer.setCreatedAt(LocalDateTime.now());
        
        timerMapper.insert(timer);
        return timer;
    }

    @Override
    public Timer endTimer(Long timerId, TimerEndDTO timerEndDTO, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 获取计时记录
        Timer timer = timerMapper.selectById(timerId);
        if (timer == null) {
            throw new BusinessException(404, "计时记录不存在");
        }
        
        // 验证所有权
        if (!timer.getUserId().equals(user.getId())) {
            throw new BusinessException(403, "权限不足");
        }
        
        // 更新计时记录
        timer.setEndTime(timerEndDTO.getEndTime());
        
        // 计算实际时长（分钟）
        long minutes = Duration.between(timer.getStartTime(), timer.getEndTime()).toMinutes();
        timer.setActualDuration((int) minutes);
        
        // 判断是否完成了预期时长
        timer.setCompleted(timer.getActualDuration() >= timer.getExpectedDuration());
        
        timer.setUpdatedAt(LocalDateTime.now());
        timerMapper.updateById(timer);
        
        return timer;
    }

    @Override
    public Page<Timer> getTimerList(int page, int pageSize, Long todoId, LocalDate startDate, LocalDate endDate, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 构建查询条件
        QueryWrapper<Timer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        
        if (todoId != null) {
            queryWrapper.eq("todo_id", todoId);
        }
        
        if (startDate != null) {
            queryWrapper.ge("start_time", startDate.atStartOfDay());
        }
        
        if (endDate != null) {
            queryWrapper.le("start_time", endDate.plusDays(1).atStartOfDay());
        }
        
        queryWrapper.orderByDesc("start_time");
        
        // 分页查询
        Page<Timer> pageParam = new Page<>(page, pageSize);
        return timerMapper.selectPage(pageParam, queryWrapper);
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