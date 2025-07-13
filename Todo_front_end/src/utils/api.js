// api.js - 封装后端API接口请求

// 基础URL - 修改为正确的后端服务器地址和端口
const BASE_URL = 'http://localhost:7080/api/v1'; // 本地

// 请求方法封装
const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = uni.getStorageSync('token') || '';

    // 默认配置
    const defaultOptions = {
      url: `${BASE_URL}${url}`,
      header: {
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data);
        } else {
          // 处理错误响应
          handleError(res);
          reject(res);
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
        reject(err);
      }
    };

    // 如果有token，添加到header
    if (token) {
      defaultOptions.header['Authorization'] = `Bearer ${token}`;
    }

    // 合并选项
    const mergedOptions = {
      ...defaultOptions,
      ...options,
      header: {
        ...defaultOptions.header,
        ...(options.header || {})
      }
    };

    // 发起请求
    uni.request(mergedOptions);
  });
};

// 错误处理
const handleError = (res) => {
  let message = '请求失败';

  if (res.data && res.data.message) {
    message = res.data.message;
  }

  // 根据状态码处理
  switch (res.statusCode) {
    case 401:
      // 未授权，清除登录状态并跳转到登录页
      uni.removeStorageSync('token');
      uni.removeStorageSync('userInfo');
      uni.removeStorageSync('isLoggedIn');

      uni.showToast({
        title: '登录已过期，请重新登录',
        icon: 'none'
      });

      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/login/index'
        });
      }, 1500);
      break;

    case 403:
      uni.showToast({
        title: '权限不足',
        icon: 'none'
      });
      break;

    case 404:
      uni.showToast({
        title: '请求的资源不存在',
        icon: 'none'
      });
      break;

    case 500:
      uni.showToast({
        title: '服务器错误',
        icon: 'none'
      });
      break;

    default:
      uni.showToast({
        title: message,
        icon: 'none'
      });
  }
};

// HTTP方法封装
const http = {
  get: (url, params = {}) => {
    return request(url, {
      method: 'GET',
      data: params
    });
  },

  post: (url, data = {}) => {
    return request(url, {
      method: 'POST',
      data
    });
  },

  put: (url, data = {}) => {
    return request(url, {
      method: 'PUT',
      data
    });
  },

  delete: (url, data = {}) => {
    return request(url, {
      method: 'DELETE',
      data
    });
  }
};

// API接口封装
const api = {
  // 1. 用户管理
  auth: {
    // 1.1 用户注册
    register: (data) => {
      return http.post('/auth/register', data);
    },

    // 1.2 用户登录
    login: (data) => {
      return http.post('/auth/login', data);
    },

    // 1.3 用户退出登录
    logout: () => {
      return http.post('/auth/logout');
    }
  },

  user: {
    // 1.3 获取用户信息
    getProfile: () => {
      return http.get('/user/profile');
    },

    // 1.4 更新用户信息
    updateProfile: (data) => {
      return http.put('/user/profile', data);
    },

    // 1.5 上传头像
    uploadAvatar: (filePath) => {
      return new Promise((resolve, reject) => {
        const token = uni.getStorageSync('token') || '';

        uni.uploadFile({
          url: `${BASE_URL}/user/avatar`,
          filePath,
          name: 'avatar',
          header: {
            'Authorization': token ? `Bearer ${token}` : ''
          },
          success: (res) => {
            if (res.statusCode >= 200 && res.statusCode < 300) {
              const data = JSON.parse(res.data);
              resolve(data);
            } else {
              handleError(res);
              reject(res);
            }
          },
          fail: (err) => {
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            });
            reject(err);
          }
        });
      });
    }
  },

  // 2. 待办事项管理
  todos: {
    // 2.1 创建待办事项
    create: (data) => {
      return http.post('/todos', data);
    },

    // 2.2 获取待办事项列表
    getList: (params) => {
      return http.get('/todos', params);
    },

    // 2.3 获取待办事项详情
    getDetail: (id) => {
      return http.get(`/todos/${id}`);
    },

    // 2.4 更新待办事项
    update: (id, data) => {
      return http.put(`/todos/${id}`, data);
    },

    // 2.5 删除待办事项
    delete: (id) => {
      return http.delete(`/todos/${id}`);
    },

    // 2.6 完成待办事项
    complete: (id) => {
      return http.post(`/todos/${id}/complete`);
    }
  },

  // 3. 计时记录管理
  timers: {
    // 3.1 开始计时
    start: (data) => {
      return http.post('/timers/start', data);
    },

    // 3.2 结束计时
    end: (timerId, data) => {
      return http.post(`/timers/${timerId}/end`, data);
    },

    // 3.3 获取计时记录列表
    getList: (params) => {
      return http.get('/timers', params);
    }
  },

  // 3.4 获取随机鸡汤语录
  quotes: {
    getRandom: () => {
      return http.get('/quotes/random');
    }
  },

  // 4. 数据统计
  stats: {
    // 4.1 获取每日统计数据
    getDaily: (params) => {
      return http.get('/stats/daily', params);
    },

    // 4.2 获取每周统计数据
    getWeekly: (params) => {
      return http.get('/stats/weekly', params);
    },

    // 4.3 获取每月统计数据
    getMonthly: (params) => {
      return http.get('/stats/monthly', params);
    }
  },

  // 5. 系统设置
  settings: {
    // 5.1 获取系统设置
    get: () => {
      return http.get('/settings');
    },

    // 5.2 更新系统设置
    update: (data) => {
      return http.put('/settings', data);
    }
  }
};

export default api;
