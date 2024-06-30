<template>
  <div class="wrapper-list">
    <!-- 标题 -->
    <a-card :bordered="false" class="wrapper-list-card" :border="false" :body-style="{ height:'100%',padding:0 }">
      <template #title>
        <a-space class="card-title">
          <!-- title，单位 -->
          <a-typography-title class="title-left-title">
            {{ title }}
            <span>{{ unit }}</span>
            <img v-if="iconShow" src="@/assets/bi/two/icon-open.png" alt="" @click="goDetail">
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
        <a-typography-title class="title-right-unit">
          <slot name="extra"></slot>
        </a-typography-title>
      </template>
      <!-- 图表 -->
      <div class="chart-view">
        <slot name="chart" />
      </div>
    </a-card>
  </div>
</template>
<script setup lang="ts">
  import { useRoute, useRouter } from "vue-router";

  const route = useRoute()
  const router = useRouter()
  const props = defineProps({
    iconShow:{
      type: Boolean,
      default: false,
    },
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
    router.push({
      path: '/biTwoOld',
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
    height: 100%;
    position: relative;
    border: 1px solid #FFFFFF;
    border-radius: 8px;

    &::before,
    &::after {
      content: '';
      z-index: 1;
      position: absolute;
      width: 10px;
      height: 10px;
      border: 3px solid #FFFFFF;
    }

    &::after {
      bottom: -3px;
      left: -3px;
      border-right: none;
      border-top: none;
      border-bottom-left-radius: 8px;
    }

    &::before {
      bottom: -3px;
      right: -3px;
      border-left: none;
      border-top: none;
      border-bottom-right-radius: 8px;
    }
  }

  /* title样式 */
  .wrapper-list-card {
    background-color: transparent;
    height: 100%;
    padding: 0;
    display: grid;
    grid-template-rows: 40px 1fr;

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

    &::before,
    &::after {
      content: '';
      z-index: 2500;
      position: absolute;
      width: 10px;
      height: 10px;
      border: 3px solid #FFFFFF;
    }

    &::after {
      top: -3px;
      left: -3px;
      border-right: none;
      border-bottom: none;
      border-top-left-radius: 8px;
    }

    &::before {
      top: -3px;
      right: -3px;
      border-left: none;
      border-bottom: none;
      border-top-right-radius: 8px;
    }

    .card-title {
      display: flex;
      align-items: center;

      .title-left-title {
        margin: 0;
        list-style: none;
        font-size: @font-size-title;
        color: @color-w;
        height: 52px;
        line-height: 52px;
        padding: 0 20px 0 12px;
        font-weight: bold;
        font-variation-settings: "opsz" auto;
        font-feature-settings: "kern" on;
        background-size: 100% 100%;
        display: flex;
        align-items: center;
        img{
          margin-left: 5px;
          width: 16px;
          height: 16px;
          cursor: pointer;
        }
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
        height: 26px;
        padding: 0 12px;
        gap: 4px;
        font-size: @font-size-mini;
        color: @color-w;
        margin-right: 8px;
        letter-spacing: -0.01px;
        background: #151494;
        border: 2px solid #FFFFFF;
        border-radius: 8px;
        box-sizing: border-box;

        &:hover {
          background: #0000FF;
        }
      }

    }

    .chart-view {
      height: 100%;
    }

    :deep(.arco-card-header) {
      padding: 0;
      padding-right: 14px;
      background: linear-gradient(90deg, rgba(28, 51, 185, 0.8) 0%, rgba(28, 51, 185, 0.65) 3%, rgba(0, 141, 240, 0) 100%);
      background-size: 100% 100%;
      height: auto;
      margin: 0 1px;
      border: 3px solid;
      border-image: linear-gradient(90deg, #1C33B9 0%, rgba(28, 51, 185, 0) 100%) 4;
      border-right: none;
      clip-path: inset(0 round 16px);
    }

    .title-right-unit {
      margin: 0;
      list-style: none;
      text-align: right;
      font-size: @font-size;
      color: @color-w;
      font-variation-settings: "opsz" auto;
      font-feature-settings: "kern" on;

      ::v-deep(.arco-icon) {
        color: #0E88F6;
        font-size: @font-size-medium;
        font-weight: bold;
      }
    }

    .chart-view {
      height: 100%;
    }
  }

</style>
