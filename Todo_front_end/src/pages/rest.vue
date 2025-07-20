<template>
  <view class="rest-container">
    <!-- 背景图片 -->
    <view
        class="bg-gradient"
        :style="{
           backgroundImage: 'url(' + cardImages[index % cardImages.length] + ')'
        }">
    </view>

    <!-- 时钟圆圈 -->
    <view class="timer-circle">
      <view class="progress-ring" :style="{ background: `conic-gradient(#ffffff ${progressPercent}%, transparent 0%)` }"></view>
      <view class="timer-inner">
        <text class="timer-text">{{ formatTime }}</text>
        <text class="rest-title">休息时光</text>
      </view>
    </view>

    <!-- 激励文字 -->
    <view class="quote-container">
      <text class="quote-text">{{ motivationalQuote }}</text>
      <text class="status-text">休息让你更专注</text>
    </view>

    <!-- 跳过按钮 -->
    <view class="skip-btn" @click="skipRest">
      <text>跳过休息</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      restDuration: 5, // 默认休息时长5分钟
      remainingTime: 300, // 默认5分钟 = 300秒
      timer: null,
      motivationalQuotes: [
        '适当的休息是为了走更长的路',
        '休息是为了更好的前进',
        '短暂的停留，是为了更好的出发',
        '劳逸结合，效率更高',
        '给大脑一个喘息的空间',
        '放松身心，重新充电',
        '休息不是浪费时间，而是投资未来'
      ],
      motivationalQuote: '',
      index: 0,
      cardImages: [
        '/static/1001.jpg',
        '/static/wallhaven-j3x3x5.jpg',
        '/static/wallhaven-zypyyv.png',
        '/static/wallhaven-gpwp1d.jpg',
        '/static/3.jpg',
        '/static/5.png',
        '/static/4.jpg'
      ]
    };
  },
  computed: {
    formatTime() {
      const minutes = Math.floor(this.remainingTime / 60);
      const seconds = this.remainingTime % 60;
      return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    },
    progressPercent() {
      const totalSeconds = this.restDuration * 60;
      const progress = ((totalSeconds - this.remainingTime) / totalSeconds) * 100;
      return Math.min(progress, 100);
    }
  },
  onLoad() {
    // 随机选择激励文字
    const randomIndex = Math.floor(Math.random() * this.motivationalQuotes.length);
    this.motivationalQuote = this.motivationalQuotes[randomIndex];

    // 随机选择一张背景图
    this.index = Math.floor(Math.random() * this.cardImages.length);

    // 开始休息计时
    this.startTimer();
  },
  onUnload() {
    // 页面卸载时清除计时器
    this.clearTimer();
  },
  methods: {
    startTimer() {
      this.timer = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime--;
        } else {
          // 休息时间到，返回首页
          this.clearTimer();
          this.restCompleted();
        }
      }, 1000);
    },
    restCompleted() {
      // 播放提示音
      const innerAudioContext = uni.createInnerAudioContext();
      innerAudioContext.src = '/static/audio/20250719_130757.m4a';
      innerAudioContext.play();

      // 震动提示
      uni.vibrateShort();

      uni.showToast({
        title: '休息结束，继续加油！',
        icon: 'success'
      });

      setTimeout(() => {
        this.goToHomePage();
      }, 1500);
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    skipRest() {
      this.clearTimer();
      this.goToHomePage();
    },
    goToHomePage() {
      // 返回首页
      uni.reLaunch({
        url: '/pages/index'
      });
    }
  }
};
</script>

<style>
.rest-container {
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
  background-size: cover;
  background-position: center;
  transition: background-image 0.5s ease;
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

.rest-title {
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
  margin-bottom: 20rpx;
  display: block;
}

.status-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.skip-btn {
  margin-top: 80rpx;
  padding: 20rpx 60rpx;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 40rpx;
  border: 2rpx solid #ffffff;
  transition: all 0.3s ease;
}

.skip-btn:active {
  transform: scale(0.98);
  background-color: rgba(255, 255, 255, 0.5);
}

.skip-btn text {
  color: #ffffff;
  font-size: 28rpx;
  font-weight: bold;
}
</style>
