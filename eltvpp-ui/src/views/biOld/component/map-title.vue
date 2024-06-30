<template>
  <div class="map-title-view">
    <img src="@/assets/bi/left-bracket.png" alt="">
    <div class="text">
      {{ data.label }}
      <span>{{ data.value }}</span>
      {{data.unit}}
    </div>
    <img src="@/assets/bi/right-bracket.png" alt="">
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
.map-title-view{
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Source Han Sans;
  .text{
    display: flex;
    align-items: flex-end;
    font-size: 20px;
    padding: 0 10px;
  }
  .text span{
    margin: 0 20px;
    font-size: 64px;
  }
}
</style>
