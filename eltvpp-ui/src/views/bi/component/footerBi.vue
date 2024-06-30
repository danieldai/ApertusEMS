<template>
  <a-row class="foot-view">
    <a-col :span="12" class="select-view">
      <a-space :size="1">
        <img src="@/assets/bi/logo.png" alt="">
        <a-select :style="{width:'300px'}" placeholder="Please select ..." >
          <a-option>山东银凯特智慧园区能源管理中心</a-option>
          <a-option>Shanghai</a-option>
          <a-option>Guangzhou</a-option>
          <a-option disabled>Disabled</a-option>
          <template #arrow-icon>
            <icon-caret-down size="18" style="color: white" />
          </template>
        </a-select>
        <div class="full-btn" @click="toggleFullscreen">
          <img v-if="isFullscreen" src="@/assets/bi/icon-shrink.png" alt="">
          <icon-expand v-else />
          <text>{{ isFullscreen ? '退出全屏' : '全屏' }}</text>
        </div>
      </a-space>
    </a-col>
    <a-col :span="12">
      <div class="version-view">EasyLink Technology iEMS Ver 1.11.01</div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
  import { useFullscreen } from '@/utils/bi/useFullscreen';
  const { isFullscreen, toggleFullscreen } = useFullscreen();

</script>

<style scoped lang="less">
  @font-size: 14px;
  @font-size-mini: 12px;
  @font-size-medium: 16px;
  @font-size-large: 22px;
  @font-size-title: 18px;
  @color-w: white;
  @font-family: Source Han Sans;

  .foot-view {
    text-align: center;
    align-items: center;
    height: 100%;

    .select-view {
      text-align: start;
      padding-left: 32px;

      img {
        width: 30px;
        height: 30px;
      }

      .full-btn {
        display: flex;
        align-items: center;
        font-size: @font-size-medium;
        margin-left: 26px;

        text {
          margin-left: 5px;
          font-size: @font-size;
        }

        img{
          width: 14px;
          height: 14px;
          margin-right: 4px;
        }
      }
    }

    .version-view {
      font-size: 15px;
      text-align: end;
      padding-right: 32px;
    }
  }

  ::v-deep(.arco-select) {
    background-color: transparent;
    border-radius: 0;
    border: none;
    color: #FFFFFF !important;

    .arco-select-view-value {
      font-size: 15px;
    }
  }
</style>
