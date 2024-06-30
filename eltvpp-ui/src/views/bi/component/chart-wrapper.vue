<template>
  <div class="wrapper-list">
    <!-- 标题 -->
    <a-card :bordered="false" class="wrapper-list-card" :border="false">
      <template #title>
        <a-space class="wrapper-card-title">
          <!-- title，单位 -->
          <a-typography-title class="title-left-title">
            {{ title }} <span>{{ unit }}</span>
          </a-typography-title>
          <!-- 按钮 -->
          <slot name="button"></slot>
          <!-- 处理状态 -->
          <a-space class="title-process">
            <slot name="process"></slot>
          </a-space>
        </a-space>
      </template>
      <template #extra>
        <!-- 右侧 -->
        <a-typography-title class="title-right-unit" @click="goDetail">
          <slot name="extra"></slot>
        </a-typography-title>
      </template>
      <!-- 图表 -->
      <slot name="chart"/>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import {useRoute,useRouter} from "vue-router";

const route = useRoute()
const router = useRouter()
const props = defineProps({
  unit: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    default: '实时日总发电量',
  },
  public:{
    type: Object,
    default: () => {
      return {}
    },
  }
})
const goDetail = () => {
  console.log(props.public)
  router.push({
    path: '/biTwo',
    query:{
      ...props.public
    }
  })
}
</script>
<style lang="less" scoped>
@font-size: 14px;
@font-size-mini: 12px;
@font-size-medium: 16px;
@font-size-large: 22px;
@font-size-title: 18px;
@color-w: white;
@font-family: Source Han Sans;

.wrapper-list {
  background: rgba(52, 144, 227, 0.1);
  width: 100%;
  height: 100%;
  position: relative;


  &::after {
    content: '';
    z-index: 1;
    position: absolute;
    width: 10px;
    height: 10px;
    border: 2px solid #3490E3;
    bottom: -2px;
    left: -2px;
    border-right: none;
    border-top: none;
    border-bottom-left-radius: 3px;
  }

  &::before {
    content: '';
    z-index: 1;
    position: absolute;
    width: 10px;
    height: 10px;
    border: 2px solid #3490E3;
    bottom: -2px;
    right: -2px;
    border-left: none;
    border-top: none;
    border-bottom-right-radius: 3px;
  }
}

/* title样式 */
.wrapper-list-card {
  background-color: transparent;
  height: 100%;
  padding: 0;
  display: grid;
  grid-template-rows: 30px 1fr;

  :deep(.arco-card-body) {
    height: 100%;
    padding: 0;
    overflow-y: auto;
    scrollbar-width: none; /* 适用于 Firefox */
    -ms-overflow-style: none; /* 适用于 Internet Explorer 和 Edge */
  }

  &::-webkit-scrollbar {
    display: none; /* 适用于 Chrome, Safari 和 Opera */
  }

  .wrapper-card-title {
    display: flex;
    align-items: center;

    .title-left-title {
      list-style: none;
      font-size: @font-size-title;
      color: @color-w;
      padding: 6px 36px 6px 6px;
      font-weight: bold;
      font-variation-settings: "opsz" auto;
      font-feature-settings: "kern" on;
      background: url("@/assets/bi/two/chart-title-bg.png") no-repeat;
      background-size: 100% 100%;
      border-left: 4px solid #0E88F6;
      margin: 0 15px 0 0;

      span {
        font-weight: 400;
        font-size: @font-size-mini;
        margin-left: 4px;
      }
    }

    :deep(.arco-space-item) {
      font-size: @font-size;
      font-variation-settings: "opsz" auto;
      font-feature-settings: "kern" on;
      color: @color-w;

      .title-process {
        span {
          color: #F3921D;
        }
      }
    }


    /* 按钮样式 */

    :deep(.arco-btn-primary, .arco-btn-primary[type='button'], .arco-btn-primary[type='submit']) {
      display: inline-block;
      padding: 5px 16px;
      gap: 4px;
      font-size: @font-size-mini;
      color: @color-w;
      line-height: 22px;
      margin-right: 8px;
      letter-spacing: -0.01px;
      background: linear-gradient(0deg, rgba(15, 141, 255, 0.4) 0%, rgba(15, 141, 255, 0) 100%);
      border: 1px solid rgba(15, 141, 255, 0.1);
      border-radius: 6px;
      box-sizing: border-box;

      &:hover {
        background: linear-gradient(0deg, rgba(15, 141, 255, 0.6) 0%, rgba(15, 141, 255, 0) 100%);
      }
    }

  }

  .chart-view {
    width: 100%;
    height: 100%;
  }

  :deep(.arco-card-header) {
    border: none;
    padding: 0 14px 0 0;
    background: rgba(0, 134, 255, 0.2);
    height: auto;
  }

  .title-right-unit {
    margin: 0;
    list-style: none;
    text-align: right;
    font-size: @font-size;
    color: @color-w;
    font-variation-settings: "opsz" auto;
    font-feature-settings: "kern" on;
    cursor: pointer;

    ::v-deep(.arco-icon) {
      color: #0E88F6;
      font-size: 16px;
      font-weight: bold;
    }
  }

  .chart-view {
    height: 100%;
  }
}

</style>
