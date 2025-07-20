<template>
  <view class="login-container">
    <!-- 背景渐变 -->
    <view class="bg-gradient"></view>

    <!-- 登录卡片 -->
    <view class="login-card">
      <!-- 标题 -->
      <view class="login-title">
        <view class="title-row">
          <view class="back-button" @click="goBack">
            <text class="back-icon">←</text>
          </view>
          <text class="welcome-text">欢迎回来</text>
        </view>
        <text class="subtitle-text">请登录您的账号</text>
      </view>

      <!-- 表单 -->
      <view class="login-form">
        <!-- 用户名输入框 -->
        <view class="input-group">
          <text class="input-label">用户名</text>
          <view class="input-wrapper">
            <input
              class="input-field"
              type="text"
              v-model="username"
              placeholder="请输入用户名"
            />
            <text class="input-icon">👤</text>
          </view>
        </view>

        <!-- 密码输入框 -->
        <view class="input-group">
          <text class="input-label">密码</text>
          <view class="input-wrapper">
            <input
              class="input-field"
              :type="showPassword ? 'text' : 'password'"
              v-model="password"
              placeholder="请输入密码"
            />
            <text class="input-icon">🔒</text>
          </view>
        </view>

        <!-- 记住我选项 -->
        <view class="remember-row">
          <view class="checkbox-group" @click="toggleRemember">
            <view class="checkbox" :class="{ checked: rememberMe }"></view>
            <text class="checkbox-label">记住我</text>
          </view>
          <text class="forgot-password" @click="forgotPassword">忘记密码?</text>
        </view>

        <!-- 登录按钮 -->
        <view class="login-btn" @click="handleLogin">
          <text>登录</text>
        </view>

        <!-- 注册链接 -->
        <view class="register-link">
          <text>还没有账号? </text>
          <text class="link" @click="goToRegister">立即注册</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import api from '../utils/api.js';
import storage from '../utils/storage.js';

export default {
  data() {
    return {
      username: '',
      password: '',
      showPassword: false,
      rememberMe: false,
      loading: false
    };
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },
    handleLogin() {
      if (!this.username.trim()) {
        uni.showToast({
          title: '请输入用户名',
          icon: 'none'
        });
        return;
      }

      if (!this.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        });
        return;
      }

      // 显示加载中
      this.loading = true;
      uni.showLoading({
        title: '登录中...'
      });

      // 调用登录接口
      api.auth.login({
        username: this.username,
        password: this.password
      }).then(res => {
        // 隐藏加载
        uni.hideLoading();
        this.loading = false;

        if (res.code === 200) {
          // 登录成功，保存用户信息和token
          const { token, userId, username, expiresIn, avatar } = res.data;

          // 保存登录信息
          storage.saveLoginInfo(token, {
            id: userId,
            username,
            avatar
          }, expiresIn);

          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });

          // 跳转到待办首页
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/index'
            });
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || '登录失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        // 隐藏加载
        uni.hideLoading();
        this.loading = false;

        uni.showToast({
          title: '登录失败，请稍后重试',
          icon: 'none'
        });
        console.error('登录失败:', err);
      });
    },
    toggleRemember() {
      this.rememberMe = !this.rememberMe;
    },
    forgotPassword() {
      uni.showToast({
        title: '忘记密码功能开发中',
        icon: 'none'
      });
    },
    goToRegister() {
      uni.navigateTo({
        url: '/pages/register'
      });
    }
  }
};
</script>

<style>
.login-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100%;
  /* === 新增：为顶部状态栏留出空间 === */
  padding-top: var(--status-bar-height);
  box-sizing: border-box;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #a78bfa, #818cf8);
  z-index: -1;
}

.login-card {
  width: 85%;
  max-width: 650rpx;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 30rpx;
  padding: 50rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 50rpx;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 10rpx;
}

.back-button {
  position: absolute;
  left: 0;
  width: 60rpx;
  height: 60rpx;
  background-color: rgba(129, 140, 248, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 36rpx;
  color: #818cf8;
  font-weight: bold;
}

.welcome-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
}

.subtitle-text {
  font-size: 28rpx;
  color: #666;
}

.login-form {
  width: 100%;
}

.input-group {
  margin-bottom: 30rpx;
}

.input-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.input-wrapper {
  position: relative;
  border-radius: 10rpx;
  background-color: #f5f7fa;
  height: 90rpx;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
}

.input-field {
  flex: 1;
  height: 100%;
  font-size: 30rpx;
  color: #333;
}

.input-icon {
  font-size: 36rpx;
  color: #818cf8;
}

.remember-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #ccc;
  border-radius: 8rpx;
  margin-right: 10rpx;
  position: relative;
}

.checkbox.checked {
  background-color: #818cf8;
  border-color: #818cf8;
}

.checkbox.checked::after {
  content: "✓";
  position: absolute;
  color: white;
  font-size: 24rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.checkbox-label {
  font-size: 26rpx;
  color: #666;
}

.forgot-password {
  font-size: 26rpx;
  color: #818cf8;
}

.login-btn {
  height: 90rpx;
  background: linear-gradient(to right, #818cf8, #a78bfa);
  border-radius: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  box-shadow: 0 6rpx 16rpx rgba(129, 140, 248, 0.4);
}

.register-link {
  text-align: center;
  font-size: 26rpx;
  color: #666;
}

.link {
  color: #818cf8;
  font-weight: 500;
}
</style>
