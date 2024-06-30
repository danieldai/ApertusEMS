<template>
  <!--  <a-card :title="`数据区1`" v-for="(item,index) in form.varList" :key="index">-->
  <!--    <a-form ref="formDataRef" :model="form" auto-label-width>-->
  <!--      <a-form-item field="varName" label="选择变量" class="input-width">-->
  <!--        <a-input-search search-button v-model="item.varName" readonly>-->
  <!--          <template #button-default>-->
  <!--            <div @click="chooseVar(index)">选择变量</div>-->
  <!--          </template>-->
  <!--        </a-input-search>-->
  <!--      </a-form-item>-->

  <!--      &lt;!&ndash;变量编码&ndash;&gt;-->
  <!--      <a-form-item field="varSn" label="变量编码">-->
  <!--        <a-input v-model="item.varSn" disabled/>-->
  <!--      </a-form-item>-->

  <!--      &lt;!&ndash;图例名称&ndash;&gt;-->
  <!--      <a-form-item field="legend" label="图例名称">-->
  <!--        <a-input v-model="item.legend"/>-->
  <!--      </a-form-item>-->

  <!--      &lt;!&ndash;指标单位&ndash;&gt;-->
  <!--      <a-form-item field="unit" label="指标单位">-->
  <!--        <a-input v-model="item.unit"/>-->
  <!--      </a-form-item>-->

  <!--      <a-form-item field="storageType" label="存储类型">-->
  <!--        <a-radio-group v-model="item.storageType">-->
  <!--          <a-radio :value="1">变化值</a-radio>-->
  <!--          <a-radio :value="2">累计值</a-radio>-->
  <!--        </a-radio-group>-->
  <!--      </a-form-item>-->

  <!--      &lt;!&ndash;数值类型&ndash;&gt;-->
  <!--      <a-form-item field="changeType" label="数值类型" v-if="item.storageType==1">-->
  <!--        <a-radio-group v-model="item.changeType">-->
  <!--          <a-radio :value="1">实时/平均值</a-radio>-->
  <!--          <a-radio :value="2">最小值</a-radio>-->
  <!--          <a-radio :value="3">最大值</a-radio>-->
  <!--        </a-radio-group>-->
  <!--      </a-form-item>-->

  <!--      <a-form-item label="子数据区">-->
  <!--        <a-space style="width: 100%" direction="vertical" fill :size="16" >-->
  <!--          <a-button type="primary" @click="handleAddData(item,index)">-->
  <!--            <template #icon>-->
  <!--              <icon-plus />-->
  <!--            </template>-->
  <!--            添加子数据区-->
  <!--          </a-button>-->
  <!--          <node-sankey v-if="item.children"  :list="item.children" :level="1" @handleAddChildList="handleAddChildList"  />-->
  <!--        </a-space>-->
  <!--      </a-form-item>-->
  <!--    </a-form>-->
  <!--  </a-card>-->
  <!--弹窗-->
  <select-var-modal :visible="selectVarVisible" @cancel="handleModalClose" @add="handleDeviceVarSelect"/>

  <a-card>
    <a-button type="primary" @click="handleAddChildList" v-if="form.varList.length<=0">添加变量</a-button>
    <a-form :model="form" ref="formRef" auto-label-width>
      <a-table ref="tableConfigRef" :indent-size="32" row-key="varSn" :columns="columns" v-show="form.varList.length>0"
               :data="form.varList" :pagination="false"
               :bordered="{ wrapper: true, cell: true }" v-bind:hide-expand-button-on-empty="true">
        <!-- 变量名称 -->
        <template #varName="{ record }">
          <a-form-item field="varSn" no-style>
            <a-input v-model="record.varName" disabled/>
          </a-form-item>
        </template>

        <!--  变量编码-->
        <template #varSn="{ record }">
          <a-form-item field="varSn" label="变量编码" no-style>
            <a-input v-model="record.varSn" disabled/>
          </a-form-item>
        </template>

        <!--图例名称-->
        <template #legend="{ record }">
          <a-form-item field="legend" label="图例名称" no-style>
            <a-input v-model="record.legend"/>
          </a-form-item>
        </template>

        <!--指标单位-->
        <template #unit="{ record }">
          <a-form-item field="unit" label="指标单位" no-style>
            <a-input v-model="record.unit" :max-length="6"/>
          </a-form-item>
        </template>

        <!--存储类型-->
        <template #storageType="{ record }">
          <a-form-item field="storageType" label="存储类型" no-style>
            <a-select v-model="record.storageType" @change="handleStorageType(record)"
                      :placeholder="$t('global.pleaseSelect')" allow-clear>
              <a-option v-for="(item,index) in datav_storage_type" :value="item.value" :key="index">{{item.label}}</a-option>
            </a-select>
          </a-form-item>
        </template>

        <!--数值类型-->
        <template #changeType="{ record }">
          <a-form-item field="changeType" label="数值类型" :disabled="record.storageType!=1" no-style>
            <a-select v-model="record.changeType" :placeholder="$t('global.pleaseSelect')" allow-clear>
              <template v-if="defaultVal.dateDim==70">
                <a-option v-for="(item,index) in datav_change_type" v-show="item.value==1" :key="index" :value="Number(item.value)">{{ item.label }}
                </a-option>
              </template>
              <template v-else v-for="(item,index) in datav_change_type" :key="index">
                <a-option :value="Number(item.value)" v-show="item.value!=1">{{ item.label }}</a-option>
              </template>
            </a-select>
          </a-form-item>
        </template>

        <template #operate="{ record }">
          <a-button size="small" type="text" @click="handleAddChildList(record)">添加子变量
          </a-button>
          <a-button size="small" type="text" status="danger" @click="handleDeviceVarDelete(record)">
            {{ $t('global.delete') }}
          </a-button>
        </template>
      </a-table>
    </a-form>
  </a-card>

