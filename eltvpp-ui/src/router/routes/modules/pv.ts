import { AppRouteRecordRaw } from "../types";

const PVROUTES: Array<AppRouteRecordRaw> = [
  {
    path: "/pv/preview",
    name: "pvPreview",
    component: () => import("@/views/pv/preview/index.vue"),
    meta: {
      locale: "menu.pv.preview",
      requiresAuth: true,
      icon: "icon-command",
      order: 21,
    },
  },
  {
    path: "/pv/device",
    name: "pvDevice",
    component: "",
    meta: {
      locale: "menu.pv.device",
      requiresAuth: true,
      icon: "icon-storage",
      order: 22,
    },
    children: [
      {
        path: "type",
        name: "pvDeviceType",
        props: { mapId: 4 },
        component: () => import("@/components/system-group/index.vue"),
        meta: {
          locale: "menu.pv.device.type",
          requiresAuth: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "group",
        name: "pvDeviceGroup",
        props: { mapId: 3 },
        component: () => import("@/components/system-group/index.vue"),
        meta: {
          locale: "menu.pv.device.group",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "list",
        name: "pvDeviceList",
        component: () => import("@/views/pv/device/list/index.vue"),
        meta: {
          locale: "menu.pv.device.list",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "create",
        name: "pvDeviceCreate",
        component: () => import("@/components/system-device-operate/index.vue"),
        meta: {
          locale: "menu.pv.device.create",
          requiresAuth: true,
          hideInMenu: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "update",
        name: "pvDeviceUpdate",
        component: () => import("@/components/system-device-operate/index.vue"),
        meta: {
          locale: "menu.pv.device.update",
          requiresAuth: true,
          hideInMenu: true,
          roles: ["*"],
          order: 0,
        },
      },
      {
        path: "listVar",
        name: "pvDeviceListVar",
        props: { stationType: 2 },
        component: () =>
          import("@/components/system-device-var-list/index.vue"),
        meta: {
          locale: "menu.pv.device.list.var",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "varCreate",
        name: "pvDeviceVarCreate",
        props: { stationType: 2 },
        component: () =>
          import("@/components/system-device-var-operate/index.vue"),
        meta: {
          locale: "menu.pv.device.list.var.create",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
          hideInMenu: true,
        },
      },
      {
        path: "varUpdate",
        name: "pvDeviceVarUpdate",
        props: { stationType: 2 },
        component: () =>
          import("@/components/system-device-var-operate/index.vue"),
        meta: {
          locale: "menu.pv.device.list.var.update",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
          hideInMenu: true,
        },
      },
    ],
  },
  {
    path: "/pv/generation",
    name: "pvGeneration",
    component: "",
    meta: {
      locale: "menu.pv.generation",
      requiresAuth: true,
      icon: "icon-sun",
      order: 23,
    },
    children: [
      {
        path: "analysis",
        name: "pvGenerationAnalysis",
        component: () => import("@/views/pv/generation/analysis/index.vue"),
        meta: {
          locale: "menu.pv.generation.analysis",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "trend",
        name: "pvGenerationTrend",
        component: () => import("@/views/pv/generation/trend/index.vue"),
        meta: {
          locale: "menu.pv.generation.trend",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "auxiliary",
        name: "pvGenerationAuxiliary",
        component: () => import("@/views/pv/generation/auxiliary/index.vue"),
        meta: {
          locale: "menu.pv.generation.auxiliary",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      // ,{
      //   path: "history",
      //   name: "pvGenerationHistory",
      //   component: () => import("@/views/pv/generation/history/index.vue"),
      //   meta: {
      //     locale: "menu.pv.generation.history",
      //     requiresAuth: true,
      //     roles: ["*"],
      //     order: 3,
      //   },
      // }
      {
        path: "basic",
        name: "pvGenerationBasic",
        component: () => import("@/views/pv/generation/basic/index.vue"),
        meta: {
          locale: "menu.pv.generation.basic",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
    ],
  },
  {
    path: "/pv/alarm",
    name: "pvAlarm",
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
        name: "pvAlarmCategory",
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
        name: "pvAlarmSetting",
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
        name: "pvAlarmList",
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
        name: "pvAlarmStatistics",
        component: () => import("@/views/pv/alarm/statistics/index.vue"),
        meta: {
          locale: "menu.pv.alarm.statistics",
          requiresAuth: true,
          roles: ["*"],
          order: 4,
        },
      },
    ],
  },
  // {
  //   path: "/pv/report",
  //   name: "pvReport",
  //   component: "",
  //   meta: {
  //     locale: "menu.pv.report",
  //     requiresAuth: true,
  //     icon: "icon-mind-mapping",
  //     order: 25,
  //   },
  //   children: [
  //     {
  //       path: "basic",
  //       name: "pvReportBasic",
  //       component: () => import("@/views/pv/report/basic/index.vue"),
  //       meta: {
  //         locale: "menu.pv.report.basic",
  //         requiresAuth: true,
  //         roles: ["*"],
  //         order: 1,
  //       },
  //     },
  //   ],
  // },
  {
    path: "/pv/maintenance",
    name: "pvMaintenance",
    component: "",
    meta: {
      locale: "menu.pv.maintenance",
      requiresAuth: true,
      icon: "icon-settings",
      order: 26,
    },
    children: [
      {
        path: "list",
        name: "pvMaintenanceList",
        component: () => import("@/views/pv/maintenance/list/index.vue"),
        meta: {
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "plan",
        name: "pvMaintenancePlan",
        component: () => import("@/views/pv/maintenance/plan/index.vue"),
        meta: {
          locale: "menu.pv.maintenance.plan",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "record",
        name: "pvMaintenanceRecord",
        component: () => import("@/views/pv/maintenance/record/index.vue"),
        meta: {
          locale: "menu.pv.maintenance.record",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
      // {
      //   path: "demo",
      //   name: "pvMaintenanceDemo",
      //   component: () => import("@/views/pv/maintenance/demo.vue"),
      //   meta: {
      //     locale: "menu.pv.maintenance.demo",
      //     requiresAuth: true,
      //     roles: ["*"],
      //     order: 4,
      //   },
      // },
      // {
      //   path: "public",
      //   name: "pvMaintenancePublic",
      //   component: () => import("@/views/pv/maintenance/public.vue"),
      //   meta: {
      //     locale: "menu.pv.maintenance.public",
      //     requiresAuth: true,
      //     roles: ["*"],
      //     order: 4,
      //   },
      // },
    ],
  },
];

export default PVROUTES;
