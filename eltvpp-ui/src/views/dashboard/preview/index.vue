<!--
 * 功能：通用配置页面
 * 作者：闫李壮
 * 日期：2024-06-05
-->
<template>
  <div style="margin-top: -16px">
    <a-spin :loading="loading" style="width: 100%">
      <!-- 布局 -->
      <template v-if="!isConfig">
        <!-- 搜索框 -->
        <search-form v-if="configType === 2" :stationType="stationType" @search="searchConfig"/>
        <!-- 拖拽组件 -->
        <GridLayout :style="{margin:'0 -10px'}"
                    v-model:layout="layout.pageConfig"
                    :row-height="30"
                    is-draggable
                    is-resizable
                    is-bounded>
          <template #item="{ item }">
            <component
                v-if="configType === 1 || (configType === 2 && searchForm.deviceSn)"
                :is="item.staticType === 1 ? 'card-chart-pie-statistics' : item.staticType === 2 ? 'card-chart-combine' : EnumChartType[item.chartType]"
                :ref="(el:any) => setComponentRef(el, item.i)"
                :configType="configType"
                :deviceSn="searchForm.deviceSn"
                :public="{
                  ...item.public,
                  chartType:item.chartType,
                  dashboardConfigId: layout.id
                }"
            >
            </component>
            <!-- 编辑or删除按钮 -->
            <div class="handle-row" v-if="!item.static">
              <a-button type="primary" size="mini" @click="handleEditItem(item)">编辑</a-button>
              <a-popconfirm :content="`确定要删除吗？`" type="error"
                            @ok="handleRemoveItem(item.i)">
                <a-button type="primary" status="danger" size="mini">删除</a-button>
              </a-popconfirm>
            </div>
          </template>
        </GridLayout>
        <!-- 底部 -->
        <a-button v-if="!isSetting" type="primary" @click="handleShowSetting">配置</a-button>
        <a-space v-else>
          <a-button type="primary" @click="getLayout">还原上次</a-button>
          <a-button type="primary" @click="handleAddItem">新建模块</a-button>
          <a-button type="primary" status="success" @click="handleSave">保存</a-button>
          <a-button type="primary" status="danger" @click="handleCancel">取消</a-button>
        </a-space>
      </template>
      <!-- 编辑 -->
      <edit-card v-else :cardEditParams="cardEditParams" :deviceSn="searchForm.deviceSn" @handleEditCancel="handleEditCancel"/>
      <!-- 新建模板弹窗 -->
      <choose-template :visible="templateModalShow"
                       @select="handleModalSelect"
                       @cancel="handleModalClose"></choose-template>
    </a-spin>
  </div>
