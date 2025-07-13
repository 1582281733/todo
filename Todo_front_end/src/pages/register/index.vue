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
        
        <!-- 邮箱输入框 -->
        <view class="input-group">
          <text class="input-label">邮箱</text>
          <view class="input-wrapper">
            <input 
              class="input-field" 
              type="email" 
              v-model="email" 
              placeholder="请输入邮箱"
            />
            <text class="input-icon">📧</text>
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
import api from '../../utils/api.js';
import storage from '../../utils/storage.js';

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
        url: '/pages/mine/index'
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
              url: '/pages/index/index'
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
        url: '/pages/login/index'
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

.register-card {
  width: 85%;
  max-width: 650rpx;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 30rpx;
  padding: 50rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.register-title {
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

.register-form {
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

.terms-row {
  display: flex;
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

.terms-link {
  font-size: 26rpx;
  color: #818cf8;
}

.register-btn {
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

.login-link {
  text-align: center;
  font-size: 26rpx;
  color: #666;
}

.link {
  color: #818cf8;
  font-weight: 500;
}
</style> 