# 待办APP接口对接文档

本文档详细描述了待办APP的后端API接口对接方式，供前端开发人员参考。

## 基础信息

- 基础URL: `https://api.todoapp.com/v1`
- 所有请求和响应均使用JSON格式
- 认证方式: Bearer Token (在请求头中添加 `Authorization: Bearer {token}`)
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
- **成功响应** (201 Created):
  ```json
  {
    "code": 201,
    "message": "创建成功",
    "data": {
      "userId": "1",
      "username": "testuser",
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 3600
    }
  }
  ```
- **失败响应** (400 Bad Request):
  ```json
  {
    "code": 400,
    "message": "请求参数错误",
    "errors": [
      {
        "field": "username",
        "message": "用户名不能为空"
      }
    ]
  }
  ```
  或
  ```json
  {
    "code": 400,
    "message": "用户名已存在"
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
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "userId": "1",
      "username": "testuser",
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 3600,
      "avatar": "https://example.com/avatar.jpg"
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "用户名或密码错误"
  }
  ```

### 1.3 获取用户信息

- **URL**: `/user/profile`
- **方法**: `GET`
- **描述**: 获取当前登录用户的信息
- **请求头**: `Authorization: Bearer {token}`
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "gender": "male",
      "birthday": "1990-01-01",
      "signature": "这是我的个性签名",
      "createdAt": "2023-01-01 12:00:00"
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

### 1.4 更新用户信息

- **URL**: `/user/profile`
- **方法**: `PUT`
- **描述**: 更新用户个人资料
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "username": "string",  // 可选
    "gender": "string",    // 可选
    "birthday": "string",  // 可选
    "signature": "string"  // 可选
  }
  ```
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "id": 1,
      "username": "testuser",
      "gender": "male",
      "birthday": "1990-01-01",
      "signature": "这是我的新签名",
      "updatedAt": "2023-01-02 12:00:00"
    }
  }
  ```
- **失败响应** (400 Bad Request):
  ```json
  {
    "code": 400,
    "message": "请求参数错误"
  }
  ```

### 1.5 上传头像

- **URL**: `/user/avatar`
- **方法**: `POST`
- **描述**: 上传用户头像
- **请求头**: `Authorization: Bearer {token}`
- **请求体**: FormData格式，包含文件字段"avatar"
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "上传成功",
    "data": {
      "avatarUrl": "https://example.com/uploads/avatars/123456.jpg"
    }
  }
  ```
- **失败响应** (400 Bad Request):
  ```json
  {
    "code": 400,
    "message": "文件不能为空"
  }
  ```
  或
  ```json
  {
    "code": 500,
    "message": "文件上传失败: 文件格式不支持"
  }
  ```

## 2. 待办事项管理

### 2.1 创建待办事项

- **URL**: `/todos`
- **方法**: `POST`
- **描述**: 创建新的待办事项
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "name": "完成项目报告",         // 待办名称，必填
    "type": "工作",                // 待办类型，必填
    "timerMode": "pomodoro",       // 计时方式，必填，可选值：pomodoro(番茄钟)、normal(普通计时)
    "duration": 25,                // 时长(分钟)，必填
    "backgroundColor": "#FF5733",  // 背景颜色，可选
    "description": "完成第三季度项目报告" // 描述，可选
  }
  ```
- **成功响应** (201 Created):
  ```json
  {
    "code": 201,
    "message": "创建成功",
    "data": {
      "id": 1,
      "name": "完成项目报告",
      "type": "工作",
      "timerMode": "pomodoro",
      "duration": 25,
      "backgroundColor": "#FF5733",
      "description": "完成第三季度项目报告",
      "createdAt": "2023-01-01 12:00:00",
      "userId": 1
    }
  }
  ```
- **失败响应** (400 Bad Request):
  ```json
  {
    "code": 400,
    "message": "请求参数错误",
    "errors": [
      {
        "field": "name",
        "message": "待办名称不能为空"
      }
    ]
  }
  ```

### 2.2 获取待办事项列表

- **URL**: `/todos`
- **方法**: `GET`
- **描述**: 获取当前用户的所有待办事项
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `page`: 页码，默认1
  - `pageSize`: 每页数量，默认10
  - `status`: 状态筛选，可选值：all(全部)、completed(已完成)、uncompleted(未完成)
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "total": 15,
      "page": 1,
      "pageSize": 10,
      "list": [
        {
          "id": 1,
          "name": "完成项目报告",
          "type": "工作",
          "timerMode": "pomodoro",
          "duration": 25,
          "backgroundColor": "#FF5733",
          "description": "完成第三季度项目报告",
          "status": "uncompleted",
          "createdAt": "2023-01-01 12:00:00"
        },
        // 更多待办事项...
      ]
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

### 2.3 获取待办事项详情

