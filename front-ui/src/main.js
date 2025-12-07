import Vue from 'vue';
import App from './App.vue';
import router from './router'; // 引入路由
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css"; // 引入 Element-UI 样式
import VueRouter from "vue-router";
Vue.use(ElementUI); // 注册 Element-UI

Vue.config.productionTip = false;
Vue.use(VueRouter); // 这里注册 VueRouter

new Vue({
  router, // 使用路由
  render: (h) => h(App),
}).$mount('#app');
