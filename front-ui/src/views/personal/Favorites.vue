<template>
  <div class="favorites">
    <h3>收藏记录</h3>
    <el-table :data="favorites" style="width: 100%" v-loading="loading">
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="菜品图片">
        <template #default="scope">
          <img :src="scope.row.productImage" alt="菜品图片" style="width: 50px; height: 50px; object-fit: cover;" />
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="菜品名称" />
      <el-table-column prop="createTime" label="收藏时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="removeFavorite(scope.row)"
          >取消收藏</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { listFavorite, delFavorite } from "@/api/ums/favorite";
export default {
  data() {
    return {
      favorites: [],
      loading: false, // 添加 loading 状态
    };
  },
  created() {
    this.fetchFavorites();
  },
  methods: {
    async fetchFavorites() {
      this.loading = true; // 开始 loading
      try {
        const response = await listFavorite();
        this.favorites = response.rows;
      } catch (error) {
        console.error("获取收藏记录失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    async removeFavorite(item) {
      this.loading = true; // 开始 loading
      try {
        await delFavorite(item.id);
        this.$message.success("已取消收藏！");
        this.fetchFavorites();
      } catch (error) {
        console.error("取消收藏失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
  },
};
</script>

<style scoped>
.favorites {
  max-width: 1200px;
  margin: 20px auto;
}
</style>
