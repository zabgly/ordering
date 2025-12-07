<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel :interval="6000" arrow="always" v-loading="loading">
      <el-carousel-item v-for="(image, index) in carouselImages" :key="index">
        <img :src="image" class="carousel-image" alt="轮播图" />
      </el-carousel-item>
    </el-carousel>

    <!-- 富文本描述 -->
    <div class="description" v-html="description"></div>

    <!-- 促销活动列表 -->
    <div class="promotion-list">
      <h3>促销活动</h3>
      <el-row :gutter="20" v-loading="loading">
        <el-col
            v-for="promotion in promotionList"
            :key="promotion.id"
            :span="8"
        >
          <el-card class="promotion-card" >
            <div @click="goToPromotionDetail(promotion.id)">
              <div class="promotion-name">{{ promotion.name }}</div>
              <div class="promotion-type">
                {{ promotion.type === 0 ? `折扣: ${promotion.discount}折` : `优惠: ¥${promotion.couponValue}` }}
              </div>
              <div class="promotion-time">
                {{ formatDate(promotion.startTime) }} - {{ formatDate(promotion.endTime) }}
              </div>
            </div>
          </el-card>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script>
import { getContent } from "@/api/cms/content";
import { listPromotion } from "@/api/cms/promotion";

export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Home",
  data() {
    return {
      carouselImages: [], // 轮播图图片列表
      description: "", // 富文本描述
      promotionList: [], // 促销活动列表
      loading: false, // 添加 loading 状态
    };
  },
  created() {
    this.loadContent();
    this.loadPromotions();
  },
  methods: {
    /** 加载首页内容 */
    async loadContent() {
      this.loading = true; // 开始 loading
      try {
        const response = await getContent();
        if (response.code === 0) {
          this.carouselImages = response.data.imageUrl.split(",");
          this.description = response.data.description;
        }
      } catch (error) {
        console.error("获取首页内容失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    async loadPromotions() {
      this.loading = true; // 开始 loading
      try {
        const response = await listPromotion();
        if (response.code === 0) {
          this.promotionList = response.data;
        }
      } catch (error) {
        console.error("获取促销活动列表失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    /** 跳转到活动详情页 */
    goToPromotionDetail(promotionId) {
      this.$router.push({ name: "PromotionDetail", params: { id: promotionId } });
    },
    /** 格式化日期 */
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
  },
};
</script>

<style scoped>
.home {
  width: 60%; /* 或者固定宽度，例如 800px */
  padding: 20px;
  margin: 0 auto; /* 水平居中 */
  align-content: center;
}

.carousel-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.description {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.promotion-list {
  margin-top: 20px;
}

.promotion-card {
  margin-top: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.promotion-card:hover {
  transform: translateY(-5px);
}

.promotion-name {
  font-size: 16px;
  font-weight: bold;
}

.promotion-type {
  color: #ff7e00;
  margin-top: 10px;
}

.promotion-time {
  color: #999;
  margin-top: 10px;
}
</style>
