package com.todoapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todoapp.entity.Quote;
import com.todoapp.mapper.QuoteMapper;
import com.todoapp.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * 名言警句服务实现类
 */
@Service
public class QuoteServiceImpl extends ServiceImpl<QuoteMapper, Quote> implements QuoteService {

    @Autowired
    private QuoteMapper quoteMapper;
    
    private final Random random = new Random();

    @Override
    public Quote getRandomQuote() {
        // 获取所有名言警句
        List<Quote> quotes = quoteMapper.selectList(new QueryWrapper<>());
        
        if (quotes.isEmpty()) {
            // 如果没有名言警句，返回默认的
            Quote defaultQuote = new Quote();
            defaultQuote.setContent("行动是治愈恐惧的良药，而犹豫、拖延将不断滋养恐惧。");
            defaultQuote.setAuthor("罗宾·夏玛");
            return defaultQuote;
        }
        
        // 随机选择一条
        int index = random.nextInt(quotes.size());
        return quotes.get(index);
    }
} 