<template>
  <view class="mine-container">
    <!-- 顶部用户信息 -->
    <view class="user-info-section" @click="handleAvatarClick">
      <view class="user-avatar">
        <view v-if="!userInfo.avatar" class="default-avatar">
          <text>{{userInfo.username ? userInfo.username.substr(0, 1) : '游'}}</text>
        </view>
        <image v-else class="avatar-image" :src="userInfo.avatar" mode="aspectFill"></image>
      </view>
      <view class="user-details">
        <text class="user-name">{{userInfo.username || '游客'}}</text>
        <text class="user-id">{{isLoggedIn ? 'ID: ' + userInfo.id : '点击登录账号'}}</text>
      </view>
    </view>
    
    <!-- 中间内容区域 - 暂时为空 -->
    <view class="content-section">
      <view class="empty-content">
        <text class="empty-text">更多功能开发中...</text>
      </view>
    </view>
    
    <!-- 底部导航栏 -->
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: activeTab === 'todo' }" @click="switchTab('todo')">
        <text class="tab-icon">📝</text>
        <text class="tab-text">待办</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'stats' }" @click="switchTab('stats')">
        <text class="tab-icon">📊</text>
        <text class="tab-text">统计</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'mine' }" @click="switchTab('mine')">
        <text class="tab-icon">👤</text>
        <text class="tab-text">我的</text>
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
      activeTab: 'mine',
      isLoggedIn: false,
      userInfo: {
        username: '游客',
        id: '',
        avatar: ''
      },
      loading: false
    }
  },
  onShow() {
    // 每次页面显示时检查登录状态
    this.checkLoginStatus();
  },
  methods: {
    checkLoginStatus() {
      // 获取登录状态
      const isLoggedIn = storage.isLoggedIn();
      this.isLoggedIn = isLoggedIn;
      
      if (isLoggedIn) {
        // 获取本地存储的用户信息
        const userInfo = storage.getUserInfo() || {};
        this.userInfo = {
          ...this.userInfo,
          ...userInfo
        };
        
        // 从服务器获取最新的用户信息
        this.fetchUserProfile();
      } else {
        // 未登录状态下的默认值
        this.userInfo = {
          username: '游客',
          id: '',
          avatar: ''
        };
      }
    },
    
    // 获取用户资料
    fetchUserProfile() {
      if (!this.isLoggedIn) return;
      
      this.loading = true;
      
      api.user.getProfile()
        .then(res => {
          this.loading = false;
          
          if (res.code === 200) {
            // 更新用户信息
            const userInfo = {
              id: res.data.id,
              username: res.data.username,
              avatar: res.data.avatar,
              email: res.data.email,
              gender: res.data.gender,
              birthday: res.data.birthday,
              signature: res.data.signature
            };
            
            // 更新本地存储
            storage.saveLoginInfo(storage.getToken(), userInfo);
            
            // 更新当前页面数据
            this.userInfo = {
              ...this.userInfo,
              ...userInfo
            };
          }
        })
        .catch(err => {
          this.loading = false;
          console.error('获取用户资料失败:', err);
        });
    },
    
    handleAvatarClick() {
      if (this.isLoggedIn) {
        // 已登录，跳转到个人资料页面
        uni.navigateTo({
          url: '/pages/profile/index'
        });
      } else {
        // 未登录，跳转到登录页面
        uni.navigateTo({
          url: '/pages/login/index'
        });
      }
    },
    
    // 退出登录
    logout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除登录信息
            storage.clearLoginInfo();
            
            // 更新状态
            this.isLoggedIn = false;
            this.userInfo = {
              username: '游客',
              id: '',
              avatar: ''
            };
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            });
          }
        }
      });
    },
    
    switchTab(tab) {
      if (tab === this.activeTab) return;
      
      if (tab === 'todo') {
        uni.redirectTo({
          url: '/pages/index/index'
        });
      } else if (tab === 'stats') {
        uni.redirectTo({
          url: '/pages/stats/index'
        });
      }
    }
  }
}
</script>

<style>
.mine-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* 用户信息部分 */
.user-info-section {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: linear-gradient(to right, #4a90e2, #6eb4f7);
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 4rpx solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.2);
}

.default-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(45deg, #fe2c55, #ff6b81);
  color: white;
  font-size: 60rpx;
  font-weight: bold;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.user-details {
  margin-left: 30rpx;
}

.user-name {
  font-size: 36rpx;
  color: #ffffff;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.user-id {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
}

/* 内容区域 */
.content-section {
  flex: 1;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-content {
  text-align: center;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 底部导航栏 */
.tab-bar {
  display: flex;
  height: 100rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #eeeeee;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999999;
}

.tab-icon {
  font-size: 40rpx;
  margin-bottom: 4rpx;
}

.tab-text {
  font-size: 24rpx;
}

.tab-item.active {
  color: #4a90e2;
}
</style> 