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
          v-hasPermi="['oms:order:add']"
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
          v-hasPermi="['oms:order:edit']"
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
          v-hasPermi="['oms:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['oms:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
<!--      <el-table-column label="订单ID" align="center" prop="id" />-->
      <el-table-column label="用户" align="center" prop="userName" />
      <el-table-column label="订单总金额" align="center" prop="totalAmount" />
      <el-table-column label="订单状态" align="center" prop="orderStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.oms_order_status" :value="scope.row.orderStatus"/>
        </template>
      </el-table-column>
<!--      <el-table-column label="支付方式" align="center" prop="paymentMethod">-->
<!--        <template slot-scope="scope">-->
<!--          <dict-tag :options="dict.type.oms_pay_type" :value="scope.row.paymentMethod"/>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['oms:order:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['oms:order:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleOrderItemDetail(scope.row)"
            v-hasPermi="['oms:order:detail']"
          >订单菜品详情</el-button>
        </template>
      </el-table-column>

      <!-- 订单菜品详情弹框 -->
      <el-dialog title="订单菜品详情" :visible.sync="orderItemDetailVisible" width="800px" append-to-body>
        <el-table :data="orderItemDetailList" v-loading="orderItemDetailLoading">
          <el-table-column label="菜品图片" align="center" width="120">
            <template slot-scope="scope">
              <el-image :src="scope.row.cmsProduct.imageUrl" style="width: 100px; height: 100px" fit="cover"></el-image>
            </template>
          </el-table-column>
          <el-table-column label="菜品名称" align="center" prop="cmsProduct.name" />
          <el-table-column label="菜品数量" align="center" prop="quantity" />
          <el-table-column label="菜品单价" align="center" prop="price" />
          <el-table-column label="菜品总价" align="center">
            <template slot-scope="scope">
              {{ (scope.row.quantity * scope.row.price).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>

    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择菜品分类">
            <el-option
              v-for="item in userListOptions"
              :key="item.userId"
              :label="item.userName"
              :value="item.userId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入订单总金额" />
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择订单状态">
            <el-option
              v-for="dict in dict.type.oms_order_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>

<!--        <el-form-item label="支付方式" prop="paymentMethod">-->
<!--          <el-select v-model="form.paymentMethod" placeholder="请选择支付方式">-->
<!--            <el-option-->
<!--              v-for="dict in dict.type.oms_pay_type"-->
<!--              :key="dict.value"-->
<!--              :label="dict.label"-->
<!--              :value="dict.value"-->
<!--            ></el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder, listUser } from "@/api/oms/order";

export default {
  name: "Order",
  dicts: ['oms_pay_type', 'oms_order_status', 'oms_logistics_status'],
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
      // 订单表格数据
      orderList: [],
      // 订单表格数据
      userListOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 订单菜品详情弹框是否显示
      orderItemDetailVisible: false,
      // 订单菜品详情数据
      orderItemDetailList: [],
      // 订单菜品详情加载状态
      orderItemDetailLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        totalAmount: null,
        orderStatus: null,
        logisticsStatus: null,
        paymentMethod: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        totalAmount: [
          { required: true, message: "订单总金额不能为空", trigger: "blur" }
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
    this.getUserListOptions();
  },
  methods: {
    /** 查询订单列表 */
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
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
        totalAmount: null,
        orderStatus: null,
        logisticsStatus: null,
        paymentMethod: null,
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
      this.title = "添加订单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改订单";
      });
    },
    /** 获取分类列表 */
    getUserListOptions() {
      listUser().then(response => {
        this.userListOptions = response.data;
      });
    },
    /** 订单菜品详情按钮操作 */
    handleOrderItemDetail(row) {
      this.orderItemDetailLoading = true;
      this.orderItemDetailVisible = true;
      // 获取订单菜品详情数据
      this.orderItemDetailList = row.omsOrderItemList || [];
      this.orderItemDetailLoading = false;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除订单编号为"' + ids + '"的数据项？').then(function() {
        return delOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('oms/order/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
