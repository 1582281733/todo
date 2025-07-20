<template>
  <view class="stats-container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="tab-group">
        <view
          class="stats-tab-item"
          :class="{ active: activeTab === 'daily' }"
          @click="switchTab('daily')"
        >
          <text>日统计</text>
        </view>
        <view
          class="stats-tab-item"
          :class="{ active: activeTab === 'weekly' }"
          @click="switchTab('weekly')"
        >
          <text>周统计</text>
        </view>
        <view
          class="stats-tab-item"
          :class="{ active: activeTab === 'monthly' }"
          @click="switchTab('monthly')"
        >
          <text>月统计</text>
        </view>
      </view>
    </view>

    <!-- 日期选择器 -->
    <view class="date-picker">
      <view class="date-arrow" @click="prevDate">
        <text>◀</text>
      </view>
      <view class="date-display" @click="showDatePicker">
        <text>{{ displayDate }}</text>
      </view>
      <view class="date-arrow" @click="nextDate">
        <text>▶</text>
      </view>
    </view>

    <!-- 统计概览 -->
    <view class="stats-overview">
      <view class="overview-item">
        <text class="overview-value">{{ overview.totalTime }}</text>
        <text class="overview-label">总时长(分钟)</text>
      </view>
      <view class="overview-item">
        <text class="overview-value">{{ overview.totalTasks }}</text>
        <text class="overview-label">总任务数</text>
      </view>
      <view class="overview-item">
        <text class="overview-value">{{ overview.completionRate }}%</text>
        <text class="overview-label">完成率</text>
      </view>
    </view>

    <!-- 时间分布图 -->
    <view class="distribution-chart">
      <view class="chart-title">
        <text>时间分布</text>
      </view>
      <view class="chart-container">
        <!-- 日统计-小时分布 -->
        <view v-if="activeTab === 'daily'" class="hourly-chart">
          <view
            v-for="(item, index) in hourlyDistribution"
            :key="stats"
            class="hour-bar"
          >
            <view
              class="bar-fill"
              :style="{ height: `${getBarHeight(item.duration)}rpx` }"
            ></view>
            <text class="bar-label">{{ item.hour }}</text>
          </view>
        </view>

        <!-- 周统计-日分布 -->
        <view v-if="activeTab === 'weekly'" class="daily-chart">
          <view
            v-for="(item, index) in dailyDistribution"
            :key="stats"
            class="day-bar"
          >
            <view
              class="bar-fill"
              :style="{ height: `${getBarHeight(item.duration)}rpx` }"
            ></view>
            <text class="bar-label">{{ getDayLabel(item.day) }}</text>
          </view>
        </view>

        <!-- 月统计-周分布 -->
        <view v-if="activeTab === 'monthly'" class="weekly-chart">
          <view
            v-for="(item, index) in weeklyDistribution"
            :key="stats"
            class="week-bar"
          >
            <view
              class="bar-fill"
              :style="{ height: `${getBarHeight(item.duration)}rpx` }"
            ></view>
            <text class="bar-label">第{{ item.week }}周</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 任务列表 -->
    <view class="task-list">
      <view class="list-title">
        <text>任务列表</text>
      </view>
      <view class="list-container">
        <view
          v-for="(task, index) in taskList"
          :key="stats"
          class="task-item"
          :style="{ backgroundColor: task.backgroundColor || '#818cf8' }"
        >
          <view class="task-info">
            <text class="task-name">{{ task.name }}</text>
            <text class="task-duration">{{ task.duration }}分钟</text>
          </view>
          <text class="task-time">{{ getTaskTimeInfo(task) }}</text>
        </view>

        <view v-if="taskList.length === 0" class="empty-list">
          <text>暂无数据</text>
        </view>
      </view>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-mask">
      <view class="loading-spinner"></view>
    </view>

    <!-- 移除原有的底部导航栏，使用组件 -->
    <bottom-navigation :active-tab="'stats'"></bottom-navigation>
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
      activeTab: 'daily',
      activeNavTab: 'stats', // 底部导航栏当前选中的tab
      currentDate: null, // 将在mounted中初始化
      loading: false,

      // 统计数据
      overview: {
        totalTime: 0,
        totalTasks: 0,
        completionRate: 0
      },
      hourlyDistribution: [],
      dailyDistribution: [],
      weeklyDistribution: [],
      taskList: []
    };
  },
  computed: {
    displayDate() {
      if (this.activeTab === 'daily') {
        return this.formatDate(this.currentDate, 'YYYY-MM-DD');
      } else if (this.activeTab === 'weekly') {
        const weekStart = this.getWeekStart(this.currentDate);
        const weekEnd = this.getWeekEnd(this.currentDate);
        return `${this.formatDate(weekStart, 'MM.DD')} - ${this.formatDate(weekEnd, 'MM.DD')}`;
      } else {
        return this.formatDate(this.currentDate, 'YYYY-MM');
      }
    }
  },
  onLoad() {
    // 初始化今天日期
    this.initToday();
    // 页面加载时获取统计数据
    this.loadStats();
  },
  methods: {
    // 切换统计类型
    switchTab(tab) {
      if (this.activeTab === tab) return;
      this.activeTab = tab;
      this.loadStats();
    },

    // 切换底部导航栏
    switchNavTab(tab) {
      if (tab === this.activeNavTab) return;

      if (tab === 'todo') {
        uni.redirectTo({
          url: '/pages/index'
        });
      } else if (tab === 'mine') {
        uni.redirectTo({
          url: '/pages/mine'
        });
      }
    },

    // 上一个日期
    prevDate() {
      if (this.activeTab === 'daily') {
        // 上一天
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        const day = this.currentDate.getDate();
        this.currentDate = new Date(year, month, day - 1);
        this.loadStats();
      } else if (this.activeTab === 'weekly') {
        // 上一周
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        const day = this.currentDate.getDate();
        this.currentDate = new Date(year, month, day - 7);
      } else {
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        this.currentDate = new Date(year, month - 1, 1);
      }
      this.loadStats();
    },

    // 下一个日期
    nextDate() {
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

      if (this.activeTab === 'daily') {
        // 下一天
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        const day = this.currentDate.getDate();
        const nextDay = new Date(year, month, day + 1);
        if (nextDay <= today) {
          this.currentDate = nextDay;
          this.loadStats();
        }
      } else if (this.activeTab === 'weekly') {
        // 下一周
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        const day = this.currentDate.getDate();
        const nextWeek = new Date(year, month, day + 7);
        if (this.getWeekStart(nextWeek) <= today) {
          this.currentDate = nextWeek;
          this.loadStats();
        }
      } else {
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();
        const nextMonth = new Date(year, month + 1, 1);
        if (nextMonth <= today) {
          this.currentDate = nextMonth;
          this.loadStats();
        }
      }
    },

    // 显示日期选择器
    showDatePicker() {
      let mode = 'date';
      if (this.activeTab === 'monthly') {
        mode = 'year-month';
      }

      uni.showToast({
        title: '日期选择功能开发中',
        icon: 'none'
      });
    },

    // 加载统计数据
    loadStats() {
      // 检查登录状态
      if (!storage.isLoggedIn()) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });

        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/login/index'
          });
        }, 1500);

        return;
      }

      this.loading = true;

      let apiCall;
      let params = {};

      if (this.activeTab === 'daily') {
        apiCall = api.stats.getDaily;
        params = {
          date: this.formatDate(this.currentDate, 'YYYY-MM-DD')
        };
      } else if (this.activeTab === 'weekly') {
        apiCall = api.stats.getWeekly;
        params = {
          startDate: this.formatDate(this.getWeekStart(this.currentDate), 'YYYY-MM-DD')
        };
      } else {
        apiCall = api.stats.getMonthly;
        params = {
          month: this.formatDate(this.currentDate, 'YYYY-MM')
        };
      }

      apiCall(params)
        .then(res => {
          this.loading = false;

          if (res.code === 200) {
            // 更新统计数据
            this.overview = res.data.overview;

            if (this.activeTab === 'daily') {
              this.hourlyDistribution = res.data.hourlyDistribution;
            } else if (this.activeTab === 'weekly') {
              this.dailyDistribution = res.data.dailyDistribution;
            } else {
              this.weeklyDistribution = res.data.weeklyDistribution;
            }

            this.taskList = res.data.taskList;
          } else {
            uni.showToast({
              title: res.message || '获取统计数据失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          this.loading = false;

          uni.showToast({
            title: '获取统计数据失败',
            icon: 'none'
          });

          console.error('获取统计数据失败:', err);
        });
    },

    // 获取柱状图高度
    getBarHeight(duration) {
      if (!duration) return 0;
      // 最大高度为200rpx
      const maxHeight = 200;
      // 根据当前分布数据中的最大值计算比例
      let maxDuration = 0;

      if (this.activeTab === 'daily') {
        maxDuration = Math.max(...this.hourlyDistribution.map(item => item.duration), 60);
      } else if (this.activeTab === 'weekly') {
        maxDuration = Math.max(...this.dailyDistribution.map(item => item.duration), 60);
      } else {
        maxDuration = Math.max(...this.weeklyDistribution.map(item => item.duration), 60);
      }

      return Math.max(20, (duration / maxDuration) * maxHeight);
    },

    // 获取星期几标签
    getDayLabel(day) {
      const labels = ['一', '二', '三', '四', '五', '六', '日'];
      return labels[day - 1] || day;
    },

    // 获取任务时间信息
    getTaskTimeInfo(task) {
      if (this.activeTab === 'daily') {
        return task.timeRange || '';
      } else if (this.activeTab === 'weekly') {
        return task.days || '';
      } else {
        return task.period || '';
      }
    },

    // 格式化日期
    formatDate(date, format) {
      if (!date) return '';

      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');

      if (format === 'YYYY-MM-DD') {
        return `${year}-${month}-${day}`;
      } else if (format === 'YYYY-MM') {
        return `${year}-${month}`;
      } else if (format === 'MM.DD') {
        return `${month}.${day}`;
      }

      return `${year}-${month}-${day}`;
    },

    // 获取周开始日期（周一）
    getWeekStart(date) {
      const newDate = new Date(date);  // 创建副本，避免修改原始日期
      const day = newDate.getDay();
      const diff = newDate.getDate() - day + (day === 0 ? -6 : 1);
      return new Date(newDate.setDate(diff));
    },

    // 获取周结束日期（周日）
    getWeekEnd(date) {
      const start = this.getWeekStart(new Date(date));
      return new Date(start.getTime() + 6 * 24 * 60 * 60 * 1000);
    },

    // 判断是否为今天
    isToday(date) {
      const today = new Date();
      return date.getFullYear() === today.getFullYear() &&
             date.getMonth() === today.getMonth() &&
             date.getDate() === today.getDate();
    },

    // 初始化今天日期
    initToday() {
      const now = new Date();
      this.currentDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    }
  }
};
</script>

