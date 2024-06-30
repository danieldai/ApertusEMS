<!--
* 功能：电站概览
* 作者：曹晓桐
* 日期：2023-12-7
-->
<template>
  <div class="container">
    <a-row :gutter="[12, 12]">

      <!-- 第一排 -->
      <a-col>
        <a-row :gutter="12" style="height: 400px;">
          <a-col :span="6">
            <!-- 电站概览【电站】基础数据 -->
            <CardOne />
          </a-col>
          <a-col :span="12">
            <a-row :gutter="12">
              <a-col>
                <!-- 水滴球 -->
                <CardTwo  style="height: 230px;" />
              </a-col>
              <a-col>
                <!-- 发电统计 -->
                <CardThree style="height: 170px;" />
              </a-col>
            </a-row>
          </a-col>
          <a-col class="weather" :span="6">
            <!-- 天气 -->
            <CardFour />
          </a-col>
        </a-row>
      </a-col>
    </a-row>
    <Dashboard />
  </div>
</template>
<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';
import CardOne from './components/card-one.vue'
import CardTwo from './components/card-two.vue'
import CardThree from './components/card-three.vue'
import CardFour from './components/card-four.vue'
import CardFive from './components/card-five.vue'
import CardSix from './components/card-six.vue'
import Dashboard from '@/views/dashboard/preview/index.vue'
import { onMounted } from 'vue';



onMounted(() => {

})
</script>

<style lang="less" scoped>
.container {
  width: 100%;
  height: 100%;
}
</style>
