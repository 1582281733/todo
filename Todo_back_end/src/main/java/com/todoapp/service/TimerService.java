package com.todoapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.todoapp.dto.TimerEndDTO;
import com.todoapp.dto.TimerStartDTO;
import com.todoapp.entity.Timer;

import java.time.LocalDate;

/**
 * 计时器服务接口
 */
public interface TimerService {

    /**
     * 开始计时
     * @param timerStartDTO 开始计时信息
     * @param username 用户名
     * @return 计时会话
     */
    Timer startTimer(TimerStartDTO timerStartDTO, String username);

    /**
     * 结束计时
     * @param timerId 计时ID
     * @param timerEndDTO 结束计时信息
     * @param username 用户名
     * @return 计时会话
     */
    Timer endTimer(Long timerId, TimerEndDTO timerEndDTO, String username);

    /**
     * 获取计时记录列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param todoId 待办事项ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param username 用户名
     * @return 计时记录分页列表
     */
    Page<Timer> getTimerList(int page, int pageSize, Long todoId, LocalDate startDate, LocalDate endDate, String username);
} 