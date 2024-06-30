<template>
  <div style="height: 100%" class="table-list-view" ref="mains">
    <ul class="table-list-header">
      <li v-for="(item,index) in columns" :key="index" :style="{width:`calc(100% / ${columns.length})`}">
        {{ item.title }}
      </li>
    </ul>
    <vue3ScrollSeamless
        class="scroll-wrap"
        :style="{height: scrollHeight + 'px'}"
        :classOptions="classOptions"
        :dataList="data"
    >
      <ul class="table-list-body" v-for="(item,index) in data" :key="index" :class="{itemBg: index % 2 === 0}">
        <li v-for="(item1,index1) in columns" :key="index1"
            :style="{width:`calc(100% / ${columns.length})`}">
          <a-badge status="warning" v-if="route.query.tId == '2' && item1.dataIndex === 'status'" />
          {{ item[item1.dataIndex] }}
        </li>
      </ul>
    </vue3ScrollSeamless>
  </div>
</template>

<script setup lang="ts">
import {vue3ScrollSeamless} from "vue3-scroll-seamless";

import {ref, onMounted} from 'vue';
import {useResizeObserver} from '@vueuse/core';
import {useRoute} from "vue-router";

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  columns: {
    type: Array,
    default: () => []
  }
});
const route = useRoute();
const mains = ref();
const scrollHeight = ref(0);

onMounted(() => {
  useResizeObserver(mains, (entries) => {
    const entry = entries[0];
    const {width, height} = entry.contentRect;
    scrollHeight.value = height - 38;
  });
})
const classOptions = {
  direction: 0,
  singleHeight: 48,
  waitTime: 3000
};
</script>

<style lang="less" scoped>
.table-list-view {
  font-size: 16px;
  color: #FFFFFF;
  margin-top: 6px;

  ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .table-list-header, .table-list-body {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    li{
      white-space: nowrap;
    }
    &:hover{
      background: rgba(52, 144, 227, 0.05);
    }
  }

  .table-list-header {
    font-weight: bold;
    padding: 6px 0;
    background: rgba(52, 144, 227, 0.1);

    li {
      text-align: center;
    }
  }

  .table-list-body {
    background: rgba(52, 144, 227, 0.1);
    li {
      text-align: center;
      font-size: 14px;
      padding: 10px 0;
    }
  }
  .itemBg{
    background: none;
  }
}

.scroll-wrap {
  width: 100%;
  overflow: hidden;
  top: 30px;
}

</style>
