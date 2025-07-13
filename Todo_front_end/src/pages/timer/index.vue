<template>
  <view class="timer-container">
    <!-- 背景使用CSS渐变替代图片 -->
    <view class="bg-gradient"></view>
    
    <!-- 时钟圆圈 -->
    <view class="timer-circle">
      <view class="progress-ring" :style="{ background: `conic-gradient(#ffffff ${progressPercent}%, transparent 0%)` }"></view>
      <view class="timer-inner">
        <text class="timer-text">{{ formattedTime }}</text>
        <text class="task-title">{{ todoName }}</text>
      </view>
    </view>
    
    <!-- 结束按钮 -->
    <view class="control-buttons">
      <view class="control-btn stop-btn" @click="stopTimer" v-if="isRunning || isPaused">
        <text>提前结束</text>
      </view>
    </view>
    
    <!-- 鸡汤语录 -->
    <view class="quote-container">
      <text class="quote-text">{{ quote.content }}</text>
      <text class="quote-author">—— {{ quote.author }}</text>
    </view>
  </view>
</template>

<script>
import api from '../../utils/api.js';
import storage from '../../utils/storage.js';

export default {
  data() {
    return {
      todoId: '',
      todoName: '',
      duration: 0,
      timerMode: 'normal',
      remainingTime: 0,
      isRunning: false,
      isCompleted: false,
      isPaused: false,
      timer: null,
      startTime: null,
      endTime: null,
      timerId: null,
      quote: {
        content: '行动是治愈恐惧的良药，而犹豫、拖延将不断滋养恐惧。',
        author: '罗宾·夏玛'
      }
    };
  },
  computed: {
    formattedTime() {
      const minutes = Math.floor(this.remainingTime / 60);
      const seconds = this.remainingTime % 60;
      return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    },
    progressPercent() {
      if (this.duration <= 0) return 0;
      const totalSeconds = this.duration * 60;
      const progress = ((totalSeconds - this.remainingTime) / totalSeconds) * 100;
      return Math.min(progress, 100);
    }
  },
  onLoad(options) {
    // 获取页面参数
    this.todoId = options.todoId;
    this.todoName = decodeURIComponent(options.name || '');
    this.duration = parseInt(options.duration || 0);
    this.timerMode = options.timerMode || 'normal';
    
    // 初始化剩余时间
    this.remainingTime = this.duration * 60;
    
    // 获取随机鸡汤语录
    this.fetchRandomQuote();
    
    // 自动开始计时
    setTimeout(() => {
      this.startTimer();
    }, 1000); // 延迟1秒开始，让用户看到界面
  },
  onUnload() {
    // 页面卸载时清除定时器
    this.clearTimer();
  },
  methods: {
    // 获取随机鸡汤语录
    fetchRandomQuote() {
      api.quotes.getRandom()
        .then(res => {
          if (res.code === 200) {
            this.quote = res.data;
          }
        })
        .catch(err => {
          console.error('获取鸡汤语录失败:', err);
        });
    },
    
    // 开始计时
    startTimer() {
      if (this.isRunning) return;
      
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
      
      // 如果是暂停后继续，不需要调用开始计时接口
      if (this.isPaused) {
        this.isPaused = false;
        this.isRunning = true;
        this.startCountdown();
        return;
      }
      
      // 记录开始时间
      this.startTime = new Date().toISOString();
      
      // 调用开始计时接口
      api.timers.start({
        todoId: this.todoId,
        startTime: this.startTime
      }).then(res => {
        if (res.code === 201) {
          // 保存计时ID
          this.timerId = res.data.timerId;
          
          // 开始倒计时
          this.isRunning = true;
          this.startCountdown();
        } else {
          uni.showToast({
            title: res.message || '开始计时失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.showToast({
          title: '开始计时失败',
          icon: 'none'
        });
        
        console.error('开始计时失败:', err);
      });
    },
    
    // 暂停计时
    pauseTimer() {
      if (!this.isRunning) return;
      
      this.isRunning = false;
      this.isPaused = true;
      this.clearTimer();
      
      uni.showToast({
        title: '已暂停',
        icon: 'none'
      });
    },
    
    // 停止计时
    stopTimer() {
      if (!this.isRunning && !this.isPaused) return;
      
      // 弹窗确认
      uni.showModal({
        title: '提示',
        content: '确定要结束计时吗？结束后将进入休息时间',
        success: (res) => {
          if (res.confirm) {
            this.endTime = new Date().toISOString();
            this.clearTimer();
            this.completeTimerAndRest();
          }
        }
      });
    },
    
    // 完成计时并进入休息
    completeTimerAndRest() {
      // 如果没有计时ID，说明还没有开始计时
      if (!this.timerId) {
        this.isRunning = false;
        this.isPaused = false;
        this.isCompleted = true;
        
        // 直接跳转到休息页面
        uni.redirectTo({
          url: '/pages/rest/index'
        });
        
        return;
      }
      
      // 调用结束计时接口
      api.timers.end(this.timerId, {
        endTime: this.endTime || new Date().toISOString()
      }).then(res => {
        if (res.code === 200) {
          this.isRunning = false;
          this.isPaused = false;
          this.isCompleted = true;
          
          // 自动将待办标记为已完成（如果计时完成）
          if (res.data.completed) {
            this.completeTodo();
          }
          
          // 跳转到休息页面
          uni.redirectTo({
            url: '/pages/rest/index'
          });
        } else {
          uni.showToast({
            title: res.message || '结束计时失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.showToast({
          title: '结束计时失败',
          icon: 'none'
        });
        
        console.error('结束计时失败:', err);
      });
    },
    
    // 完成计时（提前结束时使用）
    completeTimer() {
      // 如果没有计时ID，说明还没有开始计时
      if (!this.timerId) {
        this.isRunning = false;
        this.isPaused = false;
        this.isCompleted = true;
        
        uni.showToast({
          title: '计时结束',
          icon: 'success'
        });
        
        return;
      }
      
      // 调用结束计时接口
      api.timers.end(this.timerId, {
        endTime: this.endTime || new Date().toISOString()
      }).then(res => {
        if (res.code === 200) {
          this.isRunning = false;
          this.isPaused = false;
          this.isCompleted = true;
          
          uni.showToast({
            title: '计时结束',
            icon: 'success'
          });
          
          // 如果计时完成，询问是否将待办标记为已完成
          if (res.data.completed) {
            setTimeout(() => {
              uni.showModal({
                title: '提示',
                content: '计时已完成，是否将待办事项标记为已完成？',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    this.completeTodo();
                  }
                }
              });
            }, 1500);
          }
        } else {
          uni.showToast({
            title: res.message || '结束计时失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.showToast({
          title: '结束计时失败',
          icon: 'none'
        });
        
        console.error('结束计时失败:', err);
      });
    },
    
    // 将待办事项标记为已完成
    completeTodo() {
      api.todos.complete(this.todoId)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '已将待办标记为完成',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: res.message || '操作失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          uni.showToast({
            title: '操作失败',
            icon: 'none'
          });
          
          console.error('完成待办事项失败:', err);
        });
    },
    
    // 开始倒计时
    startCountdown() {
      this.clearTimer(); // 清除可能存在的定时器
      
      this.timer = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime--;
        } else {
          // 时间到，自动完成
          this.clearTimer();
          this.endTime = new Date().toISOString();
          
          // 播放提示音
          this.playAlarmSound();
          
          // 震动提示
          uni.vibrateShort();
          
          // 延迟一秒后跳转到休息页面，确保声音有时间播放
          setTimeout(() => {
            this.completeTimerAndRest();
          }, 1000);
        }
      }, 1000);
    },
    
    // 清除定时器
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    

    
    // 返回上一页
    goBack() {
      // 如果正在计时，提示确认
      if (this.isRunning) {
        uni.showModal({
          title: '提示',
          content: '计时正在进行中，确定要退出吗？',
          success: (res) => {
            if (res.confirm) {
              this.endTime = new Date().toISOString();
              this.clearTimer();
              this.completeTimer();
              
              setTimeout(() => {
                uni.navigateBack();
              }, 1000);
            }
          }
        });
      } else {
        uni.navigateBack();
      }
    }
  }
};
</script>

