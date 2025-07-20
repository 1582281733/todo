<template>
  <view class="add-todo-mask" v-if="visible" @click.stop="handleMaskClick">
    <view class="add-todo-container" @click.stop>
      <!-- 顶部标题栏 -->
      <view class="add-todo-header">
        <text class="add-todo-title">添加待办</text>
        <view class="add-todo-actions">
          <view class="action-btn confirm" @click="handleConfirm">
            <text class="iconfont icon-check"></text>
          </view>
          <view class="action-btn cancel" @click="handleCancel">
            <text class="iconfont icon-close"></text>
          </view>
        </view>
      </view>

      <!-- 输入区域 -->
      <view class="add-todo-content">
        <view class="input-group">
          <input
            class="todo-input"
            type="text"
            v-model="todoName"
            placeholder="请输入待办名称"
            maxlength="20"
          />
          <view class="input-border"></view>
        </view>

        <!-- 时长选择 -->
        <view class="duration-selector">
          <view
            class="duration-item"
            :class="{ active: selectedDuration === 25 }"
            @click="selectedDuration = 25"
          >
            25分钟
          </view>
          <view
            class="duration-item"
            :class="{ active: selectedDuration === 35 }"
            @click="selectedDuration = 35"
          >
            35分钟
          </view>
          <view
            class="duration-item"
            :class="{ active: selectedDuration === 'custom' }"
            @click="showCustomDuration"
          >
            自定义
          </view>
        </view>

        <!-- 自定义时长输入 -->
        <view class="custom-duration" v-if="isCustomDuration">
          <input
            class="duration-input"
            type="number"
            v-model="customDurationValue"
            placeholder="请输入分钟数"
          />
          <text class="unit">分钟</text>
        </view>

        <!-- 提示文本 -->
        <view class="tip-text">
          *倒计时25分钟即标准番茄钟时间
        </view>

        <!-- 高级设置按钮 -->
        <view class="advanced-settings" @click="toggleAdvancedSettings">
          <text>更多高级设置</text>
          <text class="iconfont" :class="isAdvancedOpen ? 'icon-up' : 'icon-down'"></text>
        </view>

        <!-- 高级设置面板 -->
        <view class="advanced-panel" v-if="isAdvancedOpen">
          <!-- 背景颜色选择 -->
          <view class="color-selector">
            <text class="section-title">背景颜色</text>
            <view class="color-list">
              <view
                class="color-item"
                v-for="(color, index) in bgColors"
                :key="index"
                :style="{ backgroundColor: color }"
                :class="{ active: selectedBgColor === color }"
                @click="selectedBgColor = color"
              ></view>
            </view>
          </view>

          <!-- 休息时间设置 -->
          <view class="rest-setting">
            <text class="section-title">休息时间</text>
            <view class="rest-slider">
              <slider
                :value="restDuration"
                :min="1"
                :max="15"
                :step="1"
                show-value
                @change="onRestDurationChange"
              />
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'AddTodoDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      todoName: '',
      selectedType: 'normal', // normal, goal, habit
      selectedTimer: 'countdown', // countdown, countup, nocount
      selectedDuration: 25, // 25, 35, custom
      customDurationValue: '',
      isCustomDuration: false,
      isAdvancedOpen: false,
      restDuration: 5,
      bgColors: [
        '#1a1a1a', '#1e4e5f', '#b88c7d', '#6e5c7e',
        '#a5c1b5', '#f3c4b4', '#4a90e2', '#50c878'
      ],
      selectedBgColor: '#4a90e2'
    };
  },
  methods: {
    handleMaskClick() {
      this.$emit('update-visible', false);
      this.$emit('cancel');
    },
    handleCancel() {
      this.$emit('update-visible', false);
      this.$emit('cancel');
    },
    handleConfirm() {
      // 验证输入
      if (!this.todoName.trim()) {
        uni.showToast({
          title: '请输入待办名称',
          icon: 'none'
        });
        return;
      }

      // 构建待办数据
      const todoData = {
        title: this.todoName.trim(),
        type: this.selectedType,
        timerType: this.selectedTimer,
        time: this.isCustomDuration ? parseInt(this.customDurationValue) || 25 : this.selectedDuration,
        bgColor: this.selectedBgColor,
        restDuration: this.restDuration
      };

      this.$emit('confirm', todoData);
      this.$emit('update-visible', false);
      this.resetForm();
    },
    showCustomDuration() {
      this.selectedDuration = 'custom';
      this.isCustomDuration = true;
    },
    toggleAdvancedSettings() {
      this.isAdvancedOpen = !this.isAdvancedOpen;
    },
    onRestDurationChange(e) {
      this.restDuration = e.detail.value;
    },
    resetForm() {
      this.todoName = '';
      this.selectedType = 'normal';
      this.selectedTimer = 'countdown';
      this.selectedDuration = 25;
      this.customDurationValue = '';
      this.isCustomDuration = false;
      this.isAdvancedOpen = false;
      this.restDuration = 5;
      this.selectedBgColor = '#4a90e2';
    }
  }
};
</script>

