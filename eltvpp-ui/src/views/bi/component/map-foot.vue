<template>
  <div class="map-foot-view">
    <div v-for="(item,index) in data" :key="index" class="item-view">
      <img :src="index === 0 ? transformer : index === 1 ? stored : index === 2 ? photovoltaic : ''" alt="">
      <div class="info-view">
        <div class="info-title">{{ item?.value }}</div>
        <div class="info-decr">{{ item?.label }} ({{ item?.unit }})</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import transformer from '@/assets/bi/icon-transformer.png'
import stored from '@/assets/bi/icon-stored.png'
import photovoltaic from '@/assets/bi/icon-photovoltaic.png'
const props:any = defineProps({
  data: {
    type: Array,
    default: () => {
      return [{
        label: '',
        value: '',
        unit: ''
      }]
    }
  }
})
</script>

<style lang="less" scoped>
.map-foot-view{
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-around;
  .item-view{
    display: flex;
    align-items: center;
    .info-view{
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .info-title{
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 10px;
      }

      .info-decr{
        font-size: 14px;
      }
    }

  }
}
</style>
