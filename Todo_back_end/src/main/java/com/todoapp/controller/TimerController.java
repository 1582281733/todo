package com.todoapp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.todoapp.common.Result;
import com.todoapp.dto.TimerEndDTO;
import com.todoapp.dto.TimerStartDTO;
import com.todoapp.entity.Timer;
import com.todoapp.entity.Todo;
import com.todoapp.service.TimerService;
import com.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 计时器控制器
 */
@RestController
@RequestMapping("/timers")
public class TimerController {

    @Autowired
    private TimerService timerService;

    @Autowired
    private TodoService todoService;

    /**
     * 开始计时
     */
    @PostMapping("/start")
    public Result<Map<String, Object>> startTimer(@Validated @RequestBody TimerStartDTO timerStartDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Timer timer = timerService.startTimer(timerStartDTO, username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("timerId", timer.getId());
        result.put("todoId", timer.getTodoId());
        result.put("startTime", timer.getStartTime());
        result.put("expectedDuration", timer.getExpectedDuration());
        
        return Result.created(result);
    }

    /**
     * 结束计时
     */
    @PostMapping("/{timerId}/end")
    public Result<Map<String, Object>> endTimer(@PathVariable Long timerId, @Validated @RequestBody TimerEndDTO timerEndDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Timer timer = timerService.endTimer(timerId, timerEndDTO, username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("timerId", timer.getId());
        result.put("todoId", timer.getTodoId());
        result.put("startTime", timer.getStartTime());
        result.put("endTime", timer.getEndTime());
        result.put("actualDuration", timer.getActualDuration());
        result.put("completed", timer.getCompleted());
        
        return Result.success("计时结束", result);
    }

    /**
     * 获取计时记录列表
     */
    @GetMapping
    public Result<Map<String, Object>> getTimerList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "todoId", required = false) Long todoId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Page<Timer> timerPage = timerService.getTimerList(page, pageSize, todoId, startDate, endDate, username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", timerPage.getTotal());
        result.put("page", timerPage.getCurrent());
        result.put("pageSize", timerPage.getSize());
        result.put("list", timerPage.getRecords());
        
        return Result.success(result);
    }
} 