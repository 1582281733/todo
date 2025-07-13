# 待办APP后端项目

这是一个基于Spring Boot、Spring Security和MyBatis-Plus的待办事项应用后端项目。

## 技术栈

- Spring Boot 2.7.5
- Spring Security
- MyBatis-Plus 3.5.2
- MySQL
- JWT认证

## 功能特性

- 用户注册与登录
- 待办事项管理（创建、查询、更新、删除、完成）
- 计时功能（番茄钟、普通计时）
- 数据统计（每日、每周、每月）
- 系统设置
- 随机鸡汤语录

## 项目结构

```
src/main/java/com/todoapp/
├── TodoApplication.java              # 应用程序入口
├── common/                           # 公共类
│   └── Result.java                   # 统一响应结果类
├── config/                           # 配置类
│   └── SecurityConfig.java           # Spring Security配置
├── controller/                       # 控制器
│   ├── AuthController.java           # 认证控制器
│   ├── QuoteController.java          # 名言警句控制器
│   ├── SettingController.java        # 系统设置控制器
│   ├── StatController.java           # 统计控制器
│   ├── TimerController.java          # 计时控制器
│   ├── TodoController.java           # 待办事项控制器
│   └── UserController.java           # 用户控制器
├── dto/                              # 数据传输对象
│   ├── LoginDTO.java                 # 登录DTO
│   ├── RegisterDTO.java              # 注册DTO
│   ├── SettingDTO.java               # 系统设置DTO
│   ├── TimerEndDTO.java              # 结束计时DTO
│   ├── TimerStartDTO.java            # 开始计时DTO
│   └── TodoDTO.java                  # 待办事项DTO
├── entity/                           # 实体类
│   ├── Quote.java                    # 名言警句实体
│   ├── Setting.java                  # 系统设置实体
│   ├── Timer.java                    # 计时记录实体
│   ├── Todo.java                     # 待办事项实体
│   └── User.java                     # 用户实体
├── exception/                        # 异常处理
│   ├── BusinessException.java        # 业务异常类
│   └── GlobalExceptionHandler.java   # 全局异常处理器
├── mapper/                           # Mapper接口
│   ├── QuoteMapper.java              # 名言警句Mapper
│   ├── SettingMapper.java            # 系统设置Mapper
│   ├── TimerMapper.java              # 计时记录Mapper
│   ├── TodoMapper.java               # 待办事项Mapper
│   └── UserMapper.java               # 用户Mapper
├── security/                         # 安全相关
│   └── JwtAuthorizationFilter.java   # JWT认证过滤器
├── service/                          # 服务接口
│   ├── QuoteService.java             # 名言警句服务接口
│   ├── SettingService.java           # 系统设置服务接口
│   ├── StatService.java              # 统计服务接口
│   ├── TimerService.java             # 计时服务接口
│   ├── TodoService.java              # 待办事项服务接口
│   └── UserService.java              # 用户服务接口
├── service/impl/                     # 服务实现类
│   └── UserDetailsServiceImpl.java   # 用户详情服务实现类
└── util/                             # 工具类
    └── JwtUtil.java                  # JWT工具类
```

## 数据库设计

项目使用MySQL数据库，主要包含以下表：

- `user`: 用户表
- `todo`: 待办事项表
- `timer`: 计时记录表
- `setting`: 系统设置表
- `quote`: 名言警句表

数据库初始化脚本位于 `src/main/resources/db/schema.sql`。

## 如何运行

1. 确保已安装JDK 8+和MySQL 5.7+
2. 创建数据库：`CREATE DATABASE todo DEFAULT CHARACTER SET utf8mb4;`
3. 执行数据库初始化脚本：`src/main/resources/db/schema.sql`
4. 修改 `application.yml` 中的数据库连接信息
5. 运行项目：`mvn spring-boot:run`

## API文档

详细的API文档请参考 `api.md` 文件。 