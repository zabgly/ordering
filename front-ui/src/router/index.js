import Home from "@/views/Home.vue";
import PromotionDetail from "@/views/promotion/PromotionDetail.vue";
import Product from "@/views/index/Product.vue";
import Login from "@/views/Login.vue";
import PersonalCenter from "@/views/personal/PersonalCenter.vue";
import MyOrders from "@/views/personal/MyOrders.vue";
import MyCart from "@/views/personal/MyCart.vue";
import Favorites from "@/views/personal/Favorites.vue";
import MyReviews from "@/views/personal/MyReviews.vue";
import BrowseHistory from "@/views/personal/BrowseHistory.vue";
import ProductDetail from "@/views/index/ProductDetail.vue";
import VueRouter from "vue-router";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        meta: {
            title: '首页'
        },
    },
    {
        path: '/promotion/:id',
        name: 'PromotionDetail',
        component: PromotionDetail,
        meta: {
            title: '促销活动菜品详情'
        },
    },
    {
        path: '/product',
        name: 'Product',
        component: Product,
        meta: {
            title: '菜品列表'
        },
    },
    {
        path: '/login',
        name: '/login',
        component: Login,
        meta: {
            title: '登录'
        },
    },
    {
        path: "/product/:id",
        name: "ProductDetail",
        component: ProductDetail,
        meta: {
            title: '菜品详情'
        },
    },
    {
        path: "/personal-center",
        name: "personal-center",
        component: PersonalCenter,
        meta: {
            title: '个人中心'
        },
    },
    {
        path: "/my-orders",
        name: "my-orders",
        component: MyOrders,
        meta: {
            title: '我的订单'
        },
    },
    {
        path: "/my-cart",
        name: "my-cart",
        component: MyCart,
        meta: {
            title: '我的菜单'
        },
    },
    {
        path: "/favorites",
        name: "favorites",
        component: Favorites,
        meta: {
            title: '我的收藏'
        },
    },
    {
        path: "/my-reviews",
        name: "my-reviews",
        component: MyReviews,
        meta: {
            title: '我的评价'
        },
    },
    {
        path: "/browse-history",
        name: "browse-history",
        component: BrowseHistory,
        meta: {
            title: '我的浏览记录'
        },
    },
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
