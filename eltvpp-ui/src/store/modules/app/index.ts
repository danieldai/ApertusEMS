import { defineStore } from "pinia";
import { Notification } from "@arco-design/web-vue";
import type { NotificationReturn } from "@arco-design/web-vue/es/notification/interface";
import { type RouteRecordNormalized } from "vue-router";
import defaultSettings from "@/config/settings.json";
import { getMenuList } from "@/api/user";
import { AppState } from "./types";
import router from "@/router";
import { NOT_FOUND_ROUTE, REDIRECT_MAIN } from "@/router/routes/base";
import { findFirstItemPath } from "@/utils/ruoyi";
import { h } from "vue";
const DEFAULT_LAYOUT = () => import("@/layout/default-layout.vue");
//业务页面
const viewsModules = import.meta.glob("../../../views/**/*.vue");
//公共组件页面
const componentsModules = import.meta.glob("../../../components/**/*.vue");
//所有页面
const modules = { ...viewsModules, ...componentsModules };

const ModalMenuContent = {
  setup() {
    return () =>
      h("div", { class: "info-modal-content" }, [
        h(
          "span",
          { style: "margin-bottom: 10px;" },
          "暂无权限，请联系管理员！"
        ),
      ]);
  },
};
const useAppStore = defineStore("app", {
  persist: false,
  state: (): AppState => ({ ...defaultSettings }),
  getters: {
    appCurrentSetting(state: AppState): AppState {
      return { ...state };
    },
    appDevice(state: AppState) {
      return state.device;
    },
    appAsyncMenus(state: AppState): RouteRecordNormalized[] {
      return state.serverMenu as unknown as RouteRecordNormalized[];
    },
    appAsyncMenusAll(state: AppState): RouteRecordNormalized[] {
      return state.serverMenuAll as unknown as RouteRecordNormalized[];
    },
    appAsyncFatherMenu(state: AppState) {
      return state.fatherMenu;
    },
  },

  actions: {
    // Update app settings
    updateSettings(partial: Partial<AppState>) {
      // @ts-ignore-next-line
      this.$patch(partial);
    },

    // Change theme color
    toggleTheme(dark: boolean) {
      if (dark) {
        this.theme = "dark";
        document.body.setAttribute("arco-theme", "dark");
      } else {
        this.theme = "light";
        document.body.removeAttribute("arco-theme");
      }
    },
    toggleDevice(device: string) {
      this.device = device;
    },
    toggleMenu(value: boolean) {
      this.hideMenu = value;
    },
    // 获取父菜单
    async fetchFatherMenuConfig() {
      if (!this.menuFromServer) {
        return;
      }
      let notifyInstance: NotificationReturn | null = null;
      try {
        await this.fetchChildMenuConfig();
      } catch (error) {
        notifyInstance = Notification.error({
          id: "menuNotice",
          content: "菜单获取失败",
          closable: true,
        });
        router.push("/login");
      }
    },
    clearFatherMenu() {
      this.fatherMenu = [];
    },
    async fetchChildMenuConfig() {
      let notifyInstance: NotificationReturn | null = null;
      try {
        const { data } = await getMenuList();
        let fMenu: any[] = [];
        let allMenu: any = [];
        data.forEach((item) => {
          allMenu = allMenu.concat(item.children);
          fMenu.push({
            name: item.meta.locale,
            icon: item.meta.icon,
            path: item.path,
          });
        });
        this.fatherMenu = fMenu;
        this.serverMenuAll = data;
        const s_data = JSON.parse(JSON.stringify(allMenu));
        const filterData = filterAsyncRouter(s_data);
        let baseRouter = {
          path: "/",
          name: "layout",
          component: DEFAULT_LAYOUT,
          redirect: findFirstItemPath(filterData, ""),
          children: [...filterData, REDIRECT_MAIN, NOT_FOUND_ROUTE],
        };
        router.addRoute(baseRouter);
        if (this.fatherMenu && this.fatherMenu.length > 0) {
          let sign = this.lastSign || this.fatherMenu[0].path;
          this.switchMenu(sign);
        }
      } catch (error) {
        notifyInstance = Notification.error({
          id: "menuNotice",
          content: "获取子菜单异常",
          closable: true,
        });
      }
    },
    switchMenu(sign: string) {
      if (this.fatherMenu && this.fatherMenu.length > 0) {
        if (sign && sign.length > 0) {
          this.fatherMenu.forEach((item, index) => {
            if (item.path == sign) {
              this.lastIndex = index;
            }
          });
        } else {
          this.lastIndex = 0;
        }
        this.serverMenu = this.serverMenuAll[this.lastIndex].children;
        this.lastSign = sign;
      }
    },
    clearServerMenu() {
      this.serverMenu = [];
      this.serverMenuAll = [];
      this.lastIndex = 0;
      this.lastSign = "";
      this.$reset();
    },
  },
});

/**
 * 过滤路由
 * @param asyncRouterMap 路由文件
 * @returns
 */
const filterAsyncRouter = (asyncRouterMap: any) => {
  return asyncRouterMap.filter((route: any) => {
    route.component = loadView(route.component);
    route.props = loadProps(route.props);
    route.meta.requiresAuth = true;
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children);
    }
    return true;
  });
};

/***
 * 加载视图
 */
const reg = "../../..";
const loadView = (view: string) => {
  if (view && view.length > 0) {
    let res;
    for (const path in modules) {
      if (path.split(reg)[1] === view) {
        res = () => modules[path]();
      }
    }
    return res;
  } else {
    return "";
  }
};
const loadProps = (props: string) => {
  return props && props.length > 0 ? JSON.parse(props) : undefined;
};
export default useAppStore;
