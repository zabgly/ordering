<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="促销名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入促销名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="促销类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择促销类型"
          clearable
        >
          <el-option
            v-for="dict in dict.type.cms_promotion_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
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
          v-hasPermi="['cms:promotion:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['cms:promotion:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['cms:promotion:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['cms:promotion:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="promotionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
<!--      <el-table-column label="促销ID" align="center" prop="id" />-->
      <el-table-column label="促销名称" align="center" prop="name" />
      <el-table-column label="促销类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cms_promotion_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="折扣率" align="center" prop="discount" />
      <el-table-column label="优惠金额" align="center" prop="couponValue" />
      <el-table-column label="活动开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活动结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['cms:promotion:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['cms:promotion:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-share"
            @click="handleDistributeProduct(scope.row)"
            v-hasPermi="['cms:promotion:distribute']"
          >分配菜品</el-button>
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

    <!-- 添加或修改促销活动对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="促销名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入促销名称" />
        </el-form-item>
        <el-form-item label="促销类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择促销类型">
            <el-option
              v-for="dict in dict.type.cms_promotion_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="折扣率" prop="discount">
          <el-input v-model="form.discount" placeholder="请输入折扣率" />
        </el-form-item>
        <el-form-item label="优惠金额" prop="couponValue">
          <el-input v-model="form.couponValue" placeholder="请输入优惠金额" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable
                          v-model="form.startTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择活动开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择活动结束时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 分配菜品弹框1 -->
    <el-dialog title="分配菜品" :visible.sync="distributeDialogVisible" width="800px" append-to-body>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="openAddProductDialog"
          >新增</el-button>
        </el-col>
      </el-row>
      <el-table :data="currentProductList" v-loading="productLoading">
        <el-table-column label="菜品图片" align="center" width="100">
          <template slot-scope="scope">
            <el-image
              :src="scope.row.imageUrl"
              style="width: 50px; height: 50px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column label="菜品名称" align="center" prop="name" />
        <el-table-column label="菜品描述" align="center" prop="description" />
        <el-table-column label="菜品价格" align="center" prop="price" />
        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="removeProduct(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="distributeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitDistributeProduct">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 新增菜品弹框2 -->
    <el-dialog title="选择菜品" :visible.sync="addProductDialogVisible" width="800px" append-to-body>
      <el-table
        v-loading="addProductLoading"
        :data="productList"
        @selection-change="handleProductSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="菜品图片" align="center" width="100">
          <template slot-scope="scope">
            <el-image
              :src="scope.row.imageUrl"
              style="width: 50px; height: 50px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column label="菜品名称" align="center" prop="name" />
        <el-table-column label="菜品描述" align="center" prop="description" />
        <el-table-column label="菜品价格" align="center" prop="price" />
      </el-table>
      <pagination
        v-show="productTotal>0"
        :total="productTotal"
        :page.sync="productQueryParams.pageNum"
        :limit.sync="productQueryParams.pageSize"
        @pagination="getProductList"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="addProductDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addSelectedProducts">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPromotion, getPromotion, delPromotion, addPromotion, updatePromotion, distributeProductByPromotionId } from "@/api/cms/promotion";
import { listProduct } from "@/api/cms/product";

export default {
  name: "Promotion",
  dicts: ['cms_promotion_type'],
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
      // 促销活动表格数据
      promotionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null,
        discount: null,
        couponCode: null,
        couponValue: null,
        startTime: null,
        endTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "促销名称不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "促销类型不能为空", trigger: "change" }
        ],
        startTime: [
          { required: true, message: "活动开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "活动结束时间不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ]
      },
      // 分配菜品弹框1相关数据
      distributeDialogVisible: false,
      productLoading: false,
      currentProductList: [], // 当前促销活动的菜品列表
      currentPromotionId: null, // 当前促销活动ID

      // 新增菜品弹框2相关数据
      addProductDialogVisible: false,
      addProductLoading: false,
      productList: [], // 菜品列表
      productTotal: 0, // 菜品总条数
      productQueryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      selectedProducts: [], // 弹框2中选中的菜品
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询促销活动列表 */
    getList() {
      this.loading = true;
      listPromotion(this.queryParams).then(response => {
        this.promotionList = response.rows;
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
        name: null,
        type: null,
        discount: null,
        couponCode: null,
        couponValue: null,
        startTime: null,
        endTime: null,
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加促销活动";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPromotion(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改促销活动";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePromotion(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPromotion(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除促销活动编号为"' + ids + '"的数据项？').then(function() {
        return delPromotion(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('cms/promotion/export', {
        ...this.queryParams
      }, `promotion_${new Date().getTime()}.xlsx`)
    },
    /** 分配菜品按钮操作 */
    handleDistributeProduct(row) {
      this.currentPromotionId = row.id;
      this.currentProductList = row.productList || [];
      this.distributeDialogVisible = true;
    },
    /** 打开新增菜品弹框2 */
    openAddProductDialog() {
      this.addProductDialogVisible = true;
      this.getProductList();
    },
    /** 查询菜品列表 */
    getProductList() {
      this.addProductLoading = true;
      listProduct(this.productQueryParams).then(response => {
        this.productList = response.rows;
        this.productTotal = response.total;
        this.addProductLoading = false;
      });
    },
    /** 菜品选择变化 */
    handleProductSelectionChange(selection) {
      this.selectedProducts = selection;
    },
    /** 添加选中的菜品到弹框1 */
    addSelectedProducts() {
      this.selectedProducts.forEach(product => {
        if (!this.currentProductList.some(p => p.id === product.id)) {
          this.currentProductList.push(product);
        }
      });
      this.addProductDialogVisible = false;
    },
    /** 删除菜品 */
    removeProduct(product) {
      this.currentProductList = this.currentProductList.filter(p => p.id !== product.id);
    },
    /** 提交菜品分配 */
    submitDistributeProduct() {
      if (!this.currentPromotionId || this.currentProductList.length === 0) {
        this.$modal.msgError("请选择要分配的菜品");
        return;
      }
      const data = {
        promotionId: this.currentPromotionId,
        productIds: this.currentProductList.map(p => p.id),
      };
      distributeProductByPromotionId(data).then(response => {
        this.$modal.msgSuccess("分配成功");
        this.distributeDialogVisible = false;
        this.getList();
      });
    },
  },
};
</script>