- **URL**: `/todos/{id}`
- **方法**: `GET`
- **描述**: 获取指定待办事项的详细信息
- **请求头**: `Authorization: Bearer {token}`
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "id": 1,
      "name": "完成项目报告",
      "type": "工作",
      "timerMode": "pomodoro",
      "duration": 25,
      "backgroundColor": "#FF5733",
      "description": "完成第三季度项目报告",
      "status": "completed",
      "createdAt": "2023-01-01 12:00:00",
      "completedAt": "2023-01-02 15:30:00"
    }
  }
  ```
- **失败响应** (404 Not Found):
  ```json
  {
    "code": 404,
    "message": "待办事项不存在"
  }
  ```
  或
  ```json
  {
    "code": 403,
    "message": "权限不足"
  }
  ```

### 2.4 更新待办事项

- **URL**: `/todos/{id}`
- **方法**: `PUT`
- **描述**: 更新指定待办事项
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "name": "修改后的项目报告",     // 可选
    "type": "工作",               // 可选
    "timerMode": "normal",        // 可选
    "duration": 30,               // 可选
    "backgroundColor": "#33FF57", // 可选
    "description": "修改后的描述"   // 可选
  }
  ```
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "id": 1,
      "name": "修改后的项目报告",
      "type": "工作",
      "timerMode": "normal",
      "duration": 30,
      "backgroundColor": "#33FF57",
      "description": "修改后的描述",
      "updatedAt": "2023-01-03 10:15:00"
    }
  }
  ```
- **失败响应** (404 Not Found):
  ```json
  {
    "code": 404,
    "message": "待办事项不存在"
  }
  ```
  或
  ```json
  {
    "code": 403,
    "message": "权限不足"
  }
  ```

### 2.5 删除待办事项

- **URL**: `/todos/{id}`
- **方法**: `DELETE`
- **描述**: 删除指定待办事项
- **请求头**: `Authorization: Bearer {token}`
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```
- **失败响应** (404 Not Found):
  ```json
  {
    "code": 404,
    "message": "待办事项不存在"
  }
  ```
  或
  ```json
  {
    "code": 403,
    "message": "权限不足"
  }
  ```

### 2.6 完成待办事项

- **URL**: `/todos/{id}/complete`
- **方法**: `POST`
- **描述**: 将待办事项标记为已完成
- **请求头**: `Authorization: Bearer {token}`
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {
      "id": 1,
      "status": "completed",
      "completedAt": "2023-01-03 16:45:00"
    }
  }
  ```
- **失败响应** (404 Not Found):
  ```json
  {
    "code": 404,
    "message": "待办事项不存在"
  }
  ```
  或
  ```json
  {
    "code": 403,
    "message": "权限不足"
  }
  ```

## 3. 计时记录管理

### 3.1 开始计时

- **URL**: `/timers/start`
- **方法**: `POST`
- **描述**: 开始一个计时会话
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "todoId": 1,                      // 待办事项ID，必填
    "startTime": "2023-01-04T09:00:00Z" // 开始时间，ISO格式，必填
  }
  ```
- **成功响应** (201 Created):
  ```json
  {
    "code": 201,
    "message": "计时开始",
    "data": {
      "timerId": 1,
      "todoId": 1,
      "startTime": "2023-01-04T09:00:00Z",
      "expectedDuration": 25
    }
  }
  ```
- **失败响应** (400 Bad Request):
  ```json
  {
    "code": 400,
    "message": "请求参数错误",
    "errors": [
      {
        "field": "todoId",
        "message": "待办事项ID不能为空"
      }
    ]
  }
  ```
  或
  ```json
  {
    "code": 404,
    "message": "待办事项不存在"
  }
  ```

### 3.2 结束计时

- **URL**: `/timers/{timerId}/end`
- **方法**: `POST`
- **描述**: 结束一个计时会话
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "endTime": "2023-01-04T09:25:00Z" // 结束时间，ISO格式，必填
  }
  ```
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "计时结束",
    "data": {
      "timerId": 1,
      "todoId": 1,
      "startTime": "2023-01-04T09:00:00Z",
      "endTime": "2023-01-04T09:25:00Z",
      "actualDuration": 25,
      "completed": true
    }
  }
  ```
- **失败响应** (404 Not Found):
  ```json
  {
    "code": 404,
    "message": "计时记录不存在"
  }
  ```
  或
  ```json
  {
    "code": 403,
    "message": "权限不足"
  }
  ```

### 3.3 获取计时记录列表

- **URL**: `/timers`
- **方法**: `GET`
- **描述**: 获取用户的计时记录列表
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `page`: 页码，默认1
  - `pageSize`: 每页数量，默认10
  - `todoId`: 按待办事项ID筛选，可选
  - `startDate`: 开始日期，格式YYYY-MM-DD，可选
  - `endDate`: 结束日期，格式YYYY-MM-DD，可选
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "total": 5,
      "page": 1,
      "pageSize": 10,
      "list": [
        {
          "timerId": 1,
          "todoId": 1,
          "todoName": "完成项目报告",
          "startTime": "2023-01-04T09:00:00Z",
          "endTime": "2023-01-04T09:25:00Z",
          "actualDuration": 25,
          "completed": true
        },
        // 更多计时记录...
      ]
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

