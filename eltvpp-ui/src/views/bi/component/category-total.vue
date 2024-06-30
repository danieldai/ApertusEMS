<template>
  <div class="detail-list-view">
    <div class="detail-list-box" v-for="(item,index) in props.data" :key="index" v-if="type == 1">
      <a-row class="detail-list-item" align="center">
        <a-col :span="12">
          <template v-if="index === 0">
            <img :src="icon0" alt="" width="16" height="16" style="margin-right: 5px"/>
          </template>
          <template v-else>
            <img :src="index === 1 ? icon1 : index === 2 ? icon2 : icon3" alt="" style="margin-right: 12px">
          </template>
          <span>{{ item.varName }}</span>
        </a-col>
        <a-col :span="6" class="detail-list-item-value">
          <span :class="`color-${index + 1}`">{{ item.singleValue }}</span>
        </a-col>
        <a-col :span="6" style="text-align: end;">
          <div>{{ item.unit }}</div>
        </a-col>
      </a-row>
    </div>

    <div class="total-list" v-else-if="type==2">
      <div class="list-item list-item-border" :style="{'background': isBg==2?'':'none'}" v-for="(item,index) in props.data" :key="index">
        <span :class="[isBg?'text-bold':'text-bold-white']">{{ item.varName }}</span>
        <div>
          <span style="margin-right:1px" class="total-text-active">{{ item.singleValue }}</span>
          <span :class="[isBg?'text-bold':'text-bold-white']">{{ item.unit }}</span>
        </div>
      </div>
    </div>

    <div class="total-list" v-else>
      <div class="list-item" v-for="(item,index) in props.data" :key="index">
        <span>{{ item.varName }}</span>
        <div>
          <span style="margin-right:1px">{{ item.singleValue }}</span>
          <span>{{ item.unit }}</span>
        </div>
      </div>
    </div>

  </div>

</template>

<script setup lang="ts">
import icon0 from '@/assets/bi/icon-total.png'
import icon1 from '@/assets/bi/icon-blue.png'
import icon2 from '@/assets/bi/icon-green.png'
import icon3 from '@/assets/bi/icon-yellow.png'

const props = defineProps({
  type: {
    type: Number,
    default: 1
  },
  data: {
    type: Array,
    default: () => {
      return [
        {name: '光伏', value: 152},
        {name: '风能', value: 152},
        {name: '其他', value: 152}
      ]
    }
  },
  isBg: {
    type: Number,
    default: 2
  }
})

const getRandomColor = () => {
  // 生成随机十六进制颜色
  return '#' + Math.floor(Math.random()*16777215).toString(16);
};

const colorConfig = (count = 3) => {
  // 创建一个数组并填充指定数量的随机颜色
  const colors = Array(count).fill(null).map(() => getRandomColor());
  return colors;
};
</script>

<style lang="less" scoped>
.detail-list-view {
  color: white;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;

  .total-list {
    //width: 90%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    box-sizing: border-box;
    padding: 8px;

    .list-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 2px 0;
      border-radius: 4px;
      white-space:nowrap;

      img {
        padding: 0 5px;
      }

      span {
        margin: 0 5px;
      }
    }

    .list-item-border{
      border: 1px solid var(--color-neutral-2);
      color: var(--color-neutral-6);
      margin-bottom: 6px;
      background: var(--color-neutral-2);

      .text-bold{
        font-weight: bold;
      }

      .text-bold-white{
        color: #fff;
      }

      &:nth-child(6n + 1),
      &:nth-child(6n - 5) {
        .total-text-active{
          color: #F3921D;
        }
      }

      &:nth-child(6n + 2),
      &:nth-child(6n - 4) {
        .total-text-active{
          color: #0F8DFF;
        }
      }

      &:nth-child(6n + 3),
      &:nth-child(6n - 6) {
        .total-text-active{
          color: #11DACA;
        }
      }

      &:nth-child(6n + 4),
      &:nth-child(6n - 8) {
        .total-text-active{
          color: #FAC61C;
        }
      }
    }

    .list-item-box {
      padding: 0 10px;
    }
  }

  .detail-list-box:first-child {
    padding: 5px;
    font-weight: bold;
    background: url("@/assets/bi/totalBack.png");
  }

  .detail-list-box {
    padding: 2px 5px;
    background: url("@/assets/bi/rectangle3.png");

    .detail-list-item {
      width: 100%;

      span, .arco-col {
        white-space: nowrap;
      }

      .detail-list-item-value {
        text-align: end;
        padding-right: 4px;
      }
    }
  }
}

.color-1 {
  color: #F3921D;
}

.color-2 {
  color: #0F8DFF;
}

.color-3 {
  color: #11DACA;
}

.color-4 {
  color: #FAC61C;
}

.total-text-active{
  font-size: 18px;
  font-weight: bold;
}
</style>
