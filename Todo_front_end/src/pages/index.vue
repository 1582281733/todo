<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="top-nav">
      <view class="nav-tabs">
        <button class="nav-tab active">待办清单</button>
      </view>
      <view class="notification-btn" @click="showNotification">
        <view class="notification-icon">
          <view class="notification-dot"></view>
        </view>
      </view>
    </view>

    <!-- 待办列表 -->
    <view class="todo-section">
      <text class="section-title"></text>
      <scroll-view class="todo-list" scroll-y>
        <view
          class="todo-card"
          v-for="(item, index) in todoList"
          :key="index"
          :style="{
            backgroundImage: 'url(' + cardImages[index % cardImages.length] + ')'
          }"
        >
          <view class="card-mask"></view>
          <view class="card-content">
            <view class="card-title">{{ item.name }}</view>
            <view class="card-time">{{ item.duration }} 分钟</view>
          </view>
          <view class="card-action" @click="startTodo(index)">开始</view>
        </view>

        <view v-if="todoList.length === 0" class="empty-tip">
          <text>暂无待办事项</text>
        </view>
      </scroll-view>
    </view>

    <!-- 添加待办按钮 -->
    <view class="add-button" @click="showAddTodoDialog">
      <text>+</text>
    </view>

    <bottom-navigation :active-tab="'todo'"></bottom-navigation>

    <!-- 添加待办对话框 -->
    <add-todo-dialog
      :visible="addTodoVisible"
      @update-visible="updateDialogVisible"
      @confirm="handleAddTodo"
      @cancel="addTodoVisible = false"
    />
  </view>
</template>

<script>
import api from '../utils/api.js';
import storage from '../utils/storage.js';
import AddTodoDialog from '../components/AddTodoDialog.vue';
import BottomNavigation from "../components/BottomNavigation";

