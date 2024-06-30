<template>
  <dv-border-box12 ref="dvBorderBoxOne">
    <a-card :title="options.headers.title" :bordered="false">
      <template #title style="height: 100%;display: flex;align-items: center;">
        <a-space>
          {{ options.headers.title }}
          <a-button type="primary" class="datav-two-button headers-button">下载</a-button>
        </a-space>
      </template>
      <template #extra>
        {{ options.headers.extra }}
      </template>
      <a-row style="height: 100%; margin: 15px;" justify="start" align="center">
        <a-col :span="15" style="height: 100%;">
          <CutomChartDataV v-if="dvBorderBoxOneRef && dvBorderBoxOneRef.height > 0" :options="options.chartData"
            :height="`${dvBorderBoxOneRef.height - 80}px`" />
        </a-col>
        <a-col :span="8" style="text-align: left;font-size: 16px;color: #ffffff;">
          <a-row align="center" justify="end" v-for="item in options.renderData" :key="item.title">
            <a-col :span="8" style="text-align: right;">{{ item.title }}</a-col>
            <a-col :span="15" style="text-align: right;">{{ `${item.value} ${item.unit}` }}</a-col>
            <!-- <a-col :span="7" style="text-align: right;">{{ item.unit == '%' ? `${item.value}${item.unit}` : item.unit }}</a-col> -->
          </a-row>
        </a-col>
      </a-row>
    </a-card>
  </dv-border-box12>

</template>
<script setup lang="ts">
import { BorderBox1 as DvBorderBox1 } from '@kjgl77/datav-vue3'
import { handleDataVChartData } from "@/utils/charts";
import { computed, onMounted, ref } from "vue";


const props = defineProps({
  // 数据
  options: {
    type: Object,
    default: {
      headers: {
        title: "",
        extra: ""
      },
      renderData: [],
      chartData: []
    },
  },
})

//图表数据
const chartData = ref<any>({});
//右侧内容
const chartCardData = ref<any>([{
  title: "光伏",
  value: 124,
  unit: "kW"
}, {
  title: "风能",
  value: 34,
  unit: "kW"
}, {
  title: "其他",
  value: 1,
  unit: "kW"
}, {
  title: "合计",
  value: 158,
  unit: "kW"
}]);
chartData.value = handleDataVChartData();


const dvBorderBoxOne = ref<InstanceType<typeof DvBorderBox1>>()
const dvBorderBoxOneRef = computed(() => {
  return dvBorderBoxOne.value;
});

</script>
<style scoped lang="less"></style>
