<template>
  <view class="container">
    <!-- 顶部用户信息 -->
    <view class="top-nav user-info-section" @click="handleAvatarClick">
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

    <!-- 内容区 -->
    <view class="content-section">
      <view class="card">
        <text class="empty-text"></text>
      </view>
    </view>

    <bottom-navigation :active-tab="'mine'"></bottom-navigation>
  </view>
</template>

<script>
import api from '../utils/api.js';
import storage from '../utils/storage.js';
import BottomNavigation from "../components/BottomNavigation";

export default {
  components: {BottomNavigation},
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
          url: '/pages/profile'
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
        uni.reLaunch({
          url: '/pages/index'
        });
      } else if (tab === 'stats') {
        uni.reLaunch({
          url: '/pages/stats'
        });
      }
    }
  }
}
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f7f8fa;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);
  position: relative;
}

.top-nav.user-info-section {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: #fff;
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 4rpx solid #f0f0f0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  background: #f5f5f5;
}

.default-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #4a90e2;
  color: white;
  font-size: 48rpx;
  font-weight: bold;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.user-details {
  margin-left: 24rpx;
}

.user-name {
  font-size: 32rpx;
  color: #222;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.user-id {
  font-size: 24rpx;
  color: #888;
}

.content-section {
  flex: 1;
  padding: 32rpx 24rpx 120rpx 24rpx;
  background: #f7f8fa;
}

.card {
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
  padding: 48rpx 0;
  text-align: center;
}

.empty-text {
  color: #bbb;
  font-size: 28rpx;
}

/* 还原底部导航栏样式 */
.tab-bar {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 100rpx;
  background: #fff;
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
  box-shadow: 0 -2rpx 8rpx rgba(0,0,0,0.04);
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  max-width: 500px;
  margin: 0 auto;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #888;
  font-size: 24rpx;
  padding: 8rpx 0;
}

.tab-item.active {
  color: #4a90e2;
}

.tab-icon {
  font-size: 38rpx;
  margin-bottom: 2rpx;
}

.tab-text {
  font-size: 22rpx;
  margin-top: 2rpx;
}

/* 其他样式保持不变 */
</style>
