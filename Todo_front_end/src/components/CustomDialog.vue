<template>
  <view class="custom-dialog-mask" v-if="visible" @click.stop="handleMaskClick">
    <view class="custom-dialog" @click.stop>
      <view class="custom-dialog-title">{{ title }}</view>
      <view class="custom-dialog-content">{{ content }}</view>
      <view class="custom-dialog-buttons">
        <view class="custom-dialog-button cancel" @click="handleCancel">{{ cancelText }}</view>
        <view class="custom-dialog-button confirm" @click="handleConfirm">{{ confirmText }}</view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CustomDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '提示'
    },
    content: {
      type: String,
      default: ''
    },
    cancelText: {
      type: String,
      default: '取消'
    },
    confirmText: {
      type: String,
      default: '确定'
    },
    closeOnClickMask: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleMaskClick() {
      if (this.closeOnClickMask) {
        this.$emit('update:visible', false);
        this.$emit('cancel');
      }
    },
    handleCancel() {
      this.$emit('update:visible', false);
      this.$emit('cancel');
    },
    handleConfirm() {
      this.$emit('update:visible', false);
      this.$emit('confirm');
    }
  }
}
</script>

<style>
.custom-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.custom-dialog {
  width: 80%;
  max-width: 600rpx;
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.custom-dialog-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  padding: 30rpx 20rpx;
}

.custom-dialog-content {
  padding: 20rpx 30rpx 40rpx;
  font-size: 28rpx;
  color: #666;
  text-align: center;
}

.custom-dialog-buttons {
  display: flex;
  border-top: 1rpx solid #eee;
}

.custom-dialog-button {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
}

.custom-dialog-button.cancel {
  color: #666;
  border-right: 1rpx solid #eee;
}

.custom-dialog-button.confirm {
  color: #4a90e2;
  font-weight: 500;
}
</style> 