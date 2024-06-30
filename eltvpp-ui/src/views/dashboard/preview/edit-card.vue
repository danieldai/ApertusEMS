<!--
* 功能：卡片编辑
* 作者：罗甜甜
* 日期：2024-6-11
-->
<template>
  <div class="edit-card-wapper">
    <a-spin class="edit-content" :loading="loading">
      <a-row div>
        <!--预览-->
        <a-col class="card-preview">
          <component class="preview-card-wapper"
                     :is="cardEditParams.staticType === 1 ? 'card-chart-pie-statistics' : cardEditParams.staticType === 2 ? 'card-chart-combine' : EnumChartType[cardEditParams.chartType]"
                     :public="cardEditParams"
                     :deviceSn="deviceSn"
                     style="margin-top: 0">
          </component>
        </a-col>
        <!--基础设置-->
        <a-col>
          <base-setting :defaultVal="defaultVal" @handleDateDim="handleDateDim" ref="baseRef"/>
        </a-col>
        <!--数据区-->
        <a-col>
          <data-area-sankey :defaultVal="defaultVal" v-if="[10].includes(defaultVal.chartType)" ref="sankeyRef"/>
          <data-area v-if="[1, 14, 13, 4, 7, 12, 6, 8, 9, 2,3,5].includes(defaultVal.chartType)"
                     :defaultVal="defaultVal" ref="dataAreaRef"/>
        </a-col>
        <!--辅助设置-->
        <a-col v-if="[1,2,3,5,7].includes(defaultVal.chartType)">
          <auxiliary :defaultVal="defaultVal" ref="auxiliaryRef"/>
        </a-col>
      </a-row>
    </a-spin>
    <a-space class="form-footer" size="large">
      <a-button type="primary" @click="handleSave">保存</a-button>
      <a-button type="primary" status="warning" @click="handleInit">重置</a-button>
      <a-button @click="closeModal" type="primary" status="danger">取消</a-button>
    </a-space>
  </div>

</template>

<script setup lang="ts">
import {onMounted, ref, watchEffect} from "vue";
import BaseSetting from "@/views/dashboard/preview/component/base-setting.vue";
import DataArea from "@/views/dashboard/preview/component/data-area.vue";
import Auxiliary from "@/views/dashboard/preview/component/auxiliary.vue";
import DataAreaSankey from "@/views/dashboard/preview/component/data-area-sankey.vue";
import {updateConfigCard, getConfigCard} from "@/api/dashboard/api";
import {EnumChartType, DefaultValueData} from '@/views/dashboard/preview/index'
import {Notification} from "@arco-design/web-vue";
import useLoading from '@/hooks/loading';
import {useRoute} from 'vue-router';

const props = defineProps({
  chartType: {
    type: Number,
    default: 10
  },
  cardEditParams: {
    type: Object,
    default: () => {
      return {
        chartType: 13,
      }
    }
  },
  deviceSn: {
    type: String,
    default: ''
  }
});
const emit = defineEmits(['handleEditCancel']);
const route = useRoute();

const {loading, setLoading} = useLoading(true);
const cardForm = ref<any>({});
// 默认数据
const defaultVal = ref<any>({});
// 获取子组件form
const baseRef = ref<any>();
const auxiliaryRef = ref<any>();
const dataAreaRef = ref<any>();
const sankeyRef = ref<any>();

// 取消
const closeModal = () => {
  emit("handleEditCancel");
};

// 保存
const handleSave = async () => {
  const valid = await baseRef?.value.validateForm();
  if(valid.valid){
    return;
  }
  setLoading(true);
  // 基本信息
  const base = baseRef.value?.form;
  // 辅助信息
  const auxiliary = auxiliaryRef.value?.form;
  // 数据区-基本
  const dataArea = dataAreaRef.value?.form;
  // 数据区-桑基图
  const sankey = sankeyRef.value?.form;

  const {chartType, cardKey, dashboardConfigId} = props.cardEditParams;
  // return
  const params = {
    // ...props.cardEditParams, //cardKey、chartType、id
    chartType,
    cardKey,
    dashboardConfigId,
    ...base, //卡片名称、时间维度、是否全日期
    indicator: defaultVal.value.indicator, //指标度量
    cardConfig: {
      // headType: base.headType,
      ...defaultVal.value, //默认数据
      ...auxiliary, //辅助信息
      ...dataArea,//数据区
      ...sankey, //数据区
    }
  };

  if([15,16].includes(chartType)){
    params.cardConfig.staticType = base.staticType;
  }

  params.isFullDate = params.isFullDate ? 1 : 0;
  // 修改参数格式
  params.cardConfig.varList?.map((val: any) => {
    // 状态量 切换tab清空表单
    if (val.equipType == 1 && defaultVal.value.chartType == 13) {
      val.varSn = "";
      val.varName = "";
      val.legend = "";
      val.unit = "";
    } else if (val.equipType == 2 && defaultVal.value.chartType == 13) {
      val.stateType = undefined;
    }
  })
  deleteItem(['public', 'isDetail'], params);

  // 分段图单独处理
  if (props.cardEditParams.chartType == 11) {
    deleteItem(['electricityPriceType', 'schemeId', 'staticType'], params);
    params.cardConfig.electricityPriceType = base.electricityPriceType;
    params.cardConfig.schemeId = base.schemeId;
    params.cardConfig.staticType = base.staticType;
  }

  params.cardConfig = JSON.stringify(params.cardConfig);
  // console.log('传参params=', params)

  // return
  try {
    const res = await updateConfigCard(params);
    if (res.code === 200) {
      Notification.success({
        title: '提示',
        content: '保存成功',
      });
      emit("handleEditCancel");
    } else {
      Notification.error({
        title: '提示',
        content: res.msg,
      });
    }
  } catch (e) {
    console.error('err', e);
  } finally {
    setLoading(false);
  }
};

