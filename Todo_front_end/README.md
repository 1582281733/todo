# 待办APP前端项目

这是一个基于uni-app开发的待办事项应用前端项目。

## 接口对接说明

本项目已完成与后端API的对接，具体如下：

### 1. 用户管理

- [x] 用户注册 `/auth/register`
- [x] 用户登录 `/auth/login`
- [x] 获取用户信息 `/user/profile`
- [x] 更新用户信息 `/user/profile`
- [x] 上传头像 `/user/avatar`

### 2. 待办事项管理

- [x] 创建待办事项 `/todos`
- [x] 获取待办事项列表 `/todos`
- [x] 获取待办事项详情 `/todos/{id}`
- [x] 更新待办事项 `/todos/{id}`
- [x] 删除待办事项 `/todos/{id}`
- [x] 完成待办事项 `/todos/{id}/complete`

### 3. 计时记录管理

- [x] 开始计时 `/timers/start`
- [x] 结束计时 `/timers/{timerId}/end`
- [x] 获取计时记录列表 `/timers`
- [x] 获取随机鸡汤语录 `/quotes/random`

### 4. 数据统计

- [x] 获取每日统计数据 `/stats/daily`
- [x] 获取每周统计数据 `/stats/weekly`
- [x] 获取每月统计数据 `/stats/monthly`

### 5. 系统设置

- [x] 获取系统设置 `/settings`
- [x] 更新系统设置 `/settings`

## 项目结构

```
src/
  ├── components/        # 组件目录
  ├── pages/             # 页面目录
  ├── static/            # 静态资源
  ├── utils/             # 工具函数
  │   ├── api.js         # API接口封装
  │   ├── storage.js     # 本地存储工具
  │   └── index.js       # 工具函数集合
  ├── App.vue            # 应用入口组件
  └── main.js            # 应用入口JS
```

## 工具类说明

### API工具 (api.js)

封装了所有后端API接口的请求方法，包括：

- 用户管理相关接口
- 待办事项管理相关接口
- 计时记录管理相关接口
- 数据统计相关接口
- 系统设置相关接口

### 存储工具 (storage.js)

封装了本地存储操作，包括：

- 用户登录信息存储与获取
- 用户Token管理
- 系统设置存储与获取

### 通用工具 (index.js)

提供了一系列实用工具函数：

- 日期格式化
- 周开始/结束日期计算
- 防抖/节流函数
- 深拷贝函数

## 使用方式

在页面中使用API接口：

```javascript
// 方式1: 导入API
import api from '../../utils/api.js';

// 调用登录接口
api.auth.login({
  username: 'test',
  password: '123456'
}).then(res => {
  console.log('登录成功', res);
}).catch(err => {
  console.error('登录失败', err);
});

// 方式2: 使用全局属性
this.$api.auth.login({
  username: 'test',
  password: '123456'
}).then(res => {
  console.log('登录成功', res);
}).catch(err => {
  console.error('登录失败', err);
});
```

## 注意事项

1. 所有接口请求都会自动处理token认证
2. 401未授权错误会自动跳转到登录页面
3. 接口返回数据格式统一为 `{ code, message, data }` 