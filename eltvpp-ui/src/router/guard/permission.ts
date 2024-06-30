import type {Router, RouteRecordNormalized} from "vue-router";
import NProgress from "nprogress"; // progress bar

import usePermission from "@/hooks/permission";
import {useUserStore, useAppStore} from "@/store";
import {appRoutes} from "../routes";
import {WHITE_LIST, NOT_FOUND} from "../constants";
import {isLogin, clearToken} from "@/utils/auth";

export default function setupPermissionGuard(router: Router) {
	router.beforeEach(async (to, from, next) => {
		const appStore = useAppStore();
		const userStore = useUserStore();
		const Permission = usePermission();
		const permissionsAllow = Permission.accessRouter(to);

		// 检查是否需要从服务器获取菜单
		if (appStore.menuFromServer) {
			// 如果异步菜单尚未加载
			if (!appStore.appAsyncMenus.length) {
				if (isLogin() && to.name !== "login") {
					userStore.info();
					await appStore.fetchFatherMenuConfig();

					let sign = false;
					let info: any = {};

					// 直接允许特定路由导航
					if (to.name === "webtopoDiagramDesign") {
						next();
						NProgress.done();
						return;
					} else {
						// 检查路由是否存在
						router.getRoutes().forEach((item: any) => {
							if (item.path === to.path) {
								sign = true;
								info = item;
							}
						});

						if (sign) {
							appStore.switchMenu("/" + info.path.split("/")[1]);
							next({ name: info.name, replace: true, query: to.query });
							NProgress.done();
							return;
						} else {
							appStore.switchMenu("");
							next({ path: "/", replace: true });
							NProgress.done();
							return;
						}
					}
				}
			}

			// 检查路由是否存在于服务器菜单配置中
			const serverMenuConfig = [...appStore.appAsyncMenusAll, ...WHITE_LIST];
			let exist = false;
			while (serverMenuConfig.length && !exist) {
				const element = serverMenuConfig.shift();
				if (element?.name === to.name) exist = true;

				if (element?.children) {
					serverMenuConfig.push(...(element.children as unknown as RouteRecordNormalized[]));
				}
			}

			if (exist && permissionsAllow) {
				next();
				NProgress.done();
				return;
			} else {
				next(NOT_FOUND);
				NProgress.done();
				return;
			}
		} else {
			// 如果不需要从服务器获取菜单
			if (permissionsAllow) {
				next();
				NProgress.done();
				return;
			} else {
				const destination = Permission.findFirstPermissionRoute(appRoutes, userStore.role) || NOT_FOUND;
				next(destination);
				NProgress.done();
				return;
			}
		}
	});

}
