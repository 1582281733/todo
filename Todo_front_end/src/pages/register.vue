<template>
  <view class="register-container">
    <!-- 背景渐变 -->
    <view class="bg-gradient"></view>

    <!-- 注册卡片 -->
    <view class="register-card">
      <!-- 标题 -->
      <view class="register-title">
        <view class="title-row">
          <view class="back-button" @click="goBack">
            <text class="back-icon">←</text>
          </view>
          <text class="welcome-text">欢迎加入</text>
        </view>
        <text class="subtitle-text">创建您的新账号</text>
      </view>

      <!-- 表单 -->
      <view class="register-form">
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
            <text class="input-icon" @click="togglePasswordVisibility">🔒</text>
          </view>
        </view>

        <!-- 确认密码输入框 -->
        <view class="input-group">
          <text class="input-label">确认密码</text>
          <view class="input-wrapper">
            <input
              class="input-field"
              :type="showPassword ? 'text' : 'password'"
              v-model="confirmPassword"
              placeholder="请再次输入密码"
            />
            <text class="input-icon">🔒</text>
          </view>
        </view>

        <!-- 同意条款选项 -->
        <view class="terms-row">
          <view class="checkbox-group" @click="toggleAgree">
            <view class="checkbox" :class="{ checked: agreeTerms }"></view>
            <text class="checkbox-label">我已阅读并同意</text>
          </view>
          <text class="terms-link" @click="showTerms">《用户协议》</text>
        </view>

        <!-- 注册按钮 -->
        <view class="register-btn" @click="handleRegister">
          <text>注册</text>
        </view>

        <!-- 登录链接 -->
        <view class="login-link">
          <text>已有账号? </text>
          <text class="link" @click="goToLogin">立即登录</text>
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
      confirmPassword: '',
      email: '',
      showPassword: false,
      agreeTerms: false,
      loading: false
    };
  },
  methods: {
    goBack() {
      uni.redirectTo({
        url: '/pages/mine'
      });
    },
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword;
    },
    toggleAgree() {
      this.agreeTerms = !this.agreeTerms;
    },
    showTerms() {
      uni.showToast({
        title: '用户协议功能开发中',
        icon: 'none'
      });
    },
    handleRegister() {
      // 表单验证
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

      if (this.password !== this.confirmPassword) {
        uni.showToast({
          title: '两次输入的密码不一致',
          icon: 'none'
        });
        return;
      }

      if (!this.agreeTerms) {
        uni.showToast({
          title: '请阅读并同意用户协议',
          icon: 'none'
        });
        return;
      }

      // 显示加载中
      this.loading = true;
      uni.showLoading({
        title: '注册中...'
      });

      // 调用注册接口
      api.auth.register({
        username: this.username,
        password: this.password,
        email: this.email || undefined // 如果没有填写邮箱，则不传该字段
      }).then(res => {
        // 隐藏加载
        uni.hideLoading();
        this.loading = false;

        if (res.code === 201) {
          // 注册成功，保存用户信息和token
          const { token, userId, username, expiresIn } = res.data;

          // 保存登录信息
          storage.saveLoginInfo(token, {
            id: userId,
            username,
            avatar: ''
          }, expiresIn);

          uni.showToast({
            title: '注册成功',
            icon: 'success'
          });

          // 跳转到首页
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index'
            });
          }, 1500);
        } else {
          uni.showToast({
            title: res.message || '注册失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        // 隐藏加载
        uni.hideLoading();
        this.loading = false;

        uni.showToast({
          title: '注册失败，请稍后重试',
          icon: 'none'
        });
        console.error('注册失败:', err);
      });
    },
    goToLogin() {
      uni.redirectTo({
        url: '/pages/login'
      });
    }
  }
};
</script>

<style>
.register-container {
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
  background: linear-gradient(135deg, #e8f4fd 0%, #c8e6fd 30%, #a1d2ff 100%);
  z-index: -1;
}

.register-card {
  width: 85%;
  max-width: 650rpx;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 32rpx;
  padding: 60rpx 50rpx;
  box-shadow: 0 20rpx 40rpx rgba(33, 150, 243, 0.12);
  backdrop-filter: blur(10rpx);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.register-title {
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

.register-form {
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

.terms-row {
  display: flex;
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

.terms-link {
  font-size: 28rpx;
  color: #42a5f5;
  font-weight: 500;
}

.register-btn {
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

.register-btn:active {
  transform: translateY(2rpx);
  box-shadow: 0 8rpx 16rpx rgba(33, 150, 243, 0.3);
}

.login-link {
  text-align: center;
  font-size: 28rpx;
  color: #546e7a;
}

.link {
  color: #42a5f5;
  font-weight: 600;
}
</style>
