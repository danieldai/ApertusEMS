<template>
  <div class="data-view">
    <YkiteFullScreenContainer :style="{ 'background-image': `url(${pageInfo.background.url || iconBg })` }">
      <!--   头部组件   -->
      <div class="header-view">
        <header-bi :title="pageInfo.pageName" :headerData="pageInfo.header"></header-bi>
      </div>
      <a-row class="midden-total-view">
        <a-col :span="7" style="padding-right: 18px;">
          <div class="title-view">
            <card-title class="card-title-view" v-if="pageInfo.content.stationNum">
              <template #icon>
                <img src="@/assets/bi/icon-node.png" alt="">
              </template>
              <span>
                站点数量:<text class="color-orange">{{ pageInfo.content.stationNum }}</text>
              </span>
            </card-title>
            <card-title class="card-title-view" v-if="pageInfo.content.deviceNum">
              <span>
                设备数量:<text class="color-orange">{{ pageInfo.content.deviceNum }}</text>
              </span>
            </card-title>
            <card-title class="card-title-view" v-if="pageInfo.content.pointNum">
              <span>
                节点数量:<text class="color-orange">{{ pageInfo.content.pointNum }}</text>
              </span>
            </card-title>
          </div>
        </a-col>
        <a-col :span="10">
        </a-col>
        <a-col :span="7" style="padding-left: 18px;">
          <div class="title-view">
            <card-title :type="1" v-if="pageInfo.content.yearEmissionReduction">
              <span>
                {{ pageInfo.content.yearEmissionReduction }}
              </span>
            </card-title>
          </div>
        </a-col>
      </a-row>
      <div class="midden-view">
        <a-row class="midden-chart-view" :gutter="24">
          <a-col :span="7" class="midden-chart-col-view">
            <div class="midden-chart-item-wrapper" v-for="(item,index) in pageInfo.cardList.leftTop" :key="index">
              <chart-wrapper :title="item.cardName" :unit="item.unit" :public="{
                           chartType:item.chartType,
                           cardKey: item.key,
                           dashboardConfigId: item.configId,
                           cardName: item.cardName,
                           pageName: pageInfo.pageName
                         }">
                <template #chart>
                  <component :is="EnumChartType[item.chartType]" :configType="0"
                             :public="{cardKey: item.key,dashboardConfigId: item.configId}"
                             :totalType="1"></component>
                </template>
                <template #extra>
                  <img :src="iconRight" alt="">
                </template>
              </chart-wrapper>
            </div>
          </a-col>
          <!--    地图      -->
          <a-col :span="10" class="midden-map-view"
                 :style="{'background-image': `url(${pageInfo.coreBackground.url || iconMap })`}">
            <map-title v-if="pageInfo.coreMain" :data="pageInfo.coreMain"/>
            <map-foot class="map-foot" v-if="pageInfo.coreSubStatData.length" :data="pageInfo.coreSubStatData"></map-foot>
          </a-col>
          <a-col :span="7" class="midden-chart-col-view">
            <!--     右侧图表       -->
            <div class="midden-chart-item-wrapper" v-for="(item,index) in pageInfo.cardList.rightTop" :key="index">
              <chart-wrapper :title="item.cardName" :unit="item.unit" :public="{
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
                             :totalType="1">
                  </component>
                </template>
                <template #extra>
                  <img :src="iconRight" alt="">
                </template>
              </chart-wrapper>
            </div>

          </a-col>
        </a-row>
        <div class="midden-table-view">
          <div class="midden-chart-item-wrapper" v-for="(item,index) in pageInfo.cardList.bottomList" :key="index">
            <chart-wrapper :title="item.cardName"
                           :public="{
                           chartType:item.chartType,
                           cardKey: item.key,
                           dashboardConfigId: item.configId,
                           cardName: item.cardName,
                           pageName: pageInfo.pageName
                         }"
                           :unit="item.unit">
              <template #chart>
                <component :is="EnumChartType[item.chartType]"
                           :configType="0"
                           :public="{cardKey: item.key,dashboardConfigId: item.configId}"
                           :totalType="1"></component>
              </template>
              <template #extra>
                <img :src="iconRight" alt="">
              </template>
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
import HeaderBi from "@/views/bi/component/headerBi.vue";
import FooterBi from "@/views/bi/component/footerBi.vue";
import ChartWrapper from "@/views/bi/component/chart-wrapper.vue";
import CardTitle from "@/views/bi/one/components/card-title.vue";
import MapTitle from "@/views/bi/component/map-title.vue";
import MapFoot from "@/views/bi/component/map-foot.vue";
import iconMap from '@/assets/bi/map.png'
import iconBg from '@/assets/bi/background.png'
import iconRight from "@/assets/bi/icon-right.png";

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

</script>

<style lang="less" scoped>
.color-orange {
  font-size: 14px;
  font-weight: bold;
  color: #F3921D;
  margin: 0 5px;
}

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
      height: 90px;
      background: url("@/assets/bi/navbar.png") no-repeat center;
      background-size: 100% 100%;
      font-size: 28px;
    }

    .midden-total-view {
      top: 90px;
      position: absolute;
      width: 100%;
      height: 30px;

      .title-view {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .card-title-view {
          width: 32.8%;
        }
      }
    }

    .midden-view {
      height: calc(100% - 190px);

      .midden-chart-view {
        height: calc(100% - 200px);

        .midden-chart-col-view {
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .midden-chart-item-wrapper {
            width: 100%;
            height: calc(100% / 3);
            margin-top: 12px;
            overflow: hidden;
          }
        }
      }

      .midden-map-view {
        background-repeat: no-repeat;
        height: 100%;
        position: relative;
        background-size: contain;
        background-position: center;

        .map-foot {
          position: absolute;
          bottom: 0;
        }
      }

      .midden-table-view {
        height: 210px;
        display: grid;
        grid-template-columns: calc(72% - 15px) 28%;
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