<style>
.stats-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  /* 统一背景色、阴影等视觉风格 */
  background-color: #f7f8fa;           /* ← 与 mine.vue 一致 */
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.08);  /* ← 阴影参数同步 */
  position: relative;
  overflow: hidden;                 /* ← 新增：防止整体超高 */
}

.nav-bar {
  background-color: #fff;
  padding: 20rpx 0;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.tab-group {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.stats-tab-item {
  padding: 10rpx 30rpx;
  border-radius: 30rpx;
  font-size: 28rpx;
  color: #666;
}

.stats-tab-item.active {
  background-color: #818cf8;
  color: #fff;
}

.date-picker {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30rpx 0;
  background-color: #fff;
  margin-top: 20rpx;
}

.date-arrow {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #818cf8;
}

.date-arrow.disabled {
  color: #ccc;
  pointer-events: none;
}

.date-display {
  padding: 0 40rpx;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.stats-overview {
  display: flex;
  justify-content: space-around;
  padding: 30rpx 20rpx;
  background-color: #fff;
  margin-top: 20rpx;
}

.overview-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.overview-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.overview-label {
  font-size: 24rpx;
  color: #666;
}

.distribution-chart {
  background-color: #fff;
  margin-top: 20rpx;
  padding: 30rpx 20rpx;
}

.chart-title, .list-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  padding-left: 20rpx;
  border-left: 8rpx solid #818cf8;
}

.chart-container {
  height: 300rpx;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 0 20rpx;
}

.hourly-chart, .daily-chart, .weekly-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  width: 100%;
  height: 100%;
}

.hour-bar, .day-bar, .week-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40rpx;
}

