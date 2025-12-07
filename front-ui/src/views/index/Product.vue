<template>
  <div class="container">
    <!-- 顶部搜索栏 -->
    <div class="search-wrapper">
      <el-input
          v-model="searchContent"
          placeholder="请输入菜品名称或描述"
          class="search-input"
          @keyup.enter.native="handleSearch"
      />
      <el-button-group>
        <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleSearch"
        />
        <el-button
            type="info"
            icon="el-icon-delete"
            @click="handleClear"
        />
      </el-button-group>
    </div>

    <!-- 主内容区 -->
    <div class="main-wrapper">
      <!-- 左侧分类导航 -->
      <div class="category-panel">
        <el-menu
            :default-active="selectedCategoryId"
            class="category-menu"
            @select="handleMenuSelect"
        >
          <template v-for="category in categories">
            <!-- 有子分类的菜单项 -->
            <el-submenu
                v-if="category.children?.length"
                :key="category.id"
                :index="category.id"
            >
              <template slot="title">
                <i class="el-icon-notebook-2" />
                <span>{{ category.name }}</span>
              </template>
              <template v-for="child in category.children">
                <!-- 三级分类 -->
                <el-submenu
                    v-if="child.children?.length"
                    :key="child.id"
                    :index="child.id"
                >
                  <template slot="title">{{ child.name }}</template>
                  <el-menu-item
                      v-for="subChild in child.children"
                      :key="subChild.id"
                      :index="subChild.id"
                  >
                    {{ subChild.name }}
                  </el-menu-item>
                </el-submenu>
                <!-- 二级分类 -->
                <el-menu-item
                    v-else
                    :key="child.id"
                    :index="child.id"
                >
                  {{ child.name }}
                </el-menu-item>
              </template>
            </el-submenu>

            <!-- 无子分类的菜单项 -->
            <el-menu-item
                v-else
                :key="category.id"
                :index="category.id"
            >
              <i class="el-icon-notebook-2" />
              <span slot="title">{{ category.name }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </div>

      <!-- 右侧商品展示 -->
      <div class="product-panel">
        <div v-loading="loading" class="product-grid">
          <div
              v-for="product in products"
              :key="product.id"
              class="product-card"
              @click="goToDetail(product.id)"
          >
            <div class="image-box">
              <el-image
                  :src="product.imageUrl"
                  fit="contain"
                  class="product-image"
                  lazy
              >
                <div slot="error" class="image-fallback">
                  <i class="el-icon-picture-outline" />
                </div>
              </el-image>
            </div>
            <div class="product-meta">
              <h3 class="product-title">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              <div class="price-info">
                <span class="price-current">￥{{ product.price }}</span>
                <el-tag
                    v-if="product.stock < 10"
                    type="danger"
                    size="mini"
                    class="stock-tag"
                >
                  仅剩{{ product.stock }}份
                </el-tag>
              </div>
              <div class="status-info">
                <el-tag
                    v-if="product.product_status === 0"
                    type="info"
                    size="mini"
                >
                  已售罄
                </el-tag>
                <el-tag
                    v-else
                    type="success"
                    size="mini"
                >
                  可订餐
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listCategory } from "@/api/cms/category";
import { listProduct, searchProduct } from "@/api/cms/product";

export default {
  data() {
    return {
      searchContent: "",
      selectedCategoryId: "",
      categories: [],
      products: [],
      loading: false
    };
  },
  created() {
    this.fetchCategories();
    this.fetchAllProducts();
  },
  methods: {
    async fetchCategories() {
      this.loading = true;
      try {
        const { data } = await listCategory();
        this.categories = data;
      } catch (error) {
        this.handleApiError(error);
      } finally {
        this.loading = false;
      }
    },

    async fetchAllProducts() {
      this.loading = true;
      try {
        const { data } = await listProduct();
        this.products = data;
      } catch (error) {
        this.handleApiError(error);
      } finally {
        this.loading = false;
      }
    },

    handleMenuSelect(categoryId) {
      this.selectedCategoryId = categoryId;
      this.handleSearch();
    },

    // 清空方法
    handleClear() {
      this.searchContent = "";
      this.selectedCategoryId = "";
      this.fetchAllProducts();
    },

    async handleSearch() {
      this.loading = true;
      try {
        const params = {
          searchContent: this.searchContent,
          categoryId: this.selectedCategoryId
        };

        const { data } = this.searchContent || this.selectedCategoryId
            ? await searchProduct(params)
            : await listProduct();

        this.products = data;
      } catch (error) {
        this.handleApiError(error);
      } finally {
        this.loading = false;
      }
    },

    handleApiError(error) {
      // 保持原有错误处理逻辑
      if (error.response) {
        switch (error.response.status) {
          case 401:
            alert("未授权，请登录！");
            this.$router.push({ name: "Login" });
            break;
          case 500:
            alert("服务器内部错误，请稍后重试！");
            break;
          default:
            alert("请求失败，请检查网络或联系管理员！");
        }
      } else {
        alert("网络错误，请检查网络连接！");
      }
    },

    goToDetail(productId) {
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px;
}

.search-wrapper {
  margin: 20px 0 30px;
  display: flex;
  justify-content: center;
}

.search-input {
  width: 600px;
  border-radius: 25px;
}

.main-wrapper {
  display: flex;
  gap: 20px;
  min-height: calc(100vh - 160px);
}

.category-panel {
  width: 280px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.category-menu {
  border-right: none;
  padding: 10px 0;
}

.category-menu >>> .el-submenu__title,
.category-menu >>> .el-menu-item {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
  transition: all 0.2s;
}

.category-menu >>> .el-menu-item.is-active {
  background: #f5f7fa;
  color: #ff6600;
  font-weight: 500;
}

.product-panel {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #ebeef5;
}

.product-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.image-box {
  height: 200px;
  overflow: hidden;
  position: relative;
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.image-fallback {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
}

.product-meta {
  padding: 16px;
}

.product-title {
  font-size: 15px;
  color: #303133;
  margin: 0 0 8px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #606266;
  height: 40px;
  line-height: 1.5;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-current {
  color: #ff6600;
  font-size: 18px;
  font-weight: 600;
}

.stock-tag {
  margin-left: 8px;
}

.status-info {
  margin-top: 8px;
  text-align: right;
}
</style>