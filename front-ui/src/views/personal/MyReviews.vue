<template>
  <div class="review-container">
    <!-- 待评价列表 -->
    <div class="not-reviewed">
      <h3 class="page-title">待评价</h3>
      <el-card v-for="(order, index) in notReviewList" :key="index" class="order-card modern-card">
        <div class="order-header">
          <span>订单号：{{ order.orderId }}</span>
        </div>
        <el-table :data="order.cmsProductList" style="width: 100%" v-loading="loading" class="modern-table">
          <el-table-column label="菜品图片">
            <template #default="scope">
              <img :src="scope.row.imageUrl" alt="菜品图片" class="dish-image" />
            </template>
          </el-table-column>
          <el-table-column label="菜品名称" prop="name"></el-table-column>
          <el-table-column label="菜品价格" prop="price"></el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="primary" size="small" @click="openReviewDialog(scope.row, order.orderId)" class="review-btn">
                评价
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 我的评价列表 -->
    <div class="my-reviews">
      <h3 class="page-title">我的评价</h3>
      <el-table :data="reviews" style="width: 100%" v-loading="loading" class="modern-table">
        <el-table-column label="序号" align="center" width="100">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="菜品图片">
          <template #default="scope">
            <img :src="scope.row.productImage" alt="菜品图片" class="dish-image" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="菜品名称" />
        <el-table-column prop="content" label="评价内容" />
        <el-table-column prop="rating" label="评分" />
        <el-table-column prop="createTime" label="评价时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="text" @click="deleteReview(scope.row)" class="delete-btn">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 评价弹框 -->
    <el-dialog title="菜品评价" :visible.sync="reviewDialogVisible" width="30%" class="modern-dialog">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-text></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
              type="textarea"
              :rows="4"
              v-model="reviewForm.content"
              placeholder="请输入评价内容"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="submitReview" class="submit-btn">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listNotReview, listReview, addReview, delReview } from "@/api/ums/review";

export default {
  data() {
    return {
      notReviewList: [], // 待评价列表
      reviews: [], // 我的评价列表
      reviewDialogVisible: false, // 评价弹框显示状态
      reviewForm: {
        productId: "", // 菜品ID
        orderId: "", // 订单ID
        content: "", // 评价内容
        rating: 0, // 评分
      },
      loading: false, // 添加 loading 状态
    };
  },
  created() {
    this.fetchNotReviewList();
    this.fetchReviews();
  },
  methods: {
    // 获取待评价列表
    async fetchNotReviewList() {
      this.loading = true; // 开始 loading
      try {
        const response = await listNotReview();
        this.notReviewList = response.rows;
      } catch (error) {
        console.error("获取待评价列表失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 获取我的评价列表
    async fetchReviews() {
      this.loading = true; // 开始 loading
      try {
        const response = await listReview();
        this.reviews = response.rows;
      } catch (error) {
        console.error("获取评价列表失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 打开评价弹框
    openReviewDialog(product, orderId) {
      this.reviewForm.productId = product.id;
      this.reviewForm.orderId = orderId;
      this.reviewDialogVisible = true;
    },
    // 提交评价
    async submitReview() {
      try {
        await addReview(this.reviewForm);
        this.$message.success("评价提交成功！");
        this.reviewDialogVisible = false;
        this.fetchNotReviewList(); // 刷新待评价列表
        this.fetchReviews(); // 刷新我的评价列表
      } catch (error) {
        this.$message.error("评价提交失败，请重试！");
      }
    },
    // 删除评价
    async deleteReview(item) {
      try {
        await delReview(item.id);
        this.$message.success("评价已删除！");
        this.fetchReviews(); // 刷新我的评价列表
      } catch (error) {
        this.$message.error("删除失败，请重试！");
      }
    },
  },
};
</script>

<style scoped>
.review-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 20px;
}

.modern-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.my-reviews {
  margin-top: 40px;
}

.modern-table {
  border-radius: 8px;
  overflow: hidden;
}

.dish-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.review-btn {
  background: #409eff;
  color: #ffffff;
  border: none;
}

.delete-btn {
  color: #f56c6c;
}

.cancel-btn {
  border: none;
}

.submit-btn {
  background: #409eff;
  color: #ffffff;
  border: none;
}

.modern-dialog {
  border-radius: 12px;
}
</style>
