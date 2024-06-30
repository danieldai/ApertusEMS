<template>
  <div class="map-title-view">
    <div class="img-view">
      <img src="@/assets/bi/title-left.png" alt="">
    </div>
    <div class="text-view">
      <div>{{ data.label }}</div>
      <div class="text">{{ data.value }}</div>
      <div>{{data.unit}}</div>
    </div>
    <div class="img-view">
      <img src="@/assets/bi/title-right.png" alt="">
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  data: {
    type: Object,
    default: () => {
      return {}
    }
  }
})
</script>

<style lang="less" scoped>
.map-title-view {
  font-family: Source Han Sans;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;

  .text-view {
    display: flex;
    align-items: flex-end;
    padding: 20px;
  }

  .text {
    font-size: 48px;
    padding: 0 10px;
    color: #11DACA;
  }
}
</style>
