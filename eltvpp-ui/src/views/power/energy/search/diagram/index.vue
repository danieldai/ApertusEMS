<template>
  <a-spin v-bind:loading="loading" style="display: block;width: 100%;height:100vh" :tip="$t('global.loading')">
<!--    <a-spin v-bind:loading="loading" style="display: block;width: 100%;" :tip="$t('global.loading')">-->

        <a-card ref="containerRef" class="content diagram-content" :style="'height:' + height">
            <a-tabs id="diagram-content" position="top" size="large">
                <a-tab-pane class="diagram-content-tab-pane" v-for="item in renderData" :key="item.id"
                    :title="item.projectName">
                    <iframe :src="`${YKITE_DIAGRA}/diagram/preview/${item.id}/${token}`" frameborder="no"
                        style="width:100%;height: calc(100vh - 80px)"></iframe>
                </a-tab-pane>
                <template #extra>
                    <div style="margin-right: 20px;">
                        <a-button type="primary" @click="toggle">进入/退出全屏</a-button>
                    </div>
                </template>
            </a-tabs>
        </a-card>
    <a-space style="margin-top: 10px">
      <a-button type="primary" @click="handleListConfig">配置</a-button>
    </a-space>
    </a-spin>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue"
import useLoading from '@/hooks/loading';
import {
    listWebtopoProject, getWebtopoProject
} from "@/api/system/webtopo";
import { StationTypeEnum } from "@/api/system/device";
import { getToken } from "@/utils/auth";
import {useRouter} from 'vue-router';
const containerRef = ref();
const YKITE_DIAGRA = import.meta.env.VITE_YKITE_DIAGRA_URL;
//加载中
const { loading, setLoading } = useLoading(true);
/*************************** 变量区域 begin ***************************/
//组件参数
const props = defineProps({
    stationType: {
        type: Number,
        default: StationTypeEnum.power,
    }
})
//表格数据
const renderData = ref<any[]>([]);
if (document.querySelector(".arco-layout-header")) { }
const arcoTabsNav = document.querySelector(".arcoTabsNav");
const width = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) - 255 - 15 + "px;";
const height = (window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) - 60 - arcoTabsNav?.offsetHeight + "px;";
const token = getToken();
const style = `width: ${width}; height: ${height};scrolling='auto'`;
//路由
const router = useRouter();

/*************************** 变量区域 end ***************************/

/*************************** 方法区域 begin ***************************/

/**
 * 获取数据
 */
const fetchData = async () => {
    setLoading(true);
    try {
        let res = await listWebtopoProject({
            pageSize: 1000,
            pageNum: 1,
            stationType: props.stationType
        });
        if (res.code == 200) {
            renderData.value = res.rows;
        }
    } catch (err) {
    } finally {
        setLoading(false);
    }
}

const handleListConfig = () => {
  if(props.stationType==1){
    router.push({
      path: "/power/energy/search/diagram/list",
    });
  }else if(props.stationType==2){
    router.push({
      path: "/pv/generation/diagram/list",
    });
  }
}


/*************************** 方法区域 end ***************************/

onMounted(async () => {
    await fetchData();
})


function enter(ele: any) {
    if (ele.requestFullscreen) {
        ele.requestFullscreen();
    } else if (ele.webkitRequestFullscreen) {
        ele.webkitRequestFullscreen();
    } else if (ele.mozRequestFullScreen) {
        ele.mozRequestFullScreen();
    } else if (ele.msRequestFullscreen) {
        ele.msRequestFullscreen();
    }
}

function exit() {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
    }
}
function fullEle() {
    return (
        document.fullscreenElement ||
        document.mozFullscreenElement ||
        document.msFullscreenElement ||
        document.webkitFullscreenElement ||
        null
    )
}
function isFull() {
    return !!fullEle();
}
function toggle(ele: any) {
    isFull() ? exit() : enter(document.querySelector("#diagram-content"));
}
</script>

<style lang="scss" scoped></style>
