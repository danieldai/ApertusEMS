import { defineStore } from "pinia";
import {
  login as userLogin,
  logout as userLogout,
  getUserInfo,
  LoginData,
} from "@/api/user";
import { setToken, clearToken, getToken } from "@/utils/auth";
import { removeRouteListener } from "@/utils/route-listener";
import { UserInfo, UserState } from "./types";
import useAppStore from "../app";
import useDictStore from "../dict";

const useUserStore = defineStore("user", {
  state: (): UserState => ({
    token: getToken() || "",
    user: {} as UserInfo,
    roles: [],
    permissions: [],
  }),

  getters: {
    userInfo(state: UserState): UserState {
      return { ...state };
    },
  },

  actions: {
    switchRoles() {
      return new Promise((resolve) => {
        resolve(null);
      });
    },
    setInfo(partial: Partial<UserState>) {
      this.$patch(partial);
    },

    // Reset user's information
    resetInfo() {
      this.$reset();
    },

    // Get user's information
    async info() {
      const res = await getUserInfo();
      if (res.code == 200) {
        console.log(res.user,'userId')
        this.setInfo({
          permissions: res.permissions,
          roles: res.roles,
          user: res.user,
        });
      } else {
        await this.logout();
      }
    },

    // Login
    async login(loginForm: LoginData) {
      try {
        const res: any = await userLogin(loginForm);
        if(res.code != 200){
          throw new Error(res.msg);
        }else{
          setToken(res.data.access_token);
        }
      } catch (err) {
        clearToken();
        throw err;
      }
    },
    logoutCallBack() {
      const appStore = useAppStore();
      this.resetInfo();
      clearToken();
      removeRouteListener();
      appStore.clearFatherMenu();
      appStore.clearServerMenu();
      const dictStore = useDictStore();
      dictStore.cleanDict();
    },
    // Logout
    async logout() {
      try {
        let res = await userLogout();
      } finally {
        this.logoutCallBack();
      }
    },
  },
});

export default useUserStore;