.bar-fill {
  width: 100%;
  background-color: #818cf8;
  border-radius: 10rpx 10rpx 0 0;
}

.bar-label {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #666;
}

.task-list {
  background-color: #fff;
  margin-top: 20rpx;
  /* 底部内边距增加，避免被 BottomNavigation 遮挡 */
  padding: 30rpx 20rpx 120rpx 20rpx;   /* ← bottom = 120rpx */
  flex: 1;                            /* 让内容区撑满剩余高度 */
  overflow-y: auto;                  /* ← 内部滚动 */
}

.list-container {
  padding: 0 20rpx;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
  color: #fff;
}

.task-info {
  display: flex;
  flex-direction: column;
}

.task-name {
  font-size: 28rpx;
  margin-bottom: 10rpx;
}

.task-duration {
  font-size: 24rpx;
  opacity: 0.8;
}

.task-time {
  font-size: 24rpx;
}

.empty-list {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200rpx;
  color: #999;
  font-size: 28rpx;
}

.loading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid #818cf8;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 底部导航栏样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 0;
  background-color: #ffffff;
  border-top: 1px solid #f1f5f9;

  /* === 新增：固定到底部并保持与 mine.vue 一致的视觉 === */
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  max-width: 500px;
  margin: 0 auto;
  z-index: 1000;
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
  box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.nav-item {
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
