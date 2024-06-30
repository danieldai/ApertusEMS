<template>
  <div class="container">
    <div class="detail-list">
      <div :class="['detail-list-item',isHasBorder?`detail-list-item${index+1}`:'detail-list-item-border']" v-for="(item,index) in listData"
        :key="index">
        <img :src="item.iconUrl || iconPower" alt="">
        <span>{{ item.name }}</span>
        <span :class="`color-${index+1}`" :style="{fontWeight:'bold',margin:'0 5px 0 10px'}">{{ item.value }}</span>
        <span>{{ item.unit }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import iconPower from "@/assets/bi/icon-power.png";
  import iconCapacity from "@/assets/bi/icon-capacity.png";
  import iconSoc from "@/assets/bi/icon-soc.png";
  import iconSoh from "@/assets/bi/icon-soh.png";
  import iconInstall from "@/assets/bi/two/icon-zhuangjiliang.png";
  import iconGeneration from "@/assets/bi/two/icon-fadianliang.png";
  import iconRate from "@/assets/bi/two/icon-fadianlv.png";

  defineProps({
    isHasBorder: {
      type: Boolean,
      default: true
    },
    listData: {
      type: Array,
      default: () => {
        return [{
          name: '装机量',
          value: 315,
          unit: 'kWp',
          iconUrl: iconInstall
        }, {
          name: '发电量',
          value: 315,
          unit: 'kw',
          iconUrl: iconGeneration
        }, {
          name: '发电率',
          value: 315,
          unit: '%',
          iconUrl: iconRate
        }]
      }
    }
  })
</script>

<style lang="less" scoped>
  .container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .detail-list {
    width: 90%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .detail-list-item {
      color: #FFFFFF;
      padding: 5px;
      background: url("@/assets/bi/rectangle3.png") no-repeat;
      background-size: 100% 100%;
      display: flex;
      align-items: center;
      margin-bottom: 15px;

      &:nth-last-child(1) {
        margin-bottom: 0;
      }

      img {
        width: 18px;
        height: 18px;
        margin-right: 5px;
      }

      span:nth-child(1) {
        width: 40px;
        text-align: center;
      }
    }

    .detail-list-item-border{
      //border: 0.25px solid #0086FF;
    }

    /* .detail-list-item:nth-child(1) {
      border-left: 1px solid #11DACA;
    }

    .detail-list-item:nth-child(2) {
      border-left: 1px solid #F3921D;
    }

    .detail-list-item:nth-child(3) {
      border-left: 1px solid #FAC61C;
    }

    .detail-list-item:nth-child(4) {
      border-left: 1px solid #0E88F6;
    } */

    .detail-list-item1 {
      border-left: 1px solid #11DACA;
    }

    .detail-list-item2 {
      border-left: 1px solid #F3921D;
    }

    .detail-list-item3 {
      border-left: 1px solid #FAC61C;
    }

    .detail-list-item4 {
      border-left: 1px solid #0E88F6;
    }
  }

  .color-2 {
    color: #F3921D;
  }

  .color-4 {
    color: #0F8DFF;
  }

  .color-1 {
    color: #11DACA;
  }

  .color-3 {
    color: #FAC61C;
  }
</style>
