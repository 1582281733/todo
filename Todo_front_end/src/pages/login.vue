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
  /* 淡蓝色清新渐变背景 */
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 30%, #90caf9 100%);
  z-index: -1; 
}

.login-card {
  width: 85%;
  max-width: 650rpx;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 32rpx;
  padding: 60rpx 50rpx;
  box-shadow: 0 20rpx 40rpx rgba(33, 150, 243, 0.12);
  backdrop-filter: blur(10rpx);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.login-title {
  text-align: center;
  margin-bottom: 60rpx;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 12rpx;
}

.back-button {
  position: absolute;
  left: 0;
  width: 64rpx;
  height: 64rpx;
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.2);
}

.back-icon {
  font-size: 32rpx;
  color: #1976d2;
  font-weight: bold;
}

.welcome-text {
  font-size: 52rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #1565c0, #1976d2);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.subtitle-text {
  font-size: 30rpx;
  color: #546e7a;
  font-weight: 400;
}

.login-form {
  width: 100%;
}

.input-group {
  margin-bottom: 36rpx;
}

.input-label {
  font-size: 30rpx;
  color: #37474f;
  margin-bottom: 12rpx;
  display: block;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  border-radius: 16rpx;
  background: linear-gradient(135deg, #f8fbff, #f0f9ff);
  height: 96rpx;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  border: 2rpx solid rgba(33, 150, 243, 0.1);
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #42a5f5;
  box-shadow: 0 0 0 4rpx rgba(66, 165, 245, 0.1);
}

.input-field {
  flex: 1;
  height: 100%;
  font-size: 32rpx;
  color: #263238;
  background: transparent;
}

.input-icon {
  font-size: 38rpx;
  color: #42a5f5;
}

.remember-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48rpx;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #90caf9;
  border-radius: 10rpx;
  margin-right: 12rpx;
  position: relative;
  background: linear-gradient(135deg, #f8fbff, #f0f9ff);
  transition: all 0.3s ease;
}

.checkbox.checked {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  border-color: #1976d2;
  transform: scale(1.05);
}

.checkbox.checked::after {
  content: "✓";
  position: absolute;
  color: white;
  font-size: 26rpx;
  font-weight: bold;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.checkbox-label {
  font-size: 28rpx;
  color: #546e7a;
  font-weight: 500;
}

.forgot-password {
  font-size: 28rpx;
  color: #42a5f5;
  font-weight: 500;
}

.login-btn {
  height: 96rpx;
  background: linear-gradient(135deg, #42a5f5 0%, #1976d2 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 34rpx;
  font-weight: 600;
  margin-bottom: 36rpx;
  box-shadow: 0 12rpx 24rpx rgba(33, 150, 243, 0.3);
  transition: all 0.3s ease;
}

.login-btn:active {
  transform: translateY(2rpx);
  box-shadow: 0 8rpx 16rpx rgba(33, 150, 243, 0.3);
}

.register-link {
  text-align: center;
  font-size: 28rpx;
  color: #546e7a;
}

.link {
  color: #42a5f5;
  font-weight: 600;
}
</style>
