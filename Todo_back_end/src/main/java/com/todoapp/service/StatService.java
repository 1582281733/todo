package com.todoapp.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatService {

    /**
     * 获取每日统计数据
     * @param date 日期
     * @param username 用户名
     * @return 统计数据
     */
    Map<String, Object> getDailyStats(LocalDate date, String username);

    /**
     * 获取每周统计数据
     * @param startDate 周起始日期
     * @param username 用户名
     * @return 统计数据
     */
    Map<String, Object> getWeeklyStats(LocalDate startDate, String username);

    /**
     * 获取每月统计数据
     * @param month 月份
     * @param username 用户名
     * @return 统计数据
     */
    Map<String, Object> getMonthlyStats(String month, String username);
} 