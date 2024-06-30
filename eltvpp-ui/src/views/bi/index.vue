<template>
  <biTemplate v-if="pageInfo.pageTemplate == '2'" :pageInfo="pageInfo" />
  <biTemplate2 v-if="pageInfo.pageTemplate == '1'" :pageInfo="pageInfo" />
</template>

<script setup lang="ts">
import biTemplate from '@/views/bi/one/index.vue'
import biTemplate2 from '@/views/biOld/one/index.vue'
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import {getBiInfo} from "@/api/manage/station";
interface IRouteQuery {
  stationId: string,
  isPre: string,
}
const route:any = useRoute();
const { stationId,isPre }:IRouteQuery = route.query;

const pageInfo = ref<any>({
  pageTemplate: '0',
  cardList:{
    bottomList: [],
    leftTop: [],
    rightTop: []
  }
})

const getInfo = async () => {
  try {
    const res = await getBiInfo({stationId, isPre});
    console.log('res',res)
    if (res.code === 200) {
      pageInfo.value = {
        ...res.data
      };
    }

  } catch (e) {
    console.log(e,'大屏模板报错')
  }
}

onMounted(() => {
  getInfo()
})
</script>

<style scoped>

</style>
