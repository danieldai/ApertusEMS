<template>
  <icon-font :type="type" :size="size"/>
</template>

<script  lang="ts" setup>
import { Icon } from '@arco-design/web-vue';
const IconFont = Icon.addFromIconFontCn({ src: 'https://at.alicdn.com/t/c/font_4182253_sjpjw87uxzm.js' });

defineProps({
  type: {
      type: String,
      default: ''
    },
    size: {
      type: Number,
      default: 18
    }
})
</script>