<style>
.timer-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  overflow: hidden;
}

.bg-gradient {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: -1;
  background: linear-gradient(to bottom, #87CEEB, #4682B4);
}



.timer-circle {
  position: relative;
  width: 400rpx;
  height: 400rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 60rpx;
}

.progress-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  opacity: 0.3;
}

.timer-inner {
  width: 350rpx;
  height: 350rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 2;
  box-shadow: 0 0 20rpx rgba(255, 255, 255, 0.5);
}

.timer-text {
  font-size: 80rpx;
  color: #ffffff;
  font-weight: bold;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.task-title {
  font-size: 32rpx;
  color: #ffffff;
  margin-top: 20rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
  max-width: 300rpx;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}



.control-buttons {
  display: flex;
  justify-content: center;
  margin-bottom: 60rpx;
}

.control-btn {
  padding: 20rpx 60rpx;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 40rpx;
  margin: 0 20rpx;
  border: 2rpx solid #ffffff;
}

.stop-btn {
  background-color: rgba(255, 99, 71, 0.6);
}

.control-btn text {
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
}

.quote-container {
  margin-top: 40rpx;
  padding: 0 60rpx;
  text-align: center;
}

.quote-text {
  font-size: 28rpx;
  color: #ffffff;
  line-height: 1.6;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.quote-author {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 20rpx;
  display: block;
}
</style> 