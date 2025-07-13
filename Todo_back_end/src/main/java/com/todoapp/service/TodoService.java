package com.todoapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.todoapp.dto.TodoDTO;
import com.todoapp.entity.Todo;

/**
 * 待办事项服务接口
 */
public interface TodoService {

    /**
     * 创建待办事项
     * @param todoDTO 待办事项信息
     * @param username 用户名
     * @return 创建的待办事项
     */
    Todo createTodo(TodoDTO todoDTO, String username);

    /**
     * 获取待办事项列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param status 状态筛选
     * @param username 用户名
     * @return 待办事项分页列表
     */
    Page<Todo> getTodoList(int page, int pageSize, String status, String username);

    /**
     * 获取待办事项详情
     * @param id 待办事项ID
     * @param username 用户名
     * @return 待办事项详情
     */
    Todo getTodoDetail(Long id, String username);

    /**
     * 更新待办事项
     * @param id 待办事项ID
     * @param todoDTO 待办事项信息
     * @param username 用户名
     * @return 更新后的待办事项
     */
    Todo updateTodo(Long id, TodoDTO todoDTO, String username);

    /**
     * 删除待办事项
     * @param id 待办事项ID
     * @param username 用户名
     */
    void deleteTodo(Long id, String username);

    /**
     * 完成待办事项
     * @param id 待办事项ID
     * @param username 用户名
     * @return 完成的待办事项
     */
    Todo completeTodo(Long id, String username);
} 