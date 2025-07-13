package com.todoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册DTO
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String email;
} 