<template>
  <!-- <div id="data-view"> -->
  <YkiteFullScreenContainer>
    <div class="data-view">
      <!--      行头部-->
      <div class="dView-head">
        <header-bi :title="chartParams.pageName"></header-bi>
      </div>
      <!-- 时间范围 -->
      <a-row class="data-view-search" align="center" style="width: 100%;">
        <a-col :span="12">
          <a-space>
            <a-typography-text style="color: #fff;font-size: 20px; font-weight: 500">时间范围：</a-typography-text>
            <component
                :is="timeOptions.timeUnit === 0 ?'a-date-picker' : timeOptions.timeUnit === 1 ? 'a-month-picker' : 'a-year-picker'"
                v-model="timeOptions.timer"
                :format="timeOptions.timeUnit === 0 ?'YYYY-MM-DD' : timeOptions.timeUnit === 1 ? 'YYYY-MM' : 'YYYY'"></component>
            <a-button type="primary" class="datav-two-button" :style="{marginLeft:'10px'}" @click="handleSearch">查询
            </a-button>
          </a-space>
        </a-col>
        <a-col :span="12" style="text-align: right;">
          <a-space>
            <a-button type="primary" class="datav-two-button" @click="handleTimeType(2)">年</a-button>
            <a-button type="primary" class="datav-two-button" @click="handleTimeType(1)">月</a-button>
            <a-button type="primary" class="datav-two-button" @click="handleTimeType(0)">日</a-button>
            <a-button type="primary" class="datav-two-button" :style="{marginLeft:'10px'}" @click="back">返回</a-button>
          </a-space>
        </a-col>
      </a-row>
      <!-- 行中间部分 -->
      <div class="dView-content">
        <div class="dView-content-list">
          <chart-wrapper :title="chartParams.cardName">
<!--            <template v-slot:button>-->
<!--              <a-button :class="index==0?'button-active':''" type="primary" v-for="(item,index) in buttonList"-->
<!--                        :key="index">{{ item }}-->
<!--              </a-button>-->
<!--            </template>-->
            <template v-slot:chart>
              <div class="chart-wrapper-content">
                <!--              <chart :option="cardInfoNew.option" autoresize />-->
                <!--              <chart :options="chartData2" autoresize></chart>-->
                <component :is="EnumChartType[chartParams.chartType]"
                           :ref="setChartRef"
                           :configType="0"
                           :public="{cardKey: chartParams.cardKey, dashboardConfigId: chartParams.dashboardConfigId}"
                           :totalType="0"></component>
              </div>
            </template>
          </chart-wrapper>
        </div>
        <!--      <div class="dView-content-list dView-content-list-multi">-->
        <!--        <a-grid :cols="{ xs: 2, sm: 2, md: 3, lg: 3, xl: 3, xxl: 3 }" :colGap="12" :rowGap="16" style="height: 100%">-->
        <!--          <a-grid-item class="multi-li" v-for="(val,idx) in 6" :key="idx">-->
        <!--            <chart-wrapper :title="'实时光伏日发电量'">-->
        <!--              <template v-slot:button>-->
        <!--                <a-button type="primary">下载</a-button>-->
        <!--              </template>-->
        <!--              <template #chart>-->
        <!--                <div class="chart-wrapper">-->
        <!--                  <div class="chart">-->
        <!--                       <chart :option="cardInfo.option" />-->
        <!--                  </div>-->
        <!--                  <div class="description">-->
        <!--                    <category-total :type="0" :data="cardInfo.expand.data"/>-->
        <!--                  </div>-->
        <!--                </div>-->
        <!--              </template>-->
        <!--            </chart-wrapper>-->
        <!--          </a-grid-item>-->
        <!--        </a-grid>-->
        <!--      </div>-->
        <div class="dView-content-list dView-content-list-table">
          <chart-wrapper :title="chartParams.cardName">
            <template #chart>
              <table-list ref="tableRef"></table-list>
            </template>
          </chart-wrapper>
        </div>
      </div>
      <!--行尾部-->
      <div class="dView-foot">
        <footer-bi @changeSite="changeSite"></footer-bi>
      </div>
    </div>
  </YkiteFullScreenContainer>
  <!-- </div> -->
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, onUnmounted, reactive, ref, emit, watchEffect} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import {handleDataVChartData} from "@/utils/charts";
import HeaderBi from "@/views/bi/component/headerBi.vue";
import FooterBi from "@/views/bi/component/footerBi.vue";
import ChartWrapper from "@/views/biOld/component/chart-wrapper.vue";
import ChartCategory from "@/views/biOld/component/chart-category.vue";
import CategoryTotal from "@/views/bi/component/category-total.vue";
import TableList from "@/views/biOld/component/table-list-scroll.vue";
import LargeTimeBar from "@/views/biOld/component/time-bar.vue";
import useLoading from '@/hooks/loading';
import {getChartInfo} from '@/api/dashboard/api';
import dayjs from "dayjs";

