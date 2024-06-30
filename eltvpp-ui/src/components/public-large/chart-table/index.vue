<!--
 * 功能：表格
 * 作者：闫李壮
 * 日期：2024-06-04
-->
<template>
  <div>
    <a-table :data="data.tableInfo.tableData"
             :pagination="false"
             :scroll="{y: 100}"
             :bordered="false">
      <template #columns>
        <a-table-column
            v-for="item in data.tableInfo.header" :key="item.id"
            align="center"
            :title="item.title"
            :data-index="item.dataIndex"
            :width="item.dataIndex === '时间' ? 155 : 0"
            ellipsis
            tooltip
        >
          <template #cell="{ record }" v-if="item.dataIndex === 'status'">
            {{ record.status }}
          </template>
        </a-table-column>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import {useRouter} from 'vue-router'
import {onMounted, ref} from 'vue';
//加载中
const router = useRouter();
import useLoading from '@/hooks/loading';
import {getChartInfo} from '@/api/dashboard/api';
import {getTimeObject} from "@/utils/charts";

const props = defineProps({
  public: {
    type: Object,
    default: () => {
      return {}
    }
  },
  configType: {
    type: Number,
    default: 1,
  },
  // 设备编号, -------------------------------------------没有timeBar的组件一定要传
  deviceSn: {
    type: String,
    default: ''
  }
})
const {loading, setLoading} = useLoading(false);

const data = ref<any>({
  tableInfo: {
    header: [],
    tableData: []
  }
});
const timeOptions = ref<any>({});
// 查看更多跳转
const lookMore = () => {
  router.push({
    path: "/power/alarm/list",
    // query: {
    //   ...props,
    //   bizId: id,
    //   redirect: router.currentRoute.value.fullPath
    // }
  });
};


const timeBarChange = async (timer:any) => {
  timeOptions.value = timer;
  await fetchData();
}

const fetchData = async () => {
  try {
    setLoading(true);
    const params = {
      configId: props.public.dashboardConfigId,
      cardKey: props.public.cardKey,
      deviceSn: props.public.deviceSn,
      ...getTimeObject(timeOptions.value)
    };
    const res = await getChartInfo(params);
    if (res.code === 200 && res.data) {
      data.value = {
        cardName: res.data.cardName,
        tableInfo: res.data.tableInfo
      };
    } else {
      data.value = {
        tableInfo: {
          header: [],
          tableData: []
        }
      }
    }
  } catch (error) {
    console.error('获取图表数据时出错:', error);
  } finally {
    setLoading(false);
  }
}
onMounted(() => {
  fetchData()
})
defineExpose({
  timeBarChange
})
</script>
<style lang="less" scoped>
@border-color: var(--color-border-2);
.arco-card {
  width: 100%;
  height: 100%;
  /* 调整卡片组件高度 */

  :deep(.arco-card-body) {
    height: calc(100% - 46px);
    padding: 0;
    overflow: hidden;
  }
}

.transition-view {
  width: 100%;
  height: 100%;
}

.warnings-list-row {
  width: 100%;
  transition: all 0.5s ease-out;
}

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
