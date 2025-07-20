<template>
  <view class="profile-container">
    <!-- 头部导航 -->
    <view class="header">
      <view class="back-button" @click="goBack">
        <text class="back-icon">←</text>
      </view>
      <text class="header-title">个人资料</text>
      <view class="save-button" @click="saveProfile">
        <text>保存</text>
      </view>
    </view>

    <!-- 个人资料表单 -->
    <view class="profile-form">
      <!-- 头像 -->
      <view class="avatar-section">
        <image
          class="avatar"
          :src="userInfo.avatar || '/static/images/default-avatar.png'"
          @click="chooseAvatar"
        />
        <text class="change-avatar">点击更换头像</text>
      </view>

      <!-- 用户名 -->
      <view class="form-item">
        <text class="form-label">用户名</text>
        <input class="form-input" type="text" v-model="userInfo.username" placeholder="请输入用户名" />
      </view>

      <!-- 性别 -->
      <view class="form-item">
        <text class="form-label">性别</text>
        <picker class="form-picker" @change="onGenderChange" :value="genderIndex" :range="genders">
          <view class="picker-value">{{ userInfo.gender ? (userInfo.gender === 'male' ? '男' : '女') : '请选择' }}</view>
        </picker>
      </view>

      <!-- 生日 -->
      <view class="form-item">
        <text class="form-label">生日</text>
        <picker class="form-picker" mode="date" @change="onBirthdayChange" :value="userInfo.birthday">
          <view class="picker-value">{{ userInfo.birthday || '请选择' }}</view>
        </picker>
      </view>

      <!-- 个性签名 -->
      <view class="form-item">
        <text class="form-label">个性签名</text>
        <textarea class="form-textarea" v-model="userInfo.signature" placeholder="请输入个性签名" />
      </view>

      <!-- 退出登录按钮 -->
      <view class="logout-section">
        <view class="logout-button" @click="handleLogout">
          <text>退出登录</text>
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
      userInfo: {
        username: '',
        avatar: '',
        gender: '',
        birthday: '',
        signature: ''
      },
      genders: ['男', '女'],
      genderIndex: 0,
      loading: false
    };
  },
  onLoad() {
    // 页面加载时获取用户信息
    this.getUserInfo();
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },

    // 获取用户信息
    getUserInfo() {
      // 先从本地获取
      const localUserInfo = storage.getUserInfo();
      if (localUserInfo) {
        this.userInfo = { ...this.userInfo, ...localUserInfo };

        // 设置性别索引
        if (this.userInfo.gender === 'male') {
          this.genderIndex = 0;
        } else if (this.userInfo.gender === 'female') {
          this.genderIndex = 1;
        }
      }

      // 从服务器获取最新信息
      this.loading = true;

      api.user.getProfile()
        .then(res => {
          this.loading = false;

          if (res.code === 200) {
            // 更新用户信息
            this.userInfo = {
              id: res.data.id,
              username: res.data.username,
              avatar: res.data.avatar,
              email: res.data.email,
              gender: res.data.gender,
              birthday: res.data.birthday,
              signature: res.data.signature
            };

            // 设置性别索引
            if (this.userInfo.gender === 'male') {
              this.genderIndex = 0;
            } else if (this.userInfo.gender === 'female') {
              this.genderIndex = 1;
            }
          }
        })
        .catch(err => {
          this.loading = false;
          console.error('获取用户资料失败:', err);

          uni.showToast({
            title: '获取用户资料失败',
            icon: 'none'
          });
        });
    },

    // 选择头像
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];

          // 显示上传中
          uni.showLoading({
            title: '上传中...'
          });

          // 上传头像
          api.user.uploadAvatar(tempFilePath)
            .then(res => {
              uni.hideLoading();

              if (res.code === 200) {
                // 更新头像
                this.userInfo.avatar = res.data.avatarUrl;

                uni.showToast({
                  title: '头像上传成功',
                  icon: 'success'
                });
              } else {
                uni.showToast({
                  title: res.message || '头像上传失败',
                  icon: 'none'
                });
              }
            })
            .catch(err => {
              uni.hideLoading();

              uni.showToast({
                title: '头像上传失败',
                icon: 'none'
              });

              console.error('头像上传失败:', err);
            });
        }
      });
    },

    // 性别选择变化
    onGenderChange(e) {
      this.genderIndex = e.detail.value;
      this.userInfo.gender = this.genderIndex === 0 ? 'male' : 'female';
    },

    // 生日选择变化
    onBirthdayChange(e) {
      this.userInfo.birthday = e.detail.value;
    },

    // 保存个人资料
    saveProfile() {
      // 表单验证
      if (!this.userInfo.username.trim()) {
        uni.showToast({
          title: '用户名不能为空',
          icon: 'none'
        });
        return;
      }

      // 显示保存中
      this.loading = true;
      uni.showLoading({
        title: '保存中...'
      });

      // 构建更新数据
      const updateData = {
        username: this.userInfo.username,
        gender: this.userInfo.gender,
        birthday: this.userInfo.birthday,
        signature: this.userInfo.signature
      };

      // 调用更新接口
      api.user.updateProfile(updateData)
        .then(res => {
          uni.hideLoading();
          this.loading = false;

          if (res.code === 200) {
            // 更新本地存储
            const currentUserInfo = storage.getUserInfo() || {};
            const updatedUserInfo = {
              ...currentUserInfo,
              ...updateData
            };
            storage.saveLoginInfo(storage.getToken(), updatedUserInfo);

            uni.showToast({
              title: '保存成功',
              icon: 'success'
            });

            // 返回上一页
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          } else {
            uni.showToast({
              title: res.message || '保存失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          uni.hideLoading();
          this.loading = false;

          uni.showToast({
            title: '保存失败，请稍后重试',
            icon: 'none'
          });

          console.error('保存个人资料失败:', err);
        });
    },

    // 退出登录
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.performLogout();
          }
        }
      });
    },

    // 执行退出登录
    performLogout() {
      uni.showLoading({
        title: '退出中...'
      });

      // 调用退出登录接口
      api.auth.logout()
        .then(res => {
          uni.hideLoading();

          // 清除本地存储的登录信息
          storage.clearLoginInfo();

          uni.showToast({
            title: '已退出登录',
            icon: 'success'
          });

          // 跳转到登录页面
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login/index'
            });
          }, 1500);
        })
        .catch(err => {
          uni.hideLoading();

          // 即使接口调用失败，也要清除本地登录信息
          storage.clearLoginInfo();

          uni.showToast({
            title: '已退出登录',
            icon: 'success'
          });

          // 跳转到登录页面
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login/index'
            });
          }, 1500);

          console.error('退出登录接口调用失败:', err);
        });
    }
  }
};
</script>