</template>

<script setup lang="ts">
import {computed, getCurrentInstance, onMounted, ref, watchEffect} from "vue";
import SelectVarModal from "@/views/manage/station/components/select-var-modal/index.vue";
import SankeyFormItem from "@/views/dashboard/preview/component/SankeyFormItem.vue"
import {TableColumnData} from "@arco-design/web-vue/es/table/interface";
import {TableInstance, Message} from "@arco-design/web-vue";
import {FormInstance} from "@arco-design/web-vue/es/form";

const proxy = getCurrentInstance()?.appContext.config.globalProperties;
const {datav_storage_type, datav_change_type} = proxy?.useDict("datav_storage_type", "datav_change_type");

const props = defineProps({
  defaultVal: {
    type: Object,
    default: {}
  }
})

const formRef = ref<FormInstance>();
const columns = computed<TableColumnData[]>(() => [
  {
    title: "变量名称",
    dataIndex: "varName",
    slotName: "varName",
    align: 'center',
    // width: 150,
    ellipsis: true,
    tooltip: {position: 'top'},
  }, {
    title: "变量编码",
    dataIndex: "varSn",
    slotName: "varSn",
    align: 'center',
    // width: 130,
    ellipsis: true,
    tooltip: {position: 'top'},
  }, {
    title: "图例名称",
    dataIndex: "legend",
    slotName: "legend",
    align: 'center',
    // width: 250,
  }, {
    title: "单位",
    dataIndex: "unit",
    slotName: "unit",
    align: 'center',
    // width: 150,
  }, {
    title: "存储类型",
    dataIndex: "storageType",
    slotName: "storageType",
    align: 'center',
    // width: 220,
  }, {
    title: "数值类型",
    dataIndex: "changeType",
    slotName: "changeType",
    align: 'center',
    // width: 220,
  },
  {
    title: "操作",
    dataIndex: "operate",
    slotName: 'operate',
    // width: 250,
    align: 'center',
  }
]);

