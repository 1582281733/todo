<template>
  <view class="container">
    <!-- 顶部工具栏 -->
    <view class="header">
      <view class="header-title">待办</view>
      <view class="add-button" @click="showAddTodoDialog">+</view>
    </view>
    
    <!-- 中间卡片区域 -->
    <scroll-view class="todo-list" scroll-y>
      <view 
        class="todo-card" 
        v-for="(item, index) in todoList" 
        :key="index"
        :style="{ backgroundColor: item.backgroundColor }"
      >
        <view class="card-title">{{ item.name }}</view>
        <view class="card-time">{{ item.duration }} 分钟</view>
        <view class="card-start" @click="startTodo(index)">开始</view>
      </view>
      
      <view v-if="todoList.length === 0" class="empty-tip">
        <text>暂无待办事项，点击右上角"+"添加</text>
      </view>
    </scroll-view>
    
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
import api from '../../utils/api.js';
import storage from '../../utils/storage.js';
import AddTodoDialog from '../../components/AddTodoDialog.vue';

export default {
  components: {
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
      hasMore: true
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
          url: '/pages/mine/index'
        });
      } else if (tab === 'stats') {
        uni.redirectTo({
          url: '/pages/stats/index'
        });
      }
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
        url: `/pages/timer/index?todoId=${todo.id}&name=${encodeURIComponent(todo.name)}&duration=${todo.duration}&timerMode=${todo.timerMode}`
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
        
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/login/index'
          });
        }, 1500);
        
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
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* 顶部工具栏样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20rpx;
  height: 100rpx;
  background-color: #4a90e2;
  color: #ffffff;
  width: 100%;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
}

.add-button {
  width: 60rpx;
  height: 60rpx;
  line-height: 56rpx;
  text-align: center;
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
}

/* 中间卡片区域样式 */
.todo-list {
  flex: 1;
  padding: 20rpx;
}

.todo-card {
  position: relative;
  margin-bottom: 20rpx;
  padding: 30rpx;
  border-radius: 16rpx;
  color: #ffffff;
}

.card-title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.card-time {
  font-size: 28rpx;
}

.card-start {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 32rpx;
  font-weight: bold;
}

.empty-tip {
  text-align: center;
  color: #999;
  margin-top: 200rpx;
}

/* 底部导航栏样式 */
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