</template>
<script setup lang="ts">
import {GridLayout} from 'grid-layout-plus'
import {nextTick, onMounted, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import EditCard from "@/views/dashboard/preview/edit-card.vue";
import ChooseTemplate from "@/views/dashboard/preview/component/choose-template.vue";
import {calculateNewPosition, EnumChartType, getPageKey, getPageName} from '@/views/dashboard/preview/index';
import {getCardConfig, updateDashboardConfig} from "@/api/dashboard/api";
import {notification} from "@/hooks/my-design";
import useLoading from "@/hooks/loading";
import {isArray} from "@/utils/is";
import SearchForm from "@/views/dashboard/preview/component/searchForm.vue";

const {loading, setLoading} = useLoading(false)

const props = defineProps({
  // 1 用电，2 光伏
  stationType: {
    type: Number,
    default: 1
  },
  // configType 1: 指定 2: 通用
  configType:{
    type: Number,
    default: 1
  }
})
const searchForm = ref<any>({
  deviceSn: null
})
// 是否显示模板弹窗
const templateModalShow = ref(false);
// 是否开启 配置模式
const isConfig = ref(false);
//路由
const router = useRouter();
const route = useRoute();
// 页面key
const pageKey = getPageKey(router);
const pageName = getPageName(route);

// 是否开启配置
const isSetting = ref(false);
// grid布局
const layout = ref<any>({
  id: 0,
  pageName: pageName,
  pageKey: pageKey,
  pageType: 2,
  pageConfig: []
});
// 传给编辑组件的数据
const cardEditParams = ref<any>();
const componentRef = ref<any>({})

const setComponentRef = (el: any, i:number) => {
  componentRef.value[i] = el
}
/**
 * 获取布局
 */
const getLayout = async () => {
  try {
    const res = await getCardConfig(pageKey);
    if (res.code === 200 && res.data) {
      layout.value = {
        ...res.data,
        pageConfig: JSON.parse(res.data.pageConfig)
      }
      console.log("pageConfig=>",layout.value.pageConfig)
    }
    // 如果是第一次进入页面 调用一下保存接口（生成id）
    if (!res.data) {
      layout.value = {
        pageName,
        pageKey,
        pageType: 2,
        pageConfig: '[]'
      }
      await updateDashboardConfig(layout.value);
      await getLayout();
    }
  } catch (e) {
    console.log('err', e);
  }
};
/**
 * 查询
 */
const searchConfig = (params: any) => {
  searchForm.value.deviceSn = null;

  nextTick(() => {
    searchForm.value.deviceSn = params.deviceSn;
    setTimeout(() => {
      for (const key in componentRef.value) {
        const currentInstance = componentRef.value[key];
        if (currentInstance.timeBarChange) {
          currentInstance.timeBarChange(params);
        }
      }
    })
  });
}
/**
 * 编辑
 */
const handleEditItem = (item: any) => {
  cardEditParams.value = {
    configType: props.configType,
    chartType: item.chartType,
    cardKey: item.i,
    headType: item.headType,
    staticType: item.staticType,
    public: item.public,
    dashboardConfigId: layout.value.id
  };
  isConfig.value = true;
};
/**
 * 删除
 */
const handleRemoveItem = (id: string) => {
  const index = layout.value.pageConfig.findIndex((item: any) => item.i === id);

  if (index > -1) {
    layout.value.pageConfig.splice(index, 1);
  }
};
/**
 * 配置此页面
 */
const handleShowSetting = async () => {
  await getLayout()
  changeLayoutDisable(false);
  isSetting.value = !isSetting.value;
};
/**
 * 新建
 */
const handleAddItem = () => {
  templateModalShow.value = true;
};
/**
 * 保存
 */
const handleSave = async () => {
  try {
    setLoading(true)
    const data = {
      ...layout.value,
      pageConfig: JSON.stringify(layout.value.pageConfig)
    }
    const res: any = await updateDashboardConfig(data);
    notification(res);
    if (res.code === 200) {
      await getLayout()
      isSetting.value = false;
      changeLayoutDisable(true);
    }
  } catch (e) {

  } finally {
    setLoading(false);
  }
}
/**
 * 取消编辑
 */
const handleCancel = async () => {
  await getLayout();
  changeLayoutDisable(true);
  isSetting.value = !isSetting.value;
};
/**
 * 选择模板
 * @param item pageConfig的每一项
 */
const handleModalSelect = (item: any) => {
  templateModalShow.value = false;
  const {x, y, i} = calculateNewPosition(layout.value.pageConfig, item.layout);
  const configItem = {
    x,
    y,
    i,
    w: item.layout.w,
    h: item.layout.h,
    chartType: item.chartType,
    headType: item.headType,
    staticType: item.staticType,
    public: {
      pageKey: pageKey,
      cardKey: i,
      cardName: ''
    }
  };
  layout.value.pageConfig.push(configItem);
};
/**
 * 关闭弹窗
 */
const handleModalClose = () => {
  templateModalShow.value = false;
};

/**
 * 子组件 是否可编辑
 * @param status
 */
const changeLayoutDisable = (status: boolean) => {
  if (!isArray(layout.value.pageConfig) && !layout.value.pageConfig.length) return
  layout.value.pageConfig.forEach((item: any) => {
    item.static = status;
    if(item.fixed){
      item.static = true;
    }
  });
}

/**
 * 关闭编辑页组件
 */
const handleEditCancel = () => {
  isConfig.value = false;
  if (props.configType === 2) {
    searchForm.value.deviceSn = null;
  }
}

onMounted(async () => {
  await getLayout();
  changeLayoutDisable(true);
});
</script>
<style lang="less" scoped>
.time-bar-card {
  width: 100%;
}

.handle-row {
  position: absolute;
  bottom: 5px;
  right: 10px;

  .arco-btn-status-danger {
    margin-left: 10px;
  }
}
</style>
