<script setup lang="ts">
import { ref } from "vue";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart, PieChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from "echarts/components";

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
]);

const lineChartOption = ref({
  tooltip: { trigger: "axis" },
  legend: {
    data: ["订单数", "销售金额"],
    bottom: 0,
    textStyle: { color: "#888" },
  },
  grid: {
    left: "4%",
    right: "5%",
    bottom: "14%",
    top: "12%",
    containLabel: true,
  },
  xAxis: {
    type: "category",
    boundaryGap: false,
    data: ["01/05", "02/05", "03/05", "04/05", "05/05", "06/05", "07/05"],
    axisLine: { lineStyle: { color: "#e8eaed" } },
    axisLabel: { color: "#999", fontSize: 12 },
  },
  yAxis: [
    {
      type: "value",
      name: "订单数",
      nameTextStyle: { color: "#aaa", fontSize: 11 },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: "#f0f2f5", type: "dashed" } },
      axisLabel: { color: "#bbb", fontSize: 11 },
    },
    {
      type: "value",
      name: "金额(元)",
      nameTextStyle: { color: "#aaa", fontSize: 11 },
      axisLine: { show: false },
      splitLine: { show: false },
      axisLabel: { color: "#bbb", fontSize: 11 },
    },
  ],
  series: [
    {
      name: "订单数",
      type: "line",
      smooth: true,
      data: [120, 132, 101, 134, 90, 230, 210],
      lineStyle: { width: 3, color: "#E67E22" },
      itemStyle: { color: "#E67E22" },
      areaStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: "rgba(230,126,34,0.25)" },
            { offset: 1, color: "rgba(230,126,34,0.03)" },
          ],
        },
      },
      symbol: "circle",
      symbolSize: 6,
    },
    {
      name: "销售金额",
      type: "line",
      smooth: true,
      yAxisIndex: 1,
      data: [2200, 3100, 2800, 3500, 2400, 5200, 4800],
      lineStyle: { width: 3, color: "#27ae60" },
      itemStyle: { color: "#27ae60" },
      areaStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: "rgba(39,174,96,0.18)" },
            { offset: 1, color: "rgba(39,174,96,0.03)" },
          ],
        },
      },
      symbol: "circle",
      symbolSize: 6,
    },
  ],
});

const pieChartOption = ref({
  tooltip: { trigger: "item", formatter: "{b}: {c} ({d}%)" },
  legend: {
    orient: "vertical",
    right: "4%",
    top: "center",
    textStyle: { color: "#777", fontSize: 13 },
    itemWidth: 14,
    itemHeight: 14,
    itemGap: 16,
  },
  series: [
    {
      type: "pie",
      radius: ["42%", "68%"],
      center: ["38%", "50%"],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 15, fontWeight: "bold", color: "#333" },
      },
      labelLine: { show: false },
      data: [
        { value: 1048, name: "宠物食品", itemStyle: { color: "#F39C12" } },
        { value: 735, name: "宠物用品", itemStyle: { color: "#5DADE2" } },
        { value: 580, name: "宠物玩具", itemStyle: { color: "#58D68D" } },
        { value: 484, name: "宠物医疗", itemStyle: { color: "#BB8FCE" } },
        { value: 300, name: "其他", itemStyle: { color: "#AEB6BF" } },
      ],
      itemStyle: { borderColor: "#fff", borderWidth: 3, borderRadius: 6 },
    },
  ],
});
</script>

