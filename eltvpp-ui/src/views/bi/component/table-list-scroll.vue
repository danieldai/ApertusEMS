<template>
  <div class="table-list">
<!--    <a-table ref="arcoTableRef" :scroll="{y: 44+'vh'}" :data="dataSource" :loading="loading" :columns="columns"-->
<!--      :bordered="false" :pagination="false" />-->
<!--    <div class="back-btn" v-show="isShowBtn" @click="backTop">-->
<!--      <icon-arrow-rise />-->
<!--    </div>-->
    <!-- <div class="table-pagination">
      <a-pagination :total="dataSource.length" show-total show-jumper></a-pagination><span>页</span>
    </div> -->

    <a-table :data="tableInfo?.tableData"
             :pagination="false"
             :loading="loading"
             :scroll="{y: 44+'vh'}"
             :bordered="false">
      <template #columns>
        <a-table-column
            v-for="(item,index) in tableInfo?.header"
            :key="index"
            align="center"
            :title="item.title"
            :data-index="item.dataIndex"
            ellipsis
            tooltip
        >
        </a-table-column>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, watch, nextTick, useEffect } from 'vue';
  import {getTimeObject} from "@/utils/charts";
  import {getChartInfo} from "@/api/dashboard/api";
  import {useRoute, useRouter} from "vue-router";
  import useLoading from "@/hooks/loading";

  defineProps({
    isShowPagination: {
      type: Boolean,
      default: true,
    },
  });


  const route = useRoute()
  const router = useRouter();
  const isFlag = ref(1); // 防抖
  const isShowBtn = ref(false); //返回按钮显示状态
  const dataSource = ref([]); // 表格数据
  const arcoTableRef = ref(null); // 表格实例
  const timeOptions = ref<any>({});
  const chartParams = ref<any>({});
  const {loading, setLoading} = useLoading(false);

  // 定义表格数据
  const initialData = {
    id: 1,
    time: '2024/04/06 13:31:21',
    active_power: '223kw',
    now_power: '223kw',
    reactive_power: '223kw',
    power_factor: '0.95',
    current: '1250A',
    voltage: '1250A',
    frequency: '1250A',
    use: '83%',
  };


  for (let i = 1; i <= 20; i++) {
    dataSource.value.push({
      ...initialData,
      id: i,
    });
  }

  const loadData = () => {
    setTimeout(() => {
      // loading.value = false;
      isFlag.value = 0;
      for (let i = 1; i <= 10; i++) {
        dataSource.value.push({
          ...initialData,
          id: i,
        });
      }
    }, 2000)
  };

  const tableInfo = ref<any>({
    header: [],
    tableData: []
  });

  // 滚动事件
  const handleScroll = (event: Event) => {
    // 在这里处理滚动事件
    const { target } = event;
    const { scrollTop, scrollHeight, clientHeight } = target as HTMLDivElement;
    console.log(scrollTop)
    if (scrollHeight - scrollTop - clientHeight < 10) {
      isFlag.value++;
      if (isFlag.value == 2) {
        loading.value = true;
        loadData()
      }
    }
    if (scrollTop > 150) {
      isShowBtn.value = true;
    } else {
      isShowBtn.value = false;
    }
  };

  // 返回顶部
  const backTop = () => {
    const bodyElement = arcoTableRef.value?.$el.querySelector('.arco-table-body');
    if (bodyElement) {
      bodyElement.scrollTop = ({ top: 0, behavior: 'smooth' });
    }
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
        isTable:1,
        ...getTimeObject(timeOptions.value),
      };

      const res = await getChartInfo(params);

      if (res?.code == 200) {
        tableInfo.value = {...res.data.tableInfo};

        console.log(tableInfo.value, 'tableInfo')
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

  onMounted(async () => {
    if (route.query) {
      chartParams.value = route.query;
    }
    await fetchData();
    // const bodyElement = arcoTableRef.value?.$el.querySelector('.arco-table-body');
    // if (bodyElement) {
    //   bodyElement.addEventListener('scroll', handleScroll);
    // }
  });


  defineExpose({
    handleTime: (val)=>{
      timeBarChange(val);
    },
  });
</script>
<style lang="less" scoped>
  .table-list {
    background: rgba(52, 144, 227, 0.1);
    margin-top: 16px;
    width: 100%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .back-btn {
    position: absolute;
    right: 20px;
    bottom: 20px;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background-color: #fff;
    text-align: center;
    line-height: 30px;
    z-index: 11;
  }

  /* table样式 */
  :deep(.arco-table-body) {
    background-color: transparent !important;
    scroll-behavior: smooth;
  }

  :deep(.arco-table-header) {
    background-color: transparent !important;
  }


  :deep(.arco-table-element) {
    .arco-table-td {
      background-color: transparent;
      color: #fff;
      border: none;
    }

    .arco-table-th {
      background: rgba(52, 144, 227, 0.1);
      color: #fff;
      height: 50px;
    }

    tbody tr {
      height: 50px;

      &:nth-child(even) {
        background: rgba(52, 144, 227, 0.05);
      }
    }

    .arco-table-tr:hover {
      list-style: none;

      .arco-table-td {
        background-color: rgba(52, 144, 227, 0.05) !important;
      }
    }
  }

  /* .arco-table-hover:not(.arco-table-dragging) .arco-table-tr:not(.arco-table-tr-empty):not(.arco-table-tr-summary):hover .arco-table-td:not(.arco-table-col-fixed-left):not(.arco-table-col-fixed-right),
    .arco-table-hover .arco-table-tr-d rag .arco-table-td:not(.arco-table-col-fixed-left):not(.arco-table-col-fixed-right) {
      background-color: transparent !important;
    } */


  /* 分页器样式 */
  .table-pagination {
    padding-bottom: 10px;
    box-sizing: border-box;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-right: 24px;
    box-sizing: border-box;

    :deep(.arco-pagination) {
      justify-content: flex-end;
      padding-right: 16px;
      box-sizing: border-box;

      .arco-pagination-total,
      .arco-pagination-jumper-text-goto,
      .arco-pagination-jumper-prepend,
      .arco-pagination-jumper-append {
        color: #fff;
      }

      .arco-pagination-item-active {
        background-color: transparent;
        color: #409EFF !important;
      }

      .arco-pagination-item:hover {
        background: rgba(52, 144, 227, 0.05);
      }

      .arco-pagination-list {

        span,
        li {
          color: #fff;
        }
      }

      .arco-pagination-jumper .arco-pagination-jumper-input {
        width: 56px;
        height: 22px;
        border-radius: 2px;
        padding: 0px 8px;
        gap: 10px;
        background: rgba(15, 141, 255, 0.1);
        background-color: transparent;
        box-sizing: border-box;
        border: 1px solid rgba(14, 136, 246, 0.5);
        color: #fff;
      }
    }

    span {
      color: #fff;
    }
  }
</style>
