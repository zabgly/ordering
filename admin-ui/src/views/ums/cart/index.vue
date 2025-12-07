<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ums:cart:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['ums:cart:edit']"-->
<!--        >修改</el-button>-->
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ums:cart:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ums:cart:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cartList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="用户" align="center" prop="umsUser.userName" />
      <el-table-column label="菜品" align="center">
        <template slot-scope="scope">
          <div v-for="product in scope.row.cmsProductList" :key="product.id">
            {{ product.name }} × {{ product.quantity }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleUpdate(scope.row)"-->
<!--            v-hasPermi="['ums:cart:edit']"-->
<!--          >修改</el-button>-->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ums:cart:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleProductDetail(scope.row)"
            v-hasPermi="['ums:cart:detail']"
          >菜品详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改我的菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择用户" filterable clearable>
            <el-option
              v-for="user in userList"
              :key="user.userId"
              :label="user.userName"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="" prop="userId">
          <el-button type="primary" size="mini" @click="openProductDialog">新增菜品</el-button>
        </el-form-item>

        <el-form-item label="菜品列表" prop="productList">
          <el-table :data="form.productList" border>
            <el-table-column label="菜品名称" prop="name" align="center"/>
            <el-table-column label="菜品数量" width="250" align="center">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  :max="scope.row.stock"
                  controls-position="right"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="scope">
                <el-button type="danger" size="mini" @click="removeProduct(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 全部菜品弹框 -->
    <el-dialog title="选择菜品" :visible.sync="productDialogVisible" width="800px" append-to-body>
      <el-table :data="productList" @selection-change="handleProductSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="菜品名称" prop="name" />
        <el-table-column label="菜品描述" prop="description" />
        <el-table-column label="菜品库存" prop="stock" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addSelectedProducts">确 定</el-button>
        <el-button @click="productDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 菜品详情弹框 -->
    <el-dialog title="菜品详情" :visible.sync="productDetailVisible" width="800px" append-to-body>
      <el-table :data="productDetailList" v-loading="productDetailLoading">
        <el-table-column label="菜品图片" align="center" width="120">
          <template slot-scope="scope">
            <el-image :src="scope.row.imageUrl" style="width: 100px; height: 100px" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="菜品名称" align="center" prop="name" />
        <el-table-column label="菜品描述" align="center" prop="description" />
        <el-table-column label="菜品数量" align="center" prop="quantity" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listCart, getCart, delCart, addCart, updateCart } from "@/api/ums/cart";
import { listUser } from "@/api/ums/user";
import { listProduct } from "@/api/cms/product";

export default {
  name: "Cart",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 我的菜单表格数据
      cartList: [],
      // 菜品详情弹框是否显示
      productDetailVisible: false,
      // 菜品详情数据
      productDetailList: [],
      // 用户列表
      userList: [],
      // 菜品列表
      productList: [],
      // 选中的菜品列表（用于弹框）
      selectedProducts: [],
      // 菜品弹框是否显示
      productDialogVisible: false,
      // 表单参数
      form: {
        userId: null,
        productList: [], // 菜品列表，包含菜品ID、名称、数量和库存
      },
      // 菜品详情加载状态
      productDetailLoading: false,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        productId: null,
        quantity: null,
        userName: null,
      },
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        productId: [
          { required: true, message: "菜品ID不能为空", trigger: "blur" }
        ],
        quantity: [
          { required: true, message: "菜品数量不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.loadUserList();
    this.loadProductList();
  },
  computed: {
    // 获取当前选中菜品的最大库存
    selectedProductStock() {
      const product = this.productList.find(p => p.id === this.form.productId);
      return product ? product.stock : 1;
    }
  },
  methods: {
    /** 加载用户列表 */
    loadUserList() {
      listUser().then(response => {
        this.userList = response.rows || [];
      });
    },
    /** 加载菜品列表 */
    loadProductList() {
      listProduct().then(response => {
        this.productList = response.rows || [];
      });
    },
    /** 打开菜品选择弹框 */
    openProductDialog() {
      this.productDialogVisible = true;
    },
    /** 处理菜品选择 */
    handleProductSelectionChange(selection) {
      this.selectedProducts = selection;
    },
    /** 添加选中的菜品到列表1 */
    addSelectedProducts() {
      this.selectedProducts.forEach(product => {
        // 检查是否已存在
        const exists = this.form.productList.some(p => p.id === product.id);
        if (!exists) {
          this.form.productList.push({
            id: product.id,
            name: product.name,
            stock: product.stock,
            quantity: 1, // 默认数量为1
          });
        }
      });
      this.productDialogVisible = false;
    },
    /** 删除菜品 */
    removeProduct(index) {
      this.form.productList.splice(index, 1);
    },
    /** 查询我的菜单列表 */
    getList() {
      this.loading = true;
      listCart(this.queryParams).then(response => {
        this.cartList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        userId: null,
        productId: null,
        quantity: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form = {
        userId: null,
        productList: [], // 初始化为空数组
      };
      this.open = true;
      this.title = "添加我的菜单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.userId || this.ids
      getCart(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改我的菜单";
      });
    },
    /** 菜品详情按钮操作 */
    handleProductDetail(row) {
      this.productDetailVisible = true;
      this.productDetailList = row.cmsProductList || [];
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 遍历 productList，拆分出每个菜品
          this.form.productList.forEach(product => {
            const cartItem = {
              userId: this.form.userId,
              productId: product.id,
              quantity: product.quantity
            };

            if (this.form.id != null) {
              // 修改操作
              updateCart(cartItem).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              // 新增操作
              addCart(cartItem).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          });
        }
      });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.userId || this.ids;
      this.$modal.confirm('是否确认删除我的菜单编号为"' + ids + '"的数据项？').then(function() {
        return delCart(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ums/cart/export', {
        ...this.queryParams
      }, `cart_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