const {loading, setLoading} = useLoading(false);
import {getTimeObject, handleConfigChart, handlePreConfigChart} from '@/utils/charts';

const chartRef = ref<any>(null);
const tableRef = ref<any>(null);
const props = defineProps({
  // 0 为指定日期选择起 1 为范围选择器
  configType: {
    type: Number,
    default: 1,
  },
})
const route = useRoute()
const router = useRouter();
const backUrl: any = router.currentRoute.value.query.referer;
const buttonList = ref(['电量', '功率', '下载']);
const cardInfo = {
  title: "实时总发电量",
  type: 1,
  option: handleDataVChartData(),
  unit: 'kwh',
  expand: {
    type: 1,
    data: [{
      name: '光伏',
      value: 124,
      unit: 'kwh'
    }, {
      name: '风能',
      value: 34,
      unit: 'kwh'
    }, {
      name: '其他',
      value: 1,
      unit: 'kwh'
    }, {
      name: '合计',
      value: 158,
      unit: 'kwh'
    }]
  }
};
const chartInfo = ref<any>({
  cardName: '',
  echartsOption: {},
  singleInfo: null,
  singleRightList: [],
});
const EnumChartType: any = {
  1: 'large-combine',
  2: 'large-combine',
  3: 'large-combine',
  4: 'large-stack',
  5: 'large-combine',
  6: 'large-pie',
  7: 'large-k',
  8: 'large-water',
  9: 'large-gauge',
  10: 'large-sankey',
  11: 'large-section',
  12: 'large-ranking',
  13: 'card-status',
  14: 'card-base',
  15: 'card-table',
  16: 'card-table',
  17: 'large-pie-statistics',
  18: 'large-pie-statistics',
};
const data = [{
  value: 70,
  name: '光伏',
}, {
  value: 80,
  name: '风能',
}, {
  value: 90,
  name: '其他',
}];

