# 待办APP API文档

本文档详细描述了待办APP所需的后端API接口规范，供后端开发人员参考实现。

## 基础信息

- 基础URL: `https://api.todoapp.com/v1`
- 所有请求和响应均使用JSON格式
- 认证方式: Bearer Token
- 状态码:
  - 200: 成功
  - 201: 创建成功
  - 400: 请求参数错误
  - 401: 未授权
  - 403: 权限不足
  - 404: 资源不存在
  - 500: 服务器错误

## 1. 用户管理

### 1.1 用户注册

- **URL**: `/auth/register`
- **方法**: `POST`
- **描述**: 注册新用户
- **请求体**:
  ```json
  {
    "username": "string", // 用户名，必填
    "password": "string", // 密码，必填
    "email": "string"     // 邮箱，可选
  }
  ```
- **响应**:
  ```json
  {
    "code": 201,
    "message": "注册成功",
    "data": {
      "userId": "string",
      "username": "string",
      "token": "string",
      "expiresIn": 3600
    }
  }
  ```

### 1.2 用户登录

- **URL**: `/auth/login`
- **方法**: `POST`
- **描述**: 用户登录
- **请求体**:
  ```json
  {
    "username": "string", // 用户名，必填
    "password": "string"  // 密码，必填
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "userId": "string",
      "username": "string",
      "token": "string",
      "expiresIn": 3600,
      "avatar": "string" // 头像URL
    }
  }
  ```

### 1.3 获取用户信息

- **URL**: `/user/profile`
- **方法**: `GET`
- **描述**: 获取当前登录用户的信息
- **请求头**: Authorization: Bearer {token}
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "userId": "string",
      "username": "string",
      "email": "string",
      "avatar": "string",
      "gender": "string",
      "birthday": "string",
      "signature": "string",
      "createdAt": "string"
    }
  }
  ```

### 1.4 更新用户信息

- **URL**: `/user/profile`
- **方法**: `PUT`
- **描述**: 更新用户个人资料
- **请求头**: Authorization: Bearer {token}
- **请求体**:
  ```json
  {
    "username": "string",  // 可选
    "gender": "string",    // 可选
    "birthday": "string",  // 可选
    "signature": "string"  // 可选
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "userId": "string",
      "username": "string",
      "gender": "string",
      "birthday": "string",
      "signature": "string",
      "updatedAt": "string"
    }
  }
  ```

### 1.5 上传头像

- **URL**: `/user/avatar`
- **方法**: `POST`
- **描述**: 上传用户头像
- **请求头**: Authorization: Bearer {token}
- **请求体**: FormData格式，包含文件字段"avatar"
- **响应**:
  ```json
  {
    "code": 200,
    "message": "上传成功",
    "data": {
      "avatarUrl": "string"
    }
  }
  ```

## 2. 待办事项管理

### 2.1 创建待办事项

- **URL**: `/todos`
- **方法**: `POST`
- **描述**: 创建新的待办事项
- **请求头**: Authorization: Bearer {token}
- **请求体**:
  ```json
  {
    "name": "string",         // 待办名称，必填
    "type": "string",         // 待办类型，必填
    "timerMode": "string",    // 计时方式，必填，可选值：pomodoro(番茄钟)、normal(普通计时)
    "duration": number,       // 时长(分钟)，必填
    "backgroundColor": "string", // 背景颜色，可选
    "description": "string"   // 描述，可选
  }
  ```
- **响应**:
  ```json
  {
    "code": 201,
    "message": "创建成功",
    "data": {
      "id": "string",
      "name": "string",
      "type": "string",
      "timerMode": "string",
      "duration": number,
      "backgroundColor": "string",
      "description": "string",
      "createdAt": "string",
      "userId": "string"
    }
  }
  ```

### 2.2 获取待办事项列表

- **URL**: `/todos`
- **方法**: `GET`
- **描述**: 获取当前用户的所有待办事项
- **请求头**: Authorization: Bearer {token}
- **查询参数**:
  - `page`: 页码，默认1
  - `pageSize`: 每页数量，默认10
  - `status`: 状态筛选，可选值：all(全部)、completed(已完成)、uncompleted(未完成)
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "total": number,
      "page": number,
      "pageSize": number,
      "list": [
        {
          "id": "string",
          "name": "string",
          "type": "string",
          "timerMode": "string",
          "duration": number,
          "backgroundColor": "string",
          "description": "string",
          "status": "string", // completed或uncompleted
          "createdAt": "string"
        }
      ]
    }
  }
  ```

