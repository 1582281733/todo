// storage.js - 封装本地存储操作

// 用户信息相关存储
const USER_TOKEN_KEY = 'token';
const USER_INFO_KEY = 'userInfo';
const LOGIN_STATUS_KEY = 'isLoggedIn';

// 系统设置相关存储
const SETTINGS_KEY = 'appSettings';

// 存储工具对象
const storage = {
  // 保存登录信息
  saveLoginInfo(token, userInfo, expiresIn) {
    // 保存token
    uni.setStorageSync(USER_TOKEN_KEY, token);
    
    // 保存用户信息
    uni.setStorageSync(USER_INFO_KEY, userInfo);
    
    // 保存登录状态
    uni.setStorageSync(LOGIN_STATUS_KEY, true);
    
    // 如果有过期时间，设置自动登出定时器
    if (expiresIn) {
      const expireTime = Date.now() + expiresIn * 1000;
      uni.setStorageSync('tokenExpireTime', expireTime);
    }
  },
  
  // 获取用户信息
  getUserInfo() {
    return uni.getStorageSync(USER_INFO_KEY) || null;
  },
  
  // 获取token
  getToken() {
    return uni.getStorageSync(USER_TOKEN_KEY) || '';
  },
  
  // 检查是否登录
  isLoggedIn() {
    const token = this.getToken();
    const isLoggedIn = uni.getStorageSync(LOGIN_STATUS_KEY) || false;
    
    // 检查token是否过期
    const expireTime = uni.getStorageSync('tokenExpireTime');
    if (expireTime && Date.now() > expireTime) {
      this.clearLoginInfo();
      return false;
    }
    
    return isLoggedIn && !!token;
  },
  
  // 清除登录信息
  clearLoginInfo() {
    uni.removeStorageSync(USER_TOKEN_KEY);
    uni.removeStorageSync(USER_INFO_KEY);
    uni.removeStorageSync(LOGIN_STATUS_KEY);
    uni.removeStorageSync('tokenExpireTime');
  },
  
  // 保存系统设置
  saveSettings(settings) {
    uni.setStorageSync(SETTINGS_KEY, settings);
  },
  
  // 获取系统设置
  getSettings() {
    return uni.getStorageSync(SETTINGS_KEY) || {
      defaultRestTime: 5,
      theme: 'light',
      notificationEnabled: true,
      soundEnabled: true
    };
  },
  
  // 更新系统设置
  updateSettings(newSettings) {
    const currentSettings = this.getSettings();
    const updatedSettings = {
      ...currentSettings,
      ...newSettings
    };
    this.saveSettings(updatedSettings);
    return updatedSettings;
  }
};

export default storage; 