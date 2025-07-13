package com.todoapp.controller;

import com.todoapp.common.Result;
import com.todoapp.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/stats")
public class StatController {

    @Autowired
    private StatService statService;

    /**
     * 获取每日统计数据
     */
    @GetMapping("/daily")
    public Result<Map<String, Object>> getDailyStats(
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        // 如果没有提供日期参数，则使用当前日期
        if (date == null) {
            date = LocalDate.now();
        } else {
            // 确保日期参数正确，加一天修正时区问题
            date = date.plusDays(1);
        }
        System.out.println("后端使用的日期: " + date);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Map<String, Object> stats = statService.getDailyStats(date, username);
        return Result.success(stats);
    }

    /**
     * 获取每周统计数据
     */
    @GetMapping("/weekly")
    public Result<Map<String, Object>> getWeeklyStats(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        if (startDate == null) {
            // 默认为本周一
            LocalDate now = LocalDate.now();
            startDate = now.minusDays(now.getDayOfWeek().getValue() - 1);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Map<String, Object> stats = statService.getWeeklyStats(startDate, username);
        return Result.success(stats);
    }

    /**
     * 获取每月统计数据
     */
    @GetMapping("/monthly")
    public Result<Map<String, Object>> getMonthlyStats(
            @RequestParam(value = "month", required = false) String month) {
        if (month == null) {
            // 默认为当月
            LocalDate now = LocalDate.now();
            month = now.getYear() + "-" + String.format("%02d", now.getMonthValue());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Map<String, Object> stats = statService.getMonthlyStats(month, username);
        return Result.success(stats);
    }
} 