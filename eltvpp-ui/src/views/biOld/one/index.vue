<template>
  <div class="data-view">
    <YkiteFullScreenContainer :style="{ 'background-image': `url(${pageInfo.background.url || iconBg })` }">
      <!--   头部组件   -->
      <div class="header-view">
        <header-bi :title="pageInfo.pageName" :headerData="pageInfo.header"></header-bi>
        <img src="@/assets/bi/navbarOld.png" alt="">
      </div>
      <a-row class="midden-total-view">
        <a-col :span="6" style="padding-right: 18px;">
          <div class="title-view">
            <span v-if="pageInfo.content.stationNum">站点数量:{{ pageInfo.content.stationNum }}</span>
            <span v-if="pageInfo.content.deviceNum">设备数量:{{ pageInfo.content.deviceNum }}</span>
            <span v-if="pageInfo.content.pointNum">节点数量:{{ pageInfo.content.pointNum }}</span>
          </div>
        </a-col>
        <a-col :span="12"/>
        <a-col :span="6" style="padding-left: 18px;">
          <div class="title-view">
            <span>
              {{ pageInfo.content.yearEmissionReduction }}
            </span>
          </div>
        </a-col>
      </a-row>
      <div class="midden-view">
        <a-row class="midden-chart-view" :gutter="24">
          <a-col :span="6" class="midden-chart-col-view">
            <div class="midden-chart-item-wrapper" v-for="(item,index) in pageInfo.cardList.leftTop" :key="index">
              <chart-wrapper :title="item.cardName" icon-show :public="{
                           chartType:item.chartType,
                           cardKey: item.key,
                           dashboardConfigId: item.configId,
                            cardName: item.cardName,
                           pageName: pageInfo.pageName
                         }">
                <template #chart>
                  <component :is="EnumChartType[item.chartType]"
                             :configType="0"
                             :public="{cardKey: item.key, dashboardConfigId: item.configId}"
                             :totalType="0"></component>
                </template>
                <template #extra></template>
              </chart-wrapper>
            </div>
          </a-col>
          <!--    地图      -->
          <a-col :span="12" class="midden-map-view"
                 :style="{'background-image': `url(${pageInfo.coreBackground.url || iconMap })`}">
            <map-title v-if="pageInfo.coreMain" :data="pageInfo.coreMain" class="map-title"/>
            <map-foot class="map-foot" v-if="pageInfo.coreSubStatData.length"
                      :data="pageInfo.coreSubStatData"></map-foot>
          </a-col>
          <a-col :span="6" class="midden-chart-col-view">
            <!--     右侧图表       -->
            <div class="midden-chart-item-wrapper" v-for="(item,index) in pageInfo.cardList.rightTop" :key="index">
              <chart-wrapper :title="item.cardName" icon-show :public="{
                           chartType:item.chartType,
                           cardKey: item.key,
                           dashboardConfigId: item.configId,
                            cardName: item.cardName,
                           pageName: pageInfo.pageName
                         }">
                <template #chart>
                  <component :is="EnumChartType[item.chartType]"
                             :configType="0"
                             :public="{cardKey: item.key,dashboardConfigId: item.configId}"
                             :totalType="0"></component>
                </template>
                <template #extra></template>
              </chart-wrapper>
            </div>

          </a-col>
        </a-row>
        <div class="midden-table-view">
          <div class="midden-chart-item-wrapper" v-for="(item,index) in pageInfo.cardList.bottomList" :key="index">
            <chart-wrapper :title="item.cardName" icon-show :public="{
                           chartType:item.chartType,
                           cardKey: item.key,
                           dashboardConfigId: item.configId,
                            cardName: item.cardName,
                           pageName: pageInfo.pageName
                         }">
              <template #chart>
                <component :is="EnumChartType[item.chartType]"
                           :configType="0"
                           :public="{cardKey: item.key,dashboardConfigId: item.configId}"
                           :totalType="0"></component>
              </template>
              <template #extra></template>
            </chart-wrapper>
          </div>
        </div>
      </div>
      <div class="footer-view">
        <footer-bi/>
      </div>
    </YkiteFullScreenContainer>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import HeaderBi from "@/views/bi/component/headerBi.vue";
