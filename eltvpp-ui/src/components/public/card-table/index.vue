<!--
 * 功能：表格
 * 作者：闫李壮
 * 日期：2024-06-04
-->
<template>
  <a-card :title="data.cardName">
    <!-- 额外操作栏，只有当configType为1时显示 -->
    <template #extra v-if="configType === 1">
      <time-bar @change="timeBarChange" isCurrentDateDisabled />
    </template>
    <a-table :data="data.tableInfo.tableData"
             :pagination="false"
             :scroll="{y: '100%'}"
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
  </a-card>
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

/* 过渡效果 */
.fade-move,
.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}

/* 进入和离开的状态 */
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scaleY(0.01) translate(30px, 0);
}

/* 离开的项目被移除出了布局流 */
.fade-leave-active {
  position: absolute;
}
</style>