// 弹窗显示隐藏
const selectVarVisible = ref<boolean>(false);
// 当前选中
const nowCheckIndex = ref<any>(null);
// 初始化数据配置
const initDataConfig = () => {
  return {
    comVariable: "", //通用变量（用电量 发电量 电流 电压 有功功率
    varSn: "",
    varName: "", //名称
    legend: "",
    color: "#fff",
    unit: "",
    percentage: 1,
    baseValue: '0',
    minValue: 0,
    maxValue: 0,
    storageType: '1',
    changeType: '',
    chain: false,
    yoy: false,
    backgroundColor: "#fff",
    children: [], //子数据区
  }
};

const form = ref<any>({
  // varList: [{...initDataConfig()}], //数据区域
  varList: [], //数据区域
});

const handleStorageType = (record) => {
  record.changeType = '';
};

// 关闭变量弹窗
const handleModalClose = () => {
  selectVarVisible.value = false;
};

const currentRowData = ref(null);
const tableConfigRef = ref<TableInstance>();

// 选择变量
const handleDeviceVarSelect = (record: any) => {
  let obj = {
    varSn: record.varSn,
    varName: record.deviceName + '/' + record.varName,
    legend: record.varName,
    unit: record.unit,
    key: `${Date.now()}${Math.floor(Math.random() * 1000000)}`
  }
  let newConfig = {...initDataConfig(), ...obj};
  if (JSON.stringify(form.value.varList) == '[]') {
    form.value.varList.push(newConfig);
    selectVarVisible.value = false;
  } else {
    if(props.defaultVal.dateDim==70){
      newConfig.changeType = 1;
    }else{
      newConfig.changeType = 2;
    }
    currentRowData.value.children.push(newConfig);
  }
  tableConfigRef.value?.expandAll(true);
};

// 添加数据区
const handleAddChildList = (item: any) => {
  currentRowData.value = item ? item : {};
  selectVarVisible.value = true;
};

const getItem = (data, parentKey: string) => {
  for (const item of data) {
    if (item.key === parentKey) {
      return item;
    }
    if (item.children && item.children.length > 0) {
      const result = getItem(item.children, parentKey);
      if (result) {
        return result;
      }
    }
  }
  return undefined;
};

// 设备变量移除
const handleDeviceVarDelete = (record: any) => {
  if (record.children && record.children.length > 0) {
    Message.error({
      content: `请先移除子变量`,
      duration: 1.5 * 1000,
    });
  } else {
    let data = form.value.varList;
    data = removeData(data, record.key);
    form.value.varList = data;
  }
};

function removeData(data, key: string) {
  return data.filter(item => {
    if (item.key === key) {
      return false;
    }
    if (item.children) {
      item.children = removeData(item.children, key);
    }
    return true;
  });
}

const formInit = () => {
  if (props.defaultVal.dateDim == 70) {
    handleChange(form.value.varList, 1)
  } else if(!props.defaultVal.isDetail) {
    handleChange(form.value.varList, 2)
  }
}

// 修改changeType
const handleChange = (data:any, changeType:number) => {
  data.map(item=>{
    item.changeType = changeType;
    if(item.storageType==2){
      item.changeType = '';
    }
    if(item.children){
      handleChange(item.children, changeType);
    }
  })
}

watchEffect(()=>{
  formInit();
  tableConfigRef.value?.expandAll(true);
})

onMounted(()=>{
  console.log(tableConfigRef.value)
  tableConfigRef.value?.expandAll(true);
})

defineExpose({
  form,
  formRef,
  setForm: (val: any) => {
    form.value.varList = [];
    form.value = val;
  }
});

</script>

<style lang="less" scoped>
.input-width {
  width: 500px;
}

.delete-icon {
  font-size: 16px;
  margin-left: 6px;
}
</style>
