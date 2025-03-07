import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import useAppStore from './modules/app'
import useUserStore from './modules/user'
import useTabBarStore from './modules/tab-bar'
import useCompanyStore from './modules/company'
import useDictStore from './modules/dict'
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

export { useAppStore, useUserStore, useTabBarStore,useCompanyStore,useDictStore }
export default pinia
