<template>
  <div class="container" :style="{ backgroundImage: `url(${bgImage})`}">
    <span class="image">
      <slot name="icon">
        <img src="@/assets/bi/icon-horn.png" alt="">
      </slot>
    </span>
    <span class="title-value-color">
      <slot/>
    </span>
  </div>
</template>

<script setup lang="ts">
import rectangle from '@/assets/bi/rectangle.png'
import rectangle2 from '@/assets/bi/rectangle2.png'
const props = defineProps({
  type: {
    type: Number,
    default: 0
  }
})
const bgImage = props.type === 1 ? rectangle2 : rectangle
</script>

<style lang="less" scoped>
.container {
  background-size: 100% 100%;
  height: 100%;
  font-size: 14px;
  display: flex;
  align-items: center;
  padding: 5px;

  .image {
    width: 16px;
    height: 16px;
    margin: 0 8px 0 5px;
  }
}
</style>