### 2.3 获取待办事项详情

- **URL**: `/todos/{id}`
- **方法**: `GET`
- **描述**: 获取指定待办事项的详细信息
- **请求头**: Authorization: Bearer {token}
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "id": "string",
      "name": "string",
      "type": "string",
      "timerMode": "string",
      "duration": number,
      "backgroundColor": "string",
      "description": "string",
      "status": "string",
      "createdAt": "string",
      "completedAt": "string" // 如果已完成，则有此字段
    }
  }
  ```

### 2.4 更新待办事项

- **URL**: `/todos/{id}`
- **方法**: `PUT`
- **描述**: 更新指定待办事项
- **请求头**: Authorization: Bearer {token}
- **请求体**:
  ```json
  {
    "name": "string",         // 可选
    "type": "string",         // 可选
    "timerMode": "string",    // 可选
    "duration": number,       // 可选
    "backgroundColor": "string", // 可选
    "description": "string"   // 可选
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "id": "string",
      "name": "string",
      "type": "string",
      "timerMode": "string",
      "duration": number,
      "backgroundColor": "string",
      "description": "string",
      "updatedAt": "string"
    }
  }
  ```

### 2.5 删除待办事项

- **URL**: `/todos/{id}`
- **方法**: `DELETE`
- **描述**: 删除指定待办事项
- **请求头**: Authorization: Bearer {token}
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 2.6 完成待办事项

- **URL**: `/todos/{id}/complete`
- **方法**: `POST`
- **描述**: 将待办事项标记为已完成
- **请求头**: Authorization: Bearer {token}
- **响应**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {
      "id": "string",
      "status": "completed",
      "completedAt": "string"
    }
  }
  ```

## 3. 计时记录管理

### 3.1 开始计时

- **URL**: `/timers/start`
- **方法**: `POST`
- **描述**: 开始一个计时会话
- **请求头**: Authorization: Bearer {token}
- **请求体**:
  ```json
  {
    "todoId": "string",     // 待办事项ID，必填
    "startTime": "string"   // 开始时间，ISO格式，必填
  }
  ```
- **响应**:
  ```json
  {
    "code": 201,
    "message": "计时开始",
    "data": {
      "timerId": "string",
      "todoId": "string",
      "startTime": "string",
      "expectedDuration": number // 预计时长(分钟)
    }
  }
  ```

### 3.2 结束计时

- **URL**: `/timers/{timerId}/end`
- **方法**: `POST`
- **描述**: 结束一个计时会话
- **请求头**: Authorization: Bearer {token}
- **请求体**:
  ```json
  {
    "endTime": "string"     // 结束时间，ISO格式，必填
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "计时结束",
    "data": {
      "timerId": "string",
      "todoId": "string",
      "startTime": "string",
      "endTime": "string",
      "actualDuration": number, // 实际时长(分钟)
      "completed": boolean      // 是否完成了预期时长
    }
  }
  ```

### 3.3 获取计时记录列表

- **URL**: `/timers`
- **方法**: `GET`
- **描述**: 获取用户的计时记录列表
- **请求头**: Authorization: Bearer {token}
- **查询参数**:
  - `page`: 页码，默认1
  - `pageSize`: 每页数量，默认10
  - `todoId`: 按待办事项ID筛选，可选
  - `startDate`: 开始日期，格式YYYY-MM-DD，可选
  - `endDate`: 结束日期，格式YYYY-MM-DD，可选
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "total": number,
      "page": number,
      "pageSize": number,
      "list": [
        {
          "timerId": "string",
          "todoId": "string",
          "todoName": "string",
          "startTime": "string",
          "endTime": "string",
          "actualDuration": number,
          "completed": boolean
        }
      ]
    }
  }
  ```

### 3.4 获取随机鸡汤语录

- **URL**: `/quotes/random`
- **方法**: `GET`
- **描述**: 获取随机鸡汤语录，用于计时页面显示
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "content": "string",
      "author": "string"
    }
  }
  ```

## 4. 数据统计

### 4.1 获取每日统计数据

