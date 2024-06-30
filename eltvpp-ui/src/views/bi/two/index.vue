<template>
  <!-- 全屏容器 -->
  <YkiteFullScreenContainer>
    <div class="data-view">
      <div class="dView-head">
        <header-bi type :title="chartParams.pageName"></header-bi>
      </div>
      <!-- 时间范围 -->
      <!--    <time-search></time-search>-->
      <a-row align="center" class="time-search">
        <a-col>
          <a-row align="center" style="width: 100%;">
            <!-- 左侧时间范围 -->
            <a-col :span="12">
              <a-space>
                <a-typography-text class="search-label">时间范围</a-typography-text>
                <!--              <a-range-picker class="search-picker" :time-picker-props="{ defaultValue: ['00:00:00', '09:09:06'] }"-->
                <!--                              format="YYYY-MM-DD" @select="onSelect">-->
                <!--              </a-range-picker>-->

                <component
                    class="search-picker"
                    :is="timeOptions.timeUnit === 0 ?'a-date-picker' : timeOptions.timeUnit === 1 ? 'a-month-picker' : 'a-year-picker'"
                    v-model="timeOptions.timer"
                    :format="timeOptions.timeUnit === 0 ?'YYYY-MM-DD' : timeOptions.timeUnit === 1 ? 'YYYY-MM' : 'YYYY'"></component>
                <a-button type="primary" class="search-button" @click="handleSearch">查询</a-button>
              </a-space>
            </a-col>
            <!-- 右侧年月日返回 -->
            <a-col :span="12" style="text-align: right;">
              <a-space>
                <a-button type="primary" class="time-button" @click="handleTimeType(2)">年</a-button>
                <a-button type="primary" class="time-button" @click="handleTimeType(1)">月</a-button>
                <a-button type="primary" class="time-button" @click="handleTimeType(0)">日</a-button>
                <a-button type="primary" class="search-button" @click="back">
                  <img src="@/assets/bi/two/icon-back.png" alt="">
                  返回
                </a-button>
              </a-space>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
      <div class="dView-content">
        <div class="dView-content-list">
          <chart-wrapper :title="chartParams.cardName">
<!--            <template v-slot:button>-->
<!--              <a-button type="primary" v-for="val in buttonList" :key="val">{{ val }}</a-button>-->
<!--            </template>-->
            <template v-slot:chart>
              <div class="chart-wrapper-content">
                <!--              <chart :option="cardInfoNew.option" autoresize />-->
                <!--              <chart :option="chartData" />-->
                <!--              <chart :options="chartData2" autoresize></chart>-->
                <component :is="EnumChartType[chartParams.chartType]"
                           :configType="0"
                           ref="chartRef"
                           :public="{cardKey: chartParams.cardKey, dashboardConfigId: chartParams.dashboardConfigId}"
                           :totalType="1"></component>
              </div>
            </template>
            <template #extra>
              kwh
            </template>
          </chart-wrapper>
        </div>
<!--        <div class="dView-content-list dView-content-list-multi">-->
<!--          <div class="multi-li" v-for="(val,idx) in 6" :key="idx">-->
<!--            <chart-wrapper :title="'实时日总发电量'">-->
<!--              <template v-slot:button>-->
<!--                <a-button type="primary">下载</a-button>-->
<!--              </template>-->
<!--              <template v-slot:chart>-->
<!--                <div class="chart-wrapper">-->
<!--                  <div class="chart">-->
<!--                    <chart-category :option="idx % 2 === 0 ? cardInfo2.option : cardInfo.option"/>-->
<!--                  </div>-->
<!--                  <div class="description">-->
<!--                    <list-detail :isHasBorder="false"/>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </template>-->
<!--              <template #extra>-->
<!--                kwh-->
<!--              </template>-->
<!--            </chart-wrapper>-->
<!--          </div>-->
<!--        </div>-->
        <div class="dView-content-list dView-content-list-table">
          <chart-wrapper :title="chartParams.cardName">
            <template v-slot:button>
              <a-button type="primary">下载</a-button>
            </template>
            <template #chart>
              <table-list ref="tableRef"></table-list>
            </template>
          </chart-wrapper>
        </div>
      </div>
      <div class="dView-foot">
        <FooterBi></FooterBi>
      </div>
    </div>
  </YkiteFullScreenContainer>
</template>

<script setup lang="ts">
import {ref, onMounted, onBeforeUnmount, watch, reactive} from "vue";
import {useRouter, useRoute} from 'vue-router';
import {handleChartLineBarData} from "@/views/bi/charts";
import useLoading from "@/hooks/loading";
import * as echarts from 'echarts';
// header
import HeaderBi from "@/views/bi/component/headerBi.vue";
// footer
import FooterBi from "@/views/bi/component/footerBi.vue";
// 图表-容器
import ChartWrapper from "@/views/bi/component/chart-wrapper.vue";
// 表格
import TableList from "@/views/bi/component/table-list-scroll.vue";
// 图表-small
import ChartCategory from "@/views/bi/component/chart-category.vue";
// 图例
import ListDetail from "@/views/bi/component/list-detail.vue";
import dayjs from "dayjs";

