<template>
  <div class="browse-history">
    <h3>浏览记录</h3>
    <el-table :data="history" style="width: 100%" v-loading="loading">
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
      <el-table-column prop="browseTime" label="浏览时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="deleteHistory(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { listHistory, delHistory } from "@/api/ums/history";
export default {
  data() {
    return {
      history: [],
      loading: false, // 添加 loading 状态
    };
  },
  created() {
    this.fetchHistory();
  },
  methods: {
    async fetchHistory() {
      this.loading = true; // 开始 loading
      try {
        const response = await listHistory();
        this.history = response.rows;
      } catch (error) {
        console.error("获取浏览记录失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    async deleteHistory(item) {
      this.loading = true; // 开始 loading
      try {
        await delHistory(item.id);
        this.$message.success("浏览记录已删除！");
        this.fetchHistory();
      } catch (error) {
        console.error("删除浏览记录失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
  },
};
</script>

<style scoped>
.browse-history {
  max-width: 1200px;
  margin: 20px auto;
}
</style>