<style>
.profile-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-button {
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

.header-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.save-button {
  padding: 10rpx 30rpx;
  background-color: #818cf8;
  border-radius: 30rpx;
  color: #fff;
  font-size: 28rpx;
}

.profile-form {
  padding: 30rpx;
  background-color: #fff;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background-color: #eee;
  margin-bottom: 20rpx;
}

.change-avatar {
  font-size: 26rpx;
  color: #818cf8;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.form-input {
  width: 100%;
  height: 90rpx;
  background-color: #f5f7fa;
  border-radius: 10rpx;
  padding: 0 30rpx;
  font-size: 30rpx;
  color: #333;
  box-sizing: border-box;
}

.form-picker {
  width: 100%;
  height: 90rpx;
  background-color: #f5f7fa;
  border-radius: 10rpx;
  padding: 0 30rpx;
  display: flex;
  align-items: center;
  box-sizing: border-box;
}

.picker-value {
  font-size: 30rpx;
  color: #333;
}

.form-textarea {
  width: 100%;
  height: 200rpx;
  background-color: #f5f7fa;
  border-radius: 10rpx;
  padding: 20rpx 30rpx;
  font-size: 30rpx;
  color: #333;
  box-sizing: border-box;
}

.logout-section {
  margin-top: 60rpx;
  padding-top: 40rpx;
  border-top: 1rpx solid #eee;
}

.logout-button {
  width: 100%;
  height: 90rpx;
  background-color: #ff4757;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #fff;
  font-weight: bold;
  transition: all 0.3s ease;
}

.logout-button:active {
  transform: scale(0.98);
  background-color: #ff3838;
}
</style>
