import axios from "axios";
import type { RouteRecordNormalized } from "vue-router";
import { UserState } from "@/store/modules/user/types";

export interface LoginData {
  username: string;
  password: string;
  browserFlag: string;
}

export interface LoginRes {
  token: string;
}
export function login(data: LoginData) {
  return axios.post<LoginRes>("/auth/login", data);
}

export function getCaptcha(params: any) {
  return axios.get<any>("/code", {
    params,
  });
}

export function getUserInfo() {
  return axios.get<UserState>("/system/user/getInfo");
}

export function logout() {
  return axios.delete<LoginRes>("/auth/logout");
}

export function getMenuList() {
  return axios.get<RouteRecordNormalized[]>("/system/menu/getEnergyRouters");
}
