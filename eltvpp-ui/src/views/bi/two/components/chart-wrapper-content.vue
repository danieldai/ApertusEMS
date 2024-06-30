<template>
  <a-row class="chart-wrapper" display="flex" justify="space-around">
    <!-- 左侧 -->
    <a-col :span="18" class="chart-wrapper-col">
      <CutomChart :options="chartData" autoresize/>
    </a-col>
    <!-- 右侧 -->
    <a-col :span="6" class="chart-wrapper-col">
      <chart :options="chartData2" autoresize></chart>
    </a-col>
  </a-row>
</template>
<script setup lang="ts">
  import { defineComponent, reactive, onMounted, onUnmounted, ref } from 'vue';
  import { computed, nextTick, provide } from 'vue';
  // import echarts from 'echarts'
  import * as echarts from 'echarts';
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
  const chartData2 = reactive({
    title: {
      text: '22222',
      subtext: '日环比',
      x: 'center',
      y: '40%',
      textStyle: {
        fontWeight: 'normal',
        fontSize: 24,
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
            value: data[0].value,
            itemStyle: {
              borderRadius: '100%',
              color: '#0F8DFF',
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
              color: '#11DACA',
              borderRadius: '100%',
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
              borderRadius: '100%',

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
  })


</script>
<style lang="less" scoped>
  .chart-wrapper {
    width: 100%;
    height: 100%;

    .chart-wrapper-col {
      height: 100%;
    }
  }
</style>