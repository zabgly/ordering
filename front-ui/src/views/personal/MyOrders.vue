<template>
  <div class="my-orders">
    <h3 class="page-title">我的订单</h3>
    <el-table :data="orders" style="width: 100%" v-loading="loading" class="modern-table">
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="总价">
        <template #default="scope">
          ￥{{ scope.row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column label="支付方式" prop="paymentMethod" />
      <el-table-column label="订单状态">
        <template #default="scope">
          <el-tag :type="scope.row.orderStatus === 2 ? 'success' : 'warning'">
            {{ getOrderStatusText(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" @click="showOrderDetail(scope.row)" class="detail-btn">展开</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 订单详情弹框 -->
    <el-dialog title="订单详情" :visible.sync="detailVisible" width="600px" class="modern-dialog">
      <el-table :data="currentOrder.omsOrderItemList" style="width: 100%" class="modern-table">
        <el-table-column label="菜品图片" width="120">
          <template #default="scope">
            <img :src="scope.row.cmsProduct.imageUrl" alt="菜品图片" class="dish-image" />
          </template>
        </el-table-column>
        <el-table-column label="菜品名称" prop="cmsProduct.name" />
        <el-table-column label="数量" prop="quantity" />
        <el-table-column label="单价">
          <template #default="scope">
            ￥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column label="总价">
          <template #default="scope">
            ￥{{ scope.row.price * scope.row.quantity }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder } from "@/api/oms/order";

export default {
  data() {
    return {
      orders: [], // 订单列表
      currentOrder: {}, // 当前订单详情
      detailVisible: false, // 订单详情弹框显示状态
      loading: false, // 添加 loading 状态
    };
  },
  created() {
    this.fetchOrders();
  },
  methods: {
    // 获取订单列表
    async fetchOrders() {
      this.loading = true; // 开始 loading
      try {
        const response = await listOrder();
        this.orders = response.data;
      } catch (error) {
        console.error("获取订单列表失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 显示订单详情
    showOrderDetail(order) {
      this.currentOrder = order;
      this.detailVisible = true;
    },
    // 获取订单状态中文描述
    getOrderStatusText(status) {
      switch (status) {
        case 1:
          return "未完成";
        case 2:
          return "已完成";
        default:
          return "未知状态";
      }
    },
  },
};
</script>

<style scoped>
.my-orders {
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

.modern-table {
  border-radius: 8px;
  overflow: hidden;
}

.detail-btn {
  color: #409eff;
  font-weight: 500;
}

.dish-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.modern-dialog {
  border-radius: 12px;
}
</style>
