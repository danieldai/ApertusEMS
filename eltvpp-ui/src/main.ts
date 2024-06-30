import { createApp } from 'vue'
import ArcoVue from '@arco-design/web-vue'
import ArcoVueIcon from '@arco-design/web-vue/es/icon'
import hljsVuePlugin from '@highlightjs/vue-plugin'
import globalComponents from '@/components'
import router from './router'
import store from './store'
import i18n from './locale'
import directive from './directive'
import { useDict } from '@/utils/dict'
// import './mock'
import App from './App.vue'
import '@arco-themes/vue-ykite-front/index.less'
import '@/assets/style/global.less'
import '@/api/interceptor'
import 'highlight.js/styles/atom-one-dark.css'
import 'highlight.js/lib/common'
import DataVVue3 from '@kjgl77/datav-vue3'
import { addDateRange } from '@/utils/ruoyi'

const app = createApp(App)
app.use(ArcoVue, {})
app.use(ArcoVueIcon)
app.use(hljsVuePlugin)
app.use(router)
app.use(store)
app.use(i18n)
app.use(globalComponents)
app.use(directive)
app.use(DataVVue3)
// 全局$t
app.config.globalProperties.$t = i18n.global.t
// 全局方法挂载
app.config.globalProperties.useDict = useDict
app.config.globalProperties.addDateRange = addDateRange
app.mount('#app')

