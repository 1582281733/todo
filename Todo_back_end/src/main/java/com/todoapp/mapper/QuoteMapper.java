package com.todoapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todoapp.entity.Quote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 名言警句Mapper接口
 */
@Mapper
public interface QuoteMapper extends BaseMapper<Quote> {
} 