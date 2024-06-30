<template>
  <a-row class="header" display="flex" align="center">
    <a-col :span="6">
      <div class="head-date" v-if="headerData.date">
        {{ getCurrentDateTime }}
      </div>
    </a-col>
    <a-col :span="12">
      <div class="head-title">
        {{ title || '阿帕图能源管理中心' }}
      </div>
    </a-col>
    <a-col :span="6">
      <div class="head-weather" v-if="headerData.weather">
        <img src="@/assets/bi/icon-weather.png" alt="">
        {{ currentWeather.text }}
        <img src="@/assets/bi/icon-temperature.png" alt="">
        {{ currentWeather.temp }}
        <img src="@/assets/bi/icon-humidness.png" alt="">
        {{ currentWeather.humidity }}
        <span>{{ currentTime || '' }}</span>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import dayjs from "dayjs";
import {getWeather} from '@/api/public';
import {useIntervalFn} from '@vueuse/core';

const props = defineProps({
  headerData: {
    type: Object,
    default: () => {
      return {
        date: '',
        weather: '',
        network: false
      }
    }
  },
  title: {
    type: String,
    default: ''
  }
});
const currentWeather = ref({
  text: '',
  temp: '',
  humidity: ''
});
const currentTime = ref('');

/**
 * @desc 获取当前时间 YYYY年MM月DD日
 */
const getCurrentDateTime = computed(() => {
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const day = dayjs();
  const curTime = day.format('YYYY年MM月DD日');
  const week = Number(day.format('d'));
  return `${curTime} ${weekDays[week]}`
})

/**
 * 获取天气
 */
const fetchWeather = async () => {
  let res: any = await getWeather("122.13,37.42");
  if (res.code == "200") {
    // currentWeather.value = `${res.now.text} ${res.now.temp}℃ ${res.now.humidity}%`
    currentWeather.value = {
      text: res.now.text,
      temp: res.now.temp + '℃',
      humidity: res.now.humidity + '%'
    };
  }
}

onMounted(() => {
  if (props.headerData.weather) {
    /**
     * @desc 每秒执行一次 获取当前时分秒
     */
    useIntervalFn(() => {
      currentTime.value = dayjs().format('HH:mm:ss');
    }, 1000);
    fetchWeather();
  }
})
</script>

<style lang="less" scoped>
@font-size: 16px;
@font-size-mini: 14px;
@font-size-medium: 26px;
@font-size-large: 20px;
@color-w: white;

.header {
  height: 100%;
  display: flex;
  align-items: center;
}

.head-date {
  font-size: @font-size-medium;
  letter-spacing: 1px;
  //padding-left: 70px;
  padding-left: 38px;
  padding-bottom: 30px;
  line-height: 40px;
  box-sizing: border-box;
}

.head-title {
  font-size: 50px;
  text-align: center;
}

.head-weather {
  font-size: @font-size-medium;
  //padding-right: 70px;
  padding-right: 38px;
  padding-bottom: 30px;
  line-height: 50px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-sizing: border-box;

  img {
    margin-left: 10px;
    margin-right: 2px;
    width: 32px;

    &:nth-child(2) {
      width: 16px;
    }

    &:nth-child(3) {
      width: 17px;
    }

  }

  span {
    margin-left: 14px;
  }
}
</style>
