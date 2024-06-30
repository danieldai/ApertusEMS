import NProgress from "nprogress"; // progress bar
import "nprogress/nprogress.css";
import { createRouter, createWebHistory } from "vue-router";

import createRouteGuard from "./guard";
import { appRoutes } from "./routes";
import { DEFAULT_LAYOUT, NOT_FOUND_ROUTE, REDIRECT_MAIN } from "./routes/base";
NProgress.configure({ showSpinner: false }); // NProgress Configuration

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/login/index.vue"),
      meta: {
        requiresAuth: false,
      },
    },
    {
      path: "/bi",
      name: "bi",
      component: () => import("@/views/bi/index.vue"  ),
      meta: {
        requiresAuth: false,
      },
    },
    {
      path: "/biTwo",
      name: "biTwo",
      component: () => import("@/views/bi/two/index.vue"),
      meta: {
        requiresAuth: false,
      },
    },
    {
      path: "/biTwoOld",
      name: "biTwoOld",
      component: () => import("@/views/biOld/two/index.vue"),
      meta: {
        requiresAuth: false,
      },
    },
    {
      path: "/webtopo/diagram/design/:projectId(\\d+)",
      name: "webtopoDiagramDesign",
      component: () => import("@/views/diagram/design/index.vue"),
      meta: {
        requiresAuth: false,
      },
    },
    {
      path: "/",
      name: "layout",
      component: DEFAULT_LAYOUT,
      // redirect: "/power/energy/preview",
      children: [REDIRECT_MAIN, NOT_FOUND_ROUTE],
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});
createRouteGuard(router);
export default router;
