<template>
  <a-row align="center" class="time-search">
    <a-col>
      <a-row align="center" style="width: 100%;">
        <!-- 左侧时间范围 -->
        <a-col :span="12">
          <a-space>
            <a-typography-text class="search-label">时间范围</a-typography-text>
            <a-range-picker class="search-picker" :time-picker-props="{ defaultValue: ['00:00:00', '09:09:06'] }"
              format="YYYY-MM-DD">
            </a-range-picker>
            <a-button type="primary" class="search-button">查询</a-button>
          </a-space>
        </a-col>
        <!-- 右侧年月日返回 -->
        <a-col :span="12" style="text-align: right;">
          <a-space>
            <a-button type="primary" class="time-button">年</a-button>
            <a-button type="primary" class="time-button">月</a-button>
            <a-button type="primary" class="time-button">日</a-button>
            <a-button type="primary" class="search-button" @click="back">
              <img src="@/assets/bi/two/icon-back.png" alt="">
              返回</a-button>
          </a-space>
        </a-col>
      </a-row>
    </a-col>

  </a-row>
</template>
<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";

const route = useRoute()
const router = useRouter();
  // 返回
  const back = () => {
    console.log("返回");
    if(route.path === '/biTwo'){
      router.push({
        path: '/bi',
        query: {tId: 2}
      })
    }
  }
</script>
<style lang="less" scoped>
  @font-size: 16px;
  @font-size-mini: 14px;
  @font-size-medium: 18px;
  @font-size-large: 24px;
  @color-w: white;
  @font-family: Source Han Sans;

  .time-search {
    padding: 12px 32px;
    box-sizing: border-box;
    font-family: @font-family;

    .arco-col {
      height: 100%;
    }

    .search-label {
      font-size: @font-size-medium;
      font-weight: normal;
      color: #fff;
      margin-right: 20px;
    }

    /* 时间选择器 */
    :deep(.arco-picker) {
      width: 328px;
      background-color: transparent;
      border: 1px solid rgba(15, 141, 255, 0.6);

      .arco-picker-range {
        z-index: 2;
      }



      input {
        background-color: transparent;
        color: @color-w;
        text-align: center;
      }

      input::placeholder {
        color: @color-w;
      }

      .arco-picker-separator {
        color: @color-w;
      }

      .arco-picker-suffix-icon {
        color: @color-w;
      }
    }

    :deep(.arco-picker-week-list-item) {
      font-size: @font-size-medium;
    }


    /* 查询,返回按钮 */
    .search-button {
      padding: 5px 16px;
      box-sizing: border-box;
      background: linear-gradient(0deg, rgba(15, 141, 255, 0.4) 0%, rgba(15, 141, 255, 0) 100%);
      border: 1px solid rgba(15, 141, 255, 0.1);
      margin-left: 20px;
      font-size: @font-size-medium;
      font-weight: normal;
      line-height: 22px;
      color: @color-w;

      &:hover {
        background: linear-gradient(0deg, rgba(15, 141, 255, 0.6) 0%, rgba(15, 141, 255, 0) 100%);
      }

      img {
        margin-right: 8px;
      }
    }

    /* 年月日按钮 */
    .time-button {
      padding: 3px 7px;
      border-radius: 4px;
      box-sizing: border-box;
      border: 1px solid #3490E3;
      background-color: transparent;

      &:hover {
        background: linear-gradient(0deg, rgba(15, 141, 255, 0.6) 0%, rgba(15, 141, 255, 0) 100%);
      }
    }
  }
</style>