<!--
* 功能：报表模板-配置模板
* 作者：闫李壮
* 日期：2024-6-11
-->
<template>
  <div>
    <a-card>
      <!--      表单-->
      <a-row>
        <a-col :span="6">
          <a-form :model="searchModel" auto-label-width>
            <a-form-item field="categoryName" label="模板名称">
              <a-input v-model="searchModel.reportName" placeholder="请输入模板名称" allow-clear/>
            </a-form-item>
          </a-form>
        </a-col>
        <a-divider style="height: 35px" direction="vertical"/>
        <a-col :span="2" style="text-align: right">
          <a-space :size="18">
            <a-button type="primary" @click="search">
              <template #icon>
                <icon-search/>
              </template>
              {{ $t('global.search') }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <!--     card-->
      <div>
        <a-button type="primary" @click="openTemplateModal">新增模板</a-button>
      </div>

      <a-spin :loading="loading" style="width: 100%;">
        <a-grid :cols="5" :colGap="12" :rowGap="16" v-if="templateData.length">
          <a-grid-item v-for="(item,index) in templateData" :key="index">
            <a-card class="description-card">
              <template #actions>
                <a-button v-if="item.systemFlag !== 0" type="text" status="danger" size="small"
                          @click="deleteTemplateItem(item)">删除
                </a-button>
                <a-button type="outline" size="small" @click="editTemplateItem(item.id)">
                  编辑
                </a-button>
              </template>
              <a-card-meta>
                <template #title>
                  <div class="title-view">
                    <a-typography-paragraph ellipsis style="font-size: 16px;">
                      {{ item.reportName }}
                    </a-typography-paragraph>
                    <div>
                      <a-tag size="small" :color="item.systemFlag === 0 ? 'green':'blue'">
                        <template #icon>
                          <icon-check-circle-fill/>
                        </template>
                        <span>{{ item.systemFlag === 0 ? '默认' : '自定义' }}</span>
                      </a-tag>
                      <a-tag size="small" color="red" v-if="item.visibilityType === 2" style="margin-left: 10px">
                        <template #icon>
                          <icon-check-circle-fill/>
                        </template>
                        <span>私有</span>
                      </a-tag>
                    </div>
                  </div>
                </template>
                <template #description>
                  <a-typography-paragraph type="secondary">
                    {{ item.createTime }}
                  </a-typography-paragraph>
                  <div style="min-width: 200px; max-width: 500px">
                    <a-typography-paragraph class="remark-view" type="secondary" :ellipsis="{
                    rows: 3,
                    showTooltip: true
                  }">
                      {{ item.remark }}
                    </a-typography-paragraph>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-grid-item>
        </a-grid>
        <a-empty v-else/>
      </a-spin>
    </a-card>
    <template-modal :visible="modalVisible" :id="editId" @confirm="confirmTemplateModal" @cancel="closeTemplateModal"/>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRoute} from 'vue-router'
import {deleteReportTemplates, getTemplateData} from "@/api/power/energy/search/report-templates";
import useLoading from '@/hooks/loading';
import TemplateModal from "@/views/power/energy/search/report/template/templateModal.vue";
import {Modal, Notification} from "@arco-design/web-vue";

const {loading, setLoading} = useLoading(true)
const route = useRoute()
const {stationType, reportType} = route.query
/**
 * 搜索表单
 */
const searchModel = ref({
  reportName: ''
})
/**
 * 模板列表
 */
const templateData = ref<any[]>([])
/**
 * @desc 新增模板弹窗
 */

const modalVisible = ref(false)
const editId = ref(0)
const search = () => {
  fetchTableData(searchModel.value.reportName)
}
/**
 * @desc 获取模板列表
 */
const fetchTableData = async (reportName?: string) => {
  setLoading(true)
  try {
    const res = await getTemplateData({reportName, reportType: reportType});
    if (res.code === 200) {
      templateData.value = res.data
    }
  } catch (e) {

  } finally {
    setLoading(false)
  }
};
/**
 * @desc 打开新增模板窗口
 */
const openTemplateModal = () => {
  modalVisible.value = true
}
const confirmTemplateModal = () => {
  editId.value = 0
  fetchTableData()
  modalVisible.value = false
}
/**
 * @desc 关闭新增模板窗口
 */
const closeTemplateModal = () => {
  editId.value = 0
  modalVisible.value = false
}
/**
 * @desc 编辑
 * @id
 */
const editTemplateItem = (id: number) => {
  editId.value = id
  modalVisible.value = true
}
/**
 * @desc 删除模板
 * @param item
 */
const deleteTemplateItem = (item: any) => {
  try {
    Modal.error({
      title: '提示',
      content: `你确定要删除 ${item.reportName} 吗？`,
      hideCancel: false,
      bodyStyle: {
        textAlign: 'center'
      },
      alignCenter: true,
      onOk: async () => {
        const res = await deleteReportTemplates(item.id)
        if (res.code === 200) {
          await fetchTableData()
          Notification.success('删除成功')
        }
        if (res.code !== 200) {
          Notification.error(res.msg)
        }
      },
    });

  } catch (e) {

  }
}
onMounted(() => {
  fetchTableData()
})
</script>

<style lang="less" scoped>
::v-deep(.description-card) {
  margin-top: 16px;

  .title-view {
    display: flex;

    div:first-child {
      flex: 1;
    }
  }

  .remark-view {
    height: 50px;
  }

  .arco-card-meta {
    height: 100%;

    .arco-card-meta-content {
      height: calc(100% - 40px);
    }
  }

  .arco-typography {
    margin-bottom: 0.3em;
  }
}
</style>
