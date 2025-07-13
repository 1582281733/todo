package com.todoapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todoapp.entity.Todo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项Mapper接口
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
} 