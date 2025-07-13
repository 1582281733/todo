package com.todoapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todoapp.dto.TodoDTO;
import com.todoapp.entity.Todo;
import com.todoapp.entity.User;
import com.todoapp.exception.BusinessException;
import com.todoapp.mapper.TodoMapper;
import com.todoapp.mapper.UserMapper;
import com.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 待办事项服务实现类
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Todo createTodo(TodoDTO todoDTO, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);

        // 创建待办事项
        Todo todo = new Todo();
        todo.setUserId(user.getId());
        todo.setName(todoDTO.getName());
        todo.setType(todoDTO.getType());
        todo.setTimerMode(todoDTO.getTimerMode());
        todo.setDuration(todoDTO.getDuration());
        todo.setBackgroundColor(todoDTO.getBackgroundColor());
        todo.setDescription(todoDTO.getDescription());
        todo.setStatus("uncompleted");
        todo.setCreatedAt(LocalDateTime.now());
        
        todoMapper.insert(todo);
        return todo;
    }

    @Override
    public Page<Todo> getTodoList(int page, int pageSize, String status, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 构建查询条件
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        if (!"all".equals(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("created_at");
        
        // 分页查询
        Page<Todo> pageParam = new Page<>(page, pageSize);
        return todoMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public Todo getTodoDetail(Long id, String username) {
        // 获取用户ID
        User user = getUserByUsername(username);
        
        // 查询待办事项
        Todo todo = todoMapper.selectById(id);
        if (todo == null) {
            throw new BusinessException(404, "待办事项不存在");
        }
        
        // 验证所有权
        if (!todo.getUserId().equals(user.getId())) {
            throw new BusinessException(403, "权限不足");
        }
        
        return todo;
    }

    @Override
    public Todo updateTodo(Long id, TodoDTO todoDTO, String username) {
        // 获取待办事项
        Todo todo = getTodoDetail(id, username);
        
        // 更新待办事项
        if (todoDTO.getName() != null) {
            todo.setName(todoDTO.getName());
        }
        if (todoDTO.getType() != null) {
            todo.setType(todoDTO.getType());
        }
        if (todoDTO.getTimerMode() != null) {
            todo.setTimerMode(todoDTO.getTimerMode());
        }
        if (todoDTO.getDuration() != null) {
            todo.setDuration(todoDTO.getDuration());
        }
        if (todoDTO.getBackgroundColor() != null) {
            todo.setBackgroundColor(todoDTO.getBackgroundColor());
        }
        if (todoDTO.getDescription() != null) {
            todo.setDescription(todoDTO.getDescription());
        }
        todo.setUpdatedAt(LocalDateTime.now());
        
        todoMapper.updateById(todo);
        return todo;
    }

    @Override
    public void deleteTodo(Long id, String username) {
        // 获取待办事项
        Todo todo = getTodoDetail(id, username);
        
        // 删除待办事项
        todoMapper.deleteById(id);
    }

    @Override
    public Todo completeTodo(Long id, String username) {
        // 获取待办事项
        Todo todo = getTodoDetail(id, username);
        
        // 完成待办事项
        todo.setStatus("completed");
        todo.setCompletedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        
        todoMapper.updateById(todo);
        return todo;
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