import FooterBi from "@/views/bi/component/footerBi.vue";
import ChartWrapper from "@/views/biOld/component/chart-wrapper.vue";
import MapTitle from "@/views/biOld/component/map-title.vue";
import MapFoot from "@/views/biOld/component/map-foot.vue";
import iconMap from '@/assets/bi/earth.png'
import iconBg from '@/assets/biTow/background.png'

const EnumChartType: any = {
  1: 'large-combine',
  2: 'large-combine',
  3: 'large-combine',
  4: 'large-stack',
  5: 'large-combine',
  6: 'large-pie',
  7: 'large-k',
  8: 'large-water',
  9: 'large-gauge',
  10: 'large-sankey',
  11: 'large-section',
  12: 'large-ranking',
  13: 'card-status',
  14: 'card-base',
  15: 'large-table',
  16: 'large-table',
  17: 'large-pie-statistics',
  18: 'large-pie-statistics',
};

const props = defineProps({
  pageInfo: {
    type: Object,
    default: () => {
      return {
        cardList: {
          leftTop: [],
          bottomList: [],
          rightTop: []
        },
        coreSubStatData: [],
        background: {},
        pageTemplate: 2,
        coreBackground: {},
        header: {
          date: true,
          weather: true,
          network: true
        },
        coreMain: {},
        pageName: "",
        content: {}
      }
    }
  }
})

const dataList = ref<any>([])

for (let i = 0; i < 10; i++) {
  dataList.value.push({
    id: i,
    time: '2024/04/06 13:31:21',
    incident: '输入电压上限越界-223V',
    site: '山东银凯特产业园区',
    facility: '总配电柜',
    category: '类别',
    level: '等级',
    status: '已处理'
  })
}

const dataList1 = ref<any>([])
for (let i = 0; i < 10; i++) {
  dataList1.value.push({
    id: i,
    site: '山东银凯特产业园区',
    maintain: '日常维护',
    category: '类别',
    status: '未处理'
  })
}

// 切换站点
const changeSite = () => {
  // window.location.replace(window.location.origin + "/biOneOld");
}
</script>

<style lang="less" scoped>
.data-view {
  width: 100vw;
  height: 100vh;

  #ykite-full-screen-container {
    height: 100%;
    background-size: 100% 100%;
    background-repeat: no-repeat;
    color: #FFFFFF;
    font-family: Source Han Sans;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    position: relative;

    .header-view, .midden-total-view, .midden-view, .table-view {
      z-index: 99;
      padding: 0 32px;
    }

    .header-view {
      height: 80px;
      font-size: 28px;
      position: relative;
      padding: 0 !important;
      img {
        width: 100%;
        height: 70%;
        position: absolute;
        bottom: -25px;
        left: -5px;
      }
    }

    .midden-total-view {
      top: 80px;
      position: absolute;
      width: 100%;
      height: 30px;

      .title-view {
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-size: 18px;

        .card-title-view {
          width: 32.8%;
        }
      }
    }

    .midden-view {
      height: calc(100% - 180px);

      .midden-chart-view {
        height: calc(100% - 190px);

        .midden-chart-col-view {
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .midden-chart-item-wrapper {
            height: calc(100% / 3);
            margin-top: 12px;
          }
        }
      }

      .midden-map-view {
        background-repeat: no-repeat;
        background-size: contain;
        background-position: center;
        height: 100%;
        position: relative;

        .map-title {
          margin-top: 15px;
        }

        .map-foot {
          position: absolute;
          bottom: 0;
        }
      }

      .midden-table-view {
        height: 200px;
        display: grid;
        grid-template-columns: calc(76% - 12px) 24%;
        grid-column-gap: 12px;
        margin-top: 12px;

        .midden-chart-item-wrapper {
          width: 100%;
          height: 100%;
          overflow: hidden;
        }
      }
    }

    .footer-view {
      height: 50px;
    }
  }
}
</style>