<style>
/* 抖音风格UI */
.add-todo-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.add-todo-container {
  width: 90%;
  max-width: 650rpx;
  background-color: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.2);
  animation: slide-up 0.3s ease;
}

@keyframes slide-up {
  from {
    transform: translateY(50rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.add-todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.add-todo-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.add-todo-actions {
  display: flex;
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 20rpx;
}

.action-btn.confirm {
  background-color: #fe2c55;
  border-radius: 50%;
  color: #fff;
}

.action-btn.cancel {
  color: #999;
}

.iconfont {
  font-size: 36rpx;
}

.icon-check:before {
  content: "✓";
}

.icon-close:before {
  content: "✕";
}

.icon-down:before {
  content: "▼";
}

.icon-up:before {
  content: "▲";
}

.add-todo-content {
  padding: 30rpx;
}

.input-group {
  margin-bottom: 40rpx;
  position: relative;
}

.input-label {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #666;
}

.label-dot {
  width: 12rpx;
  height: 12rpx;
  background-color: #fe2c55;
  border-radius: 50%;
  margin-right: 10rpx;
}

.todo-input {
  height: 80rpx;
  font-size: 32rpx;
  color: #333;
  width: 100%;
}

.input-border {
  height: 2rpx;
  background: linear-gradient(to right, #fe2c55, #fe2c55 50%, #eee 50%);
  background-size: 200% 100%;
  background-position: 100% 0;
  transition: background-position 0.3s ease;
}

.todo-input:focus + .input-border {
  background-position: 0 0;
}

.type-selector, .timer-selector, .duration-selector {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 30rpx;
}

.type-item, .timer-item, .duration-item {
  flex: 1;
  min-width: 160rpx;
  height: 80rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f8f8f8;
  border-radius: 10rpx;
  margin: 10rpx;
  font-size: 28rpx;
  color: #666;
  transition: all 0.2s ease;
}

.type-item.active, .timer-item.active, .duration-item.active {
  background-color: rgba(254, 44, 85, 0.1);
  color: #fe2c55;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(254, 44, 85, 0.2);
}

.custom-duration {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  padding: 0 10rpx;
}

.duration-input {
  flex: 1;
  height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.unit {
  margin-left: 20rpx;
  font-size: 28rpx;
  color: #666;
}

.tip-text {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 30rpx;
  padding: 0 10rpx;
}

.advanced-settings {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80rpx;
  font-size: 28rpx;
  color: #4a90e2;
  margin-bottom: 20rpx;
}

.advanced-settings .iconfont {
  margin-left: 10rpx;
  font-size: 24rpx;
}

.advanced-panel {
  padding: 20rpx 10rpx;
  background-color: #f9f9f9;
  border-radius: 10rpx;
  animation: fade-in 0.3s ease;
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.section-title {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
  display: block;
}

.color-list {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 30rpx;
}

.color-item {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin: 10rpx;
  transition: all 0.2s ease;
  position: relative;
}

.color-item.active::after {
  content: "";
  position: absolute;
  width: 20rpx;
  height: 20rpx;
  background-color: #fff;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.rest-setting {
  margin-bottom: 20rpx;
}

.rest-slider {
  padding: 0 10rpx;
}
</style>
