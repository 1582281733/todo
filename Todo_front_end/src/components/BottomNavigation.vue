<template>
  <view class="bottom-nav">
    <view class="nav-item" @click="switchTab('todo')">
      <view class="nav-icon card-icon" :class="{ active: currentTab === 'todo' }"></view>
      <text class="nav-text" :class="{ active: currentTab === 'todo' }">待办</text>
    </view>
    <view class="nav-item" @click="switchTab('stats')">
      <view class="nav-icon chart-icon" :class="{ active: currentTab === 'stats' }"></view>
      <text class="nav-text" :class="{ active: currentTab === 'stats' }">统计</text>
    </view>
    <view class="nav-item" @click="switchTab('mine')">
      <view class="nav-icon settings-icon" :class="{ active: currentTab === 'mine' }"></view>
      <text class="nav-text" :class="{ active: currentTab === 'mine' }">我的</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'BottomNavigation',
  props: {
    activeTab: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      currentTab: 'todo'
    };
  },
  mounted() {
    this.detectCurrentPage();
  },
  methods: {
    // 自动检测当前页面
    detectCurrentPage() {
      if (this.activeTab) {
        // 如果父组件传递了 activeTab，优先使用
        this.currentTab = this.activeTab;
        return;
      }

      // 自动检测当前页面路径
      const pages = getCurrentPages();
      if (pages.length > 0) {
        const currentPage = pages[pages.length - 1];
        const route = currentPage.route;

        if (route.includes('pages/index/index')) {
          this.currentTab = 'todo';
        } else if (route.includes('pages/stats/index')) {
          this.currentTab = 'stats';
        } else if (route.includes('pages/mine')) {
          this.currentTab = 'mine';
        }
      }
    },

    switchTab(tab) {
      if (tab === this.currentTab) return;

      // 更新当前选中状态
      this.currentTab = tab;

      // 触发父组件的切换事件
      this.$emit('switch-tab', tab);

      // 页面跳转 - 使用 reLaunch 确保清除页面栈
      if (tab === 'todo') {
        uni.reLaunch({
          url: '/pages/index'
        });
      } else if (tab === 'stats') {
        uni.reLaunch({
          url: '/pages/stats'
        });
      } else if (tab === 'mine') {
        uni.reLaunch({
          url: '/pages/mine'
        });
      }
    }
  },
  watch: {
    // 监听 activeTab prop 变化
    activeTab(newVal) {
      if (newVal) {
        this.currentTab = newVal;
      }
    }
  }
}
</script>

<style>
/* 底部导航栏样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 0;
  background-color: #ffffff;
  border-top: 1px solid #f1f5f9;
  
  /* === 新增：固定到底部 === */
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10rpx 0;
}

.nav-icon {
  width: 40rpx;
  height: 40rpx;
  margin-bottom: 8rpx;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}

.card-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a1a1aa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect width='20' height='14' x='2' y='5' rx='2'%3E%3C/rect%3E%3Cline x1='2' x2='22' y1='10' y2='10'%3E%3C/line%3E%3C/svg%3E");
}

.card-icon.active {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%2310b981' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect width='20' height='14' x='2' y='5' rx='2'%3E%3C/rect%3E%3Cline x1='2' x2='22' y1='10' y2='10'%3E%3C/line%3E%3C/svg%3E");
}

.chart-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a1a1aa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cline x1='12' x2='12' y1='20' y2='10'%3E%3C/line%3E%3Cline x1='18' x2='18' y1='20' y2='4'%3E%3C/line%3E%3Cline x1='6' x2='6' y1='20' y2='16'%3E%3C/line%3E%3C/svg%3E");
}

.chart-icon.active {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%2310b981' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cline x1='12' x2='12' y1='20' y2='10'%3E%3C/line%3E%3Cline x1='18' x2='18' y1='20' y2='4'%3E%3C/line%3E%3Cline x1='6' x2='6' y1='20' y2='16'%3E%3C/line%3E%3C/svg%3E");
}

.settings-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a1a1aa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z'%3E%3C/path%3E%3Ccircle cx='12' cy='12' r='3'%3E%3C/circle%3E%3C/svg%3E");
}

.settings-icon.active {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%2310b981' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z'%3E%3C/path%3E%3Ccircle cx='12' cy='12' r='3'%3E%3C/circle%3E%3C/svg%3E");
}

.nav-text {
  font-size: 20rpx;
  color: #71717a;
  font-weight: 500;
}
</style>
