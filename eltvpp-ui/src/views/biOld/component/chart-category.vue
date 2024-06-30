<template>
  <div class="chart-container">
    <chart :options="option" autoresize></chart>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import * as echarts from 'echarts'
import CategoryTotal from "@/views/bi/component/category-total.vue";
const props = defineProps({
  type: {
    type: Number,
    default: 0
  },
  option: {
    type:Object,
    default: () => {
      return {}
    }
  }
})


</script>

<style lang="less" scoped>
.chart-container {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
}
</style>
