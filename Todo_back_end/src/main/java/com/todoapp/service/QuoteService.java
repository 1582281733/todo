package com.todoapp.service;

import com.todoapp.entity.Quote;

/**
 * 名言警句服务接口
 */
public interface QuoteService {

    /**
     * 获取随机名言警句
     * @return 名言警句
     */
    Quote getRandomQuote();
} 