const chartRef = ref<any>(null);
const tableRef = ref<any>(null);
const route = useRoute()
const router = useRouter();
const {loading, setLoading} = useLoading(false);
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
// 功能按钮
const buttonList = ref(['电量', '功率', '下载']);
// 监听页面是否存在滚动条
const hasScrollbar = ref(false);
const data = [{
  value: 80,
  name: '光伏',
}, {
  value: 62,
  name: '风能',
}, {
  value: 30,
  name: '其他',
}];

// 时间选择
const timeOptions = ref<any>({
  timer: dayjs().format("YYYY-MM-DD"),
  timeUnit: 0,
});

const chartData2 = reactive({
  title: {
    text: '环比',
    // subtext: '日环比',
    x: 'center',
    y: '40%',
    textStyle: {
      fontWeight: 'normal',
      fontSize: 36,
      color: '#FAC61C', // 设置标题文本颜色为黄色
    },
    subtextStyle: {
      color: '#fff', // 设置子标题文本颜色为白色
      fontSize: 14,
    },
  },
  color: ['#11DACA', '#0F8DFF', '#FAC61C',],
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
      radius: ['30%', '40%'],
      center: ['50%', '44%'],
      animation: false,
      data: [
        {
          value: data[2].value,
          itemStyle: {
            // borderRadius: '100%',
            color: '#F3921D',
          },
        },
        {
          value: data[0].value - data[2].value,
          itemStyle: {
            color: 'rgba(0, 134, 255, 0.2)',
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
    {
      name: '风能',
      type: 'pie',
      radius: ['44%', '54%'],
      center: ['50%', '44%'],
      animation: false,
      data: [
        {
          value: data[1].value,
          itemStyle: {
            color: '#0071BC',
            // borderRadius: '100%',
          },
        },
        {
          value: data[0].value - data[1].value,
          itemStyle: {
            color: 'rgba(17, 218, 202, 0.4)',
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
    {
      name: '其他',
      type: 'pie',
      animation: false,
      radius: ['58%', '68%'],
      center: ['50%', '44%'],
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
            color: '#FAC61C',
            // borderRadius: '100%',

          },
        },
        {
          value: data[0].value - data[2].value,
          itemStyle: {
            color: 'rgba(250, 198, 28, 0.4)',
          },
        },
      ],
    },
  ],
});

const chartData = ref({
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(0,0,0,0.6)',
    // formatter:"{b}:{c}"
    formatter: function (params) {
      // let str = params[0].name + ' ' + params[0].data.stationName + '</br>';
      let str = params[0].name + '</br>';
      params.forEach(item => {
        if (item.seriesName === '光伏' || item.seriesName === '回温') {
          str += item.marker + item.seriesName + ' : ' + item.data.value + ' %' + '</br>';
        } else if (item.seriesName === '风能') {
          // 柱状图渐变时设置marker
          item.marker = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#11DACA;"></span>';
          str += item.marker + item.seriesName + ' : ' + item.data.value + ' %';
        }
      });
      return str;
    }
  },
  grid: {
    top: '12%',
    left: '50px',
    bottom: '15%',
    right: '50px'
  },
  xAxis: [{
    type: 'category',
    data: ["1月", '2月', '3月', '4月', '5月', '6月', "7月", '7月', '9月', '10月', '11月', '12月'],
    boundaryGap: true,
    axisLine: {
      lineStyle: {
        color: '#B4B4B4'
      }
    },
    axisTick: {
      show: false
    }
  }],
  yAxis: [{
    // name: '供回温度(℃）',
    // nameLocation: 'middle',
    // nameTextStyle: {
    //     padding: [3, 4, 50, 6]
    // },
    splitLine: {
      show: true,
      lineStyle: {
        type: 'solid',
        color: '#eee'
      }
    },
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      textStyle: {
        color: '#fff'
      },
      formatter: '{value} '
    }
  },
    {
      // name: '压力值(Mpa)',
      nameLocation: 'middle',
      nameTextStyle: {
        padding: [50, 4, 5, 6]
      },
      splitLine: {
        show: false
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
    }
  ],
  series: [{
    name: '光伏',
    type: 'line',
    smooth: false,
    showSymbol: true,
    symbol: 'circle',
    symbolSize: 12,
    yAxisIndex: 0,
    areaStyle: {
      normal: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
          offset: 0,
          color: 'rgba(250,180,101,0.3)'
        },
          {
            offset: 1,
            color: 'rgba(250,180,101,0)'
          }
        ],),
        shadowColor: 'rgba(250,180,101,0.2)',
        shadowBlur: 20
      }
    },
    itemStyle: {
      normal: {
        color: '#FAC61C'
      }
    },
    // data中可以使用对象，value代表相应的值，另外可加入自定义的属性
    data: [{
      value: 80,
      stationName: "1月"
    }, {
      value: 88,
      stationName: "2月"
    }, {
      value: 92,
      stationName: "3月"
    }, {
      value: 46,
      stationName: "4月"
    }, {
      value: 30,
      stationName: "5月"
    }, {
      value: 60,
      stationName: "6月"
    }, {
      value: 46,
      stationName: "7月"
    }, {
      value: 40,
      stationName: "8月"
    }, {
      value: 60,
      stationName: "9月"
    }, {
      value: 46,
      stationName: "10月"
    }, {
      value: 30,
      stationName: "11月"
    }, {
      value: 46,
      stationName: "12月"
    }]
  },
    {
      name: '风能',
      type: 'bar',
      barWidth: 20,
      yAxisIndex: 1,
      itemStyle: {
        normal: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgba(17, 218, 202)' // 0% 处的颜色
          }, {
            offset: 1,
            color: 'rgba(17, 218, 202, 0)' // 100% 处的颜色
          }], false),
          barBorderRadius: [20, 20, 0, 0]
        }
      },
      data: [{
        value: 11,
        stationName: "1月"
      }, {
        value: 34,
        stationName: "2月"
      }, {
        value: 54,
        stationName: "3月"
      }, {
        value: 39,
        stationName: "4月"
      }, {
        value: 63,
        stationName: "5月"
      }, {
        value: 24,
        stationName: "6月"
      }, {
        value: 39,
        stationName: "7月"
      }, {
        value: 63,
        stationName: "8月"
      }, {
        value: 24,
        stationName: "9月"
      }, {
        value: 39,
        stationName: "10月"
      }, {
        value: 63,
        stationName: "11月"
      }, {
        value: 24,
        stationName: "12月"
      }]
    }
  ]
});

