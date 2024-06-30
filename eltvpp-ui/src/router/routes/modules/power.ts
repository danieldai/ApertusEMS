import { AppRouteRecordRaw } from "../types";

const POWERROUTES: Array<AppRouteRecordRaw> = [
  {
    path: "/power/energy/preview",
    name: "powerEnergyPreview",
    component: () => import("@/views/power/energy/preview/index.vue"),
    meta: {
      locale: "menu.power.energy.preview",
      requiresAuth: true,
      icon: "icon-command",
      order: 10,
    },
  },
  {
    path: "/power/energy/preview-two",
    name: "powerEnergyPreviewTwo",
    component: () => import("@/views/power/energy/preview-two/index.vue"),
    meta: {
      locale: "menu.power.energy.preview-two",
      requiresAuth: true,
      icon: "icon-command",
      order: 10,
    },
  },
  {
    path: "/power/energy/search",
    name: "powerEnergySearch",
    component: "",
    meta: {
      locale: "menu.power.energy.search",
      requiresAuth: true,
      icon: "icon-find-replace",
      order: 11,
    },
    children: [
      {
        path: "diagram",
        name: "powerEnergySearchDiagram",
        component: () =>
          import("@/views/power/energy/search/diagram/index.vue"),
        meta: {
          locale: "menu.power.energy.search.diagram",
          requiresAuth: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "realtime",
        name: "powerEnergySearchRealtime",
        component: () =>
          import("@/views/power/energy/search/realtime/index.vue"),
        meta: {
          locale: "menu.power.energy.search.realtime",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "history",
        name: "powerEnergySearchHistory",
        component: () =>
          import("@/views/power/energy/search/history/index.vue"),
        meta: {
          locale: "menu.power.energy.search.history",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "pvcr",
        name: "powerEnergySearchPvcr",
        component: () => import("@/views/power/energy/search/pvcr/index.vue"),
        meta: {
          locale: "menu.power.energy.search.pvcr",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
      {
        path: "basic",
        name: "powerEnergySearchBasic",
        component: () => import("@/views/power/energy/search/basic/index.vue"),
        meta: {
          locale: "menu.power.energy.search.basic",
          requiresAuth: true,
          roles: ["*"],
          order: 4,
        },
      },
    ],
  },
  {
    path: "/power/energy/analysis",
    name: "powerEnergyAnalysis",
    component: "",
    meta: {
      locale: "menu.power.energy.analysis",
      requiresAuth: true,
      icon: "icon-computer",
      order: 12,
    },
    children: [
      {
        path: "peak",
        name: "powerEnergyAnalysisPeak",
        component: () => import("@/views/power/energy/analysis/peak/index.vue"),
        meta: {
          locale: "menu.power.energy.analysis.peak",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "electricity",
        name: "powerEnergyAnalysisElectricity",
        component: () =>
          import("@/views/power/energy/analysis/electricity/index.vue"),
        meta: {
          locale: "menu.power.energy.analysis.electricity",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      // {
      //   path: "loadforecast",
      //   name: "powerEnergyAnalysisLoadforecast",
      //   component: () =>
      //     import("@/views/power/energy/analysis/loadforecast/index.vue"),
      //   meta: {
      //     locale: "menu.power.energy.analysis.loadforecast",
      //     requiresAuth: true,
      //     roles: ["*"],
      //     order: 3,
      //   },
      // },
    ],
  },
  {
    path: "/power/energy/flow",
    name: "powerEnergyFlow",
    component: () => import("@/views/power/energy/flow/index.vue"),
    meta: {
      locale: "menu.power.energy.flow",
      requiresAuth: true,
      icon: "icon-swap",
      order: 13,
    },
  },
  {
    path: "/power/energy/quality",
    name: "powerEnergyQuality",
    component: () => import("@/views/power/energy/quality/index.vue"),
    meta: {
      locale: "menu.power.energy.quality",
      requiresAuth: true,
      icon: "icon-thunderbolt",
      order: 14,
    },
  },
  // {
  //   path: "/power/energy/report",
  //   name: "energyReport",
  //   component: '',
  //   meta: {
  //     locale: "menu.power.energy.report",
  //     requiresAuth: true,
  //     icon: "icon-mind-mapping",
  //     order: 15,
  //   },
  //   children: [
  //     {
  //       path: "basic",
  //       name: "energyReportBasic",
  //       component: () => import("@/views/power/report/basic/index.vue"),
  //       meta: {
  //         locale: "menu.power.energy.report.basic",
  //         requiresAuth: true,
  //         roles: ["*"],
  //         order: 1,
  //       },
  //     }]
  // },
  {
    path: "/power/energy/device",
    name: "powerEnergyDevice",
    component: "",
    meta: {
      locale: "menu.power.energy.device",
      requiresAuth: true,
      icon: "icon-storage",
      order: 16,
    },
    children: [
      {
        path: "type",
        name: "powerEnergyDeviceType",
        props: { mapId: 4 },
        component: () => import("@/components/system-group/index.vue"),
        meta: {
          locale: "menu.power.energy.device.type",
          requiresAuth: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "group",
        name: "powerEnergyDeviceGroup",
        props: { mapId: 2 },
        component: () => import("@/components/system-group/index.vue"),
        meta: {
          locale: "menu.power.energy.device.group",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "list",
        name: "powerEnergyDeviceList",
        component: () => import("@/views/power/energy/device/list/index.vue"),
        meta: {
          locale: "menu.power.energy.device.list",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "create",
        name: "powerEnergyDeviceCreate",
        component: () => import("@/components/system-device-operate/index.vue"),
        meta: {
          locale: "menu.power.energy.device.create",
          requiresAuth: true,
          hideInMenu: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "update",
        name: "powerEnergyDeviceUpdate",
        component: () => import("@/components/system-device-operate/index.vue"),
        meta: {
          locale: "menu.power.energy.device.update",
          requiresAuth: true,
          hideInMenu: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "listVar",
        name: "powerEnergyDeviceListVar",
        props: { stationType: 1 },
        component: () => import("@/components/system-device-var-list/index.vue"),
        meta: {
          locale: "menu.power.energy.device.list.var",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "varCreate",
        name: "powerEnergyDeviceVarCreate",
        props: { stationType: 1 },
        component: () => import("@/components/system-device-var-operate/index.vue"),
        meta: {
          locale: "menu.power.energy.device.list.var.create",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
          hideInMenu:true,
        },
      },
      {
        path: "varUpdate",
        name: "powerEnergyDeviceVarUpdate",
        props: { stationType: 2 },
        component: () => import("@/components/system-device-var-operate/index.vue"),
        meta: {
          locale: "menu.power.energy.device.list.var.update",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
          hideInMenu:true,
        },
      },
    ],
  },
  {
    path: "/power/alarm",
    name: "powerAlarm",
    component: "",
    meta: {
      locale: "menu.pv.alarm",
      requiresAuth: true,
      icon: "icon-dashboard",
      order: 24,
    },
    children: [
      {
        path: "category",
        name: "powerAlarmCategory",
        component: () => import("@/views/pv/alarm/category/index.vue"),
        meta: {
          locale: "menu.pv.alarm.category",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "setting",
        name: "powerAlarmSetting",
        component: () => import("@/views/pv/alarm/setting/index.vue"),
        meta: {
          locale: "menu.pv.alarm.setting",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "list",
        name: "powerAlarmList",
        component: () => import("@/views/pv/alarm/list/index.vue"),
        meta: {
          locale: "menu.pv.alarm.list",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
      {
        path: "statistics",
        name: "powerAlarmStatistics",
        component: () => import("@/views/pv/alarm/statistics/index.vue"),
        meta: {
          locale: "menu.pv.alarm.statistics",
          requiresAuth: true,
          roles: ["*"],
          order: 4,
        },
      },
    ],
  }
];

export default POWERROUTES;