const chartData2 = reactive({
  title: {
    text: '环比',
    // subtext: '日环比',
    x: 'center',
    y: '36%',
    textStyle: {
      fontWeight: 'normal',
      fontSize: 34,
      color: '#fff', // 设置标题文本颜色为黄色
    },
    subtextStyle: {
      color: '#fff', // 设置子标题文本颜色为白色
      fontSize: 14,
    },
  },
  color: ['#F3921D', '#0F8DFF'],
  backgroundColor: 'transparent',
  legend: {
    show: true,
    icon: 'rect',
    orient: 'horizontal',
    bottom: '30px',
    data: ['光伏', '风能', '其他'],
    itemGap: 25,
    itemWidth: 20,
    itemHeight: 20,
    formatter(name) {
      return `/n{title|${name}} {value|${data.find((item) => item.name === name).value
      }}  {title|%}`;
    },
    textStyle: {
      rich: {
        title: {
          fontSize: 16,
          lineHeight: 15,
          color: '#fff',
        },
        value: {
          fontSize: 18,
          lineHeight: 20,
          color: '#76A3FC',
        },
      },
    },
  },
  series: [
    {
      name: '光伏',
      type: 'pie',
      radius: ['40%', '54%'],
      center: ['50%', '40%'],
      animation: false,
      data: [
        {
          value: data[0].value,
          itemStyle: {
            // borderRadius: '100%',
            color: '#F3921D',
          },
        },
        {
          value: data[2].value,
          itemStyle: {
            color: '#0071BC',
          },
        },
      ],
      label: {
        normal: {
          show: false,
        },
      },
      labelLine: {
        normal: {
          show: false,
        },
      },
    },
    // {
    //   name: '风能',
    //   type: 'card-chart-pie',
    //   radius: ['44%', '54%'],
    //   center: ['50%', '44%'],
    //   // radius: ['30%', '40%'],
    //   // center: ['50%', '44%'],
    //   animation: false,
    //   data: [
    //     {
    //       value: data[1].value,
    //       itemStyle: {
    //         color: '#0071BC',
    //         // borderRadius: '100%',
    //       },
    //     },
    //     {
    //       value: data[0].value - data[1].value,
    //       itemStyle: {
    //         color: 'rgba(17, 218, 202, 0.4)',
    //       },
    //     },
    //   ],
    //   label: {
    //     normal: {
    //       show: false,
    //     },
    //   },
    //   labelLine: {
    //     normal: {
    //       show: false,
    //     },
    //   },
    // },
    {
      name: '其他',
      type: 'pie',
      animation: false,
      radius: ['59%', '73%'],
      center: ['50%', '40%'],
      label: {
        normal: {
          show: false,
        },
      },
      labelLine: {
        normal: {
          show: false,
        },
      },
      // data: [{
      //   value: data[0].value,
      //   itemStyle: {
      //     color: '#FAC61C',
      //   },
      // }],
      data: [
        {
          value: data[1].value,
          itemStyle: {
            color: '#F3921D',
            // borderRadius: '100%',
          },
        },
        {
          value: data[1].value,
          // value: data[0].value - data[2].value,
          itemStyle: {
            color: '#0071BC',
          },
        },
      ],
    },
  ],
});
const cardInfoNew = {
  title: "实时总发电量",
  type: 1,
  option: {
    grid: {
      top: 30,
      left: 20,
      right: 20,
      bottom: 20,
      containLabel: true,
    },
    backgroundColor: "rgba(0, 0, 0, 0)",
    xAxis: {
      type: "category",
      data: [
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23
      ],
      axisTick: {
        show: false,
      },
      axisLabel: {
        fontSize: 16,
        color: "#ffffff",
        fontWeight: 'bold'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value} kW',
        color: "#ffffff",
        fontSize: 16,
        fontWeight: 'bold'
      },
      splitLine: {
        show: true, // 显示分隔线
        lineStyle: {
          type: 'dashed' // 设置分隔线类型为虚线
        }
      }
    },
    series: [
      {
        name: "bar",
        data: [50, 30, 30, 30, 40, 60, 70, 80, 90, 110, 110, 95, 105, 105, 95, 75, 80, 85, 90, 65, 45, 30, 30, 20],
        type: "bar",
        itemStyle: {
          color: function (params: any) {
            // 根据数据索引设置不同的颜色
            const colorList = ["#1acfff", "#ffffff"];
            return colorList[params.dataIndex < 13 ? 0 : 1];
          },
        }
      },
      {
        name: "line",
        data: [100, 60, 60, 60, 80, 120, 140, 160, 180, 110, 110, 95],
        type: "line",
        smooth: true,
        lineStyle: {
          color: '#ffff00' // 设置线条颜色为黄色
        },
        itemStyle: {
          color: '#ffff00' // 设置交汇点颜色为黄色
        },
        symbol: 'circle', // 设置交汇点为实心圆
        symbolSize: 10 // 设置交汇点大小为 5px
      },
    ],
  },
  unit: 'kwh',
  expand: {
    type: 1,
    data: [{
      name: '光伏',
      value: 124,
      unit: 'kwh'
    }, {
      name: '风能',
      value: 34,
      unit: 'kwh'
    }, {
      name: '其他',
      value: 1,
      unit: 'kwh'
    }, {
      name: '合计',
      value: 158,
      unit: 'kwh'
    }]
  }
};

const hasScrollbar = ref(false);

