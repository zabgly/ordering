<template>
  <div class="product-detail">
    <div class="product-container">
      <!-- 左侧菜品图片 -->
      <div class="product-image" v-loading="loading">
        <img :src="product.imageUrl" alt="Product Image" />
      </div>

      <!-- 右侧菜品信息 -->
      <div class="product-info" v-loading="loading">
        <!-- 标题和收藏按钮 -->
        <div class="title-row">
          <h2>{{ product.name }}</h2>
          <div v-if="product.isCollect" class="collected-text">已收藏</div>
          <button v-else class="collect-button" @click="handleCollect">收藏</button>
        </div>

        <p class="description">{{ product.description }}</p>

        <!-- 价格部分 -->
        <div class="price-section">
          <p v-if="product.isDiscount !== null" class="original-price">
            原价: ￥{{ product.price }}
          </p>
          <p class="final-price">
            {{ product.isDiscount !== null ? "优惠价: " : "价格: " }}
            ￥{{ calculateFinalPrice(product) }}
          </p>
        </div>

        <p class="stock">库存: {{ product.stock }}</p>

        <!-- 按钮组 -->
        <div class="button-group">
          <button class="cart-button" @click="addToCart">加入菜单</button>
          <button class="order-button" @click="placeOrder">立即下单</button>
        </div>
      </div>
    </div>

    <!-- 用户评论 -->
    <div class="reviews" v-loading="loading">
      <h3>用户评价</h3>
      <div v-if="product.productReviewList.length === 0">暂无评价</div>
      <div v-for="review in product.productReviewList" :key="review.id" class="review-item">
        <p class="review-user">{{ review.userName }} - {{ review.createTime }}</p>
        <p class="review-content">{{ review.content }}</p>
        <p class="review-rating">评分: {{ review.rating }}⭐</p>
      </div>
    </div>
  </div>
</template>


<script>
import { getProduct } from "@/api/cms/product";
import { placeOrder } from "@/api/oms/order";
import { addProductInCart } from "@/api/ums/cart";
import { addCollect } from "@/api/ums/favorite"; // 引入收藏接口

export default {
  data() {
    return {
      product: {
        productReviewList: [],
        isCollect: false, // 默认未收藏
        isDiscount: null, // 是否折扣
        discountNum: null, // 折扣或优惠金额
        loading: false, // 添加 loading 状态
      },
    };
  },
  created() {
    this.fetchProduct();
  },
  methods: {
    async fetchProduct() {
      this.loading = true; // 开始 loading
      try {
        const response = await getProduct(this.$route.params.id);
        this.product = response.data;
      } catch (error) {
        console.error("获取菜品详情失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    async placeOrder() {
      const orderData = {
        userId: "当前用户ID", // 这里需要替换为真实的用户ID
        totalAmount: this.product.price,
        orderStatus: 0, // 0-待支付
        logisticsStatus: 0, // 0-待发货
        paymentMethod: "在线支付",
        isCar: false,
        omsPlaceOrderProductItemList: [
          {
            productId: this.product.id,
            quantity: 1, // 默认购买一件
            price: this.product.price,
          },
        ],
      };

      try {
        const response = await placeOrder(orderData);
        if (response.code === 0) {
          this.$message.success("下单成功！");
        } else {
          this.$message.error("下单失败：" + response.msg);
        }
      } catch (error) {
        console.error("下单失败:", error);
        this.$message.error("下单失败，请重试！");
      }
    },
    async addToCart() {
      const cartData = {
        productId: this.product.id,
        quantity: 1, // 默认加入一件
      };

      try {
        const response = await addProductInCart(cartData);
        if (response.code === 0) {
          this.$message.success("菜品已成功加入菜单！");
        } else {
          this.$message.error("加入菜单失败，请重试！");
        }
      } catch (error) {
        console.error("加入菜单失败:", error);
        this.$message.error("加入菜单失败，请重试！");
      }
    },
    // 收藏菜品
    async handleCollect() {
      try {
        const response = await addCollect({ productId: this.product.id });
        if (response.code === 0) {
          this.product.isCollect = true; // 更新收藏状态
          this.$message.success("收藏成功！");
        } else {
          this.$message.error("收藏失败：" + response.msg);
        }
      } catch (error) {
        console.error("收藏失败:", error);
        this.$message.error("收藏失败，请重试！");
      }
    },
    // 计算优惠后金额
    calculateFinalPrice(product) {
      if (product.isDiscount === null) {
        return product.price; // 无促销，返回原价
      }
      if (product.isDiscount) {
        // 折扣
        return (product.price * product.discountNum).toFixed(2);
      } else {
        // 优惠
        return (product.price - product.discountNum).toFixed(2);
      }
    },
  },
};
</script>


<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-container {
  display: flex;
  gap: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.product-image img {
  width: 350px;
  height: 350px;
  object-fit: cover;
  border-radius: 8px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 标题行样式 */
.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

/* 收藏按钮样式 */
.collect-button {
  padding: 8px 16px;
  font-size: 14px;
  color: #fff;
  background: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.collect-button:hover {
  background: #0056b3;
}

/* 已收藏文字样式 */
.collected-text {
  font-size: 14px;
  color: #666;
}

.description {
  color: #666;
  margin-bottom: 10px;
}

/* 价格部分样式 */
.price-section {
  margin-bottom: 10px;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.final-price {
  font-size: 24px;
  color: #ff6600;
  font-weight: bold;
}

.stock {
  margin-bottom: 20px;
  color: #333;
}

.button-group {
  display: flex;
  gap: 10px;
}

.cart-button,
.order-button {
  padding: 8px 16px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.cart-button {
  background: #007bff;
  color: white;
}

.cart-button:hover {
  background: #0056b3;
}

.order-button {
  background: #ff6600;
  color: white;
}

.order-button:hover {
  background: #cc5200;
}

.reviews {
  margin-top: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.review-item {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}

.review-user {
  font-weight: bold;
}

.review-content {
  color: #666;
}

.review-rating {
  color: #ff6600;
  font-weight: bold;
}
</style>



