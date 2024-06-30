<!--
* 功能：报表-新增、编辑
* 作者：闫李壮
* 日期：2024-6-6
-->
<template>
  <div>
    <a-modal v-if="visible" :visible="visible" :title="id ? '编辑模板' : '新增模板'" width="50%" @ok="handleOk"
             @cancel="handleCancel">
      <a-form class="basic-report-template-form" ref="formRef" :model="form" :rules="formRules" auto-label-width>
        <a-form-item field="reportName" label="报表名称">
          <a-input v-model="form.reportName" placeholder="请输入字母或数字，最多20个字符" :max-length="20"/>
        </a-form-item>
        <a-form-item field="visibilityType" label="可见类型">
          <a-radio-group v-model="form.visibilityType" :disabled="form.systemFlag === 0">
            <a-radio :value="1">公有</a-radio>
            <a-radio :value="2">私有</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item field="remark" label="报表描述">
          <a-textarea v-model="form.remark" placeholder="请输入报表描述，最多不超过200字." allow-clear
                      :max-length="200"/>
        </a-form-item>
        <a-form-item field="headConfig" label="表头配置">
          <a-table :columns="columns" :data="form.headConfig" :pagination="false" bordered style="width:100%">
            <template #deviceName="{ record }">
              <a-input v-model="record.deviceName" readonly></a-input>
            </template>
            <template #deviceVarId="{ record }">
              <a-input v-model="record.deviceVarId" readonly></a-input>
            </template>
            <template #deviceVarName="{ record }">
              <a-input v-model="record.deviceVarName" allow-clear/>
            </template>
            <template #operation="{ rowIndex  }">
              <a-button type="primary" :status="'danger'" size="mini" @click="handleDeleteTableItem(rowIndex)">
                删除
              </a-button>
            </template>
          </a-table>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleAddTableItem">新增一行</a-button>
        </a-form-item>
      </a-form>
      <select-var-modal :visible="selectVarVisible" @cancel="handleModalClose" @add="handleDeviceVarSelect"/>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import SelectVarModal from "@/views/manage/station/components/select-var-modal/index.vue";
import {ref, watch} from "vue";
import {Notification} from '@arco-design/web-vue'
import {
  addReportTemplates,
  editReportTemplates,
  getReportTemplateDetail
} from "@/api/power/energy/search/report-templates";
import {useRoute} from "vue-router";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  id: {
    type: Number,
    default: 0
  }
});
const emit = defineEmits(["confirm", 'cancel']);
const formRef = ref<any>(null);
const route = useRoute();
const {stationType, reportType} = route.query;
/**
 * @desc 表单
 */
const form = ref<any>({
  reportName: '',
  remark: '',
  visibilityType: 1,
  multiLevelHeader: 1,
  headConfig: []
});
const formRules = {
  reportName: [
    {
      required: true,
      message: '报表名称不得为空',
    },
  ]
}
/**
 * @desc 表头
 */
const columns: any[] = [
  {
    title: '设备',
    dataIndex: 'deviceName',
    slotName: 'deviceName',
    align: 'center'
  },
  {
    title: '变量',
    dataIndex: 'deviceVarId',
    slotName: 'deviceVarId',
    align: 'center'
  },
  {
    title: '显示名称',
    dataIndex: 'deviceVarName',
    slotName: 'deviceVarName',
    align: 'center'
  },
  {
    title: '操作',
    dataIndex: 'operation',
    slotName: 'operation',
    align: 'center'
  },
];
/**
 * @desc 选择变量弹窗
 */
const selectVarVisible = ref(false);
// 新增一行
const handleAddTableItem = () => {
  selectVarVisible.value = true;
};
/**
 * @desc 选择变量弹窗确认
 * @param record
 */
const handleDeviceVarSelect = (record: any) => {
  const {deviceName, deviceId, varName: deviceVarName, varSn: deviceVarId} = record;
  const item: any = {
    deviceName,
    deviceId,
    deviceVarName,
    deviceVarId
  };
  form.value.headConfig.push(item);
  selectVarVisible.value = false;
};
/**
 * @desc 关闭变量弹窗确认
 */
const handleModalClose = () => {
  selectVarVisible.value = false;
};
/**
 * @desc 移除
 */
const handleDeleteTableItem = (index: number) => {
  form.value.headConfig.splice(index, 1);
}
// 确定提交
const handleOk = async () => {
  formRef.value.validate(async (valid: boolean) => {
    if (valid) return

    try {
      const params = {
        ...form.value,
        headConfig: JSON.stringify(form.value.headConfig),
        reportType
      }
      let res: any;
      if (props.id) {
        res = await editReportTemplates(params)
      } else {
        res = await addReportTemplates(params);
      }
      if (res.code === 200) {
        Notification.success(`${props.id ? '编辑' : '新增'}成功`)
        emit("confirm");
        resetFields()
      } else {
        Notification.error(res.msg)
      }
    } catch (e) {

    }
  })
};
/**
 * 关闭当前弹窗
 */
const handleCancel = () => {
  resetFields()
  emit("cancel");
};
const getTemplateDetail = async () => {
  const res = await getReportTemplateDetail(props.id);
  if (res.code === 200) {
    form.value = {
      ...res.data,
      headConfig: JSON.parse(res.data.headConfig)
    };
    console.log(form.value,'form')
  }
};

/**
 * @desc 重置表单
 */
const resetFields = () => {
  form.value = {
    reportName: '',
    remark: '',
    visibilityType: 1,
    multiLevelHeader: 1,
    headConfig: []
  }
}
/**
 * @desc 监听id变化
 */
watch(
    () => props.id,
    (id) => {
      if (id) {
        getTemplateDetail()
      }
    }
)
</script>

<style lang="less" scoped>

</style>
