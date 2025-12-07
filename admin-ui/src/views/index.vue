<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>点餐后台管理</h2>
      </el-col>
    </el-row>

    <!-- 图表展示区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 订单状态分布 -->
      <el-col :sm="24" :lg="12" class="chart-container">
        <el-card>
          <div slot="header">订单状态分布</div>
          <!-- 使用 highcharts 组件，通过 options 绑定配置 -->
          <highcharts
            :options="statusChartOptions"
            class="chart-wrapper"
          ></highcharts>
        </el-card>
      </el-col>

      <!-- 支付方式占比 -->
      <el-col :sm="24" :lg="12" class="chart-container">
        <el-card>
          <div slot="header">支付方式占比</div>
          <highcharts
            :options="paymentChartOptions"
            class="chart-wrapper"
          ></highcharts>
        </el-card>
      </el-col>

      <!-- 订单金额趋势 -->
      <el-col :sm="24" class="chart-container">
        <el-card>
          <div slot="header">订单金额趋势</div>
          <highcharts
            :options="amountChartOptions"
            class="chart-wrapper"
          ></highcharts>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// 引入 highcharts 核心库和 vue 适配器组件
import Highcharts from 'highcharts'
import { HighchartsVue } from 'highcharts-vue'
import { listAllOrder } from "@/api/oms/order";
import { Message } from 'element-ui'

export default {
  name: "Index",
  // 注册 highcharts 组件
  components: {
    HighchartsVue
  },
  data() {
    return {
      version: "3.5.0",
      orderList: [],
      // 图表配置项（初始为空，后续动态生成）
      statusChartOptions: {},
      paymentChartOptions: {},
      amountChartOptions: {}
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.fetchOrderData();
    });
  },
  methods: {
    async fetchOrderData() {
      try {
        const response = await listAllOrder();
        console.log(response)
        if (response.code === 200) {
          this.orderList = response.rows || [];
          this.initCharts(); // 数据获取后初始化图表配置
        } else {
          Message.error('获取数据失败：' + response.msg);
        }
      } catch (error) {
        Message.error('获取订单数据失败：' + (error.message || '网络错误'));
        console.error(error);
      }
    },

    // 初始化图表配置（将配置赋值给 data 中的 options）
    initCharts() {
      this.initStatusChart();
      this.initPaymentChart();
      this.initAmountChart();
    },

    // 订单状态分布图表配置
    initStatusChart() {
      const statusData = {
        '未完成': this.orderList.filter(order => order.orderStatus === 1).length,
        '已完成': this.orderList.filter(order => order.orderStatus === 2).length
      };

      this.statusChartOptions = {
        chart: { type: 'bar' },
        title: { text: '' },
        xAxis: { categories: Object.keys(statusData) },
        yAxis: { title: { text: '订单数量' } },
        series: [{
          name: '订单状态',
          data: Object.values(statusData),
          color: '#42b983'
        }],
        tooltip: {
          formatter: function() {
            return `<b>${this.x}</b><br/>数量: ${this.y}`;
          }
        },
        credits: { enabled: false }
      };
    },

    // 支付方式占比图表配置
    initPaymentChart() {
      const paymentMap = {};
      this.orderList.forEach(order => {
        const method = order.paymentMethod || '未知';
        paymentMap[method] = (paymentMap[method] || 0) + 1;
      });

      this.paymentChartOptions = {
        chart: { type: 'pie' },
        title: { text: '' },
        series: [{
          name: '支付方式',
          data: Object.entries(paymentMap).map(([name, value]) => ({ name, y: value })),
          colors: ['#42b983', '#3498db', '#9b59b6', '#e74c3c', '#f1c40f']
        }],
        tooltip: {
          formatter: function() {
            return `<b>${this.name}</b><br/>数量: ${this.y} (${Math.round(this.percentage)}%)`;
          }
        },
        credits: { enabled: false }
      };
    },

    // 订单金额趋势图表配置
    initAmountChart() {
      const amountMap = {};
      this.orderList.forEach(order => {
        if (order.createTime) {
          const date = new Date(order.createTime).toLocaleDateString().replace(/\//g, '-');
          const amount = order.totalAmount ? Number(order.totalAmount.toString()) : 0;
          amountMap[date] = (amountMap[date] || 0) + amount;
        }
      });

      const sortedDates = Object.keys(amountMap).sort((a, b) => new Date(a) - new Date(b));
      const sortedAmounts = sortedDates.map(date => amountMap[date]);

      this.amountChartOptions = {
        chart: { type: 'line' },
        title: { text: '' },
        xAxis: {
          categories: sortedDates,
          title: { text: '日期' },
          tickInterval: Math.ceil(sortedDates.length / 7)
        },
        yAxis: {
          title: { text: '订单总金额 (元)' },
          min: 0,
          labels: {
            formatter: function() {
              return '¥' + this.value.toFixed(0);
            }
          }
        },
        series: [{
          name: '订单总金额',
          data: sortedAmounts,
          color: '#3498db',
          marker: { enabled: true }
        }],
        tooltip: {
          formatter: function() {
            return `<b>${this.x}</b><br/>总金额: ¥${this.y.toFixed(2)}`;
          }
        },
        credits: { enabled: false }
      };
    },

    goTarget(href) {
      window.open(href, "_blank");
    }
  }
};
</script>

<style scoped lang="scss">
/* 样式保持不变 */
.home {
  padding: 20px;
  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  .chart-container {
    margin-bottom: 20px;
    .el-card {
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }
  }

  .chart-wrapper {
    width: 100%;
    height: 400px;
    padding: 10px 0;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
    color: #333;
  }

  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>
