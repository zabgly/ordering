<template>
  <nav class="navbar">
    <!-- Logo -->
    <div class="navbar-logo" @click="goToHome">
      <img src="@/assets/logo.png" alt="Logo" />
    </div>

    <!-- 首页和菜品列表按钮 -->
    <div class="navbar-buttons">
      <div class="nav-button hover-orange" @click="goToHome">首页</div>
      <div class="nav-button hover-orange" @mouseenter="hoverOrange = true" @mouseleave="hoverOrange = false"
           @click="goToProductList">
        菜品列表
      </div>
    </div>

    <!-- 登录后显示头像+用户中心 -->
    <div v-if="token" class="navbar-user" @mouseenter="showUserMenu = true" @mouseleave="showUserMenu = false">
      <img :src="avatar" alt="Avatar" class="user-avatar" />
      <span class="user-icon">▼</span>
      <div v-if="showUserMenu" class="user-dropdown">
        <ul>
          <li @click="goToPage('personal-center')">个人中心</li>
          <li @click="goToPage('my-orders')">我的订单</li>
          <li @click="goToPage('my-cart')">我的菜单</li>
          <li @click="goToPage('favorites')">收藏记录</li>
          <li @click="goToPage('my-reviews')">我的评价</li>
          <li @click="goToPage('browse-history')">浏览记录</li>
          <li @click="handleLogout">退出登录</li>
        </ul>
      </div>
    </div>

    <!-- 未登录时显示登录/注册按钮 -->
    <div v-else class="navbar-auth">
      <el-button class="login-button" type="text" @click="goToLogin">登录</el-button>
    </div>
  </nav>
</template>

<script>
import avatar from '@/assets/avatar.png';
import { getToken, getUser } from "@/utils/auth";

export default {
  data() {
    return {
      showUserMenu: false, // 控制用户中心下拉菜单显示
      hoverOrange: false, // 控制菜品列表按钮的悬停效果
      token: getToken() || '', // 从 localStorage 读取 token
      avatar: getUser().userAvatar || avatar, // 从 localStorage 读取头像
    };
  },
  computed: {
    isLoggedIn() {
      return !!this.token; // 判断是否登录
    },
  },
  methods: {
    /** 跳转到首页 */
    goToHome() {
      if (this.$route.path !== "/") {
        this.$router.push("/");
      }
    },
    /** 跳转到菜品列表页 */
    goToProductList() {
      if (this.$route.path !== "/product") {
        this.$router.push("/product");
      }
    },
    /** 跳转到登录页 */
    goToLogin() {
      if (this.$route.path !== "/login") {
        this.$router.push("/login");
      }
    },
    /** 跳转到指定页面 */
    goToPage(pageName) {
      if (this.$route.name !== pageName) {
        this.$router.push({ name: pageName });
      }
    },
    /** 处理退出登录 */
    handleLogout() {
      localStorage.removeItem("Front-Token"); // 移除 token
      localStorage.removeItem("Avatar"); // 移除头像
      this.token = "";
      this.avatar = "";
      this.$router.push("/login"); // 退出后跳转到登录页
    },
  },
  watch: {
    // 监听路由变化，刷新 NavBar
    $route() {
      this.token = getToken() || "";
      this.avatar = getUser().userAvatar || avatar;
    },
  },
};
</script>

<style scoped>
.navbar {
  position: fixed; /* 让导航栏固定在页面顶部 */
  top: 0;
  left: 0;
  width: 100%;
  height: 60px; /* 设定固定高度 */
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: space-between; /* 让 logo 和用户信息稍微往中间靠 */
  padding: 0 20px;
  z-index: 1000; /* 确保优先级高，不被其他元素覆盖 */
  border-bottom: 1px solid #ddd; /* 可选，增加分割感 */
}

.navbar-logo img {
  height: 40px;
  cursor: pointer;
}

.navbar-buttons {
  display: flex;
  margin-left: 20px;
}

.nav-button {
  padding: 10px 15px;
  margin-right: 10px;
  cursor: pointer;
  font-size: 16px;
  color: #515a6e;
  transition: background-color 0.3s, color 0.3s;
}

.nav-button:hover {
  color: #fff;
}

.hover-orange:hover {
  background-color: #ff8c00; /* 橙色背景 */
  border-radius: 4px;
}

.navbar-user {
  position: relative;
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 20px; /* 往中间靠 */
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.user-icon {
  margin-left: 5px;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1001; /* 确保在 NavBar 之上 */
  min-width: 150px;
}

.user-dropdown ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-dropdown li {
  padding: 10px 20px;
  cursor: pointer;
  color: #333;
}

.user-dropdown li:hover {
  background-color: #f0f0f0;
}

.navbar-auth {
  display: flex;
  align-items: center;
  margin-right: 70px;
}

.login-button {
  color: #888888; /* 按钮文字颜色 */
  font-size: 16px; /* 按钮文字大小 */
  font-weight: 500; /* 按钮文字粗细 */
  padding: 8px 16px; /* 按钮内边距 */
  border-radius: 4px; /* 按钮圆角 */
  transition: all 0.3s ease; /* 过渡效果 */
}

.login-button:hover {
  background-color: rgba(255, 255, 255, 0.1); /* 鼠标悬停时的背景色 */
  color: #ff7e00; /* 鼠标悬停时的文字颜色 */
}

.login-button:active {
  background-color: rgba(255, 255, 255, 0.2); /* 按钮点击时的背景色 */
}
</style>