<template>
  <div class="dashboard">
    <div class="stat-cards">
      <div class="stat-card">
        <div
          class="card-icon"
          style="background: linear-gradient(135deg, #f5a623, #e08e0b)"
        >
          <el-icon :size="26" color="#fff"><Document /></el-icon>
        </div>
        <div class="card-info">
          <p class="card-label">今日订单</p>
          <p class="card-value">
            1,286<span class="trend up"
              ><el-icon><Top /></el-icon> 12.5%</span
            >
          </p>
        </div>
      </div>
      <div class="stat-card">
        <div
          class="card-icon"
          style="background: linear-gradient(135deg, #2ecc71, #27ae60)"
        >
          <el-icon :size="26" color="#fff"><Money /></el-icon>
        </div>
        <div class="card-info">
          <p class="card-label">今日销售额</p>
          <p class="card-value">
            ¥52,860<span class="trend up"
              ><el-icon><Top /></el-icon> 8.3%</span
            >
          </p>
        </div>
      </div>
      <div class="stat-card">
        <div
          class="card-icon"
          style="background: linear-gradient(135deg, #5dade2, #3498db)"
        >
          <el-icon :size="26" color="#fff"><CreditCard /></el-icon>
        </div>
        <div class="card-info">
          <p class="card-label">昨日销售额</p>
          <p class="card-value">
            ¥48,760<span class="trend down"
              ><el-icon><Bottom /></el-icon> 2.1%</span
            >
          </p>
        </div>
      </div>
      <div class="stat-card">
        <div
          class="card-icon"
          style="background: linear-gradient(135deg, #af7ac5, #9b59b6)"
        >
          <el-icon :size="26" color="#fff"><User /></el-icon>
        </div>
        <div class="card-info">
          <p class="card-label">新增用户</p>
          <p class="card-value">
            326<span class="trend up"
              ><el-icon><Top /></el-icon> 15.2%</span
            >
          </p>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <h3>
            <el-icon><TrendCharts /></el-icon> 近7日订单与销售趋势
          </h3>
        </div>
        <VChart :option="lineChartOption" autoresize style="height: 380px" />
      </div>
      <div class="chart-card">
        <div class="card-header">
          <h3>
            <el-icon><PieChartIcon /></el-icon> 商品分类销售占比
          </h3>
        </div>
        <VChart :option="pieChartOption" autoresize style="height: 380px" />
      </div>
    </div>

    <div class="info-row">
      <div class="info-card">
        <div class="card-header">
          <h3>
            <el-icon><List /></el-icon> 待处理订单
          </h3>
          <el-tag size="small" type="warning" effect="light">共 86 单</el-tag>
        </div>
        <div class="order-stats">
          <div class="order-stat-item">
            <span class="dot" style="background: #f5a623"></span>
            <span class="label">待付款</span>
            <span class="count">28</span>
            <el-progress
              :percentage="32.5"
              :show-text="false"
              color="#f5a623"
            />
          </div>
          <div class="order-stat-item">
            <span class="dot" style="background: #5dade2"></span>
            <span class="label">待发货</span>
            <span class="count">32</span>
            <el-progress
              :percentage="37.2"
              :show-text="false"
              color="#5dade2"
            />
          </div>
          <div class="order-stat-item">
            <span class="dot" style="background: #58d68d"></span>
            <span class="label">已发货</span>
            <span class="count">15</span>
            <el-progress
              :percentage="17.4"
              :show-text="false"
              color="#58d68d"
            />
          </div>
          <div class="order-stat-item">
            <span class="dot" style="background: #bb8fce"></span>
            <span class="label">待确认收货</span>
            <span class="count">7</span>
            <el-progress :percentage="8.1" :show-text="false" color="#bb8fce" />
          </div>
          <div class="order-stat-item">
            <span class="dot" style="background: #ec7063"></span>
            <span class="label">待处理退货</span>
            <span class="count">4</span>
            <el-progress :percentage="4.7" :show-text="false" color="#ec7063" />
          </div>
        </div>
      </div>
      <div class="info-card">
        <div class="card-header">
          <h3>
            <el-icon><Goods /></el-icon> 商品总览
          </h3>
          <el-tag size="small" type="info" effect="light">共 1,256 件</el-tag>
        </div>
        <div class="product-stats">
          <div class="product-stat-item">
            <div class="stat-number">1,256</div>
            <div class="stat-label">全部商品</div>
            <div class="stat-bar">
              <div
                class="bar-fill"
                style="width: 100%; background: #aab7c4"
              ></div>
            </div>
          </div>
          <div class="product-stat-item">
            <div class="stat-number success">986</div>
            <div class="stat-label">已上架</div>
            <div class="stat-bar">
              <div class="bar-fill success-bar" style="width: 78%"></div>
            </div>
          </div>
          <div class="product-stat-item">
            <div class="stat-number muted">270</div>
            <div class="stat-label">已下架</div>
            <div class="stat-bar">
              <div class="bar-fill muted-bar" style="width: 21%"></div>
            </div>
          </div>
          <div class="product-stat-item">
            <div class="stat-number warning">38</div>
            <div class="stat-label">库存紧张</div>
            <div class="stat-bar">
              <div class="bar-fill warning-bar" style="width: 3%"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
  transition: transform 0.25s, box-shadow 0.25s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.card-icon {
  width: 62px;
  height: 62px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-info {
  flex: 1;
}

.card-label {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px;
  font-weight: 500;
}

.card-value {
  font-size: 26px;
  font-weight: 700;
  color: #334155;
  margin: 0;
  letter-spacing: -0.5px;
}

.trend {
  font-size: 12px;
  font-weight: 600;
  margin-left: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.trend.up {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.08);
}

.trend.down {
  color: #e17055;
  background: rgba(225, 112, 85, 0.08);
}

.charts-row {
  display: grid;
  grid-template-columns: 1.3fr 0.7fr;
  gap: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 14px;
  padding: 28px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-card {
  background: #fff;
  border-radius: 14px;
  padding: 28px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
}

.order-stats {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.order-stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  background: #fafbfc;
  border-radius: 10px;
  border: 1px solid #f0f2f5;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.label {
  flex: 1;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.count {
  font-size: 20px;
  font-weight: 700;
  color: #334155;
  min-width: 40px;
  text-align: right;
}

.product-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-stat-item {
  text-align: center;
  padding: 24px 16px;
  background: #fafbfc;
  border-radius: 12px;
  border: 1px solid #f0f2f5;
}

.stat-number {
  font-size: 30px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.stat-number.success {
  color: #27ae60;
}
.stat-number.muted {
  color: #95a5a6;
}
.stat-number.warning {
  color: #f5a623;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.stat-bar {
  height: 4px;
  border-radius: 2px;
  background: #eee;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.6s ease;
}

.success-bar {
  background: #27ae60;
}
.muted-bar {
  background: #bdc3c7;
}
.warning-bar {
  background: #f5a623;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-row {
    grid-template-columns: 1fr;
  }
  .info-row {
    grid-template-columns: 1fr;
  }
}
</style>
