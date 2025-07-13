package com.todoapp.controller;

import com.todoapp.common.Result;
import com.todoapp.entity.User;
import com.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    public Result<User> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserProfile(username);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<User> updateUserProfile(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User updatedUser = userService.updateUserProfile(username, user);
        return Result.success("更新成功", updatedUser);
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.badRequest("文件不能为空");
        }

        try {
            // 生成文件名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // 保存文件
            String uploadDir = "uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 文件URL
            String avatarUrl = "/uploads/avatars/" + fileName;
            
            // 更新用户头像
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            userService.uploadAvatar(username, avatarUrl);
            
            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);
            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.serverError("文件上传失败: " + e.getMessage());
        }
    }
} 