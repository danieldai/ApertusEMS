<template>
  <a-config-provider :locale="locale">
    <router-view/>
    <global-setting/>
  </a-config-provider>
</template>

<script lang="ts" setup>
import {computed, provide, ref} from 'vue';
import enUS from '@arco-design/web-vue/es/locale/lang/en-us';
import zhCN from '@arco-design/web-vue/es/locale/lang/zh-cn';
import GlobalSetting from '@/components/global-setting/index.vue';
import useLocale from '@/hooks/locale';
const {currentLocale} = useLocale();
import {SocketMessage, useSocket} from '@/api/websocketService'

const locale = computed(() => {
  switch (currentLocale.value) {
    case 'zh-CN':
      return zhCN
    case 'en-US':
      return enUS
    default:
      return enUS
  }
})

const webSocketData = ref<SocketMessage>({
  value: 0
})
const {on, send} = useSocket(import.meta.env.VITE_WEBSOCKET_URL);
/**
 * 收到服务端数据
 */
on('message', (data: any) => {
  webSocketData.value = data;
});
/**
 * 像socket 发送订阅消息
 * @param message
 */
const sendData = (message: { action: string, varSn: any}) => {
  const params = {
    action: message.action,
    bizSn: message.varSn
  }
  send(JSON.stringify(params))
}
provide('webSocketData', {
  data: webSocketData,
  sendData:sendData
});
</script>
