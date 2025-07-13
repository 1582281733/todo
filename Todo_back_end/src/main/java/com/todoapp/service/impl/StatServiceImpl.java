package com.todoapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.todoapp.entity.Timer;
import com.todoapp.entity.Todo;
import com.todoapp.entity.User;
import com.todoapp.exception.BusinessException;
import com.todoapp.mapper.TimerMapper;
import com.todoapp.mapper.TodoMapper;
import com.todoapp.mapper.UserMapper;
import com.todoapp.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private TimerMapper timerMapper;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getDailyStats(LocalDate date, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 查询当天的计时记录
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        QueryWrapper<Timer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                .ge("start_time", startOfDay)
                .lt("start_time", endOfDay)
                .isNotNull("end_time");
        List<Timer> timers = timerMapper.selectList(queryWrapper);
        
        // 计算总时长和完成率
        int totalTime = timers.stream().mapToInt(Timer::getActualDuration).sum();
        long completedCount = timers.stream().filter(Timer::getCompleted).count();
        double completionRate = timers.isEmpty() ? 0 : (double) completedCount / timers.size() * 100;
        
        // 按小时分布统计
        Map<Integer, Integer> hourlyMap = new HashMap<>();
        for (Timer timer : timers) {
            int hour = timer.getStartTime().getHour();
            hourlyMap.put(hour, hourlyMap.getOrDefault(hour, 0) + timer.getActualDuration());
        }
        
        List<Map<String, Object>> hourlyDistribution = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : hourlyMap.entrySet()) {
            Map<String, Object> hourData = new HashMap<>();
            hourData.put("hour", entry.getKey());
            hourData.put("duration", entry.getValue());
            hourlyDistribution.add(hourData);
        }
        
        // 任务列表
        List<Map<String, Object>> taskList = new ArrayList<>();
        Map<Long, Todo> todoMap = getTodoMap(timers);
        
        for (Timer timer : timers) {
            Todo todo = todoMap.get(timer.getTodoId());
            if (todo != null) {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("id", todo.getId());
                taskData.put("name", todo.getName());
                taskData.put("duration", timer.getActualDuration());
                taskData.put("timeRange", formatTimeRange(timer.getStartTime(), timer.getEndTime()));
                taskData.put("backgroundColor", todo.getBackgroundColor());
                taskList.add(taskData);
            }
        }
        
        // 组装结果
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTime", totalTime);
        overview.put("totalTasks", timers.size());
        overview.put("completionRate", (int) completionRate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("overview", overview);
        result.put("hourlyDistribution", hourlyDistribution);
        result.put("taskList", taskList);
        
        return result;
    }

    @Override
    public Map<String, Object> getWeeklyStats(LocalDate startDate, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 计算周结束日期
        LocalDate endDate = startDate.plusDays(6);
        
        // 查询本周的计时记录
        LocalDateTime startOfWeek = startDate.atStartOfDay();
        LocalDateTime endOfWeek = endDate.plusDays(1).atStartOfDay();
        
        QueryWrapper<Timer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                .ge("start_time", startOfWeek)
                .lt("start_time", endOfWeek)
                .isNotNull("end_time");
        List<Timer> timers = timerMapper.selectList(queryWrapper);
        
        // 计算总时长和完成率
        int totalTime = timers.stream().mapToInt(Timer::getActualDuration).sum();
        long completedCount = timers.stream().filter(Timer::getCompleted).count();
        double completionRate = timers.isEmpty() ? 0 : (double) completedCount / timers.size() * 100;
        
        // 按天分布统计
        Map<Integer, Integer> dailyMap = new HashMap<>();
        for (Timer timer : timers) {
            int dayOfWeek = timer.getStartTime().getDayOfWeek().getValue(); // 1-7表示周一到周日
            dailyMap.put(dayOfWeek, dailyMap.getOrDefault(dayOfWeek, 0) + timer.getActualDuration());
        }
        
        List<Map<String, Object>> dailyDistribution = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", i);
            dayData.put("duration", dailyMap.getOrDefault(i, 0));
            dailyDistribution.add(dayData);
        }
        
        // 任务列表
        List<Map<String, Object>> taskList = new ArrayList<>();
        Map<Long, Todo> todoMap = getTodoMap(timers);
        
        // 按待办事项ID分组计时记录
        Map<Long, List<Timer>> todoTimersMap = timers.stream()
                .collect(Collectors.groupingBy(Timer::getTodoId));
        
        for (Map.Entry<Long, List<Timer>> entry : todoTimersMap.entrySet()) {
            Long todoId = entry.getKey();
            List<Timer> todoTimers = entry.getValue();
            Todo todo = todoMap.get(todoId);
            
            if (todo != null) {
                int totalDuration = todoTimers.stream().mapToInt(Timer::getActualDuration).sum();
                
                // 统计出现在哪些天
                Set<Integer> days = todoTimers.stream()
                        .map(timer -> timer.getStartTime().getDayOfWeek().getValue())
                        .collect(Collectors.toSet());
                
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("id", todo.getId());
                taskData.put("name", todo.getName());
                taskData.put("duration", totalDuration);
                taskData.put("days", formatDays(days));
                taskData.put("backgroundColor", todo.getBackgroundColor());
                taskList.add(taskData);
            }
        }
        
        // 组装结果
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTime", totalTime);
        overview.put("totalTasks", todoTimersMap.size());
        overview.put("completionRate", (int) completionRate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("overview", overview);
        result.put("dailyDistribution", dailyDistribution);
        result.put("taskList", taskList);
        
        return result;
    }

    @Override
    public Map<String, Object> getMonthlyStats(String month, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 解析月份
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(month, formatter);
        
        // 计算月份的开始和结束日期
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // 查询本月的计时记录
        LocalDateTime startOfMonth = startDate.atStartOfDay();
        LocalDateTime endOfMonth = endDate.plusDays(1).atStartOfDay();
        
        QueryWrapper<Timer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                .ge("start_time", startOfMonth)
                .lt("start_time", endOfMonth)
                .isNotNull("end_time");
        List<Timer> timers = timerMapper.selectList(queryWrapper);
        
        // 计算总时长和完成率
        int totalTime = timers.stream().mapToInt(Timer::getActualDuration).sum();
        long completedCount = timers.stream().filter(Timer::getCompleted).count();
        double completionRate = timers.isEmpty() ? 0 : (double) completedCount / timers.size() * 100;
        
        // 按周分布统计
        Map<Integer, Integer> weeklyMap = new HashMap<>();
        for (Timer timer : timers) {
            LocalDate date = timer.getStartTime().toLocalDate();
            int weekOfMonth = getWeekOfMonth(date);
            weeklyMap.put(weekOfMonth, weeklyMap.getOrDefault(weekOfMonth, 0) + timer.getActualDuration());
        }
        
        List<Map<String, Object>> weeklyDistribution = new ArrayList<>();
        for (int i = 1; i <= 5; i++) { // 一个月最多5周
            Map<String, Object> weekData = new HashMap<>();
            weekData.put("week", i);
            weekData.put("duration", weeklyMap.getOrDefault(i, 0));
            weeklyDistribution.add(weekData);
        }
        
        // 任务列表
        List<Map<String, Object>> taskList = new ArrayList<>();
        Map<Long, Todo> todoMap = getTodoMap(timers);
        
        // 按待办事项ID分组计时记录
        Map<Long, List<Timer>> todoTimersMap = timers.stream()
                .collect(Collectors.groupingBy(Timer::getTodoId));
        
        for (Map.Entry<Long, List<Timer>> entry : todoTimersMap.entrySet()) {
            Long todoId = entry.getKey();
            List<Timer> todoTimers = entry.getValue();
            Todo todo = todoMap.get(todoId);
            
            if (todo != null) {
                int totalDuration = todoTimers.stream().mapToInt(Timer::getActualDuration).sum();
                
                // 统计出现在哪些周
                Set<Integer> weeks = todoTimers.stream()
                        .map(timer -> getWeekOfMonth(timer.getStartTime().toLocalDate()))
                        .collect(Collectors.toSet());
                
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("id", todo.getId());
                taskData.put("name", todo.getName());
                taskData.put("duration", totalDuration);
                taskData.put("period", formatPeriod(weeks));
                taskData.put("backgroundColor", todo.getBackgroundColor());
                taskList.add(taskData);
            }
        }
        
        // 组装结果
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTime", totalTime);
        overview.put("totalTasks", todoTimersMap.size());
        overview.put("completionRate", (int) completionRate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("overview", overview);
        result.put("weeklyDistribution", weeklyDistribution);
        result.put("taskList", taskList);
        
        return result;
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
    
    /**
     * 获取待办事项映射
     */
    private Map<Long, Todo> getTodoMap(List<Timer> timers) {
        if (timers.isEmpty()) {
            return Collections.emptyMap();
        }
        
        // 提取所有待办事项ID
        Set<Long> todoIds = timers.stream()
                .map(Timer::getTodoId)
                .collect(Collectors.toSet());
        
        // 批量查询待办事项
        QueryWrapper<Todo> todoQueryWrapper = new QueryWrapper<>();
        todoQueryWrapper.in("id", todoIds);
        List<Todo> todos = todoMapper.selectList(todoQueryWrapper);
        
        // 转换为映射
        return todos.stream().collect(Collectors.toMap(Todo::getId, todo -> todo));
    }
    
    /**
     * 格式化时间范围
     */
    private String formatTimeRange(LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return start.format(formatter) + "-" + end.format(formatter);
    }
    
    /**
     * 格式化天数
     */
    private String formatDays(Set<Integer> days) {
        if (days.size() == 7) {
            return "全周";
        }
        
        List<String> dayNames = Arrays.asList("", "周一", "周二", "周三", "周四", "周五", "周六", "周日");
        return days.stream()
                .sorted()
                .map(dayNames::get)
                .collect(Collectors.joining("、"));
    }
    
    /**
     * 格式化周期
     */
    private String formatPeriod(Set<Integer> weeks) {
        if (weeks.size() == 5 || (weeks.size() >= 4 && weeks.contains(1) && weeks.contains(5))) {
            return "全月";
        }
        
        List<Integer> sortedWeeks = new ArrayList<>(weeks);
        Collections.sort(sortedWeeks);
        
        if (sortedWeeks.size() == 1) {
            return "第" + sortedWeeks.get(0) + "周";
        }
        
        // 检查是否连续
        boolean isConsecutive = true;
        for (int i = 1; i < sortedWeeks.size(); i++) {
            if (sortedWeeks.get(i) != sortedWeeks.get(i-1) + 1) {
                isConsecutive = false;
                break;
            }
        }
        
        if (isConsecutive) {
            return "第" + sortedWeeks.get(0) + "-" + sortedWeeks.get(sortedWeeks.size() - 1) + "周";
        } else {
            return "第" + sortedWeeks.stream().map(String::valueOf).collect(Collectors.joining("、")) + "周";
        }
    }
    
    /**
     * 获取日期在月中的第几周
     */
    private int getWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        int offset = firstDayOfMonth.getDayOfWeek().getValue() - 1; // 调整为周一为一周的开始
        int dayOfMonth = date.getDayOfMonth() - 1; // 减1是因为日期从1开始
        return (dayOfMonth + offset) / 7 + 1;
    }
} 