package com.todoapp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.todoapp.common.Result;
import com.todoapp.dto.TodoDTO;
import com.todoapp.entity.Todo;
import com.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 待办事项控制器
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * 创建待办事项
     */
    @PostMapping
    public Result<Todo> createTodo(@Validated @RequestBody TodoDTO todoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Todo todo = todoService.createTodo(todoDTO, username);
        return Result.created(todo);
    }

    /**
     * 获取待办事项列表
     */
    @GetMapping
    public Result<Map<String, Object>> getTodoList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "status", defaultValue = "all") String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Page<Todo> todoPage = todoService.getTodoList(page, pageSize, status, username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", todoPage.getTotal());
        result.put("page", todoPage.getCurrent());
        result.put("pageSize", todoPage.getSize());
        result.put("list", todoPage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取待办事项详情
     */
    @GetMapping("/{id}")
    public Result<Todo> getTodoDetail(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Todo todo = todoService.getTodoDetail(id, username);
        return Result.success(todo);
    }

    /**
     * 更新待办事项
     */
    @PutMapping("/{id}")
    public Result<Todo> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Todo todo = todoService.updateTodo(id, todoDTO, username);
        return Result.success("更新成功", todo);
    }

    /**
     * 删除待办事项
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTodo(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        todoService.deleteTodo(id, username);
        return Result.success("删除成功");
    }

    /**
     * 完成待办事项
     */
    @PostMapping("/{id}/complete")
    public Result<Map<String, Object>> completeTodo(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Todo todo = todoService.completeTodo(id, username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", todo.getId());
        result.put("status", todo.getStatus());
        result.put("completedAt", todo.getCompletedAt());
        
        return Result.success("操作成功", result);
    }
} 