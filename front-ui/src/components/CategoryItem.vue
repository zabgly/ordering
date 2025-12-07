<template>
  <li
      @mouseenter="handleCategoryHover(category)"
      @mouseleave="handleCategoryLeave(category)"
  >
    <span @click="fetchProducts(category)">{{ category.name }}</span>
    <div v-if="category.showChildren && category.children && category.children.length" class="sub-sub-category-dropdown">
      <ul>
        <category-item
            v-for="child in category.children"
            :key="child.id"
            :category="child"
            @fetch-products="$emit('fetch-products', child)"
        />
      </ul>
    </div>
  </li>
</template>

<script>
export default {
  props: {
    category: Object,
  },
  methods: {
    handleCategoryHover(category) {
      category.showChildren = true;
    },
    handleCategoryLeave(category) {
      category.showChildren = false;
    },
    fetchProducts(category) {
      if (!category.children || category.children.length === 0) {
        this.$emit("fetch-products", category);
      }
    },
  },
};
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
}

.navbar-logo img {
  height: 40px;
}

.navbar-categories {
  display: flex;
  align-items: center;
  gap: 20px;
}

.category-item {
  position: relative;
  cursor: pointer;
  padding: 10px 0;
}

.category-item span {
  font-size: 16px;
  color: #333;
}

.sub-category-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 200px;
}

.sub-category-dropdown ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sub-category-dropdown li {
  padding: 10px 20px;
  cursor: pointer;
  color: #333;
  position: relative;
}

.sub-category-dropdown li:hover {
  background-color: #f0f0f0;
}

.sub-sub-category-dropdown {
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-width: 200px;
}

.navbar-user {
  position: relative;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  height: 30px;
  border-radius: 50%;
}

.user-icon {
  margin-left: 5px;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 150px;
}

.user-dropdown ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-dropdown li {
  padding: 10px 20px;
  cursor: pointer;
  color: #333;
}

.user-dropdown li:hover {
  background-color: #f0f0f0;
}
</style>
