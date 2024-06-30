<template>
  <!--新建配置-->
  <a-modal width="80%" :modal-style="{height:'85%'}" :body-style="{height:'92%'}" v-model:visible="props.visible"
           :closable="false" @cancel="handleCancel" @ok="handleOk">
    <!--      <template #title></template>-->
    <div v-if="props.visible">
      <!--通用模板-->
      <a-card title="通用模板">
        <a-row :gutter="[16, 16]">
          <a-col :span="6" class='item-wrapper'
                 v-for="(item,index) in globalTemplate"
                 :key="index" @click="selectTemplateItem(item)">
            <dl>
              <dd>{{ item.title }}</dd>
              <dt>
                <img :src="item.url" alt="找不到了">
              </dt>
            </dl>
            <a-button class="choose-button" size="mini">选择</a-button>
          </a-col>
        </a-row>
      </a-card>

      <!--专用模板-->
      <a-card title="专用模板" style="margin-top: 20px">
        <a-row :gutter="[16, 16]">
          <a-col class="item-wrapper dedicated"
                 v-for="(item,index) in specializedTemplate"
                 :key="index"
                 :span="item.layout_span"
                 @click="selectTemplateItem(item)">
            <dl>
              <dd>{{ item.title }}</dd>
              <dt :style="{height:index < 4  ? '26vh':''}">
                <img :src="item.url" alt="找不到了">
              </dt>
            </dl>
            <a-button class="choose-button" size="mini">选择</a-button>
          </a-col>
        </a-row>
      </a-card>
    </div>
  </a-modal>
</template>
<script lang="ts" setup>
import line from '@/assets/charts/line.png'
import strip from '@/assets/charts/bar.png'
import bar from "@/assets/dashboard/bar.png";
import stack from '@/assets/charts/stack.png'
import area from '@/assets/charts/area.png'
import kLine from '@/assets/charts/k-line.png'
import rank from '@/assets/charts/rank.png'
import single from '@/assets/charts/single.png'
import gauge from "@/assets/dashboard/gauge.png";
import pie from "@/assets/dashboard/pie.png";
import pieAnnular from "@/assets/dashboard/pie-annular.png";
import pieStatistics from "@/assets/dashboard/pie-statistics.png";
import status from "@/assets/dashboard/status.png";
import table from "@/assets/dashboard/table.png";
import tableSimple from "@/assets/dashboard/table-simple.png";
import water from "@/assets/dashboard/water.png";
import workOrder from "@/assets/dashboard/work-order.png";
import workOrderSimple from "@/assets/dashboard/work-order-simple.png";
import section from '@/assets/dashboard/section.png'
import sankey from '@/assets/dashboard/sankey.png'
import workOrderStatistics from "@/assets/dashboard/work-order-statistics.png";
import {nextTick, onMounted, ref} from "vue";

interface IItem {
  chartType: number,
  url: any,
  title: string,
  layout_span?: number,
  staticType?: number,
  headType?: number,
  layout: {
    w: number,
    h: number
  }
}

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['cancel', 'select']);

