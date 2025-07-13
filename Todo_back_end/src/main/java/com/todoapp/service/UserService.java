package com.todoapp.service;

import com.todoapp.dto.LoginDTO;
import com.todoapp.dto.RegisterDTO;
import com.todoapp.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    Map<String, Object> register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    Map<String, Object> login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getUserProfile(String username);

    /**
     * 更新用户信息
     * @param username 用户名
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUserProfile(String username, User user);

    /**
     * 上传头像
     * @param username 用户名
     * @param avatarUrl 头像URL
     * @return 头像URL
     */
    String uploadAvatar(String username, String avatarUrl);
} 