const checkScrollbar = () => {
  const body = document.body;
  const html = document.documentElement;
  const hasScroll = html.clientHeight < html.scrollHeight || body.clientHeight < body.scrollHeight;
  hasScrollbar.value = hasScroll;
};

const chartParams = ref<any>({});

const lineBarSetting = {
  offsetStartColor: '#F3921D',
  offsetEndColor: '#f3921d00',
  showBackground: false
};

const lineBarSetting2 = {
  offsetStartColor: '#0e88f6',
  offsetEndColor: '#0e88f600',
  showBackground: false
};
const cardInfo = {
  option: handleChartLineBarData(lineBarSetting),
};
const cardInfo2 = {
  option: handleChartLineBarData(lineBarSetting2),
};

// 返回
const back = () => {
  router.back()
};

// 时间选择器
const onSelect = (dateString, date) => {
  console.log('onSelect', dateString, date);
  timeOptions.value = dateString;
};


// 时间搜索
const handleSearch = () => {
  chartRef?.value.handleTime(timeOptions.value);
  tableRef?.value.handleTime(timeOptions.value);
};

// 年月日
const handleTimeType = (unit: number) => {
  console.log(unit)
  timeOptions.value.timeUnit = unit;
  timeOptions.value.timer = dayjs(timeOptions.value.timer).format(unit == 0 ? 'YYYY-MM-DD' : unit == 1 ? 'YYYY-MM' : "YYYY");
  chartRef?.value.handleTime(timeOptions.value);

  tableRef?.value.handleTime(timeOptions.value);
}


onMounted(() => {
  checkScrollbar();
  chartParams.value = route.query;
  window.addEventListener('resize', checkScrollbar);
  console.log(chartRef.value)
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkScrollbar);
});


</script>

<style lang="less" scoped>
@font-size: 16px;
@font-size-mini: 14px;
@font-size-medium: 18px;
@font-size-large: 24px;
@color-w: white;
@font-family: Source Han Sans;

.grid-title-num {
  color: #F3921D;
}

.data-view {
  width: 100vw;
  height: 100vh;
  background: url("@/assets/bi/backgroundOld.png") no-repeat;
  background-size: 100% 100%;
  color: @color-w;
  font-family: Source Han Sans;
  display: flex;
  flex-direction: column;
}

.dView-head {
  height: 110px;
  background: url("@/assets/bi/navbar.png") no-repeat bottom;
  background-size: 100% 100%;
  font-size: 28px;

  div {
    line-height: 50px;
  }
}

//时间范围
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
    //width: 328px;
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

.dView-content {
  flex: 1;
  scrollbar-width: none;
  -ms-overflow-style: none;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 0 32px;

  .dView-content-list {
    height: 360px;
    margin-bottom: 16px;

    .chart-wrapper-content {
      width: 100%;
      height: 100%;
    }
  }

  .dView-content::-webkit-scrollbar {
    display: none;
  }

  .dView-content-list-multi {
    height: auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;

    .multi-li {
      height: 22vh;
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

    .chart {
      width: 70%;
      height: 100%;
    }

    .description {
      width: 30%;
      height: 100%;
    }
  }
}

.dView-foot {
  height: 50px;
}
</style>