// 去除多余的key值
const deleteItem = (data: any, params: any) => {
  data.map((item: any) => {
    delete params[item];
  })
};

// 重置
const handleInit = () => {
  getEditData();
};

const resetFormData = () => {
  baseRef.value?.setForm({});
  // 辅助信息
  auxiliaryRef.value?.setForm({});
  // 数据区-基本
  dataAreaRef.value?.setForm({varList: []});
  // 数据区-桑基图
  sankeyRef.value?.setForm({varList: []});
  cardForm.value = {};
};

// 获取编辑数据
const getEditData = async () => {
  setLoading(true);
  resetFormData();
  try {
    const res = await getConfigCard(props.cardEditParams);
    // console.log(res, '22222222')

    if (res.code === 200 && res.data && res.data.id > 0) {
      res.data.cardConfig = JSON.parse(res.data.cardConfig);

      // 默认数据
      defaultVal.value.dateDim = res.data.dateDim;
      defaultVal.value.secondDateDim = res.data.secondDateDim;
      defaultVal.value.isFullDate = res.data.isFullDate;
      defaultVal.value.isDetail = true;

      // 基本信息
      baseRef.value?.setForm({
        id: res.data.id,
        cardName: res.data.cardName,
        dateDim: res.data.dateDim,
        secondDateDim: res.data.secondDateDim,
        isFullDate: res.data.isFullDate,
        electricityPriceType: res.data.cardConfig.electricityPriceType,
        schemeId: res.data.cardConfig.schemeId,
        staticType: res.data.cardConfig.staticType,
      });
      // 辅助信息
      const {legendPosition, lineChildren, isShowLine} = res.data.cardConfig;
      auxiliaryRef.value?.setForm({
        legendPosition,
        lineChildren,
        isShowLine
      });
      // 数据区-基本
      dataAreaRef.value?.setForm({varList: res.data.cardConfig.varList});

      // 数据区-桑基图
      sankeyRef.value?.setForm({varList: res.data.cardConfig.varList ? res.data.cardConfig.varList : []});

      cardForm.value = res.data;

    }
  } catch (e) {
    console.error('err', e);
  } finally {
    setLoading(false);
  }
}

// 基础区域时间维度change
const handleDateDim = (val: number) => {
  defaultVal.value['dateDim'] = val;
  defaultVal.value['isDetail'] = false;
}

onMounted(() => {
  // 概览页面进来 indicator指定变量 1, 通用变量2, 当前版全是指定！！！
  defaultVal.value = {
    chartType: props.cardEditParams.chartType,
    idDetail: false,
    headType: props.cardEditParams.headType,
    staticType: props.cardEditParams.staticType,
    dateDim: 70,
    ...DefaultValueData[props.cardEditParams.chartType]
  };

  if (!defaultVal.value.hasOwnProperty('indicator')) {
    defaultVal.value.indicator = props.cardEditParams.configType;
  }

  getEditData();
  // console.log('我是onMounted',props.cardEditParams.configType)
});

</script>

<style lang="less" scoped>
.card-preview {
  height: 350px;
}

.edit-content {
  width: 100%;
  padding-bottom: 40px;
}

.form-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60px;
  padding: 14px 20px 14px 0;
  background: var(--color-bg-2);
  box-shadow: 0 10px 30px 0 rgb(0 0 0 / 40%);
  display: flex;
  justify-content: center;
}

.arco-card {
  margin-bottom: 16px !important;
  margin-top: 16px;
}

</style>
