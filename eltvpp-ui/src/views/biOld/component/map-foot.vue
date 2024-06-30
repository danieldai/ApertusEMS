<template>
  <div class="map-foot-view">
    <div v-for="(item,index) in data" :key="index" class="item-view">
      <div class="title">
        {{ item.value }}
      </div>
      <div class="decr">
        {{ item.label }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
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
    flex-direction: column;
    align-items: flex-start;
    .title{
      font-size: 32px;
      font-weight: bold;
      margin-bottom: 10px;
    }

    .decr{
      font-size: 20px;
    }
  }
}
</style>
