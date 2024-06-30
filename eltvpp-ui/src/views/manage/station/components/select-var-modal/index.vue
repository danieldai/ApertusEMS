<template>
  <a-modal width="800px" :visible="visible" :footer="false" @cancel="handleCancel" title-align="start"
           title="请选择变量">
    <a-form :model="searchForm" auto-label-width>
      <a-row :gutter="24">
        <!--能源类型-->
        <a-col :span="10">
          <a-form-item label="能源类型">
            <a-select v-model="searchForm.stationType" allow-clear
                      :placeholder="$t('manage.station.list.stationTypeSelect')" @change="handleStationType">

              <a-option v-for="dict in sys_station_type" :key="dict.value" :label="dict.label"
                        :value="Number(dict.value)"></a-option>
            </a-select>
          </a-form-item>
        </a-col>
        <!--设备-->
        <a-col :span="10">
          <a-form-item :label="$t('manage.station.pageConfig.deviceSelect')">
            <a-tree-select v-model="searchForm.deviceId" :data="renderDeviceData"
                           :placeholder="$t('manage.station.pageConfig.devicePlaceholder')"
                           :fieldNames="{key: 'id', title: 'deviceName', children: 'children'}"
                           :allow-search="true" allow-clear
                           :filter-tree-node="filterTreeNode"></a-tree-select>
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-button type="primary" @click="searchDeviceVar">
            <template #icon>
              <icon-search/>
            </template>
            {{ $t('global.search') }}
          </a-button>
        </a-col>
      </a-row>
    </a-form>
    <a-table :scroll="{ y: 400 }" row-key="varSn" :bordered="{ wrapper: true, cell: true }"
             :columns="deviceVarTabColumns" :data="renderData" @page-change="onDeviceVarPageChange"
             :pagination="deviceVarPagination" @page-size-change="onDeviceVarPageSizeChange">
      <template #operate="{ record }">
        <a-button size="small" type="primary" @click="handleDeviceVarSelect(record)">添加</a-button>
      </template>
    </a-table>
  </a-modal>
</template>

<script setup lang="ts">
import {getCurrentInstance, onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import {listFusionGroup} from "@/api/system/device";
import {processSelectable} from '@/utils/ruoyi';
import {TableColumnData} from "@arco-design/web-vue/es/table/interface";
import {listDeviceVar} from "@/api/system/device-var";
import {BasePagination} from "@/api/common";
import {StationTypeEnum} from "@/api/system/device";

const proxy = getCurrentInstance()?.appContext.config.globalProperties;
const {sys_station_type} = proxy?.useDict("sys_station_type");

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  // 变量类型 1模拟量 2状态量
  varType: {
    type: Number,
    default: 1,
  },
  // 是否累积量  1是 0否
  isAccumulation: {
    type: Number,
    default: 0,
  },
  // 站点
  deptId: {
    type: Number,
    default: -100,
  },
  // 能源类型 1电力 2光伏
  stationType: {
    type: Number,
    default: 0
  }
})
const emit = defineEmits(['cancel', 'add']);
//查询表单
const searchForm = reactive({
  stationType: props.stationType ? props.stationType : undefined,
  deviceId: '',
  isAccumulation: props.isAccumulation == 0 ? undefined : props.isAccumulation,
  deptId: props.deptId == -100 ? undefined : props.deptId,
});
// 设备数据
const renderDeviceData = ref<any>([]);

//变量列
const deviceVarTabColumns = reactive<TableColumnData[]>([
  {
    title: "变量名称",
    dataIndex: "varName",
    align: 'left',
    width: 150
  }, {
    title: "变量编码",
    dataIndex: "varSn",
    align: 'left',
    width: 150
  }, {
    title: "单位",
    dataIndex: "unit",
    align: 'left',
    width: 100
  },
  {
    title: "操作",
    dataIndex: "operate",
    slotName: 'operate',
    width: 100,
    align: 'center'
  }
]);
// 分页
const deviceVarPagination: any = reactive({...BasePagination()});
// 表格数据
const renderData = ref<any>([]);

/**
 * 获取设备列表
 */
const fetchDeviceData = async () => {
  try {
    const params = {
      stationType: searchForm.stationType,
      deptId: props.deptId == -100 ? undefined : props.deptId
    };
    const res = await listFusionGroup(params);
    processSelectable(res.data);
    renderDeviceData.value = res.data;
  } catch (err) {
    // you can report use errorHandler or other
  } finally {
  }
};
/**
 * 设备搜索
 * @param searchValue
 * @param nodeData
 */
const filterTreeNode = (searchValue: any, nodeData: any) => {
  return nodeData.deviceName.toLowerCase().indexOf(searchValue.toLowerCase()) > -1;
};
/**
 * 获取设备变量列表
 */
const fetchDeviceVarData = async () => {
  renderData.value = [];
  try {
    const params = {
      ...searchForm,
      pageSize: deviceVarPagination.pageSize,
      pageNum: deviceVarPagination.current,
      varType: props.varType
    };
    const res = await listDeviceVar(params);
    if (res.code == 200) {
      renderData.value = res.rows;
      deviceVarPagination.total = res.total;
    }
  } catch (err) {
    // you can report use errorHandler or other
  } finally {
  }
};
/**
 * 表格页码发生变化-变量列表
 * @param val
 */
const onDeviceVarPageChange = async (val: number) => {
  deviceVarPagination.current = val;
  await fetchDeviceVarData();
};

const searchDeviceVar = async () => {
  deviceVarPagination.current = 1;
  await fetchDeviceVarData();
};

/**
 * 表格每页数量发生变化-变量列表
 * @param val 值
 */
const onDeviceVarPageSizeChange = async (val: number) => {
  deviceVarPagination.pageSize = val;
  await fetchDeviceVarData();
};
/**
 * 设备变量选中
 */
const handleDeviceVarSelect = (record: any) => {
  emit('add', record);
};
// 关闭弹窗
const handleCancel = () => {
  emit('cancel');
};

const handleStationType = (val) => {
  searchForm.deviceId = '';
  fetchDeviceData();
};

/**
 * @desc 监听id变化
 */
watch(
    () => props.visible,
    async (visible) => {
      if (visible) {
        searchForm.stationType = props.stationType?props.stationType:undefined;
        searchForm.deviceId = '';
        await fetchDeviceData();
        await fetchDeviceVarData();
      }
    }
)
</script>

<style lang="less" scoped>

</style>
