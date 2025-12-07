<template>
  <div v-if="isLoading" class="loading-overlay">
    <div class="loading-spinner">
      <i class="el-icon-loading"></i> <!-- 使用 Element UI 的加载图标 -->
      <span>加载中...</span>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  data() {
    return {
      state: {
        isLoading: false, // 确保 state 已初始化
      },
    };
  },
  computed: {
    ...mapState('loading', ['isLoading']), // 从 Vuex 中获取加载状态
  },
};
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-spinner {
  text-align: center;
}

.el-icon-loading {
  font-size: 40px;
  color: #409eff;
}

.loading-spinner span {
  display: block;
  margin-top: 10px;
  font-size: 16px;
  color: #409eff;
}
</style>
