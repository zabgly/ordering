<template>
  <div class="promotion-detail">
    <!-- 活动信息 -->
    <div class="promotion-info">
      <h2 class="text-center">{{ promotion.name }}</h2>
      <div class="promotion-type text-center">
        {{ promotion.type === 0 ? `折扣: ${promotion.discount}折` : `优惠: ¥${promotion.couponValue}` }}
      </div>
      <div class="promotion-time text-center">
        {{ formatDate(promotion.startTime) }} - {{ formatDate(promotion.endTime) }}
      </div>
    </div>

    <!-- 菜品列表 -->
    <div class="product-list">
      <h3 class="text-center">活动菜品</h3>
      <el-row :gutter="20">
        <el-col v-for="product in promotion.productList" :key="product.id" :span="6">
          <div @click="goProduct(product.id)">
            <el-card class="product-card">
              <img :src="product.imageUrl" class="product-image" alt="菜品图片" />
              <div class="product-name">{{ product.name }}</div>
              <div class="product-price">
                <span class="original-price">¥{{ product.price }}</span>
                <span v-if="promotion.type === 0" class="discounted-price">
                ¥{{ (product.price * promotion.discount).toFixed(2) }}
              </span>
                <span v-else class="discounted-price">
                ¥{{ (product.price - promotion.couponValue).toFixed(2) }}
              </span>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getPromotion } from "@/api/cms/promotion";

export default {
  name: "PromotionDetail",
  data() {
    return {
      promotion: {
        id: "",
        name: "",
        type: 0,
        discount: 0,
        couponValue: 0,
        startTime: "",
        endTime: "",
        productList: [],
      },
    };
  },
  created() {
    this.loadPromotionDetail();
  },
  methods: {
    /** 加载活动详情 */
    async loadPromotionDetail() {
      const promotionId = this.$route.params.id;
      const response = await getPromotion(promotionId);
      if (response.code === 0) {
        this.promotion = response.data;
      }
    },
    /** 格式化日期 */
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    goProduct(productId){
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    }
  },
};
</script>

<style scoped>
.promotion-detail {
  padding: 20px;
}

.promotion-info {
  margin-bottom: 20px;
}

.text-center {
  text-align: center;
}

.promotion-type {
  color: #e6a23c;
  margin-top: 10px;
}

.promotion-time {
  color: #999;
  margin-top: 10px;
}

.product-list {
  margin-top: 20px;
}

.product-card {
  text-align: center;
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-name {
  font-size: 14px;
  margin-top: 10px;
}

.product-price {
  margin-top: 10px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  margin-right: 10px;
}

.discounted-price {
  color: #e6a23c;
  font-weight: bold;
}
</style>
