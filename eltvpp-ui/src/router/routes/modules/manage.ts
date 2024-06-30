import { AppRouteRecordRaw } from "../types";

const MANAGEROUTES: Array<AppRouteRecordRaw> = [
  {
    path: "/manage/system",
    name: "manageSystem",
    component: "",
    meta: {
      locale: "menu.manage.system",
      requiresAuth: true,
      icon: "icon-settings",
      order: 31,
    },
    children: [
      {
        path: "enterprise",
        name: "manageSystemEnterprise",
        component: () => import("@/views/manage/system/enterprise/index.vue"),
        meta: {
          locale: "menu.manage.system.enterprise",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "parameter",
        name: "manageSystemParameter",
        component: () => import("@/views/manage/system/parameter/index.vue"),
        meta: {
          locale: "menu.manage.system.parameter",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "dictionary",
        name: "manageSystemDictionary",
        component: () =>
          import("@/views/manage/system/dictionary/list/index.vue"),
        meta: {
          locale: "menu.manage.system.dictionary",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
      {
        path: "dictionaryValue",
        name: "manageSystemDictionaryValue",
        component: () =>
          import("@/views/manage/system/dictionary/value/index.vue"),
        meta: {
          locale: "menu.manage.system.dictionary.value",
          requiresAuth: true,
          roles: ["*"],
          order: 4,
          hideInMenu: true,
        },
      },
    ],
  }
  ,
  {
    path: "/manage/station",
    name: "manageStation",
    component: "",
    meta: {
      locale: "menu.manage.station",
      requiresAuth: true,
      icon: "icon-storage",
      order: 32,
    },
    children: [
      {
        path: "list",
        name: "manageStationList",
        component: () => import("@/views/manage/station/list/index.vue"),
        meta: {
          locale: "menu.manage.station.list",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "pageConfig",
        name: "manageStationPageConfig",
        component: () => import("@/views/manage/station/pageconfig/index.vue"),
        meta: {
          locale: "menu.manage.station.pageconfig",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "largeScreenConfig",
        name: "manageStationLargeScreenConfig",
        component: () => import("@/views/manage/station/large-screen-config/index.vue"),
        meta: {
          locale: "menu.manage.station.largescreenconfig",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "largeScreenDetail",
        name: "manageStationLargeScreenDetail",
        component: () => import("@/views/manage/station/large-screen-detail/index.vue"),
        meta: {
          locale: "menu.manage.station.largescreendetail",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "dataList",
        name: "manageStationDataList",
        component: () => import("@/views/manage/station/data-list/index.vue"),
        meta: {
          locale: "menu.manage.station.datalist",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "scheme",
        name: "manageStationScheme",
        component: () => import("@/views/manage/station/scheme/index.vue"),
        meta: {
          locale: "menu.manage.station.scheme",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
    ],
  },
  {
    path: "/manage/account",
    name: "manageAccount",
    component: "",
    meta: {
      locale: "menu.manage.account",
      requiresAuth: true,
      icon: "icon-user-group",
      order: 34,
    },
    children: [
      {
        path: "menu",
        name: "manageAccountMenu",
        component: () => import("@/views/manage/account/menu/index.vue"),
        meta: {
          locale: "menu.manage.account.menu",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "enterprise",
        name: "manageAccountEnterprise",
        component: () => import("@/views/manage/account/menu/enterprise/index.vue"),
        meta: {
          locale: "menu.manage.account.menu.enterprise",
          requiresAuth: true,
          hideInMenu:true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "authority",
        name: "manageAccountAuthority",
        component: () => import("@/views/manage/account/authority/index.vue"),
        meta: {
          locale: "menu.manage.account.authority",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "role",
        name: "manageAccountRole",
        component: () => import("@/views/manage/account/role/index.vue"),
        meta: {
          locale: "menu.manage.account.role",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
      {
        path: "post",
        name: "manageAccountPost",
        component: () => import("@/views/manage/account/post/index.vue"),
        meta: {
          locale: "menu.manage.account.post",
          requiresAuth: true,
          roles: ["*"],
          order: 4,
        },
      },
      {
        path: "user",
        name: "manageAccountUser",
        component: () => import("@/views/manage/account/user/index.vue"),
        meta: {
          locale: "menu.manage.account.user",
          requiresAuth: true,
          roles: ["*"],
          order: 5,
        },
      },
    ],
  },
  {
    path: "/manage/company",
    name: "manageCompany",
    component: "",
    meta: {
      locale: "menu.manage.company",
      requiresAuth: true,
      icon: "icon-mind-mapping",
      order: 34,
    },
    children: [
      {
        path: "list",
        name: "manageCompanyList",
        component: () => import("@/views/manage/company/list/index.vue"),
        meta: {
          locale: "menu.manage.company.list",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      }
    ]
  },
  {
    path: "/manage/interface",
    name: "manageInterface",
    component: "",
    meta: {
      locale: "menu.manage.interface",
      requiresAuth: true,
      icon: "icon-codepen",
      order: 35,
    },
    children: [
      {
        path: "list",
        name: "manageInterfaceList",
        component: () => import("@/views/manage/interface/list/index.vue"),
        meta: {
          locale: "menu.manage.interface.list",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "apply",
        name: "manageInterfaceApply",
        component: () => import("@/views/manage/interface/apply/index.vue"),
        meta: {
          locale: "menu.manage.interface.apply",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      }
    ],
  },
  {
    path: "/manage/notice",
    name: "manageNotice",
    component: "",
    meta: {
      locale: "menu.manage.notice",
      requiresAuth: true,
      icon: "icon-bulb",
      order: 36,
    },
    children: [
      {
        path: "list",
        name: "manageNoticeList",
        component: () => import("@/views/manage/notice/list/index.vue"),
        meta: {
          locale: "menu.manage.notice.list",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      }
    ],
  },
  {
    path: "/manage/message",
    name: "manageMessage",
    component: "",
    meta: {
      locale: "menu.manage.message",
      requiresAuth: true,
      icon: "icon-message",
      order: 37,
    },
    children: [
      {
        path: "template",
        name: "manageMessageTemplate",
        component: () => import("@/views/manage/message/template/index.vue"),
        meta: {
          locale: "menu.manage.message.template",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "list",
        name: "manageMessageList",
        component: () => import("@/views/manage/message/list/index.vue"),
        meta: {
          locale: "menu.manage.message.list",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      }
    ],
  },
  {
    path: "/manage/channel",
    name: "manageChannel",
    component: "",
    meta: {
      locale: "menu.manage.channel",
      requiresAuth: true,
      icon: "icon-filter",
      order: 33,
    },
    children: [
      {
        path: "list",
        name: "manageChannelList",
        component: () => import("@/views/manage/channel/list/index.vue"),
        meta: {
          locale: "menu.manage.channel.list",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "device",
        name: "manageChannelDevice",
        component: () => import("@/views/manage/channel/device/index.vue"),
        meta: {
          locale: "menu.manage.channel.device",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
    ],
  },
  {
    path: "/manage/logs",
    name: "manageLogs",
    component: "",
    meta: {
      locale: "menu.manage.logs",
      requiresAuth: true,
      icon: "icon-bookmark",
      order: 38,
    },
    children: [
      {
        path: "login",
        name: "manageLogsLogin",
        component: () => import("@/views/manage/logs/login/index.vue"),
        meta: {
          locale: "menu.manage.logs.login",
          requiresAuth: true,
          roles: ["*"],
          order: 1,
        },
      },
      {
        path: "operate",
        name: "manageLogsOperate",
        component: () => import("@/views/manage/logs/operate/index.vue"),
        meta: {
          locale: "menu.manage.logs.operate",
          requiresAuth: true,
          roles: ["*"],
          order: 2,
        },
      },
      {
        path: "execute",
        name: "manageLogsExecute",
        component: () => import("@/views/manage/logs/execute/index.vue"),
        meta: {
          locale: "menu.manage.logs.execute",
          requiresAuth: true,
          roles: ["*"],
          order: 3,
        },
      },
      {
        path: "system",
        name: "manageLogsSystem",
        component: () => import("@/views/manage/logs/system/index.vue"),
        meta: {
          locale: "menu.manage.logs.system",
          requiresAuth: true,
          roles: ["*"],
          order: 4,
        },
      },
    ],
  },
];

export default MANAGEROUTES;