### 3.4 获取随机鸡汤语录

- **URL**: `/quotes/random`
- **方法**: `GET`
- **描述**: 获取随机鸡汤语录，用于计时页面显示
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "content": "行动是治愈恐惧的良药，而犹豫、拖延将不断滋养恐惧。",
      "author": "罗宾·夏玛"
    }
  }
  ```
- **失败响应** (500 Internal Server Error):
  ```json
  {
    "code": 500,
    "message": "服务器内部错误"
  }
  ```

## 4. 数据统计

### 4.1 获取每日统计数据

- **URL**: `/stats/daily`
- **方法**: `GET`
- **描述**: 获取用户当日的统计数据
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `date`: 日期，格式YYYY-MM-DD，默认为当天
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "overview": {
        "totalTime": 120,     // 总时长(分钟)
        "totalTasks": 5,      // 总任务数
        "completionRate": 80  // 完成率(百分比)
      },
      "hourlyDistribution": [
        {
          "hour": 9,         // 9点
          "duration": 55     // 该小时的专注时长(分钟)
        },
        {
          "hour": 14,        // 14点
          "duration": 65     // 该小时的专注时长(分钟)
        }
      ],
      "taskList": [
        {
          "id": 1,
          "name": "完成项目报告",
          "duration": 55,
          "timeRange": "09:00-09:55",
          "backgroundColor": "#FF5733"
        },
        {
          "id": 2,
          "name": "团队会议",
          "duration": 65,
          "timeRange": "14:00-15:05",
          "backgroundColor": "#33FF57"
        }
      ]
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

### 4.2 获取每周统计数据

- **URL**: `/stats/weekly`
- **方法**: `GET`
- **描述**: 获取用户本周的统计数据
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `startDate`: 周起始日期，格式YYYY-MM-DD，默认为本周一
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "overview": {
        "totalTime": 540,     // 总时长(分钟)
        "totalTasks": 12,     // 总任务数
        "completionRate": 75  // 完成率(百分比)
      },
      "dailyDistribution": [
        {
          "day": 1,           // 周一
          "duration": 120     // 该天的专注时长(分钟)
        },
        {
          "day": 2,           // 周二
          "duration": 150     // 该天的专注时长(分钟)
        },
        // 其他天...
      ],
      "taskList": [
        {
          "id": 1,
          "name": "完成项目报告",
          "duration": 120,
          "days": "周一、周三",
          "backgroundColor": "#FF5733"
        },
        {
          "id": 2,
          "name": "团队会议",
          "duration": 150,
          "days": "周二、周四",
          "backgroundColor": "#33FF57"
        }
      ]
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

### 4.3 获取每月统计数据

- **URL**: `/stats/monthly`
- **方法**: `GET`
- **描述**: 获取用户本月的统计数据
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `month`: 月份，格式YYYY-MM，默认为当月
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "overview": {
        "totalTime": 2400,    // 总时长(分钟)
        "totalTasks": 45,     // 总任务数
        "completionRate": 80  // 完成率(百分比)
      },
      "weeklyDistribution": [
        {
          "week": 1,          // 第1周
          "duration": 540     // 该周的专注时长(分钟)
        },
        {
          "week": 2,          // 第2周
          "duration": 620     // 该周的专注时长(分钟)
        },
        // 其他周...
      ],
      "taskList": [
        {
          "id": 1,
          "name": "完成项目报告",
          "duration": 480,
          "period": "第1-2周",
          "backgroundColor": "#FF5733"
        },
        {
          "id": 2,
          "name": "团队会议",
          "duration": 360,
          "period": "全月",
          "backgroundColor": "#33FF57"
        }
      ]
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

## 5. 系统设置

### 5.1 获取系统设置

- **URL**: `/settings`
- **方法**: `GET`
- **描述**: 获取用户的系统设置
- **请求头**: `Authorization: Bearer {token}`
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "defaultRestTime": 5,          // 默认休息时间(分钟)
      "theme": "light",              // 主题，如"light"、"dark"
      "notificationEnabled": true,   // 是否启用通知
      "soundEnabled": true           // 是否启用声音
    }
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "code": 401,
    "message": "未授权"
  }
  ```

### 5.2 更新系统设置

- **URL**: `/settings`
- **方法**: `PUT`
- **描述**: 更新用户的系统设置
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "defaultRestTime": 10,         // 可选
    "theme": "dark",               // 可选
    "notificationEnabled": false,  // 可选
    "soundEnabled": true           // 可选
  }
  ```
- **成功响应** (200 OK):
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "defaultRestTime": 10,
      "theme": "dark",
      "notificationEnabled": false,
      "soundEnabled": true,
      "updatedAt": "2023-01-05 14:30:00"
    }
  }
  ```
- **失败响应** (400 Bad Request):
  ```json
  {
    "code": 400,
    "message": "请求参数错误"
  }
  ```
  或
  ```json
  {
    "code": 401,
    "message": "未授权"
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