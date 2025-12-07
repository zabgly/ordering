<template>
  <div class="my-cart">
    <h3>我的菜单</h3>
    <el-table
        :data="cartItems"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="菜品图片">
        <template #default="scope">
          <img :src="scope.row.imageUrl" alt="菜品图片" style="width: 50px; height: 50px; object-fit: cover;" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="菜品名称" />
      <el-table-column label="数量">
        <template #default="scope">
          <el-input-number
              v-model="scope.row.quantity"
              :min="0"
              @change="handleQuantityChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="price" label="原价" />
      <el-table-column label="优惠后价格">
        <template #default="scope">
          {{ calculateDiscountedPrice(scope.row) }}
        </template>
      </el-table-column>
    </el-table>
    <div class="cart-total">
      <span>总价: ￥{{ totalPrice }}</span>
      <el-button type="primary" @click="checkout">结算</el-button>
    </div>
  </div>
</template>

<script>
import { listCart, addProductInCart, removeProductInCart } from "@/api/ums/cart";
import { placeOrder } from "@/api/oms/order";

export default {
  data() {
    return {
      cartItems: [],
      selectedItems: [], // 选中的菜品
      loading: false, // 添加 loading 状态
    };
  },
  computed: {
    // 计算总价（基于优惠后价格）
    totalPrice() {
      return this.selectedItems.reduce(
          (total, item) => {
            const discountedPrice = this.calculateDiscountedPrice(item);
            const price = discountedPrice === "无优惠" ? item.price : discountedPrice;
            return total + price * item.quantity;
          },
          0
      );
    },
  },
  created() {
    this.fetchCart();
  },
  methods: {
    // 获取我的菜单数据
    async fetchCart() {
      this.loading = true; // 开始 loading
      try {
        const response = await listCart();
        this.cartItems = response.rows[0].cmsProductList.map(item => {
          return {
            ...item,
            oldQuantity: item.quantity,
            isDiscount: item.isDiscount || null,
            discountNum: item.discountNum || 0,
          };
        });
      } catch (error) {
        console.error("获取我的菜单数据失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 计算优惠后价格
    calculateDiscountedPrice(item) {
      if (item.isDiscount === null) {
        return "无优惠"; // 如果 isDiscount 为 null，显示“无优惠”
      } else if (item.isDiscount) {
        // 如果是折扣，原价乘以折扣并取整
        return Math.floor(item.price * item.discountNum);
      } else {
        // 如果是优惠金额，原价减去优惠金额并取整
        return Math.floor(item.price - item.discountNum);
      }
    },
    // 处理数量变化
    async handleQuantityChange(item) {
      if (item.quantity === item.oldQuantity) {
        return; // 如果数量没有变化，直接返回
      }

      if (item.quantity > item.oldQuantity) {
        // 数量增加
        await addProductInCart({
          productId: item.id,
          quantity: item.quantity,
        });
      } else if (item.quantity < item.oldQuantity) {
        // 数量减少
        var res = await removeProductInCart({
          productId: item.id,
          quantity: item.quantity,
        });
        if (res.msg === '删除成功') {
          this.fetchCart();
        }
      }

      // 更新 oldQuantity
      item.oldQuantity = item.quantity;
      this.$message.success("数量更新成功！");
    },
    // 处理选中项变化
    handleSelectionChange(selection) {
      this.selectedItems = selection;
    },
    // 结算
    async checkout() {
      this.loading = true; // 开始 loading
      try {
        const orderData = {
          userId: "当前用户ID",
          totalAmount: this.totalPrice,
          orderStatus: 0,
          logisticsStatus: 0,
          paymentMethod: "在线支付",
          isCar: true,
          omsPlaceOrderProductItemList: this.selectedItems.map(item => ({
            productId: item.id,
            quantity: item.quantity,
            price: item.price,
            isDiscount: item.isDiscount,
            discountNum: item.discountNum,
          })),
        };
        const response = await placeOrder(orderData);
        if (response.code === 0) {
          this.$message.success("下单成功！");
        }
        this.fetchCart();
      } catch (error) {
        console.error("下单失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
  },
};
</script>

<style scoped>
.my-cart {
  max-width: 1200px;
  margin: 20px auto;
}

.cart-total {
  text-align: right;
  margin-top: 20px;
}

.cart-total span {
  margin-right: 20px;
  font-size: 18px;
}
</style>