- **URL**: `/stats/daily`
- **方法**: `GET`
- **描述**: 获取用户当日的统计数据
- **请求头**: Authorization: Bearer {token}
- **查询参数**:
  - `date`: 日期，格式YYYY-MM-DD，默认为当天
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "overview": {
        "totalTime": number,    // 总时长(分钟)
        "totalTasks": number,   // 总任务数
        "completionRate": number // 完成率(百分比)
      },
      "hourlyDistribution": [
        {
          "hour": number,       // 0-23小时
          "duration": number    // 该小时的专注时长(分钟)
        }
      ],
      "taskList": [
        {
          "id": "string",
          "name": "string",
          "duration": number,   // 时长(分钟)
          "timeRange": "string", // 时间范围，如"09:15-10:15"
          "backgroundColor": "string"
        }
      ]
    }
  }
  ```

### 4.2 获取每周统计数据

- **URL**: `/stats/weekly`
- **方法**: `GET`
- **描述**: 获取用户本周的统计数据
- **请求头**: Authorization: Bearer {token}
- **查询参数**:
  - `startDate`: 周起始日期，格式YYYY-MM-DD，默认为本周一
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "overview": {
        "totalTime": number,    // 总时长(分钟)
        "totalTasks": number,   // 总任务数
        "completionRate": number // 完成率(百分比)
      },
      "dailyDistribution": [
        {
          "day": number,        // 1-7，表示周一到周日
          "duration": number    // 该天的专注时长(分钟)
        }
      ],
      "taskList": [
        {
          "id": "string",
          "name": "string",
          "duration": number,   // 时长(分钟)
          "days": "string",     // 如"周一、周三"
          "backgroundColor": "string"
        }
      ]
    }
  }
  ```

### 4.3 获取每月统计数据

- **URL**: `/stats/monthly`
- **方法**: `GET`
- **描述**: 获取用户本月的统计数据
- **请求头**: Authorization: Bearer {token}
- **查询参数**:
  - `month`: 月份，格式YYYY-MM，默认为当月
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "overview": {
        "totalTime": number,    // 总时长(分钟)
        "totalTasks": number,   // 总任务数
        "completionRate": number // 完成率(百分比)
      },
      "weeklyDistribution": [
        {
          "week": number,       // 1-5，表示第几周
          "duration": number    // 该周的专注时长(分钟)
        }
      ],
      "taskList": [
        {
          "id": "string",
          "name": "string",
          "duration": number,   // 时长(分钟)
          "period": "string",   // 如"全月"、"第1-3周"
          "backgroundColor": "string"
        }
      ]
    }
  }
  ```

## 5. 系统设置

### 5.1 获取系统设置

- **URL**: `/settings`
- **方法**: `GET`
- **描述**: 获取用户的系统设置
- **请求头**: Authorization: Bearer {token}
- **响应**:
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "defaultRestTime": number,  // 默认休息时间(分钟)
      "theme": "string",          // 主题，如"light"、"dark"
      "notificationEnabled": boolean, // 是否启用通知
      "soundEnabled": boolean     // 是否启用声音
    }
  }
  ```

### 5.2 更新系统设置

- **URL**: `/settings`
- **方法**: `PUT`
- **描述**: 更新用户的系统设置
- **请求头**: Authorization: Bearer {token}
- **请求体**:
  ```json
  {
    "defaultRestTime": number,    // 可选
    "theme": "string",            // 可选
    "notificationEnabled": boolean, // 可选
    "soundEnabled": boolean       // 可选
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "defaultRestTime": number,
      "theme": "string",
      "notificationEnabled": boolean,
      "soundEnabled": boolean,
      "updatedAt": "string"
    }
  }
  ```

## 错误响应格式

当API调用失败时，将返回以下格式的错误响应：

```json
{
  "code": number,       // HTTP状态码
  "message": "string",  // 错误信息
  "errors": [           // 详细错误信息，可选
    {
      "field": "string",  // 错误字段
      "message": "string" // 该字段的错误信息
    }
  ]
}
```

## 注意事项

1. 所有需要认证的接口必须在请求头中包含有效的Bearer Token
2. 日期时间格式统一使用ISO 8601标准
3. 分页接口默认每页返回10条记录
4. 接口返回的数据中，时间相关的字段均为字符串格式的ISO时间
5. 所有接口的响应均包含code和message字段，用于表示状态码和消息 