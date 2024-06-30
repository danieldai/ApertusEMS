<!--
 * 功能：饼图
 * 作者：闫李壮
 * 日期：2024-06-04
-->
<template>
  <a-card class="chart-container" :title="chartInfo.cardName">
    <!-- 额外操作栏，只有当 configType 为 1 时显示 -->
    <template #extra v-if="configType === 1">
      <time-bar @change="timeBarChange" isCurrentDateDisabled/>
    </template>
    <!-- 加载动画，加载完成后显示图表或空状态 -->
    <a-spin :loading="loading" class="chart-view" :tip="$t('global.loading')">
      <template v-if="chartInfo?.statusRightList&&chartInfo?.statusRightList.length">
        <a-row :gutter="20" justify="center" align="center" style="width:100%;height:100%">
          <a-col v-for="(item, index) in chartInfo?.statusRightList" :key="index" :span="4">
            <a-progress type="circle" :color="item?.status?'rgb(var(--green-6))':'rgb(var(--red-5))'" size="large" :strokeWidth="10" :percent="1">
              <template v-slot:text="scope">
                {{item?.status?'在线':'离线'}}
              </template>
            </a-progress>
          </a-col>
        </a-row>
      </template>
      <a-empty v-else/>
    </a-spin>
  </a-card>
</template>

<script setup lang="ts">
import {reactive, ref} from 'vue';
import useLoading from '@/hooks/loading';
import {getTimeObject} from '@/utils/charts';
import {getChartInfo} from '@/api/dashboard/api';

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
});

// 状态管理
const {loading, setLoading} = useLoading(false);
const chartInfo = ref({
  cardName: '',
  statusInfo: {},
  statusRightList: []
});

const statusConfig = [{
  id: 1,
  status: false,
  percent: 1
},{
  id: 2,
  status: true,
  percent: 1
},{
  id: 3,
  status: false,
  percent: 1
}]

const statusList = ref<any>(statusConfig);
const timeOptions = ref<any>({});

// 时间变化处理函数
const timeBarChange = async (timer: any) => {
  timeOptions.value = timer;
  await fetchData();
};

// 查询图表数据
const fetchData = async () => {
  try {
    setLoading(true);
    const params = {
      configId: props.public.dashboardConfigId,
      cardKey: props.public.cardKey,
      ...getTimeObject(timeOptions.value),
    };
    const res = await getChartInfo(params);
    if (res.code === 200 && res.data) {
      chartInfo.value = {
        ...res.data,
      };

      console.log(res.data, 'data')
    }
  } catch (error) {
    console.error('获取图表数据时出错:', error);
  } finally {
    setLoading(false);
  }
};

// 生成图表配置
const chartData = (option: any) => {
  return {
    tooltip: {
      trigger: 'item',
    },
    legend: {
      bottom: 0,
      left: 'center',
      type: 'scroll',
    },
    series: [
      {
        name: option.series[0].name,
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderWidth: 2,
          show: false,
          borderRadius: 5,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold',
            formatter: '{d}%',
          },
        },
        labelLine: {
          show: false,
        },
        data: option.series[0].objData,
      },
    ],
  };
};


// 这里借鉴'5643我'和'小明的小可'两位大神的圆环图格式演示一下效果
const dataStyle = {
  normal: {
    label: {
      show: false
    },
    labelLine: {
      show: false
    },
    shadowBlur: 40,
    shadowColor: 'rgba(40, 40, 40, 0.5)',
  }
};

const placeHolderStyle = {
  normal: {
    color: 'rgba(44,59,70,1)', // 未完成的圆环的颜色
    label: {
      show: false
    },
    labelLine: {
      show: false
    }
  },
  emphasis: {
    color: 'rgba(44,59,70,1)' // 未完成的圆环的颜色
  }
};


const option = {
  title: {
    text: '50%',
    x: 'center',
    y: 'center',
    textStyle: {
      fontWeight: 'normal',
      color: '#b697cd',
      fontSize: 35
    }
  },
  tooltip: {
    show: false,
  },
  toolbox: {
    show: false,
  },
  // color : ['#3dd4de','#b697cd','#a6f08f'],
  backgroundColor: 'rgba(0,0,0,0.8)',
  series: [{
    name: 'Pie1',
    type: 'pie',
    clockWise: false,
    radius: [80, 85],
    itemStyle: dataStyle,
    hoverAnimation: false,
    center: ['25%', '50%'],
    data: [{
      value: 25,
      label: {
        normal: {
          formatter: '{d}%',
          position: 'center',
          show: true,
          textStyle: {
            fontSize: '35',
            fontWeight: 'normal',
            color: '#3dd4de'
          }
        }
      },
      itemStyle: {
        normal: {
          color: '#3dd4de',
          shadowColor: '#3dd4de',
          shadowBlur: 10
        }
      }
    }, {
      value: 75,
      name: 'invisible',
      itemStyle: placeHolderStyle,
    }]
  }, {
    name: 'Pie2',
    type: 'pie',
    clockWise: false,
    radius: [80, 85],
    itemStyle: dataStyle,
    hoverAnimation: false,
    center: ['50%', '50%'],
    data: [{
      value: 50,
      itemStyle: {
        normal: {
          color: '#b697cd',
          shadowColor: '#b697cd',
          shadowBlur: 10
        }
      }
    }, {
      value: 50,
      name: 'invisible',
      itemStyle: placeHolderStyle,
    }]
  }, {
    name: 'Pie3',
    type: 'pie',
    clockWise: false,
    radius: [80, 85],
    itemStyle: dataStyle,
    hoverAnimation: false,
    center: ['75%', '50%'],
    data: [{
      value: 75,
      label: {
        normal: {
          formatter: '{d}%',
          position: 'center',
          show: true,
          textStyle: {
            fontSize: '35',
            fontWeight: 'normal',
            color: '#a6f08f'
          }
        }
      },
      itemStyle: {
        normal: {
          color: '#a6f08f',
          shadowColor: '#a6f08f',
          shadowBlur: 10
        }
      }
    }, {
      value: 25,
      name: 'invisible',
      itemStyle: placeHolderStyle,
    }]
  }, ]
};

// 暴露 timeBarChange 方法
defineExpose({
  handleTime: (val)=>{
    timeBarChange(val);
  },
})
</script>

<style scoped lang="less">
.chart-container {
  height: 100%;
  width: 100%;

  :deep(.arco-card-body) {
    width: 100%;
    height: calc(100% - 46px);
  }

  .chart-view {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.arco-empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

:deep(.arco-progress-circle-wrapper){
  width: 100px!important;
}

:deep(.arco-progress-circle-text){
  transform: translate(-50%, -0%);
}

</style>
