<!--
 * 功能：折线图
 * 作者：闫李壮
 * 日期：2024-06-04
-->
<template>
  <div class="chart-container">
    <a-tooltip content="下载" position="bottom">
      <icon-download class="download-icon" @click="download" size="20"/>
    </a-tooltip>

    <!-- 额外操作栏，只有当configType为1时显示 -->
    <div class="time-search" v-if="configType === 1">
      <time-bar @change="timeBarChange" isCurrentDateDisabled/>
    </div>
    <!-- 加载动画，加载完成后显示图表 -->
    <a-spin :loading="loading" class="chart-view" :tip="$t('global.loading')">
      <div class="echart-wapper">
        <CutomChart ref="chartRef" v-if="!loading" :options="chartInfo.echartsOption"/>
      </div>
      <div class="total-wapper" v-if="chartInfo.singleRightList?.length">
        <category-total class="category-total" :data="chartInfo.singleRightList" :type="totalType"/>
      </div>
    </a-spin>
  </div>
</template>


<script setup lang="ts">
import {onBeforeUnmount, onMounted, ref} from 'vue';
import useLoading from '@/hooks/loading';
import {getChartInfo} from '@/api/dashboard/api';
import {useIntervalFn} from '@vueuse/core';
import {getTimeObject, handleConfigChart, handlePreConfigChart} from '@/utils/charts';
import CategoryTotal from "@/views/bi/component/category-total.vue";

// 定义图表信息的接口
interface IChartInfo {
  cardName: string;
  echartsOption: any;
  singleInfo: null | any;
  singleRightList: Array<any>;
}

// 定义组件接收的属性
const props = defineProps({
  public: {
    type: Object,
    default: () => ({}),
  },
  configType: {
    type: Number,
    default: 1,
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

// 状态管理
const {loading, setLoading} = useLoading(false);
const chartInfo = ref<IChartInfo>({
  cardName: '',
  echartsOption: {},
  singleInfo: null,
  singleRightList: [],
});

const timeOptions = ref<any>({});

// 时间变化处理函数
const timeBarChange = async (timer: any) => {
  timeOptions.value = timer;
  await fetchData();
};

// 数据获取函数
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
        ...res.data,
        echartsOption: handlePreConfigChart(res.data.echartsOption),
      };
    } else {
      chartInfo.value = {
        cardName: '',
        echartsOption: {},
        singleInfo: null,
      };
    }
  } catch (error) {
    console.error('获取图表数据时出错:', error);
  } finally {
    setLoading(false);
  }
};


const chartRef = ref<any>(null)

const download = () => {
  chartRef.value.downloadEchartsImg(chartInfo.value.cardName)
}
// 每5分钟轮询一次数据
useIntervalFn(fetchData, 300000);

onMounted(() => {
  fetchData()
});

defineExpose({
  handleTime: (val)=>{
    timeBarChange(val);
  },
});
</script>

<style scoped lang="less">
.chart-container {
  height: 100%;
  width: 100%;
  position: relative;

  .download-icon{
    position: absolute;
    color: #FFFFFF;
    cursor: pointer;
    left: 0;
  }

  .time-search {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }

  :deep(.arco-card-body) {
    width: 100%;
    height: calc(100% - 46px);
  }

  .chart-view {
    width: 100%;
    height: calc(100% - 8px);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: nowrap;
    padding: 0 10px;

    .echart-wapper {
      flex: 1;
      height: 100%;
    }

    .total-wapper {
      width: 30%;
      height: 100%;
    }
  }

  .arco-empty {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
}
</style>

