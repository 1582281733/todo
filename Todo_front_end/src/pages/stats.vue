<template>
  <view class="stats-container">
    <!-- 顶部状态栏占位 -->
    <view class="status-bar-placeholder"></view>

    <!-- 当日统计卡片 -->
    <view class="daily-stats-card">
      <!-- 标题行 -->
      <view class="card-header">
        <text class="card-title">当日专注</text>
        <text class="card-date">{{ currentDateStr }}</text>
        
        <!-- 日期导航箭头 -->
        <view class="date-navigation">
          <text class="nav-arrow" @click="prevDate">◀</text>
          <text class="nav-arrow" @click="nextDate">▶</text>
        </view>
      </view>

      <!-- 统计数据行 -->
      <view class="stats-data">
        <view class="stat-group">
          <text class="stat-label">次数</text>
          <text class="stat-number">{{ overview.totalTasks || 0 }}</text>
        </view>
        
        <view class="stat-group time-group">
          <text class="stat-label">时长</text>
          <view class="time-display">
            <text class="stat-number">{{ hours }}</text>
            <text class="time-unit">小时</text>
            <text class="stat-number">{{ minutes }}</text>
            <text class="time-unit">分钟</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 底部导航栏 -->
    <bottom-navigation :active-tab="'stats'"></bottom-navigation>
  </view>
</template>

<script>
import api from '../utils/api.js';
import storage from '../utils/storage.js';
import BottomNavigation from "../components/BottomNavigation";

export default {
  components: { BottomNavigation },
  data() {
    return {
      currentDate: null,
      overview: {
        totalTime: 0,     // 总时长(分钟)
        totalTasks: 0,    // 总任务数(次数)
        completionRate: 0 // 完成率
      },
      loading: false
    };
  },
  computed: {
    currentDateStr() {
      if (!this.currentDate) return '';
      const y = this.currentDate.getFullYear();
      const m = String(this.currentDate.getMonth() + 1).padStart(2, '0');
      const d = String(this.currentDate.getDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    },
    hours() {
      return Math.floor((this.overview.totalTime || 0) / 60);
    },
    minutes() {
      return (this.overview.totalTime || 0) % 60;
    }
  },
  onLoad() {
    this.initToday();
    this.loadStats();
  },
  methods: {
    initToday() {
      const now = new Date();
      this.currentDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    },

    prevDate() {
      const y = this.currentDate.getFullYear();
      const m = this.currentDate.getMonth();
      const d = this.currentDate.getDate();
      this.currentDate = new Date(y, m, d - 1);
      this.loadStats();
    },

    nextDate() {
      const today = new Date();
      const nextDay = new Date(this.currentDate);
      nextDay.setDate(nextDay.getDate() + 1);
      if (nextDay <= today) {
        this.currentDate = nextDay;
        this.loadStats();
      }
    },

    loadStats() {
      // 检查登录状态
      if (!storage.isLoggedIn()) {
        uni.showToast({ 
          title: '请先登录', 
          icon: 'none' 
        });
        // 跳转到登录页面
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/login'
          });
        }, 1500);
        return;
      }

      this.loading = true;
      
      // 调用后端API获取每日统计数据
      api.stats.getDaily({ 
        date: this.currentDateStr 
      })
      .then(res => {
        console.log('统计数据响应:', res);
        this.loading = false;
        
        if (res.code === 200 && res.data) {
          // 更新统计数据
          this.overview = {
            totalTime: res.data.overview?.totalTime || 0,
            totalTasks: res.data.overview?.totalTasks || 0,
            completionRate: res.data.overview?.completionRate || 0
          };
          
          console.log('更新后的overview:', this.overview);
        } else {
          console.warn('响应格式异常:', res);
          this.resetStats();
        }
      })
      .catch(err => {
        this.loading = false;
        console.error('获取统计数据失败:', err);
        
        // 显示错误提示
        uni.showToast({ 
          title: '获取统计数据失败', 
          icon: 'none' 
        });
        
        // 重置为默认值
        this.resetStats();
      });
    },

    // 重置统计数据为默认值
    resetStats() {
      this.overview = {
        totalTime: 0,
        totalTasks: 0,
        completionRate: 0
      };
    }
  }
};
</script>

<style>
/* 页面整体背景 */
.stats-container {
  min-height: 100vh;
  background-color: #ffffff;
  position: relative;
}

/* 状态栏占位 */
.status-bar-placeholder {
  height: var(--status-bar-height);
}

/* 当日统计卡片 */
.daily-stats-card {
  margin: 32rpx;
  padding: 48rpx 40rpx;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(33, 150, 243, 0.15);
  border: 1px solid rgba(33, 150, 243, 0.1);
}

/* 卡片标题行 */
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 48rpx;
  position: relative;
}

.card-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1565c0;
  margin-right: 24rpx;
}

.card-date {
  font-size: 30rpx;
  color: #1976d2;
  font-weight: 500;
}

/* 日期导航箭头 */
.date-navigation {
  position: absolute;
  right: 0;
  display: flex;
  gap: 24rpx;
}

.nav-arrow {
  font-size: 32rpx;
  color: #1976d2;
  padding: 8rpx;
}

/* 统计数据行 */
.stats-data {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.stat-group {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 28rpx;
  color: #1976d2;
  margin-bottom: 20rpx;
  font-weight: 500;
}

.stat-number {
  font-size: 84rpx;
  font-weight: 700;
  color: #0d47a1;
  line-height: 1;
}

/* 时长显示 */
.time-group {
  align-items: center;
}

.time-display {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.time-unit {
  font-size: 24rpx;
  color: #1976d2;
  font-weight: 500;
  margin-right: 16rpx;
}

/* 加载状态 */
.loading-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #e3f2fd;
  border-top: 4rpx solid #1976d2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #1976d2;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