const timeOptions = ref<any>({
  timer: dayjs().format("YYYY-MM-DD"),
  timeUnit: 0,
});

const checkScrollbar = () => {
  const body = document.body;
  const html = document.documentElement;
  const hasScroll = html.clientHeight < html.scrollHeight || body.clientHeight < body.scrollHeight;
  hasScrollbar.value = hasScroll;
};

const setChartRef = (ref) => {
  console.log(ref, '111111111111111')
  chartRef.value = ref;
}
// 切换站点
const changeSite = () => {
  router.push({
    path: '/biOneOld'
  })
};

// 返回
const back = () => {
  router.back();
};


// 时间搜索
const handleSearch = () => {
  chartRef.value.handleTime(timeOptions.value);
  tableRef?.value.handleTime(timeOptions.value);

};

// 年月日
const handleTimeType = (unit: number) => {
  timeOptions.value.timeUnit = unit;
  timeOptions.value.timer = dayjs(timeOptions.value.timer).format(unit == 0 ? 'YYYY-MM-DD' : unit == 1 ? 'YYYY-MM' : "YYYY");
  chartRef.value.handleTime(timeOptions.value);
  tableRef?.value.handleTime(timeOptions.value);
}

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
      configId: chartParams.value.dashboardConfigId,
      cardKey: chartParams.value.cardKey,
      deviceSn: chartParams.value.deviceSn ? chartParams.value.deviceSn : undefined,
      ...getTimeObject(timeOptions.value),
    };

    const res = await getChartInfo(params);
    console.log('首页fetch')
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

/*************************** 方法区域 end ***************************/
const chartParams = ref<any>({});

/**
 * 组件加载完成
 */
onMounted(async () => {
  checkScrollbar();
  chartParams.value = route.query;
  window.addEventListener('resize', checkScrollbar);
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkScrollbar);
});

/**
 * 组件卸载
 */
onUnmounted(() => {
  // stopInterval()
})

</script>

<style lang="less" scoped>
@font-size: 16px;
@font-size-mini: 14px;
@font-size-medium: 18px;
@font-size-large: 24px;
@color-w: white;

.grid-title-num {
  color: #F3921D;
}

.data-view {
  width: 100vw;
  height: 100vh;
  background: url("@/assets/biTow/background.png") no-repeat;
  background-size: 100% 100%;
  color: @color-w;
  font-family: Source Han Sans;
  display: flex;
  flex-direction: column;
}

.data-view-search {
  width: 100%;
  padding: 12px 32px;
  box-sizing: border-box;

  :deep(.datav-two-button) {
    padding: 0 10px;
    font-size: 20px;
    font-weight: 500;
    background-color: rgba(28, 50, 183, 0.8814119397759104);
    border: 1px solid #fff;
    border-radius: var(--border-radius-large);

    &:hover {
      background: #0000FF;
    }
  }

  :deep(.headers-button) {
    height: 20px;
    font-size: 15px;
  }
}

.dView-head {
  height: 110px;
  background: url("@/assets/bi/navbarOld.png") no-repeat bottom;
  background-size: 100%;
  font-size: 28px;
  z-index: 100;
}

.dView-content {
  flex: 1;
  scrollbar-width: none;
  -ms-overflow-style: none;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 0 32px;

  .dView-content-list {
    height: 407px;
    margin-bottom: 16px;

    .chart-wrapper-content {
      width: 100%;
      height: 100%;
    }
  }

  .dView-content-list-multi {
    height: auto;

    .multi-li {
      height: 180px;
    }
  }

  .dView-content-list-table {
    height: auto;
  }

  .chart-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    padding: 10px;
    box-sizing: border-box;

    .chart {
      width: 72%;
      height: 100%;
    }

    .description {
      width: 28%;
      height: 100%;
      font-size: 16px;
    }
  }
}

.dView-content::-webkit-scrollbar {
  display: none;
}

.back-button {
  bottom: 100px;
  right: 80px;
}

:deep(.arco-back-top-btn) {
  background-color: #fff;
  color: #0000FF;
}

.dView-foot {
  height: 50px;
}

.button-active {
  margin-left: 50px;
}
</style>
