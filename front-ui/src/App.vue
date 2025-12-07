<template>
  <div id="app">
    <!-- 固定的导航栏 -->
    <NavBar />
    <Loading /> <!-- 全局加载组件 -->
    <!-- 主要内容区域，使用 padding-top 让出空间 -->
    <div class="main-content">
      <router-view />
    </div>  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue"; // 引入 NavBar 组件
import Loading from '@/components/Loading.vue';

export default {
  name: 'App',
  components: {
    NavBar,
    Loading,
  },
};
</script>

<style>
body {
  margin: 0;
  font-family: Arial, sans-serif;
  background-color: #f0f0f0;
}
/* 保证导航栏不会被页面内容覆盖 */
.main-content {
  padding-top: 60px; /* 让出空间，值要和 NavBar 高度一致 */
}
</style>
