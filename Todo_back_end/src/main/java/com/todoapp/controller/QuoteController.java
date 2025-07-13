package com.todoapp.controller;

import com.todoapp.common.Result;
import com.todoapp.entity.Quote;
import com.todoapp.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 名言警句控制器
 */
@RestController
@RequestMapping("/quotes")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    /**
     * 获取随机名言警句
     */
    @GetMapping("/random")
    public Result<Quote> getRandomQuote() {
        Quote quote = quoteService.getRandomQuote();
        return Result.success(quote);
    }
} 