const myScrollingDiv = ref<HTMLElement | null>(null);
// 通用模板
const globalTemplate: IItem[] = [
  {
    chartType: 1,
    url: line,
    title: '折线图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 3,
    url: strip,
    title: '条形图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 2,
    url: bar,
    title: '柱状图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 4,
    url: stack,
    title: '堆叠图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 5,
    url: area,
    title: '面积图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 10,
    url: sankey,
    title: '桑基图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 11,
    url: section,
    title: '分段图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 7,
    url: kLine,
    title: 'K线图',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 12,
    url: rank,
    title: '排行榜',
    layout: {
      w: 6,
      h: 8
    }
  },
  {
    chartType: 14,
    url: single,
    title: '单值图',
    layout: {
      w: 2,
      h: 4
    }
  },
  {
    chartType: 6,
    url: pieAnnular,
    title: '饼图',
    layout: {
      w: 3,
      h: 8
    }
  },
  {
    chartType: 9,
    url: gauge,
    title: '仪表盘',
    layout: {
      w: 3,
      h: 8
    }
  },
  {
    chartType: 8,
    url: water,
    title: '水滴图',
    layout: {
      w: 3,
      h: 8
    }
  },
  {
    chartType: 13,
    url: status,
    title: '状态量',
    layout: {
      w: 1,
      h: 4
    }
  }
];
// 专用模板
const specializedTemplate: IItem[] = [
  {
    chartType: 15,
    url: table,
    title: '实时报警',
    layout_span: 16,
    headType: 1,
    layout: {
      w: 8,
      h: 8
    }
  },
  {
    chartType: 15,
    url: tableSimple,
    title: '实时报警',
    layout_span: 8,
    headType: 2,
    layout: {
      w: 4,
      h: 8
    }
  },
  {
    chartType: 16,
    url: workOrder,
    title: '工单信息',
    layout_span: 16,
    headType: 1,
    layout: {
      w: 8,
      h: 8
    }
  },
  {
    chartType: 16,
    url: workOrderSimple,
    title: '工单信息',
    layout_span: 8,
    headType: 2,
    layout: {
      w: 4,
      h: 8
    }
  },
  {
    chartType: 17,
    url: pie,
    title: '报警统计（按级别）',
    layout_span: 6,
    staticType: 1,
    layout: {
      w: 3,
      h: 8
    }
  },
  {
    chartType: 17,
    url: bar,
    title: '报警统计（按类型）',
    layout_span: 6,
    staticType: 2,
    layout: {
      w: 3,
      h: 8
    }
  },
  {
    chartType: 18,
    url: pieStatistics,
    title: '工单统计（按类别）',
    layout_span: 6,
    staticType: 1,
    layout: {
      w: 3,
      h: 8
    }
  },
  {
    chartType: 18,
    url: workOrderStatistics,
    title: '工单统计（按状态）',
    layout_span: 6,
    staticType: 2,
    layout: {
      w: 3,
      h: 8
    }
  },
];
// 选择模板
const selectTemplateItem = (item: IItem) => {
  const {chartType, layout, layout_span} = item;
  emit('select', {
    chartType,
    layout,
    layout_span,
    headType: item.headType ? item.headType : undefined,
    staticType: item.staticType ? item.staticType : undefined
  });
}
const scrollToTop = async () => {
  // 使用nextTick确保DOM更新后再执行滚动操作
  await nextTick();
  if (myScrollingDiv.value) {
    // 使用scrollTo方法滚动到顶部
    myScrollingDiv.value.scrollTo(0, 0);
  }
};

const handleOk = () => {
  scrollToTop()
  emit('cancel');
}


const handleCancel = () => {
  emit('cancel');
}

onMounted(() => {

})


</script>
<style lang="less" scoped>
.dedicated {
  img {
    height: auto !important;
  }
}

.item-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;

  dl {
    padding: 0;
    margin: 0;
    width: 100%;
    flex: 1;
    box-sizing: border-box;
    border: 1px solid var(--color-neutral-4);
    border-radius: 4px;

    dd {
      padding: 0;
      margin: 0;
      line-height: 24px;
      text-align: center;
      color: var(--color-neutral-10);
      background-color: var(--color-neutral-4);
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
    }

    dt {
      padding: 0;
      margin: 0;
      overflow: hidden;
      display: flex;
      width: 100%;
      //height: 160px;
      justify-content: center;
      align-items: center;

      img {
        width: 100%;
        height: 106px;
        object-fit: contain;
      }
    }
  }


  button {
    margin-top: 8px;
  }
}

.item-wrapper:hover {
  dl {
    border-color: var(--color-neutral-6);
  }

  .choose-button {
    background: var(--color-neutral-3);
  }
}

.disabled-item:hover {
  cursor: not-allowed;

  dl {
    border: 1px solid var(--color-neutral-4);
  }

  .choose-button {
    background: none;
  }
}
</style>
