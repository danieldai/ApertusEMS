import type { Router, LocationQueryRaw } from 'vue-router'
import NProgress from 'nprogress' // progress bar

import { useAppStore, useUserStore } from '@/store'
import { isLogin } from '@/utils/auth'

export default function setupUserLoginInfoGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    NProgress.start()
    const userStore = useUserStore();
    if (isLogin()) {
      if (userStore.roles) {
        next()
      } else {
        try {
          await userStore.info();
          next()
        } catch (error) {
          await userStore.logout()
          next({
            name: 'login',
            query: {
              redirect: to.fullPath,
              ...to.query,
            } as LocationQueryRaw,
          })
        }
      }
    } else {
      if (to.name === 'login' || ['bi','biOne','biTwo','biOneOld','biTwoOld'].includes(<string>to.name)) {
        next()
        return
      }
      next({
        name: 'login',
        query: {
          redirect: to.fullPath,
          ...to.query,
        } as LocationQueryRaw,
      })
    }
  })
}
