<!--
 * 功能：基础图
 * 作者：闫李壮
 * 日期：2024-06-04
-->
<template>
<a-spin class="card-base-wrapper" :loading="loading">
  <a-card :bordered="false" :style="{backgroundColor: chartInfo.backgroundColor}">
    <div class="chart-view" v-if="chartInfo.cardName">
      <div class="echart-wapper">
        <a-typography-title
            :heading="5"
            :ellipsis="{ rows: 1, showTooltip: true }"
            style="margin-top: 0"
        >
          {{ chartInfo.cardName }}
        </a-typography-title>
        <div class="content-wrap">
          <!-- 标题、数值start -->
          <div class="content">
            <a-statistic :value="chartInfo.singleValue" :precision="2" show-group-separator>
              <template #suffix>{{ chartInfo.unit }}</template>
            </a-statistic>
            <div class="desc" v-if="chartInfo.chain">
              <a-typography-text type="secondary" class="content-label">较昨日</a-typography-text>
              <a-typography-text :type="chartInfo.chainRatio < 0 ? 'danger' : 'success'">
                {{ Math.abs(chartInfo.chainRatio) }}%
                <icon-arrow-fall v-if="chartInfo.chainRatio < 0"/>
                <icon-arrow-rise v-else/>
              </a-typography-text>
            </div>
            <div class="desc" v-if="chartInfo.yoy">
              <a-typography-text type="secondary" class="content-label">较上月</a-typography-text>
              <a-typography-text :type="chartInfo.yoyRatio < 0 ? 'danger' : 'success'">
                {{ Math.abs(chartInfo.yoyRatio) }}%
                <icon-arrow-fall v-if="chartInfo.yoyRatio < 0"/>
                <icon-arrow-rise v-else/>
              </a-typography-text>
            </div>
          </div>
          <!-- 标题、数值end -->
          <!-- 右侧图标start -->
          <div class="icon-view">
            <component :is="chartInfo.icon" :size="38" :style="{ color: chartInfo.iconColor }"></component>
          </div>
        </div>
      </div>
      <div class="total-wapper" v-if="chartInfo.singleRightList?.length">
        <category-total class="category-total" :data="chartInfo.singleRightList" :type="totalType"/>
      </div>
    </div>
    <a-empty v-else/>
  </a-card>
</a-spin>
</template>

<script lang="ts" setup>
  import {ref, onMounted, inject, watch, onBeforeUnmount} from 'vue';
  import useLoading from '@/hooks/loading';
  import {getChartInfo} from "@/api/dashboard/api";
  import {SocketData} from "@/api/websocketService";
  import {useIntervalFn} from "@vueuse/core";
  import CategoryTotal from "@/views/bi/component/category-total.vue";
  import {getTimeObject} from "@/utils/charts";

  const props = defineProps({
    public: {
      type: Object,
      default: () => ({}),
    },
    // 设备编号
    deviceSn: {
      type: String,
      default: ''
    },
    totalType: {
      type: Number,
      default: 2,
    }
  });

  const {loading, setLoading} = useLoading(false);

  const chartInfo = ref<any>({
    cardName: '',
    singleInfo:{},
    singleRightList:[]
  });

  // WebSocket 数据
  const {data, sendData}: SocketData = inject('webSocketData', {
    data: {value: 0},
    sendData: () => {
    }
  });

  const timeOptions = ref<any>({});
  /**
   * 时间改变
   */
  const timeBarChange = async (timer: any) => {
    timeOptions.value = timer;
    await fetchData();
  }

  // 监听 WebSocket 数据
  watch(data, async (newVal) => {
    if (newVal.key && newVal.key === chartInfo.value.varSn) {
      await fetchData();
    }
  });

  // 5 分钟轮询
  const {resume: startInterval, pause: stopInterval} = useIntervalFn(async () => {
    await fetchData();
  }, 1000 * 60 * 5);

  // 获取数据
  const fetchData = async () => {
    try {
      setLoading(true);
      const params = {
        configId: props.public.dashboardConfigId,
        cardKey: props.public.cardKey,
        deviceSn: props.deviceSn,
        ...getTimeObject(timeOptions.value),
      };
      const res = await getChartInfo(params);
      if (res.code === 200 && res.data) {
        chartInfo.value = {
          cardName: res.data.cardName,
          singleInfo: res.data.singleInfo,
          singleRightList: res.data.singleRightList,
          ...res.data.singleInfo
        };
      } else {
        chartInfo.value = {
          cardName: ''
        }
      }
    } catch (e) {
      console.error('单值组件出错', e);
    } finally {
      setLoading(false);
    }
  };

  onMounted(async () => {
    await fetchData();
    startInterval();
    sendData({
      action: 'subscribe',
      varSn: chartInfo.value.varSn
    });
  });

  onBeforeUnmount(() => {
    stopInterval();
    sendData({
      action: 'unsubscribe',
      varSn: chartInfo.value.varSn
    });
  });

  defineExpose({
    fetchData,
    handleTime: (val)=>{
      timeBarChange(val);
    },
  });
</script>

<style scoped lang="less">
  .card-base-wrapper {
    width: 100%;
    height: 100%;

    :deep(.arco-card) {
      border-radius: 4px;
      height: 100%;

      .arco-card-body {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }
    }

    .content-wrap {
      width: 100%;
      display: flex;
      justify-content: space-between;

      .content {
        flex: 1;
        display: flex;
        flex-direction: column;

        .desc {
          white-space: nowrap;
        }
      }

      .icon-view {
        flex-shrink: 0;
        align-items: center;
        font-size: var(--font-size-display-1);
        margin: 0 auto;
      }

      .content-label {
        padding-right: 8px;
        font-size: 12px;
      }

      :deep(.arco-statistic) {
        .arco-statistic-title {
          font-size: 16px;
          font-weight: bold;
          white-space: nowrap;
        }

        .arco-statistic-content {
          margin-top: 0;
        }
      }

      :deep(.arco-statistic-title) {
        width: 140%;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      :deep(.arco-statistic-content .arco-statistic-value) {
        color: var(--color-text-2);
      }

      :deep(.arco-card-header) {
        border: none;
      }
    }
  }

  .chart-view {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: nowrap;

    .echart-wapper{
      flex: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    .total-wapper{
      width: 30%;
      height: 100%;
    }
  }
</style>