export default {
  components: {
    BottomNavigation,
    AddTodoDialog
  },
  data() {
    return {
      activeTab: 'todo',
      addTodoVisible: false,
      todoList: [],
      statusFilter: 'all',
      page: 1,
      pageSize: 10,
      totalItems: 0,
      loading: false,
      refreshing: false,
      loadingMore: false,
      hasMore: true,
      cardImages: [
        '/static/1001.jpg',
        '/static/wallhaven-j3x3x5.jpg',
        '/static/wallhaven-gpwp1d.jpg',
        '/static/3.jpg',
        '/static/5.png',
        '/static/4.jpg'
      ]
    };
  },
  computed: {
    filteredTodos() {
      return this.todoList;
    }
  },
  onLoad() {
    // 页面加载时获取待办事项列表
    this.loadTodos();
  },
  onPullDownRefresh() {
    // 下拉刷新
    this.refreshTodos();
  },
  onReachBottom() {
    // 上拉加载更多
    this.loadMoreTodos();
  },
  methods: {
    switchTab(tab) {
      if (tab === this.activeTab) return;

      if (tab === 'mine') {
        uni.redirectTo({
          url: '/pages/mine'
        });
      } else if (tab === 'stats') {
        uni.redirectTo({
          url: '/pages/stats'
        });
      }
    },
    showNotification() {
      uni.showToast({
        title: '暂无通知',
        icon: 'none'
      });
    },
    showAddTodoDialog() {
      this.addTodoVisible = true;
    },
    updateDialogVisible(value) {
      this.addTodoVisible = value;
    },
    handleAddTodo(todoData) {
      // 转换数据格式以匹配后端接口
      const apiData = {
        name: todoData.title,
        type: todoData.type,
        timerMode: todoData.timerType,
        duration: todoData.time,
        backgroundColor: todoData.bgColor,
        description: todoData.description || ''
      };

      // 调用创建待办事项接口
      api.todos.create(apiData).then(res => {
        if (res.code === 201) {
          uni.showToast({
            title: '创建成功',
            icon: 'success'
          });

          // 关闭对话框
          this.addTodoVisible = false;

          // 刷新列表
          this.refreshTodos();
        } else {
          uni.showToast({
            title: res.message || '创建失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.showToast({
          title: '创建失败',
          icon: 'none'
        });

        console.error('创建待办事项失败:', err);
      });
    },
    startTodo(index) {
      // 跳转到计时页面
      const todo = this.todoList[index];
      uni.navigateTo({
        url: `/pages/timer?todoId=${todo.id}&name=${encodeURIComponent(todo.name)}&duration=${todo.duration}&timerMode=${todo.timerMode}`
      });
    },
    // 获取待办事项列表
    loadTodos() {
      if (this.loading) return;

      this.loading = true;

      // 检查登录状态
      if (!storage.isLoggedIn()) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });

        this.loading = false;
        return;
      }

      // 调用获取待办事项列表接口
      api.todos.getList({
        page: this.page,
        pageSize: this.pageSize,
        status: this.statusFilter
      }).then(res => {
        this.loading = false;

        if (res.code === 200) {
          this.todoList = res.data.list;
          this.totalItems = res.data.total;
          this.hasMore = this.todoList.length < this.totalItems;
        } else {
          uni.showToast({
            title: res.message || '获取待办事项失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        this.loading = false;

        uni.showToast({
          title: '获取待办事项失败',
          icon: 'none'
        });

        console.error('获取待办事项失败:', err);
      });
    },

    // 刷新待办事项列表
    refreshTodos() {
      this.refreshing = true;
      this.page = 1;

      // 调用获取待办事项列表接口
      api.todos.getList({
        page: this.page,
        pageSize: this.pageSize,
        status: this.statusFilter
      }).then(res => {
        this.refreshing = false;
        uni.stopPullDownRefresh();

        if (res.code === 200) {
          this.todoList = res.data.list;
          this.totalItems = res.data.total;
          this.hasMore = this.todoList.length < res.data.total;

          uni.showToast({
            title: '刷新成功',
            icon: 'success'
          });
        } else {
          uni.showToast({
            title: res.message || '刷新失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        this.refreshing = false;
        uni.stopPullDownRefresh();

        uni.showToast({
          title: '刷新失败',
          icon: 'none'
        });

        console.error('刷新待办事项失败:', err);
      });
    },

    // 加载更多待办事项
    loadMoreTodos() {
      if (this.loadingMore || !this.hasMore) return;

      this.loadingMore = true;
      this.page += 1;

      // 调用获取待办事项列表接口
      api.todos.getList({
        page: this.page,
        pageSize: this.pageSize,
        status: this.statusFilter
      }).then(res => {
        this.loadingMore = false;

        if (res.code === 200) {
          const newTodos = res.data.list;
          this.todoList = [...this.todoList, ...newTodos];
          this.hasMore = this.todoList.length < res.data.total;
        } else {
          this.page -= 1; // 恢复页码

          uni.showToast({
            title: res.message || '加载更多失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        this.loadingMore = false;
        this.page -= 1; // 恢复页码

        uni.showToast({
          title: '加载更多失败',
          icon: 'none'
        });

        console.error('加载更多待办事项失败:', err);
      });
    },

    // 切换待办事项状态过滤器
    changeStatusFilter(status) {
      if (this.statusFilter === status) return;

      this.statusFilter = status;
      this.page = 1;
      this.loadTodos();
    },

    // 完成待办事项
    completeTodo(id) {
      uni.showLoading({
        title: '处理中...'
      });

      // 调用完成待办事项接口
      api.todos.complete(id).then(res => {
        uni.hideLoading();

        if (res.code === 200) {
          uni.showToast({
            title: '已完成',
            icon: 'success'
          });

          // 更新本地数据
          const index = this.todoList.findIndex(todo => todo.id === id);
          if (index !== -1) {
            this.todoList[index].status = 'completed';
            this.todoList[index].completedAt = res.data.completedAt;
          }
        } else {
          uni.showToast({
            title: res.message || '操作失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();

        uni.showToast({
          title: '操作失败',
          icon: 'none'
        });

        console.error('完成待办事项失败:', err);
      });
    },

    // 删除待办事项
    deleteTodo(id) {
      uni.showModal({
        title: '提示',
        content: '确定要删除这个待办事项吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '删除中...'
            });

            // 调用删除待办事项接口
            api.todos.delete(id).then(res => {
              uni.hideLoading();

              if (res.code === 200) {
                uni.showToast({
                  title: '删除成功',
                  icon: 'success'
                });

                // 更新本地数据
                this.todoList = this.todoList.filter(todo => todo.id !== id);
              } else {
                uni.showToast({
                  title: res.message || '删除失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              uni.hideLoading();

              uni.showToast({
                title: '删除失败',
                icon: 'none'
              });

              console.error('删除待办事项失败:', err);
            });
          }
        }
      });
    },

    // 编辑待办事项
    editTodo(todo) {
      // 跳转到编辑页面或打开编辑对话框
      uni.showToast({
        title: '编辑功能开发中',
        icon: 'none'
      });
    }
  }
}
</script>

<style>
/* 引入Inter字体 */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8fafc;
  font-family: 'Inter', sans-serif;
  position: relative;
  /* === 新增：为顶部状态栏留出空间 === */
  padding-top: var(--status-bar-height);
  box-sizing: border-box;
}

/* 顶部导航栏样式 */
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #ffffff;
  border-bottom: 1px solid #f1f5f9;
}

.nav-tabs {
  display: flex;
  gap: 16rpx;
}

.nav-tab {
  font-size: 24rpx;
  font-weight: 500;
  padding: 10rpx 30rpx;
  border-radius: 100rpx;
  background: transparent;
  color: #64748b;
  border: none;
}

.nav-tab.active {
  background-color: #111827;
  color: #ffffff;
}

.notification-btn {
  position: relative;
  width: 40rpx;
  height: 40rpx;
  padding: 15rpx;
  border-radius: 50%;
}

.notification-icon {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  position: relative;
  border: 2px solid #334155;
}

.notification-dot {
  position: absolute;
  top: -4rpx;
  right: -4rpx;
  width: 16rpx;
  height: 16rpx;
  background-color: #10b981;
  border-radius: 50%;
  border: 2px solid #ffffff;
}

/* 待办列表部分 */
.todo-section {
  flex: 1;
  padding: 30rpx;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #111827;
  margin-bottom: 20rpx;
}

.todo-list {
  flex: 1;
  overflow-y: auto;
}

.todo-card {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  margin-bottom: 20rpx;
  background-size: cover;
  background-position: center;
  border-radius: 20rpx;
  box-shadow: 0 2px 8px rgba(0,0,0,0.10);
  overflow: hidden;
}

.card-mask {
  position: absolute;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.25); /* 半透明黑色遮罩 */
  z-index: 1;
}

.card-content, .card-action {
  position: relative;
  z-index: 2;
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #ffffff; /* 确保文字在图片上方清晰可见 */
  margin-bottom: 8rpx;
}

.card-time {
  font-size: 24rpx;
  color: #ffffff; /* 确保文字在图片上方清晰可见 */
}

.card-action {
  color: white;
  font-size: 28rpx;
  font-weight: 500;
  padding: 12rpx 30rpx;
}

.empty-tip {
  text-align: center;
  color: #9ca3af;
  margin-top: 200rpx;
  font-size: 28rpx;
}

/* 底部导航栏样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 0;
  background-color: #ffffff;
  border-top: 1px solid #f1f5f9;
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

.trophy-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%2310b981' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M10 14.66v1.626a2 2 0 0 1-.976 1.696A5 5 0 0 0 7 21.978'%3E%3C/path%3E%3Cpath d='M14 14.66v1.626a2 2 0 0 0 .976 1.696A5 5 0 0 1 17 21.978'%3E%3C/path%3E%3Cpath d='M18 9h1.5a1 1 0 0 0 0-5H18'%3E%3C/path%3E%3Cpath d='M4 22h16'%3E%3C/path%3E%3Cpath d='M6 9a6 6 0 0 0 12 0V3a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1z'%3E%3C/path%3E%3Cpath d='M6 9H4.5a1 1 0 0 1 0-5H6'%3E%3C/path%3E%3C/svg%3E");
}

.chart-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a1a1aa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cline x1='12' x2='12' y1='20' y2='10'%3E%3C/line%3E%3Cline x1='18' x2='18' y1='20' y2='4'%3E%3C/line%3E%3Cline x1='6' x2='6' y1='20' y2='16'%3E%3C/line%3E%3C/svg%3E");
}

.settings-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a1a1aa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z'%3E%3C/path%3E%3Ccircle cx='12' cy='12' r='3'%3E%3C/circle%3E%3C/svg%3E");
}

.nav-text {
  font-size: 20rpx;
  color: #71717a;
  font-weight: 500;
}

.nav-item:nth-child(1) .nav-text {
  color: #10b981;
}

/* 添加按钮 */
.add-button {
  position: absolute;
  right: 30rpx;
  bottom: 120rpx;
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 60rpx;
  box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3);
  z-index: 100;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.todo-card {
  animation: fadeIn 0.3s ease-out;
}
</style>
